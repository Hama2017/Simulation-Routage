package tp.reseaux;
import java.io.*;
import java.util.*;

public class ChargeurFormatDGS implements ChargeurTopologie {
    @Override
    public void chargerTopologie(Reseau reseau, String source) throws IOException {
        BufferedReader lecteur = new BufferedReader(new FileReader(source));
        String ligne;
        
        try {
            ligne = lecteur.readLine();
            if (!ligne.startsWith("DGS")) {
                throw new IOException("Format de fichier invalide. L'en-tête DGS est attendu.");
            }
            
            lecteur.readLine(); 
            
            Map<String, Integer> compteursInterfaces = new HashMap<>();
            
            while ((ligne = lecteur.readLine()) != null) {
                ligne = ligne.trim();
                if (ligne.isEmpty() || ligne.startsWith("#")) {
                    continue;
                }
                
                String[] parties = ligne.split("\\s+");
                String typeEvenement = parties[0];
                
                switch (typeEvenement) {
                    case "an": 
                        traiterEvenementAjoutNoeud(reseau, compteursInterfaces, parties);
                        break;
                    case "ae": 
                        traiterEvenementAjoutArete(reseau, compteursInterfaces, parties);
                        break;
                }
            }
        } finally {
            lecteur.close();
        }
    }
    
    @Override
    public boolean peutCharger(String source) {
        return source.toLowerCase().endsWith(".dgs");
    }
    
    private void traiterEvenementAjoutNoeud(Reseau reseau, Map<String, Integer> compteursInterfaces, String[] parties) {
        if (parties.length < 2) return;
        
        String idNoeud = nettoyerIdentifiant(parties[1]);
        boolean estMachine = false;
        
        for (int i = 2; i < parties.length; i++) {
            if (parties[i].startsWith("type") && (parties[i].contains(":") || parties[i].contains("="))) {
                String[] partiesAttr = parties[i].split("[:=]", 2);
                if (partiesAttr.length > 1 && partiesAttr[1].equalsIgnoreCase("machine")) {
                    estMachine = true;
                }
            }
        }
        
        if (idNoeud.startsWith("PC-")) {
            estMachine = true;
        }
        
        if (estMachine) {
            reseau.ajouterMachine(idNoeud);
        } else {
            reseau.ajouterCommutateur(idNoeud);
        }
        
        compteursInterfaces.put(idNoeud, 0);
    }
    
    private void traiterEvenementAjoutArete(Reseau reseau, Map<String, Integer> compteursInterfaces, String[] parties) {
        if (parties.length < 4) return;
        
        String idArete = nettoyerIdentifiant(parties[1]);
        String idSource = nettoyerIdentifiant(parties[2]);
        
        int indexCible = 3;
        boolean estDirige = false;
        
        if (parties[3].equals(">") || parties[3].equals("<")) {
            estDirige = true;
            indexCible = 4;
        }
        
        if (indexCible >= parties.length) return;
        
        String idCible = nettoyerIdentifiant(parties[indexCible]);
        
        Noeud source = reseau.trouverNoeud(idSource);
        Noeud cible = reseau.trouverNoeud(idCible);
        
        if (source == null || cible == null) return;
        
        int poids = 1; 
        for (int i = indexCible + 1; i < parties.length; i++) {
            if (parties[i].contains("weight")) {
                String[] partiesPoids = parties[i].split("[:=]", 2);
                if (partiesPoids.length > 1) {
                    try {
                        poids = Integer.parseInt(partiesPoids[1].trim());
                    } catch (NumberFormatException e) {
                    }
                }
                break;
            }
        }
        
        String interfaceSource = genererNomInterface(compteursInterfaces, idSource);
        String interfaceCible = genererNomInterface(compteursInterfaces, idCible);
        
        reseau.ajouterLien(source, interfaceSource, cible, interfaceCible, poids);
    }
    
    private String nettoyerIdentifiant(String id) {
        if (id.startsWith("\"") && id.endsWith("\"")) {
            return id.substring(1, id.length() - 1);
        }
        return id;
    }
    
    private String genererNomInterface(Map<String, Integer> compteursInterfaces, String idNoeud) {
        int compteur = compteursInterfaces.getOrDefault(idNoeud, 0);
        String nomInterface = "eth" + compteur;
        compteursInterfaces.put(idNoeud, compteur + 1);
        return nomInterface;
    }
}