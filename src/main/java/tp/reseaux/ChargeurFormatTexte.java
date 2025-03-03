package tp.reseaux;
import java.io.*;

public class ChargeurFormatTexte implements ChargeurTopologie {
    @Override
    public void chargerTopologie(Reseau reseau, String source) throws IOException {
        BufferedReader lecteur = new BufferedReader(new FileReader(source));
        String ligne;
        
        boolean lectureNoeuds = false;
        boolean lectureLiens = false;
        
        try {
            while ((ligne = lecteur.readLine()) != null) {
                ligne = ligne.trim();
                if (ligne.equals("#NOEUDS")) {
                    lectureNoeuds = true;
                    lectureLiens = false;
                    continue;
                } else if (ligne.equals("#LIENS")) {
                    lectureNoeuds = false;
                    lectureLiens = true;
                    continue;
                } else if (ligne.isEmpty() || ligne.startsWith("#")) {
                    continue;
                }
                
                if (lectureNoeuds) {
                    traiterLigneNoeud(reseau, ligne);
                } else if (lectureLiens) {
                    traiterLigneLien(reseau, ligne);
                }
            }
        } finally {
            lecteur.close();
        }
    }
    
    @Override
    public boolean peutCharger(String source) {
        return source.toLowerCase().endsWith(".txt");
    }
    
    private void traiterLigneNoeud(Reseau reseau, String ligne) {
        String[] parties = ligne.split(",");
        String type = parties[0];
        String id = parties[1];
        
        if ("machine".equalsIgnoreCase(type)) {
            reseau.ajouterMachine(id);
        } else if ("commutateur".equalsIgnoreCase(type)) {
            reseau.ajouterCommutateur(id);
        }
    }
    
    private void traiterLigneLien(Reseau reseau, String ligne) {
        String[] parties = ligne.split(",");
        String noeud1Id = parties[0];
        String interface1 = parties[1];
        String noeud2Id = parties[2];
        String interface2 = parties[3];
        int poids = Integer.parseInt(parties[4]);
        
        Noeud noeud1 = reseau.trouverNoeud(noeud1Id);
        Noeud noeud2 = reseau.trouverNoeud(noeud2Id);
        
        if (noeud1 != null && noeud2 != null) {
            reseau.ajouterLien(noeud1, interface1, noeud2, interface2, poids);
        }
    }
}