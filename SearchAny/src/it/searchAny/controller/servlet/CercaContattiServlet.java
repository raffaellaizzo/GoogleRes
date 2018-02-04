package it.searchAny.controller.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.xml.XmlPage;

import it.searchAny.bean.BeanRes;
import it.searchAny.bean.Contatto;
import it.searchAny.bean.Place;
import it.searchAny.controller.PlacesService;
import it.searchAny.db.dao.MariaDBContattiDAO;

public class CercaContattiServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String KEY = "AIzaSyD1ezQ62XUDXri4hvQnuUElCThtpcSypPY";

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		List<Contatto> listaContatti = null;
		CercaContattiServlet cercaContatti = new CercaContattiServlet();
		
		//Cerca Città
		Place city = searchCity("Berlino");

		//Cerca Conttti
		List<Contatto> contactsList = searchContactsPre(KEY, "italian restaurant", city.getLatCentre(),
				city.getLngNorthEast(), "1875", city);
		
		//Salva Contatti
		saveContacts(contactsList);
		
		System.out.println("Citta: " + city.getCitta());
	}

	private WebClient configureApi() {
		WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setCssEnabled(false);
		return webClient;
	}

	private Place searchCity(String city) {
		Place place = PlacesService.searchCity(KEY, city);
		return place;

	}

	private List<Contatto> searchContactsPre(String key, String keyword, String lat, String lng, String radius,
			Place city) {
		List<Contatto> contactsList = new ArrayList<Contatto>();

		float latNE = Float.valueOf(city.getLatNorthEast()).floatValue();
		float latSW = Float.valueOf(city.getLatSouthWest()).floatValue();
		float lngNE = Float.valueOf(city.getLngNorthEast()).floatValue();
		float lngSW = Float.valueOf(city.getLngSouthWest()).floatValue();
		float earthRadius = 6378137;
		float PI = 3.14f;
		float GR = 180;
		float latNew = latSW;
		float lngNew = lngSW;

		/*
		 * Position, decimal degrees lat = 51.0 lon = 0.0
		 * 
		 * //Earth’s radius, sphere R=6378137 57,32 //offsets in meters dn = 100 de =
		 * 100
		 * 
		 * //Coordinate offsets in radians dLat = dn/R dLon = de/(R*Cos(Pi*lat/180))
		 * 
		 * //OffsetPosition, decimal degrees latO = lat + dLat * 180/Pi lonO = lon +
		 * dLon * 180/Pi
		 */
		int i = 0;
		boolean latFlag = true;
		boolean lngFlag = true;
		while (latFlag) {
			latNew = latNew + (Integer.valueOf(radius).intValue() * 1000 / earthRadius) * (PI / GR);
			if (latNew > latNE) {
				latFlag = false;
			}
			while (lngFlag) {
				 contactsList = searchContacts(contactsList, key, keyword,
				 String.valueOf(latNew),
				 String.valueOf(lngNew), radius, city);
				// System.out.println("lat: " + latNew + "latNE" + latNE + " lng: " + lngNew);
				lngNew = lngNew + (((Integer.valueOf(radius).intValue() * 1000)
						/ (earthRadius * ((float) java.lang.Math.cos((PI * (latNew / GR)))))) * (PI / GR));
				if (lngNew > lngNE) {
					lngFlag = false;
				}
				i++;
			}
			lngFlag = true;
		}
		System.out.println(i);
		return contactsList;
	}

	private List<Contatto> searchContacts(List<Contatto> contactsList, String key, String keyword, String lat,
			String lng, String radius, Place city) {
		boolean isIterable = true;

		while (isIterable) {
			StringBuilder jsonResults = PlacesService.searchPlace(key, keyword, lat, lng, radius, city);
			try {
				// Create a JSON object hierarchy from the results
				JSONObject jsonObj = new JSONObject(jsonResults.toString());
				// String nextPage = jsonObj.getString("next_page_token");
				JSONArray predsJsonArray = jsonObj.getJSONArray("results");

				// Extract the Place descriptions from the results
				for (int i = 0; i < predsJsonArray.length(); i++) {
					String name = predsJsonArray.getJSONObject(i).getString("name");
					if (name != null && !name.equals("")) {
						Contatto contact = new Contatto();
						contact.setDescrizione(name);
						contact.setCitta(city.getCitta());
						contact.setNazione(city.getNazione());
						contact.setTipo(predsJsonArray.getJSONObject(i).getJSONArray("types").get(0).toString());
						contact.setIdGoogle(predsJsonArray.getJSONObject(i).getString("place_id"));
						PlacesService.details(key, contact.getIdGoogle(), contact);
						findEmail(contact);
						contactsList.add(contact);
					}
				}
				isIterable = !jsonObj.isNull("next_page_token");
			} catch (JSONException e) {
				// Log.e(LOG_TAG, "Error processing JSON results", e);
			}
		}
		return contactsList;
	}

	private Contatto findEmail(Contatto contact) {
		if(contact.getSitoWeb() != null && !contact.getSitoWeb().equals("")) {
			WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setCssEnabled(false);
	
			HtmlPage page3 = null;
			try {
				page3 = webClient.getPage(contact.getSitoWeb());
			} catch (FailingHttpStatusCodeException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String email = "";
			List<HtmlAnchor> anchors = page3.getAnchors();
			for (HtmlAnchor htmlAnchor : anchors) {
				if (htmlAnchor.getHrefAttribute().startsWith("mailto:"))
					email = htmlAnchor.getHrefAttribute().substring(7);
			}
			System.out.println(contact.getDescrizione()+(!email.trim().equals("") ? "-->Email Tovata: " + email : "-->Email Non Trovata")); // BeanRes bean = new
		}
		return contact;
	}

	private List<Contatto> apiCall(String citta, WebClient webClient, String key) {
		List<Contatto> listaContatti = null;
		Boolean result = true;
		String next_page = "";
		String urlNew = "";

		// logger.info("Inizio...");
		System.out.println("Inizio...");

		// key = "AIzaSyD1ezQ62XUDXri4hvQnuUElCThtpcSypPY";
		// String key = "AIzaSyCJHdNtJE3fSBe0QgfC8-ho6swNlWJ0ciw";
		String url = "https://maps.googleapis.com/maps/api/place/textsearch/xml?query=italian restaurants+in+" + citta
				+ "&key=" + KEY;
		int rowCount = 0;
		while (result) {
			urlNew = url + "&pagetoken=" + next_page;
			System.out.println("url: " + urlNew);
			XmlPage page = null;
			try {
				page = webClient.getPage(urlNew);
			} catch (FailingHttpStatusCodeException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println(urlNew);
			DomNodeList<DomElement> ids = page.getElementsByTagName("place_id");
			listaContatti = new ArrayList<Contatto>();
			for (DomElement id : ids) {
				try {
					String url2 = "https://maps.googleapis.com/maps/api/place/details/xml?placeid=" + id.asText()
							+ "&key=" + key + "&pagetoken=" + next_page;
					System.out.println(url2);
					XmlPage page2 = webClient.getPage(url2);
					DomNodeList<DomElement> id2s = page2.getElementsByTagName("website");
					String descrizione = page2.getElementsByTagName("name").get(0).getFirstChild().toString();
					String email = "";
					Contatto contatto = new Contatto();
					contatto.setDescrizione(descrizione);

					for (DomElement id2 : id2s) {
						String url3 = id2.getFirstChild().toString();
						HtmlPage page3 = webClient.getPage(url3);
						System.out.println("url3: " + url3);
						List<HtmlAnchor> anchors = page3.getAnchors();
						for (HtmlAnchor htmlAnchor : anchors) {
							if (htmlAnchor.getHrefAttribute().startsWith("mailto:"))
								email = htmlAnchor.getHrefAttribute().substring(7);
							if (email != null && email.contains("@"))
								contatto.setEmail(email);
						}
						System.out.println(!email.trim().equals("") ? " Tovata: " + email : " Non Trovata");
						contatto.setSitoWeb(url3);
					}

					listaContatti.add(contatto);
				} catch (Exception e) {
					// logger.error(e);
					continue;
				}
			}
		}
		return listaContatti;
	}
	private boolean saveContacts(List<Contatto> contactsList) {
		for (Iterator iterator = contactsList.iterator(); iterator.hasNext();) {
			MariaDBContattiDAO contattoDAO = new MariaDBContattiDAO();
			contattoDAO.insertContatto((Contatto) iterator.next());
		}
		return true;
	}
}
