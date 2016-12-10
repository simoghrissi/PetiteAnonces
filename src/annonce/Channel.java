package annonce;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import application.Chat;


public class Channel implements Runnable {
	public DatagramSocket socket;
	private boolean running;
	ArrayList<Message> messageClient = new ArrayList<>();
	public DatagramSocket bind(int port) throws SocketException {
		socket = new DatagramSocket(port);
		return socket;
	}

	public void star() {
		Thread thread = new Thread(this);
		thread.start();
	}

	public void stop() {
		running = false;
		socket.close();
	}

	@Override
	public void run() {

		byte[] buffer = new byte[1024];
		DatagramPacket dataPack = new DatagramPacket(buffer, buffer.length);
		
		running = true;
		while (running) {
			try {
				socket.receive(dataPack);
				String messageRecu =new String(dataPack.getData(), 0, dataPack.getLength());
				String[] message =messageRecu.split(" ");
				String message0=message[0];
				System.out.println("hadaaaaaaaaa: "+message0);
				String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
				System.out.println("leuuurr"+timeStamp);
				InetSocketAddress addressInet = new  InetSocketAddress(dataPack.getAddress(),dataPack.getPort());
				if(message0.equals("WNT")){
					String nomClient =message[2];
					int j=0;
					boolean trouver = Chat.existClient(Chat.listClient,nomClient);
					if(trouver == false){
						Client client = new Client();
						String portClient = message[5];
						String ipClientN = null;
						if(dataPack.getAddress().toString().contains("/")){
							ipClientN=dataPack.getAddress().toString().split("/")[1];
							client.setIp("" + ipClientN);
						}else{
							client.setIp("" + dataPack.getAddress().toString());

						}
						client.setNom(""+nomClient);
						client.setPort("" +portClient);
						Chat.listClient.add(client);
						String nomProprio=message[3];
						String idAnnonce = message[4];
			            System.out.println("wssel hada : "+messageRecu);
						Message messageWNT = new Message();
						messageWNT.setIdAnnonce(idAnnonce);
						messageWNT.setNomClient(nomClient);
						messageWNT.setPortClient(portClient);
						messageWNT.setIpClient(dataPack.getAddress().toString());
						for(int i=0;i< Chat.listClient.size();i++){
							if(Chat.listClient.get(i).getNom().equals(nomProprio)){
								Chat.listClient.get(i).messageClient.add(messageWNT);
								Chat.controller.listeMessageClear();
								for( j=0;j<Chat.listClient.get(i).messageClient.size();j++){
									 String idAn= Chat.listClient.get(i).messageClient.get(j).idAnnonce;
									 String nom = Chat.listClient.get(i).messageClient.get(j).nomClient;
									 String ipClient =Chat.listClient.get(i).messageClient.get(j).ipClient;
									 String portClientS =Chat.listClient.get(i).messageClient.get(j).portClient; 
									 System.out.println("idAn : "+idAn+"nom: "+nom+"ipClient:"+ipClient+"portClient"+portClient);
									 Chat.controller.listeMessage(timeStamp +" : "+"idAnnnonce : "+idAn+"|| nom: "+nom+" ||ipClient: "+ipClient+" ||portClient "+portClient+"\n");
									 Chat.controller.clickChangedTrue();
									 Chat.controller.changerTestLabel();

								}
							}
						}	
					
						}else{
							String nomProprio=message[2];
							String idAnnonce = message[4];
							String portClientS=message[5];
				            System.out.println("wssel hada : "+messageRecu);
							Message messageWNT = new Message();
							messageWNT.setIdAnnonce(idAnnonce);
							messageWNT.setNomClient(nomClient);
							messageWNT.setPortClient(portClientS);
							messageWNT.setIpClient(dataPack.getAddress().toString());
							for(int i=0;i< Chat.listClient.size();i++){
								if(Chat.listClient.get(i).getNom().equals(nomProprio)){
									Chat.listClient.get(i).messageClient.add(messageWNT);
									Chat.controller.listeMessageClear();
									for( j=0;j<Chat.listClient.get(i).messageClient.size();j++){
										 String idAn= Chat.listClient.get(i).messageClient.get(j).idAnnonce;
										 String nom = Chat.listClient.get(i).messageClient.get(j).nomClient;
										 String ipClient =Chat.listClient.get(i).messageClient.get(j).ipClient;
										 String portClient =Chat.listClient.get(i).messageClient.get(j).portClient; 
										 System.out.println("idAn : "+idAn+"nom: "+nom+"ipClient:"+ipClient+"portClient"+portClientS);
										 Chat.controller.listeMessage(timeStamp+" : "+"idAnnnonce : "+idAn+"|| nom: "+nom+" ||ipClient: "+ipClient+" ||portClient "+portClientS+"\n");
										 Chat.controller.clickChangedTrue();
										 Chat.controller.changerTestLabel();
									}
								}
							}	
						}
				}
				if(message0.equals("MSG")){
					message =messageRecu.split(" ",4);
					String nomClient =message[2];
					String rest =message[3];
					Chat.controller.appendListChat(timeStamp+" : "+nomClient+" dit : "+rest+"\n");
				}
				if(message0.equals("OKK")){
					message = messageRecu.split(" ");
					String reponseOK =message[0];
					String nomClient =message[3];
					//String nomClient =Chat.NomClient(Integer.toString(dataPack.getPort()));
					Chat.controller.appendListChat(timeStamp+" : "+nomClient+" dit : "+reponseOK+"\n");
				}
				if(message0.equals("NOO")){
					message = messageRecu.split(" ");
					String reponseNO =message[0];
					//String nomClient =Chat.NomClient(Integer.toString(dataPack.getPort()));
					Chat.controller.appendListChat(timeStamp+" : "+"Desoler pour vous, vous avez reçu :"+reponseNO+"\n");
					//Chat.controller.clearListChat();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public void sendTo(InetSocketAddress address, String msg,DatagramSocket socket)
			throws IOException {
		byte[] buffer = msg.getBytes();
		DatagramPacket datapacket = new DatagramPacket(buffer,buffer.length,address);
		datapacket.setSocketAddress(address);

		socket.send(datapacket);

	}
	
}
