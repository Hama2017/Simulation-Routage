package tp.reseaux;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
 public static void main(String[] args) {
     Scanner scanner = new Scanner(System.in);
     Reseau reseau = new Reseau();
     
     System.out.println("=== TP Réseau - Routage ===");
     System.out.println("Comment souhaitez-vous saisir la topologie ?");
     System.out.println("1. Charger depuis un fichier");
     System.out.println("2. Utiliser l'exemple prédéfini");
     
     int choix = scanner.nextInt();
     
     switch (choix) {
         case 1:
             chargerDepuisFichier(reseau, scanner);
             break;
         case 2:
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
                 System.out.println("Au revoir !");
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
         
         // chemin complet par exemple /Users/hamaba/Documents/LICENCE-3-SEMESTRE-2/RESEAUX/TP/reseaux-tp-4-routage/tp4/src/main/resources/topo.txt

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
 
 private static void chargerExemple(Reseau reseau) {
     System.out.println("Chargement de l'exemple...");
     
     Machine machineA = reseau.ajouterMachine("PC-A");
     Machine machineB = reseau.ajouterMachine("PC-B");
     Machine machineC = reseau.ajouterMachine("PC-C");
     Machine machineD = reseau.ajouterMachine("PC-D");
     
     Commutateur r1 = reseau.ajouterCommutateur("R1");
     Commutateur r2 = reseau.ajouterCommutateur("R2");
     Commutateur r3 = reseau.ajouterCommutateur("R3");
     Commutateur r4 = reseau.ajouterCommutateur("R4");
     
     reseau.ajouterLien(machineA, "eth0", r1, "eth0", 1);
     reseau.ajouterLien(machineB, "eth0", r2, "eth0", 1);
     reseau.ajouterLien(machineC, "eth0", r3, "eth0", 1);
     reseau.ajouterLien(machineD, "eth0", r4, "eth0", 1);
     
     reseau.ajouterLien(r1, "eth1", r2, "eth1", 10);
     reseau.ajouterLien(r1, "eth2", r3, "eth1", 5);
     reseau.ajouterLien(r2, "eth2", r4, "eth1", 1);
     reseau.ajouterLien(r3, "eth2", r4, "eth2", 2);
     
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