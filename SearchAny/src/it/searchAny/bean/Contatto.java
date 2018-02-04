package it.searchAny.bean;

import java.io.Serializable;

public class Contatto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id = 0;
	private String idGoogle = "";
	private String tipo = "";
	private String descrizione = "";
	private String Citta = "";
	private String Nazione = "";
	private String email = "";
	private String sitoWeb = "";
	private int emailInviata = 0;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIdGoogle() {
		return idGoogle;
	}
	public void setIdGoogle(String idGoogle) {
		this.idGoogle = idGoogle;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	public String getCitta() {
		return Citta;
	}
	public void setCitta(String citta) {
		Citta = citta;
	}
	public String getNazione() {
		return Nazione;
	}
	public void setNazione(String nazione) {
		Nazione = nazione;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSitoWeb() {
		return sitoWeb;
	}
	public void setSitoWeb(String sitoWeb) {
		this.sitoWeb = sitoWeb;
	}
	public int getEmailInviata() {
		return emailInviata;
	}
	public void setEmailInviata(int emailInviata) {
		this.emailInviata = emailInviata;
	}
	

}
