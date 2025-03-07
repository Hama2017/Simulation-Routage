/**
 * TP4 - Simulation de routage dans un reseau informatique
 * Cette classe Main permet de simuler le routage dans un reseau informatique
 * Elle permet de charger un graphe a partir d'un fichier DGS
 * Calculer le chemin le plus court entre deux machines
 * Generer une table de routage pour tous les ordinateurs du reseau
 * 
 * @author HAMADOU BA
 * @github https://github.com/Hama2017
 * @git https://www-apps.univ-lehavre.fr/forge/bh243413/reseaux-tp-4-routage.git
 */


package tp.reseaux;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import org.graphstream.ui.view.Viewer;

public class Main {
    
    private static Reseaux reseaux;
    private static Scanner scanner = new Scanner(System.in);
    private static boolean quitter = false;

    public static void main(String[] args) {
        while (!quitter) {
            afficherMenuPrincipal();
            
            try {
                int choix = lireChoixUtilisateur();
                traiterChoixMenuPrincipal(choix);
            } catch (InputMismatchException e) {
                System.out.println("Erreur: metre un nombre");
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Erreur: " + e.getMessage());
            }
        }
        
        System.out.println("\n Bye : = )");
        scanner.close();
    }
    
    private static void afficherMenuPrincipal() {
    	
    	  String titre = 
                  " _______                                                                     ________\n" +
                  "/       \\                                                                   /        |\n" +
                  "$$$$$$$  |  ______    _______   ______    ______   __    __  __    __       $$$$$$$$/______   ______    _______   ______    ______  \n" +
                  "$$ |__$$ | /      \\  /       | /      \\  /      \\ /  |  /  |/  \\  /  |         $$ | /      \\ /      \\  /       | /      \\  /      \\ \n" +
                  "$$    $$< /$$$$$$  |/$$$$$$$/ /$$$$$$  | $$$$$$  |$$ |  $$ |$$  \\/$$/          $$ |/$$$$$$  |$$$$$$  |/$$$$$$$/ /$$$$$$  |/$$$$$$  |\n" +
                  "$$$$$$$  |$$    $$ |$$      \\ $$    $$ | /    $$ |$$ |  $$ | $$  $$<           $$ |$$ |  $$/ /    $$ |$$ |      $$    $$ |$$ |  $$/ \n" +
                  "$$ |  $$ |$$$$$$$$/  $$$$$$  |$$$$$$$$/ /$$$$$$$ |$$ \\__$$ | /$$$$  \\          $$ |$$ |     /$$$$$$$ |$$ \\_____ $$$$$$$$/ $$ |      \n" +
                  "$$ |  $$ |$$       |/     $$/ $$       |$$    $$ |$$    $$/ /$$/ $$  |         $$ |$$ |     $$    $$ |$$       |$$       |$$ |      \n" +
                  "$$/   $$/  $$$$$$$/ $$$$$$$/   $$$$$$$/  $$$$$$$/  $$$$$$/  $$/   $$/          $$/ $$/       $$$$$$$/  $$$$$$$/  $$$$$$$/ $$/       "
    			+ "\n";
          
         
    	  String auteur = ""
    	  		+ ""
    	  		+ "\n"
    	  		+ ""
    	  		+ " ▄▄▄▄ ▓██   ██▓    ██░ ██  ▄▄▄       ███▄ ▄███▓ ▄▄▄      ▓█████▄  ▒█████   █    ██     ▄▄▄▄    ▄▄▄      \n"
    	  		+ "▓█████▄▒██  ██▒   ▓██░ ██▒▒████▄    ▓██▒▀█▀ ██▒▒████▄    ▒██▀ ██▌▒██▒  ██▒ ██  ▓██▒   ▓█████▄ ▒████▄    \n"
    	  		+ "▒██▒ ▄██▒██ ██░   ▒██▀▀██░▒██  ▀█▄  ▓██    ▓██░▒██  ▀█▄  ░██   █▌▒██░  ██▒▓██  ▒██░   ▒██▒ ▄██▒██  ▀█▄  \n"
    	  		+ "▒██░█▀  ░ ▐██▓░   ░▓█ ░██ ░██▄▄▄▄██ ▒██    ▒██ ░██▄▄▄▄██ ░▓█▄   ▌▒██   ██░▓▓█  ░██░   ▒██░█▀  ░██▄▄▄▄██ \n"
    	  		+ "░▓█  ▀█▓░ ██▒▓░   ░▓█▒░██▓ ▓█   ▓██▒▒██▒   ░██▒ ▓█   ▓██▒░▒████▓ ░ ████▓▒░▒▒█████▓    ░▓█  ▀█▓ ▓█   ▓██▒\n"
    	  		+ "░▒▓███▀▒ ██▒▒▒     ▒ ░░▒░▒ ▒▒   ▓▒█░░ ▒░   ░  ░ ▒▒   ▓▒█░ ▒▒▓  ▒ ░ ▒░▒░▒░ ░▒▓▒ ▒ ▒    ░▒▓███▀▒ ▒▒   ▓▒█░\n"
    	  		+ "▒░▒   ░▓██ ░▒░     ▒ ░▒░ ░  ▒   ▒▒ ░░  ░      ░  ▒   ▒▒ ░ ░ ▒  ▒   ░ ▒ ▒░ ░░▒░ ░ ░    ▒░▒   ░   ▒   ▒▒ ░\n"
    	  		+ " ░    ░▒ ▒ ░░      ░  ░░ ░  ░   ▒   ░      ░     ░   ▒    ░ ░  ░ ░ ░ ░ ▒   ░░░ ░ ░     ░    ░   ░   ▒   \n"
    	  		+ " ░     ░ ░         ░  ░  ░      ░  ░       ░         ░  ░   ░        ░ ░     ░         ░            ░  ░\n"
    	  		+ "      ░░ ░                                                ░                                 ░           ";
         

        System.out.println(titre);
    	  
    	  
    	System.out.println(auteur);
          
        System.out.println("\n===== MENU =====");
        System.out.println("1. Charger un Fichier DGS");
        System.out.println("2. Charger Fichier Exemple");
        System.out.println("3. Aide");
        System.out.println("0. Quitter");
        System.out.print("Ton choix: ");
    }
    
    private static int lireChoixUtilisateur() {
        int choix = scanner.nextInt();
        scanner.nextLine();
        return choix;
    }
    
    private static void traiterChoixMenuPrincipal(int choix) {
        switch (choix) {
            case 0:
                quitter = true;
                break;
            case 1:
                chargerFichierDGS();
                break;
            case 2:
                chargerFichierExempleParDefaut();
                break;
            case 3:
                afficherAide();
                break;
            default:
                System.out.println("Option pas valide, reessaye");
        }
    }

    private static void chargerFichierDGS() {
        System.out.print("Entre le chemin du fichier DGS (ou entrer pour revenir): ");
        String chemin = scanner.nextLine();
        
        if (chemin.isEmpty()) {
            return;
        }
        
        File fichier = new File(chemin);
        
        if (fichier.exists() && fichier.isFile()) {
            try {
                ReseauxParseur rp = new DGSReseauxParseur(fichier);
                reseaux = rp.importDGS();
                System.out.println("Fichier charge");
                afficherReseaux();
                menuActions();
            } catch (FileNotFoundException e) {
                System.out.println("Erreur: Fichier pas trouve");
            } catch (IOException e) {
                System.out.println("Probleme lecture fichier: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Erreur: " + e.getMessage());
            }
        } else {
            System.out.println("Le fichier existe pas");
        }
    }

    private static void chargerFichierExempleParDefaut() {
    	
    	File fichier = null;

        try {

            URL ressourceUrl = Main.class.getClassLoader().getResource("TopologieExemple.dgs");
            fichier = new File(ressourceUrl.toURI());            
            if (!fichier.exists()) {
                System.out.println("Fichier exemple existe pas");
                System.out.print("Entre le chemin du fichier exemple (ou entrer pour revenir): ");
                String chemin = scanner.nextLine();
                
                if (chemin.isEmpty()) {
                    return;
                }
                
                fichier = new File(chemin);
                if (!fichier.exists() || !fichier.isFile()) {
                    System.out.println("Fichier existe pas");
                    return;
                }
            }
            
            ReseauxParseur rp = new DGSReseauxParseur(fichier);
            reseaux = rp.importDGS();
            
            System.out.println("Fichier exemple charge");
            afficherReseaux();
            menuActions();
            
        } catch (FileNotFoundException e) {
            System.out.println("Erreur: Fichier pas trouve");
        } catch (IOException e) {
            System.out.println("Probleme lecture fichier: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }

    private static void afficherReseaux() {
        if (reseaux != null && reseaux.getGraph() != null) {
            try {
                Viewer vw = reseaux.getGraph().display();
                vw.disableAutoLayout();
            } catch (Exception e) {
                System.out.println("Erreur affichage graphe: " + e.getMessage());
            }
        }
    }

    private static void afficherAide() {
        System.out.println("\n===== AIDE =====");
        System.out.println("Ce programme charge un reseau a partir dun fichier DGS");
        System.out.println("Tu peux:");
        System.out.println("- Voir le reseau");
        System.out.println("- Calculer chemin le plus court");
        System.out.println("- Generer table de routage");
        System.out.println("\nAppuyer sur entrer pour revenir...");
        scanner.nextLine();
    }

    private static void menuActions() {
        if (reseaux == null) {
            System.out.println("Erreur: Pas de reseau charge");
            return;
        }

        boolean retourMenuPrincipal = false;
        
        while (!retourMenuPrincipal) {
            System.out.println("\n===== ACTIONS =====");
            System.out.println("1. Trouver chemin plus court");
            System.out.println("2. Generer table routage");
            System.out.println("0. Retour menu");
            System.out.print("Ton choix: ");
            
            try {
                int choix = scanner.nextInt();
                scanner.nextLine();
                
                switch (choix) {
                    case 0:
                        retourMenuPrincipal = true;
                        break;
                    case 1:
                        trouverCheminPlusCourt();
                        break;
                    case 2:
                        tracerTableauRoutage();
                        break;
                    default:
                        System.out.println("Option invalide, reessayer");
                }
            } catch (InputMismatchException e) {
                System.out.println("Erreur: mettre un nombre");
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Erreur: " + e.getMessage());
            }
        }
    }

    private static void trouverCheminPlusCourt() {
        if (reseaux == null || reseaux.getOrdinateurs().isEmpty()) {
            System.out.println("Pas dordinateur dispo");
            return;
        }

        List<Ordinateur> ordinateurs = reseaux.getOrdinateurs();
        
        System.out.println("\nOrdinateurs dispo:");
        for (Ordinateur ord : ordinateurs) {
            System.out.println("- " + ord.getId());
        }
        
        System.out.print("\nMachine source (ou entrerr pour revenir): ");
        String sourceName = scanner.nextLine();
        
        if (sourceName.isEmpty()) {
            return;
        }
    
        Ordinateur source = trouveOrdinateurParNom(sourceName);
        if (source == null) {
            System.out.println("Machine source " + sourceName + " introuvable");
            return;
        }

        System.out.print("Machine destination (ou entrer pour revenir): ");
        String destinationName = scanner.nextLine();
        
        if (destinationName.isEmpty()) {
            return;
        }
        
        if (sourceName.equalsIgnoreCase(destinationName)) {
            System.out.println("Les machine doivent etre different");
            return;
        }
        
        Ordinateur destination = trouveOrdinateurParNom(destinationName);
        if (destination == null) {
            System.out.println("Machine destination " + destinationName + " introuvable");
            return;
        }
        
        try {
            String cheminLePlusCourt = reseaux.trouverCheminPlusCourt(sourceName, destinationName);
            System.out.println("\nChemin plus court de " + sourceName + " vers " + destinationName + " : " + cheminLePlusCourt);
        } catch (Exception e) {
            System.out.println("Erreur calcul du chemin: " + e.getMessage());
        }
        
        System.out.println("\nAppuyer sur entrerpour continuer...");
        scanner.nextLine();
    }

    private static void tracerTableauRoutage() {
        try {
            System.out.println("\nTable de routage du reseau:");
            reseaux.genererTableDeRoutage();
        } catch (Exception e) {
            System.out.println("Erreur generation table: " + e.getMessage());
        }
        
        System.out.println("\nAppuyer sur entrerpour continuer...");
        scanner.nextLine();
    }

    private static Ordinateur trouveOrdinateurParNom(String nom) {
        if (reseaux == null) return null;
        
        for (Ordinateur ord : reseaux.getOrdinateurs()) {
            if (ord.getId().equalsIgnoreCase(nom)) {
                return ord;
            }
        }
        return null;
    }
}