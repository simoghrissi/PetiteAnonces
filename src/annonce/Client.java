package annonce;

import java.io.Serializable;
import java.util.ArrayList;

public class Client implements Serializable {

	String nom ; 
	String ip ; 
	String port ;
	public static ArrayList<Message> messageClient = new ArrayList<>();
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	} 
	
}
