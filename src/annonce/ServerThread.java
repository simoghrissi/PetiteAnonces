package annonce;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Partie 2
 *
 */
public class ServerThread implements Runnable {
	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private boolean connected = true;
	private String nameOfClient="";
	/**
	 * @param soc
	 * @param out
	 * 
	 */
	// ServerThread class constructor
	public ServerThread(Socket soc, ObjectOutputStream out) {
		// TODO Auto-generated constructor stub
		socket = soc;
		output = out;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Object [][]requestFromClient;
		try {
			input = new ObjectInputStream(socket.getInputStream());
			while(connected){
				// ici traiter les clients
				requestFromClient = (Object[][]) input.readObject();
				////System.out.println("Requete envoyée par le Client :"+requestFromClient[0][0]);
				treatMsg(requestFromClient);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Serveur.getListSockets().remove(output);
			Serveur.setAnnounces(Serveur.removeClientAnnounce(nameOfClient));// suppression des annonces du client apres CTRL-C
			Serveur.sendAnnouncestoAllClient();
			//System.out.println("Fin de communication avec Le client: ["+nameOfClient+"]");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("Au revoir!!!!");
	}


	/**
	 * @param requestFromClient
	 */
	/* Handling incoming message by identifying the protocol
	 * */
	private void treatMsg(Object[][] requestFromClient) {
		// TODO Auto-generated method stub
		switch((String)requestFromClient[0][0]){
		case ProtocoleName.SLM: // register client
			//System.out.println("SLM/");
			boolean regist = Serveur.registerClient((String)requestFromClient[0][1], (String)requestFromClient[0][2], String.valueOf(requestFromClient[0][3]), output);
			if(regist){
				Serveur.sendRFS(output);
				connected = false;
				break;
			}
			nameOfClient = (String)requestFromClient[0][1];
			Serveur.sendAnnouncestoAClient(output);
			break;
		case ProtocoleName.PST: // get a client announcement
			//System.out.println("PST/");
			Serveur.getAnnounce((String)requestFromClient[0][1], Integer.parseInt((String) requestFromClient[0][2]), (String)requestFromClient[0][3]);
			Serveur.sendAnnouncestoAllClient();
			break;
		case ProtocoleName.HMD: // Remove all of announces of this client
			//System.out.println("HMD/");
			Serveur.setAnnounces(Serveur.removeClientAnnounce((String)requestFromClient[0][1]));
			//System.out.println("Fin de la communication avec le client :["+nameOfClient+"]");
			Serveur.getListSockets().remove(output);
			Serveur.sendAnnouncestoAllClient();
			break;
		case ProtocoleName.DEL:
			//System.out.println("DEL/");
			Serveur.setAnnounces(Serveur.deleteAnnonce(Integer.parseInt((String)requestFromClient[0][2]), (String) requestFromClient[0][1]));
			Serveur.sendAnnouncestoAllClient();
			break;
		default:
		}
	}
}
