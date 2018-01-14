package it.free;



public class BeanRes {
	private String website;
	private String mail;
	private String descrizione;
	
	

	public String getWebsite() {
		return website;
	}



	public void setWebsite(String website) {
		this.website = website;
	}



	public String getMail() {
		return mail;
	}



	public void setMail(String mail) {
		this.mail = mail;
	}






	public String getDescrizione() {
		return descrizione;
	}



	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}



	public BeanRes(String mail, String descrizione, String website) {
		super();
		this.website = website;
		this.mail = mail;
		this.descrizione = descrizione;
	}
	

}
