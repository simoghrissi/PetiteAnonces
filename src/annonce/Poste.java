package annonce;

import java.util.ArrayList;

public class Poste {
	Object pst ; 
	Object nomClient ; 
	Object idAnnonce ; 
	Object description ;
	Object ipAdresse;
	Object PortNumber;
		
	public Object getIpAdresse() {
		return ipAdresse;
	}
	public void setIpAdresse(Object ipAdresse) {
		this.ipAdresse = ipAdresse;
	}
	public Object getPortNumber() {
		return PortNumber;
	}
	public void setPortNumber(Object portNumber) {
		PortNumber = portNumber;
	}
	
	public Poste(Object pst, Object nomClient, Object idAnnonce,
			Object description, Object ipAdresse, Object portNumber) {
		this.pst = pst;
		this.nomClient = nomClient;
		this.idAnnonce = idAnnonce;
		this.description = description;
		this.ipAdresse = ipAdresse;
		PortNumber = portNumber;
	}
	public Poste(Object pst, Object nomClient, Object idAnnonce,
			Object description) {
		
		this.pst = pst;
		this.nomClient = nomClient;
		this.idAnnonce = idAnnonce;
		this.description = description;
	}
	public Object getPst() {
		return pst;
	}
	public void setPst(Object pst) {
		this.pst = pst;
	}
	public Object getNomClient() {
		return nomClient;
	}
	public void setNomClient(Object nomClient) {
		this.nomClient = nomClient;
	}
	public Object getIdAnnonce() {
		return idAnnonce;
	}
	public void setIdAnnonce(Object idAnnonce) {
		this.idAnnonce = idAnnonce;
	}
	public Object getDescription() {
		return description;
	}
	public void setDescription(Object description) {
		this.description = description;
	}
	
	
	}
