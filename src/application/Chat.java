package application;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import annonce.Channel;
import annonce.Client;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Chat extends Application {

	static int clientNum = 0;
	static boolean var = true;
	static InetSocketAddress address;
	static Object tab[][] = new Object[1][6];
	static ObjectInputStream networkIn = null;
	static ObjectOutputStream out = null;
	static Socket theSocket = null;
	static int idAnnonce = 0;
	static String reponse;
	public static ArrayList<Client> listClient = new ArrayList<>();
	static String nom_client = null;
	static String ipAdresse = null;
	static String ip_client = null;
	static DatagramSocket socket;
	static Channel channel;
	@FXML
	public Button send, btnEnvoyerA, buttonSendAnnonce, btnQuitter, demandeAnn;
	public TextField textAnonce, textsupprimer, textDemande;
	public Label message, labeNotif;
	public TextArea textAnnonceList, listMessage;
	public TextArea listChat;
	public TextArea fieldChat;
	public static Stage primaryStage;
	public static Chat controller;
	public Tab tablistMessage;
	public GridPane GridListAnnonce;
	FXMLLoader loader = new FXMLLoader();

	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		loader.setLocation(Chat.class.getResource("Chat.fxml"));
		AnchorPane chat = (AnchorPane) loader.load();
		controller = (Chat) loader.getController();
		Scene scene = new Scene(chat);
		primaryStage.setScene(scene);
		primaryStage.show();
		controller.modifierNom(nom_client);
		Thread thread = new Thread() {
			public void run() {
				while (var) {
					try {
						tab = (Object[][]) networkIn.readObject();
						System.out.println(tab[0][0]);
						if (tab[0][0] != null && tab[0][0].equals("RFS")) {
							System.out.println("RFS : client refuser");
							networkIn.close();
							out.close();
							theSocket.close();
							var = false;
							System.exit(0);
							try {
								Thread.sleep(10);
							} catch (InterruptedException ex) {
								Thread.currentThread().interrupt();
								break;
							}
						}
						if (tab[0][0] != null && tab[0][0].equals("MLS")) {
							boolean trouver = true;
							int j;
							for (int i = 0; i < tab.length; i++) {
								j = 0;
								trouver = false;
								System.out.println(tab[i][1]);
								if (listClient.size() == 0) {
									Client client = new Client();
									client.setIp("" + tab[i][4]);
									client.setNom("" + tab[i][1]);
									client.setPort("" + tab[i][5]);
									listClient.add(client);
								} else if (tab[i][1] == null) {
									continue;
								} else {
									while (j < listClient.size() && trouver == false) {
										if (tab[i][1].equals(listClient.get(j).getNom())) {
											trouver = true;
										}

										j++;
									}
									if (trouver == false) {
										Client client = new Client();
										client.setIp("" + tab[i][4]);
										client.setNom("" + tab[i][1]);
										client.setPort("" + tab[i][5]);
										listClient.add(client);
									}

								}

							}

							System.out.println("taille liste" + listClient.size());
							controller.viderListe();

							for (int i = 0; i < tab.length; i++) {
								if (tab[i][1] == null) {
									continue;
								}
								controller.appendMessage(tab[i][1] + "," + tab[i][2] + "," + tab[i][3] + "," + tab[i][4]
										+ "," + tab[i][5] + "\n");
							}
						}
						Thread.sleep(300);
					} catch (Exception e) {
						System.out.println("Deconnection ");
						// e.printStackTrace();
						var = false;
					}
				}
			};
		};
		thread.start();

	}

	// pour envoyer une annonce
	public void envoyerAnnonce() throws IOException {
		String message = textAnonce.getText();
		Object tabPST[][] = new Object[1][6];
		tabPST[0][0] = (String) "PST";
		tabPST[0][1] = (String) nom_client;
		tabPST[0][2] = String.valueOf(idAnnonce);
		tabPST[0][3] = (String) message;
		tabPST[0][4] = (String) ip_client;
		tabPST[0][5] = String.valueOf(theSocket.getLocalPort());
		idAnnonce++;
		textAnonce.clear();
		out.writeObject(tabPST);
		out.flush();

	}

	public void modifierNom(String nom) {
		primaryStage.setTitle("Chat de : " + nom);

	}

	public void supprimerAnonce() throws IOException {

		String message = textsupprimer.getText();
		Object tabDEL[][] = new Object[1][3];
		tabDEL[0][0] = (String) "DEL";
		tabDEL[0][1] = (String) nom_client;
		tabDEL[0][2] = (String) message;
		out.writeObject(tabDEL);
		out.flush();
		textsupprimer.clear();

	}

	public void demandeAnnonce() throws IOException {
		String message = textDemande.getText();

		String[] arrayM = message.split(" ");
		String port = PortClient(arrayM[0]);
		String idAnnonce = (arrayM[1]);
		String destinationIp = IPClient(arrayM[0]);
		int monPort = theSocket.getLocalPort();
		String messageEnvoyer = message.length() + " " + nom_client + " " + arrayM[0] + " " + idAnnonce + " " + monPort;
		message = "WNT " + messageEnvoyer.length() + " " + nom_client + " " + arrayM[0] + " " + idAnnonce + " "
				+ monPort;
		int destinationPort = Integer.parseInt(port);
		InetSocketAddress addressInet = new InetSocketAddress(destinationIp, destinationPort);
		channel.sendTo(addressInet, message, socket);
		textDemande.clear();
	}

	public void appendListChat(String message) {
		listChat.appendText(message);
	}

	public void clearListChat() {
		listChat.clear();
	}

	public void changerTestLabel() {
		labeNotif.setStyle("-fx-background-color: red;-fx-background-radius: 10;");

	}

	public void changerNomTab() {
		String message = tablistMessage.getText();

	}

	public void clickChanged() {
		labeNotif.setVisible(false);
	}

	public void clickChangedTrue() {
		labeNotif.setVisible(true);
	}

	// dialogue pour envoyer message
	public void sendMessage() throws IOException {
		String message = fieldChat.getText();
		String[] arrayM = message.split(" ", 2);
		String nom = (arrayM[0]);
		String destinationIp = IPClient(nom);
		String port = PortClient(nom);
		int destinationPort = Integer.parseInt(port);
		String restMessage = arrayM[1];
		if (restMessage.contains("OKK")) {
			String[] rest = restMessage.split(" ");
			String idAnnonce = rest[1];
			String messageE = idAnnonce + " " + nom_client;
			String messageEnvoyer = "OKK " + messageE.length() + " " + messageE;
			System.out.println(messageEnvoyer);
			System.out.println("destion ip : " + destinationIp);
			System.out.println("destionsation port" + destinationPort);
			String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
			listChat.appendText(timeStamp + " : " + nom_client + " dit :" + rest[0] + "\n");
			fieldChat.clear();
			InetSocketAddress addressInet = new InetSocketAddress(destinationIp, destinationPort);
			channel.sendTo(addressInet, messageEnvoyer, socket);
			
			Object tabDEL[][] = new Object[1][3];
			tabDEL[0][0] = (String) "DEL";
			tabDEL[0][1] = (String) nom_client;
			tabDEL[0][2] = (String) idAnnonce;
			out.writeObject(tabDEL);
			out.flush();
			
			for (int i = 0; i < listClient.get(i).messageClient.size(); i++) {
				if (!(listClient.get(i).messageClient.get(i).getNomClient().equals(nom))
						&& listClient.get(i).messageClient.get(i).getIdAnnonce().equals(idAnnonce)) {
					String destinationIpC = IPClient(listClient.get(i).messageClient.get(i).getNomClient());
					String portC = PortClient(listClient.get(i).messageClient.get(i).getNomClient());
					int destinationPortC = Integer.parseInt(portC);
					String messageEnvoyerC = "NOO " +idAnnonce.length() + " " + idAnnonce;
					InetSocketAddress addressInetC = new InetSocketAddress(destinationIpC, destinationPortC);
					channel.sendTo(addressInetC, messageEnvoyerC, socket);
				}
			}
			
		} else if (restMessage.contains("NOO")) {
			String[] rest = restMessage.split(" ");
			String idAnnonce = rest[1];
			String messageEnvoyer = "NOO " + idAnnonce.length() + " " + idAnnonce;
			System.out.println(messageEnvoyer);
			String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
			listChat.appendText(timeStamp + " : " + nom_client + " dit :" + rest[0] + "\n");
			fieldChat.clear();
			InetSocketAddress addressInet = new InetSocketAddress(destinationIp, destinationPort);
			channel.sendTo(addressInet, messageEnvoyer, socket);

		} else {
			String messageEnvoyer = "MSG " + message.length() + " " + nom_client + " " + restMessage;
			System.out.println(messageEnvoyer);
			System.out.println(destinationPort);
			System.out.println(destinationIp);
			String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
			listChat.appendText(timeStamp + " : " + nom_client + " dit :" + restMessage + "\n");
			fieldChat.clear();
			InetSocketAddress addressInet = new InetSocketAddress(destinationIp, destinationPort);
			channel.sendTo(addressInet, messageEnvoyer, socket);
		}

	}

	public void quitterApplication() {
		Object tabDEL[][] = new Object[1][3];
		tabDEL[0][0] = (String) "HMD";
		tabDEL[0][1] = (String) nom_client;
		try {
			out.writeObject(tabDEL);
			out.flush();

		} catch (Exception e) {

		} finally {
			System.exit(0);
		}

	}

	public void listeMessage(String message) {
		listMessage.appendText(message);
	}

	public void listeMessageClear() {
		listMessage.clear();
	}

	public void appendMessage(String message) {
		textAnnonceList.appendText(message);
	}

	public void viderListe() {
		textAnnonceList.clear();
	}

	public static String NomClient(String port) {

		for (int i = 0; i < listClient.size(); i++) {
			if (listClient.get(i).getPort().equals(port)) {
				return listClient.get(i).getNom();

			}
		}
		return "-1";
	}

	public static boolean existClient(ArrayList<Client> list, String nomClient) {
		int j = 0;
		boolean trouver = false;
		while (j < list.size() && trouver == false) {
			if (nomClient.equals(list.get(j).getNom())) {
				trouver = true;
			}

			j++;
		}
		return trouver;
	}

	public static String IPClient(String nom) {
		for (int i = 0; i < listClient.size(); i++) {
			if (listClient.get(i).getNom().equals(nom)) {
					if(listClient.get(i).getIp().toString().contains("/")){
						return (listClient.get(i).getIp().toString()).split("/")[1];
					}else{
						return listClient.get(i).getIp();
	
					}

			}
		}
		return "-1";
	}

	public static String PortClient(String nom) {
		for (int i = 0; i < listClient.size(); i++) {
			if (listClient.get(i).getNom().equals(nom)) {
				return listClient.get(i).getPort();
			}
		}
		return "-1";
	}

	public static void main(String[] args) throws IOException {
		try {
			theSocket = new Socket(args[2], 1028);

			out = new ObjectOutputStream(theSocket.getOutputStream());
			networkIn = new ObjectInputStream(theSocket.getInputStream());

		} catch (Exception e) {
			e.printStackTrace();
		}

		channel = new Channel();
		socket = channel.bind(theSocket.getLocalPort());
		channel.star();

		if (args.length > 0) {
			String SLM = args[0];
			nom_client = args[1];
			ipAdresse = args[2];
			ip_client = args[3];
			// portNumber = Integer.parseInt(args[3]);

			if (args[0].equals("SLM")) {
				tab[0][0] = SLM;
				tab[0][1] = nom_client;
				tab[0][2] = ip_client;
				tab[0][3] = String.valueOf(theSocket.getLocalPort());
				System.out.println("Client: " + nom_client + " Connecte au serveur" + theSocket);
				out.writeObject(tab);
				out.flush();

			} else {
				networkIn.close();
				out.close();
				theSocket.close();
			}

		}
		launch(args);

	}
}