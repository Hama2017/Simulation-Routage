/**
 * TP4 - Simulation de routage dans un reseau informatique
 * Cette classe DGSReseauxParseur permet de lire et parser les fichiers au format DGS
 * Elle analyse chaque ligne du fichier pour extraire les noeuds (ordinateurs) et 
 * les aretes (connexions) avec leurs attributs comme les coordonnees et les poids
 * Elle implemente l'interface ReseauxParseur pour standardiser l'importation
 * 
 * @author HAMADOU BA
 * @github https://github.com/Hama2017
 * @git https://www-apps.univ-lehavre.fr/forge/bh243413/reseaux-tp-4-routage.git
 */

package tp.reseaux;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DGSReseauxParseur implements ReseauxParseur {
	
	protected File nomFichier;
	
	public DGSReseauxParseur(File nomFichier) {
		
		this.nomFichier = nomFichier;
	}
	
    @Override
    public Reseaux importDGS() throws FileNotFoundException, IOException {
        
    	List<Ordinateur> ordinateurs = new ArrayList<>();
    	List<Connection> connections = new ArrayList<>();
    	Reseaux reseaux;
    	String labelReseau = null;
        String idOrdinateur = null;
        String idConnection = null;
        Ordinateur ord1 = null;
        Ordinateur ord2 = null;
        int cout = 0;
        Point position = null;
        BufferedReader br = new BufferedReader(new FileReader(nomFichier));
        String ligne;
        
        int cptLigne=0;

        while ((ligne = br.readLine()) != null) {
        	cptLigne++;
        	
        	if(cptLigne==2) {
    
        		String[] parties = ligne.split(" ");
        		
        		if(parties.length >=1) {
        			labelReseau = parties[0];
        		}else {
        			labelReseau= "Reseaux01";
        		}
        	}
        	
            if (ligne.startsWith("an")) {

            	String[] parties = ligne.split(" ");
                if (parties.length >= 1) {
                    idOrdinateur = parties[1];
                } else {
                    throw new FormatFichierException("Ligne mal formater : " + ligne);
                }

                if (ligne.contains("xy:")) {
                    if (parties.length >= 3) {
                        String[] xy = parties[3].split(",");
                        int x = Integer.parseInt(xy[0].trim());
                        int y = Integer.parseInt(xy[1].trim());
                        position = new Point(x, y);
                    } else {
                        throw new FormatFichierException("Ligne mal formater xy sans valeur : " + ligne);
                    }
                }

                if (idOrdinateur != null) {
                    Ordinateur ordinateur;
                    if (position != null) {
                        ordinateur = new Ordinateur(idOrdinateur, position);
                    } else {
                        ordinateur = new Ordinateur(idOrdinateur);
                    }
                    ordinateurs.add(ordinateur);
                }

            } else if (ligne.startsWith("ae")) {

            	String[] parties = ligne.split(" ");
                if (parties.length >= 4) {
                    idConnection = parties[1];
                    String idOrd1 = parties[2];
                    String idOrd2 = parties[3];

                    ord1 = findOrdinateurById(idOrd1, ordinateurs);
                    ord2 = findOrdinateurById(idOrd2, ordinateurs);

                    if (ord1 == null || ord2 == null) {
                        throw new FormatFichierException("Ordinateur non trouver dans la liste pour : " + idOrd1 + ", " + idOrd2);
                    }

                    if (ligne.contains("weight:")) {
                        String[] weightPart = ligne.split("weight:");
                        cout = Integer.parseInt(weightPart[1].trim());
                    }

                    Connection connection = new Connection(idConnection, ord1, ord2, cout);
                    connections.add(connection);
                } else {
                    throw new FormatFichierException("Ligne mal formatée : " + ligne);
                }
            }
        }
        
        reseaux = new Reseaux(labelReseau, ordinateurs, connections);
        
        return reseaux;
        
    }

    private Ordinateur findOrdinateurById(String id, List<Ordinateur> ordinateurs) {
        for (Ordinateur ord : ordinateurs) {
            if (ord.getId().equals(id)) {
                return ord;
            }
        }
        return null;
    }





}
