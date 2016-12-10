package annonce;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.LinkedList;

public class Serveur implements Runnable{
	private  static int PORT = 1028;
	private static String IPSERVEUR = "localhost";
	private ServerSocket server;
	private static LinkedList<ObjectOutputStream> listSockets;
	private static Hashtable<String , String> clients;
	private static Object[][] announces;
	private boolean on = true;
	private static int nbrAnnounces = 8;
	private static int flag = 0;
	private static Hashtable<ObjectOutputStream, String> nameAndStream;
	private final static int COL = 6;
	
	
	public static Object[][] getAnnounces(){
		return announces;
	}
	/**
	 * @param tmp
	 *  setter for announces Object
	 */
	public static void setAnnounces(Object[][] tmp){
		announces = tmp;
	}
	/**
	 * 
	 */
	// Serveur class constructor
	public Serveur(int portNumber) {
		// TODO Auto-generated constructor stub
		/*
		 * Choix automatique du premier numero de port disponible 
		 */
		clients = new Hashtable<String, String>();
		announces = new String[nbrAnnounces][COL];
		listSockets = new LinkedList<ObjectOutputStream>();
		nameAndStream = new Hashtable<ObjectOutputStream, String>();
		PORT = portNumber;
		try {
			server = new ServerSocket(PORT);
			System.out.println("Numero de port choisi : "+PORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Numero de port indisponible");
		}
		/*for(int i = 1028; i <= 65535; i++){
			try {
				server = new ServerSocket(i);
				PORT = i;
				System.out.println("Numero de port choisi : "+PORT);
				break; // ieme est un numero de port disponible
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Numero de port indisponible");
			}
		}*/
	}
	
	/**
	 * @return
	 * Getter for listSockets object
	 */
	
	public static LinkedList<ObjectOutputStream> getListSockets(){
		return listSockets;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Socket socket;
		ObjectOutputStream out;
		while(on){
			try {
				socket = server.accept();
				out = new ObjectOutputStream(socket.getOutputStream());
				listSockets.add(out);
				Thread thClient = new Thread(new ServerThread(socket, out));
				thClient.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if(args.length != 1){
			System.err.println("Veuillez specifier [le numero de Port] SVP");
			System.err.println("java "+Serveur.class.getCanonicalName()+" <Numero de Port>");
			System.exit(0);
		}
		int port = 1028;
		try{
			port = Integer.parseInt(args[0]);
		}
		catch(java.lang.NumberFormatException nexn){
			System.err.println(port +": n'est pas un nombre");
			//System.out.println("Donc numero de port choisi "+port);
		}
		
		Thread thServeur = new Thread(new Serveur(port));
		thServeur.start();
	}
	/**
	 * @return
	 * Getter for Server Port
	 */
	public static int getPort() {
		// TODO Auto-generated method stub
		return PORT;
	}
	
	/**
	 * @return
	 * Getter for Server IP
	 */
	public static String getIpServeur(){
		return IPSERVEUR;
	}
	
	/**
	 * @param nom
	 * @param ip
	 * @param port
	 * @param output
	 * @return
	 * 
	 */
	/* Method to saving the client name in table of client who has type
	 * Hashtable. In this method, there is an unicity test for client name
	 * If the client name is exist then it will be refused
	 * If not, it will be saved using this order (client name, ip : port )
	 * */
	public static synchronized boolean registerClient(String nom, String ip, String port, ObjectOutputStream output){
		if(clients.keySet().contains(nom)) return true;
		clients.put(nom, ip + ":"+port);
		nameAndStream.put(output, nom);// CTRL C
		return false;
	}
	
	public static String getInfos(String name){
		if(clients.contains(name))
			return clients.get(name);
		return "";
			
	}
	
	/**
	 * @param name
	 * @param idAnnounce
	 * @param description
	 */
	/*
	 * Updating the list of announces if there is a client who posts an announces.
	 * It fills first column with MLS to facilitate the sending of list of announces 
	 * */
	public static synchronized void getAnnounce(String name, int idAnnounce, String description){
		if((flag + 3) == nbrAnnounces){
			nbrAnnounces *= 2;
			announces = copyClients(announces, nbrAnnounces);
		}
		//System.out.println("Flag === "+ flag +", length == "+announces.length);
		announces[flag][0] = ProtocoleName.MLS;
		announces[flag][1] = name;
		announces[flag][2] = String.valueOf(idAnnounce);
		announces[flag][3] = description;
		announces[flag][4] = extractClientIpAdresse(name);
		announces[flag][5] = extractClientPortNumber(name);
		flag ++;
		
	}
	
	/**
	 * @param name
	 * @return
	 */
	/* Method for removing announce from client. We need to add synchronized
	 * in order to prevent the problem of concurrency because there are several
	 * process who try to access same ressources
	 * */
	public static synchronized Object[][] removeClientAnnounce(String name){
		clients.remove(name);
		Object tmp[][] = new Object [announces.length][COL];
		int k = 0;
		for(int i = 0; i < announces.length; i++){
			if(announces[i][1]!=null && !announces[i][1].equals(name)){
					for(int j = 0; j < announces[i].length; j++){
						tmp[k][j] = announces[i][j];
					}
			k++;	
			}
		}
		flag = k;
		//announces = tmp;
		return tmp;
		
	}
	
	
	
	/**
	 * @param announ
	 * @param nbrAnnounces2
	 * @return
	 */
	/* Method for copy list of annonces 
	 * */
	private static Object[][] copyClients(Object[][] announ,
			int nbrAnnounces2) {
		// TODO Auto-generated method stub
		Object [][]tmp = new Object[nbrAnnounces2][COL];
		for(int i = 0; i < announ.length; i++){
			for(int j = 0; j < announ[i].length; j++){
				tmp[i][j] = announ[i][j];
			}
		}
		return tmp;
	}
	/**
	 * @param name
	 * @return
	 */
	/* Parsing client port by searching client name identified in
	 * clients table who has type hash table
	 * */
	private static String extractClientPortNumber(String name) {
		// TODO Auto-generated method stub
		String ipAddr = clients.get(name);
		return ipAddr.split(":")[1];
		
	}
	
	/**
	 * @param name
	 * @return
	 */
	/* Parsing client IP by searching client name identified in
	 * clients table who has type hash table
	 * */
	private static String extractClientIpAdresse(String name) {
		// TODO Auto-generated method stub
		String ipAddr = clients.get(name);
		return ipAddr.split(":")[0];
	}
	
	/**
	 * @return
	 */
	/* Getter for clients table
	 * */
	public static Hashtable<String , String> getClients() {
		return clients;
	}
	/**
	 * @param clients
	 */
	/* Setter for clients table
	 * */
	public static void setClients(Hashtable<String , String> clients) {
		Serveur.clients = clients;
	}
	/**
	 * 
	 */
	/* Sending list of annonces to all connected client
	 * */
	public synchronized static void sendAnnouncestoAllClient() {
		// TODO Auto-generated method stub
		System.out.println("Envoi des annonces ...");
		display(announces);
		for(ObjectOutputStream output : listSockets){
			try {
				output.writeObject(announces);
				output.reset();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Exception in SendAnnouncestoAllClient");
			}
		}
		System.out.println(".Fin de l'envoi");
		
	}
	
	/**
	 * @param annoncesTable
	 */
	public static String display(Object[][] annoncesTable) {
		// TODO Auto-generated method stub
		String res = "";
		System.out.println("-------Liste des annonces ---------");
		res += "-------Liste des annonces ---------\n";
		for(int i = 0; i<annoncesTable.length; i++){
			if(annoncesTable[i][0] == null)
				break;
			for(int j = 0; j < annoncesTable[i].length; j++){
				System.out.print(annoncesTable[i][j]+" : ");
				res += annoncesTable[i][j]+" : ";
			}
			System.out.println();
			res += "\n";
		}
		System.out.println("-----------------------------------");
		res += "-----------------------------------\n";
		return res;
	}
	
	/**
	 * @param output
	 */
	public static void sendAnnouncestoAClient(ObjectOutputStream output) {
		// TODO Auto-generated method stub
		try {
			output.writeObject(announces);
			output.reset();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param output
	 */
	/* Method for sending protocol RFS to client because it does not satisfy
	 * with the condition of unicity. We need to use syncronized to prevent concurrency problem
	 * */
	public synchronized static void sendRFS(ObjectOutputStream output) {
		// TODO Auto-generated method stub
		Object[][] rfs = new Object[1][1];
		rfs[0][0]=ProtocoleName.RFS;
		try {
			output.writeObject(rfs);
			output.reset();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public synchronized static Object[][] deleteAnnonce(int idAnnonce, String name) {
		// TODO Auto-generated method stub
		Object tmp[][] = new Object [announces.length][COL];
		int k = 0;
		System.out.println("idAnnounce : "+idAnnonce+" name : "+name);
		System.out.println(announces[0][1].equals(name)+"====> "+announces[0][2]);
		
		for(int i = 0; i < announces.length; i++){
			if((announces[i][1]!=null && !announces[i][1].equals(name))){
				for(int j = 0; j < announces[i].length; j++){
					tmp[k][j] = announces[i][j];
				}
				k++;
			}
			else{
				if(announces[i][2]!=null && Integer.parseInt((String)announces[i][2]) != idAnnonce) {
					for(int j = 0; j < announces[i].length; j++){
						tmp[k][j] = announces[i][j];
					}
					k++;
				}
			}
		}
		System.out.println("kkkkkkkk flag : " +k);
		flag = k;
		//announces = tmp;
		return tmp;
	}
}
