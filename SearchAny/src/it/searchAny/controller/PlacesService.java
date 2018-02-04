package it.searchAny.controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.searchAny.bean.Contatto;
import it.searchAny.bean.Place;

//import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author saxman
 */
public class PlacesService {
	private static final String LOG_TAG = "ExampleApp";

	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";

	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String TYPE_DETAILS = "/details";
	private static final String TYPE_SEARCH = "/search";
	private static final String TYPE_TEXTSEARCH = "/textsearch";

	private static final String OUT_JSON = "/json";

	// KEY!
	private static final String API_KEY = "YOUR KEY";

	public static StringBuilder searchPlace(String key, String keyword, String lat, String lng, String radius,
			Place city) {
		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE);
			sb.append(TYPE_TEXTSEARCH);
			sb.append(OUT_JSON);
			sb.append("?sensor=false");
			sb.append("&key=" + key);
			sb.append("&language=it");
			sb.append("&query=" + URLEncoder.encode(keyword, "utf8"));
			sb.append("&location=" + lat + "," + lng);
			sb.append("&radius=" + radius);

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());
			System.out.println("" + sb.toString());
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			// Log.e(LOG_TAG, "Error processing Places API URL", e);
		} catch (IOException e) {
			// Log.e(LOG_TAG, "Error connecting to Places API", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return jsonResults;
	}

	public static Place searchCity(String key, String city) {
		Place place = null;

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE);
			sb.append(TYPE_TEXTSEARCH);
			sb.append(OUT_JSON);
			sb.append("?sensor=false");
			sb.append("&key=" + key);
			sb.append("&language=it");
			sb.append("&query=" + URLEncoder.encode(city, "utf8"));
			// sb.append("&location=" + String.valueOf(lat) + "," + String.valueOf(lng));
			// sb.append("&radius=" + String.valueOf(radius));

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			// Log.e(LOG_TAG, "Error processing Places API URL", e);
			return place;
		} catch (IOException e) {
			// Log.e(LOG_TAG, "Error connecting to Places API", e);
			return place;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray predsJsonArray = jsonObj.getJSONArray("results");

			// Extract the Place descriptions from the results
			String formatted_address = predsJsonArray.getJSONObject(0).getString("formatted_address");
			if (formatted_address != null && !formatted_address.equals("")) {
				String[] splitResult = formatted_address.split(",");
				place = new Place();
				place.setCitta(city);
				place.setNazione(splitResult[0]);
				place.setLatCentre("" + predsJsonArray.getJSONObject(0).getJSONObject("geometry")
						.getJSONObject("location").getDouble("lat"));
				place.setLngCentre("" + predsJsonArray.getJSONObject(0).getJSONObject("geometry")
						.getJSONObject("location").getDouble("lng"));
				place.setLatNorthEast("" + predsJsonArray.getJSONObject(0).getJSONObject("geometry")
						.getJSONObject("viewport").getJSONObject("northeast").getDouble("lat"));
				place.setLngNorthEast("" + predsJsonArray.getJSONObject(0).getJSONObject("geometry")
						.getJSONObject("viewport").getJSONObject("northeast").getDouble("lng"));
				place.setLatSouthWest("" + predsJsonArray.getJSONObject(0).getJSONObject("geometry")
						.getJSONObject("viewport").getJSONObject("southwest").getDouble("lat"));
				place.setLngSouthWest("" + predsJsonArray.getJSONObject(0).getJSONObject("geometry")
						.getJSONObject("viewport").getJSONObject("southwest").getDouble("lng"));
			}
		} catch (JSONException e) {
			// Log.e(LOG_TAG, "Error processing JSON results", e);
		}

		return place;
	}

	public static Contatto details(String key, String placeId, Contatto contact) {
		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE);
			sb.append(TYPE_DETAILS);
			sb.append(OUT_JSON);
			sb.append("?sensor=false");
			sb.append("&key=" + key);
			sb.append("&placeid=" + URLEncoder.encode(placeId, "utf8"));

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			// Log.e(LOG_TAG, "Error processing Places API URL", e);
			return contact;
		} catch (IOException e) {
			// Log.e(LOG_TAG, "Error connecting to Places API", e);
			return contact;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString()).getJSONObject("result");

			// luogo.icon = jsonObj.getString("icon");
			// luogo.name = jsonObj.getString("name");
			// luogo.formatted_address = jsonObj.getString("formatted_address");
//			if (jsonObj.has("formatted_phone_number")) {
				// luogo.formatted_phone_number = jsonObj.getString("formatted_phone_number");
//			}
			if (jsonObj.has("website")) {
				 contact.setSitoWeb(jsonObj.getString("website"));
			}
		} catch (JSONException e) {
			// Log.e(LOG_TAG, "Error processing JSON results", e);
		}

		return contact;
	}

	public static ArrayList<Place> autocomplete(String input) {
		ArrayList<Place> resultList = null;

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE);
			sb.append(TYPE_AUTOCOMPLETE);
			sb.append(OUT_JSON);
			sb.append("?sensor=false");
			sb.append("&key=" + API_KEY);
			sb.append("&input=" + URLEncoder.encode(input, "utf8"));

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			// Log.e(LOG_TAG, "Error processing Places API URL", e);
			return resultList;
		} catch (IOException e) {
			// Log.e(LOG_TAG, "Error connecting to Places API", e);
			return resultList;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

			// Extract the Place descriptions from the results
			resultList = new ArrayList<Place>(predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {
				Place place = new Place();
				// luogo.reference = predsJsonArray.getJSONObject(i).getString("reference");
				// luogo.name = predsJsonArray.getJSONObject(i).getString("description");
				resultList.add(place);
			}
		} catch (JSONException e) {
			// Log.e(LOG_TAG, "Error processing JSON results", e);
		}

		return resultList;
	}

	public static ArrayList<Place> search(String keyword, double lat, double lng, int radius) {
		ArrayList<Place> resultList = null;

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE);
			sb.append(TYPE_TEXTSEARCH);
			sb.append(OUT_JSON);
			sb.append("?sensor=false");
			sb.append("&key=" + API_KEY);
			sb.append("&query=" + URLEncoder.encode(keyword, "utf8"));
			sb.append("&location=" + String.valueOf(lat) + "," + String.valueOf(lng));
			sb.append("&radius=" + String.valueOf(radius));

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			// Log.e(LOG_TAG, "Error processing Places API URL", e);
			return resultList;
		} catch (IOException e) {
			// Log.e(LOG_TAG, "Error connecting to Places API", e);
			return resultList;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray predsJsonArray = jsonObj.getJSONArray("results");

			// Extract the Place descriptions from the results
			resultList = new ArrayList<Place>(predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {
				Place place = new Place();
				// luogo.reference = predsJsonArray.getJSONObject(i).getString("reference");
				// luogo.name = predsJsonArray.getJSONObject(i).getString("name");
				resultList.add(place);
			}
		} catch (JSONException e) {
			// Log.e(LOG_TAG, "Error processing JSON results", e);
		}

		return resultList;
	}
}
