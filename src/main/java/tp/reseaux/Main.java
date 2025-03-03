package tp.reseaux;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
	
	private static final String CHEMIN_FICHIER_EXEMPLE_DGS = "/Users/hamaba/Documents/Simulation-Routage/src/main/resources/TopologieExemple.dgs";
    private static GestionnaireChargeurs gestionnaireChargeurs = new GestionnaireChargeurs();
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Reseau reseau = new Reseau();
        
        System.out.println("=== TP Réseau - Routage ===");
        System.out.println("Comment souhaitez-vous saisir la topologie ?");
        System.out.println("1. Charger depuis un fichier texte");
        System.out.println("2. Charger depuis un fichier DGS");
        System.out.println("3. Charger l'exemple .DGS prédéfini");
        
        int choix = scanner.nextInt();
        
        switch (choix) {
            case 1:
                chargerDepuisFichier(reseau, scanner);
                break;
            case 2:
                chargerDepuisFichierAvecGestionnaire(reseau, scanner);
                break;
            case 3:
                chargerExemple(reseau);
                break;
            default:
                System.out.println("Choix invalide. Utilisation de l'exemple prédéfini.");
                chargerExemple(reseau);
        }
        
        GrapheVisualisation visualisation = new GrapheVisualisation(reseau, false);
        visualisation.afficher();
        
        while (true) {
            System.out.println("\n=== Menu principal ===");
            System.out.println("1. Trouver le chemin le plus court entre deux noeuds");
            System.out.println("2. Générer les tables de routage");
            System.out.println("3. Établir un circuit virtuel");
            System.out.println("4. Quitter");
            System.out.print("Votre choix: ");
            
            int menuChoice = scanner.nextInt();
            
            switch (menuChoice) {
                case 1:
                    rechercherCheminPlusCourt(reseau, scanner, visualisation);
                    break;
                case 2:
                    genererTablesRoutage(reseau);
                    break;
                case 3:
                    etablirCircuitVirtuel(reseau, scanner);
                    break;
                case 4:
                    System.out.println("Au revoir merci = ) ");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Choix invalide.");
            }
        }
    }
    
    private static void chargerDepuisFichier(Reseau reseau, Scanner scanner) {
        try {
            System.out.print("Entrez le chemin du fichier: ");
            
            String filePath = scanner.next();
            
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            // Format attendu:
            // #NOEUDS
            // type,id
            // #LIENS
            // noeud1,interface1,noeud2,interface2,poids
            
            boolean readingNodes = false;
            boolean readingLinks = false;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.equals("#NOEUDS")) {
                    readingNodes = true;
                    readingLinks = false;
                    continue;
                } else if (line.equals("#LIENS")) {
                    readingNodes = false;
                    readingLinks = true;
                    continue;
                } else if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                if (readingNodes) {
                    String[] parts = line.split(",");
                    String type = parts[0];
                    String id = parts[1];
                    
                    if ("machine".equalsIgnoreCase(type)) {
                        reseau.ajouterMachine(id);
                    } else if ("commutateur".equalsIgnoreCase(type)) {
                        reseau.ajouterCommutateur(id);
                    }
                } else if (readingLinks) {
                    String[] parts = line.split(",");
                    String noeud1Id = parts[0];
                    String interface1 = parts[1];
                    String noeud2Id = parts[2];
                    String interface2 = parts[3];
                    int poids = Integer.parseInt(parts[4]);
                    
                    Noeud noeud1 = reseau.trouverNoeud(noeud1Id);
                    Noeud noeud2 = reseau.trouverNoeud(noeud2Id);
                    
                    if (noeud1 != null && noeud2 != null) {
                        reseau.ajouterLien(noeud1, interface1, noeud2, interface2, poids);
                    }
                }
            }
            
            reader.close();
            System.out.println("Fichier chargé avec succès.");
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture du fichier: " + e.getMessage());
            System.out.println("Utilisation de l'exemple prédéfini à la place.");
            chargerExemple(reseau);
        }
    }
    
    private static void chargerDepuisFichierAvecGestionnaire(Reseau reseau, Scanner scanner) {
        System.out.print("Entrez le chemin du fichier DGS: ");
        String cheminFichier = scanner.next();
        
        boolean succes = gestionnaireChargeurs.chargerTopologie(reseau, cheminFichier);
        
        if (succes) {
            System.out.println("Fichier DGS chargé avec succès.");
        } else {
            System.out.println("Impossible de charger le fichier DGS. Utilisation de l'exemple prédéfini.");
            chargerExemple(reseau);
        }
    }
    
    private static void chargerExemple(Reseau reseau) {
        System.out.println("Chargement de l'exemple...");
        
        
        boolean succes = gestionnaireChargeurs.chargerTopologie(reseau, CHEMIN_FICHIER_EXEMPLE_DGS);
        
        if (succes) {
            System.out.println("Fichier DGS chargé avec succès.");
        } else {
            System.out.println("Impossible de charger le fichier DGS. Utilisation de l'exemple prédéfini.");
            chargerExemple(reseau);
        }
        
        System.out.println("Exemple chargé avec succès.");
    }
    
    private static void rechercherCheminPlusCourt(Reseau reseau, Scanner scanner, GrapheVisualisation visualisation) {
        System.out.println("\nNoeuds disponibles:");
        for (Noeud noeud : reseau.getNoeuds()) {
            System.out.println("- " + noeud.getId());
        }
        
        System.out.print("Entrez l'ID du noeud source: ");
        String sourceId = scanner.next();
        System.out.print("Entrez l'ID du noeud destination: ");
        String destId = scanner.next();
        
        Noeud source = reseau.trouverNoeud(sourceId);
        Noeud destination = reseau.trouverNoeud(destId);
        
        if (source == null || destination == null) {
            System.out.println("Source ou destination invalide.");
            return;
        }
        
        List<Noeud> chemin = reseau.trouverCheminPlusCourt(source, destination);
        
        if (chemin.isEmpty()) {
            System.out.println("Aucun chemin trouvé entre " + sourceId + " et " + destId);
            return;
        }
        
        System.out.println("\nChemin le plus court de " + sourceId + " à " + destId + ":");
        for (int i = 0; i < chemin.size(); i++) {
            System.out.print(chemin.get(i).getId());
            if (i < chemin.size() - 1) {
                Noeud courant = chemin.get(i);
                Noeud suivant = chemin.get(i + 1);
                Interface interfaceSortie = reseau.trouverInterfaceVers(courant, suivant);
                System.out.print(" (" + interfaceSortie.getNom() + ") -> ");
            }
        }
        System.out.println();
        
        visualisation.afficherChemin(chemin);
    }
    
    private static void genererTablesRoutage(Reseau reseau) {
        System.out.println("\nGénération des tables de routage...");
        reseau.genererTablesRoutage();
        reseau.afficherTablesRoutage();
    }
    
    private static void etablirCircuitVirtuel(Reseau reseau, Scanner scanner) {
        System.out.println("\nMachines disponibles:");
        for (Noeud noeud : reseau.getNoeuds()) {
            if (noeud instanceof Machine) {
                System.out.println("- " + noeud.getId());
            }
        }
        
        System.out.print("Entrez l'ID de la machine source: ");
        String sourceId = scanner.next();
        System.out.print("Entrez l'ID de la machine destination: ");
        String destId = scanner.next();
        
        Noeud source = reseau.trouverNoeud(sourceId);
        Noeud destination = reseau.trouverNoeud(destId);
        
        if (source == null || destination == null || 
            !(source instanceof Machine) || !(destination instanceof Machine)) {
            System.out.println("Source ou destination invalide. Veuillez choisir des machines.");
            return;
        }
        
        List<String> circuit = reseau.genererCircuitVirtuel(source, destination);
        
        if (circuit.isEmpty()) {
            System.out.println("Impossible d'établir un circuit virtuel entre " + 
                             sourceId + " et " + destId);
            return;
        }
        
        System.out.println("\nCircuit virtuel de " + sourceId + " à " + destId + ":");
        for (int i = 0; i < circuit.size(); i++) {
            System.out.println((i+1) + ". " + circuit.get(i));
        }
    }
}