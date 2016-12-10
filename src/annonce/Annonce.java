//
//package annonce;
//
//import java.io.IOException;
//import java.io.ObjectOutputStream;
//
//public class Annonce {
//	
//	public static Object[][] tabAnnonce(Object tabRet[][],int nbAnnonce){
//		
//		tabRet = new Object[nbAnnonce][6];
//		for (int i = 0; i < nbAnnonce; i++) {
//			tabRet[i][0] = "MLS";
//			tabRet[i][1] = (String) ServeurTCP.listAnnonce
//					.get(i).getNomClient();
//			tabRet[i][2] = (String) ServeurTCP.listAnnonce
//					.get(i).getIdAnnonce();
//			tabRet[i][3] = (String) ServeurTCP.listAnnonce
//					.get(i).getDescription();
//			tabRet[i][4] = (String) ServeurTCP.listAnnonce
//					.get(i).getIpAdresse();
//			tabRet[i][5] = ServeurTCP.listAnnonce.get(i)
//					.getPortNumber();
//
//		}
//		return tabRet;
//	}
//}
