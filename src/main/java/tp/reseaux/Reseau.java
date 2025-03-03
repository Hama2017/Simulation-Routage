package tp.reseaux;

import java.util.*;

public class Reseau {
 private List<Noeud> noeuds;
 private List<Lien> liens;
 
 public Reseau() {
     this.noeuds = new ArrayList<>();
     this.liens = new ArrayList<>();
 }
 
 public List<Noeud> getNoeuds() {
     return noeuds;
 }
 
 public List<Lien> getLiens() {
     return liens;
 }
 
 public void ajouterNoeud(Noeud noeud) {
     noeuds.add(noeud);
 }
 
 public Machine ajouterMachine(String id) {
     Machine machine = new Machine(id);
     noeuds.add(machine);
     return machine;
 }
 
 public Commutateur ajouterCommutateur(String id) {
     Commutateur commutateur = new Commutateur(id);
     noeuds.add(commutateur);
     return commutateur;
 }
 
 public Lien ajouterLien(Noeud noeud1, String interfaceNom1, 
                       Noeud noeud2, String interfaceNom2, int poids) {
     Interface interface1 = noeud1.getInterfaceByName(interfaceNom1);
     if (interface1 == null) {
         interface1 = noeud1.ajouterInterface(interfaceNom1);
     }
     
     Interface interface2 = noeud2.getInterfaceByName(interfaceNom2);
     if (interface2 == null) {
         interface2 = noeud2.ajouterInterface(interfaceNom2);
     }
     
     Lien lien = new Lien(interface1, interface2, poids);
     liens.add(lien);
     return lien;
 }
 
 public Noeud trouverNoeud(String id) {
     for (Noeud noeud : noeuds) {
         if (noeud.getId().equals(id)) {
             return noeud;
         }
     }
     return null;
 }
 
 public Lien trouverLien(Noeud noeud1, Noeud noeud2) {
     for (Lien lien : liens) {
         if ((lien.getNoeud1().equals(noeud1) && lien.getNoeud2().equals(noeud2)) ||
             (lien.getNoeud1().equals(noeud2) && lien.getNoeud2().equals(noeud1))) {
             return lien;
         }
     }
     return null;
 }
 
 public Interface trouverInterfaceVers(Noeud source, Noeud destination) {
     Lien lien = trouverLien(source, destination);
     if (lien != null) {
         if (lien.getNoeud1().equals(source)) {
             return lien.getInterface1();
         } else {
             return lien.getInterface2();
         }
     }
     return null;
 }
 
 public List<Noeud> trouverVoisins(Noeud noeud) {
     List<Noeud> voisins = new ArrayList<>();
     for (Interface interf : noeud.getInterfaces()) {
         if (interf.getNoeudConnecte() != null) {
             voisins.add(interf.getNoeudConnecte());
         }
     }
     return voisins;
 }
 
 public List<Noeud> trouverCheminPlusCourt(Noeud source, Noeud destination) {
     Map<Noeud, Integer> distances = new HashMap<>();
     Map<Noeud, Noeud> predecesseurs = new HashMap<>();
     Set<Noeud> nonVisites = new HashSet<>();
     
     for (Noeud noeud : noeuds) {
         distances.put(noeud, Integer.MAX_VALUE);
         nonVisites.add(noeud);
     }
     distances.put(source, 0);
     
     while (!nonVisites.isEmpty()) {
         Noeud courant = null;
         int distanceMin = Integer.MAX_VALUE;
         for (Noeud noeud : nonVisites) {
             int distance = distances.get(noeud);
             if (distance < distanceMin) {
                 distanceMin = distance;
                 courant = noeud;
             }
         }
         
         if (courant == null) {
             break; 
         }
         
         if (courant.equals(destination)) {
             break;
         }
         
         nonVisites.remove(courant);
         
         for (Noeud voisin : trouverVoisins(courant)) {
             if (nonVisites.contains(voisin)) {
                 Lien lien = trouverLien(courant, voisin);
                 int nouvelleDistance = distances.get(courant) + lien.getPoids();
                 if (nouvelleDistance < distances.get(voisin)) {
                     distances.put(voisin, nouvelleDistance);
                     predecesseurs.put(voisin, courant);
                 }
             }
         }
     }
     
     return reconstruireChemin(source, destination, predecesseurs);
 }
 
 private List<Noeud> reconstruireChemin(Noeud source, Noeud destination, 
                                      Map<Noeud, Noeud> predecesseurs) {
     LinkedList<Noeud> chemin = new LinkedList<>();
     Noeud courant = destination;
     
     if (!predecesseurs.containsKey(destination) && !destination.equals(source)) {
         return chemin; 
     }
     
     while (courant != null) {
         chemin.addFirst(courant);
         courant = predecesseurs.get(courant);
     }
     
     return chemin;
 }
 
 public void genererTablesRoutage() {
     for (Noeud noeud : noeuds) {
         if (noeud instanceof Commutateur) {
             Commutateur commutateur = (Commutateur) noeud;
             
             for (Noeud destination : noeuds) {
                 if (!destination.equals(noeud)) {
                     List<Noeud> chemin = trouverCheminPlusCourt(noeud, destination);
                     if (chemin.size() > 1) {
                         Noeud nextHop = chemin.get(1);
                         Interface interfaceSortie = trouverInterfaceVers(noeud, nextHop);
                         
                         commutateur.ajouterEntreeTableRoutage(
                             destination.getId(), interfaceSortie.getNom());
                     }
                 }
             }
         }
     }
 }
 
 public void afficherTablesRoutage() {
     for (Noeud noeud : noeuds) {
         if (noeud instanceof Commutateur) {
             ((Commutateur) noeud).afficherTableRoutage();
         }
     }
 }
 
 public List<String> genererCircuitVirtuel(Noeud source, Noeud destination) {
     List<Noeud> chemin = trouverCheminPlusCourt(source, destination);
     List<String> circuit = new ArrayList<>();
     
     if (chemin.size() <= 1) {
         return circuit;
     }
     
     for (int i = 0; i < chemin.size() - 1; i++) {
         Noeud courant = chemin.get(i);
         Noeud suivant = chemin.get(i + 1);
         Interface interfaceSortie = trouverInterfaceVers(courant, suivant);
         
         String connexion = courant.getId() + ":" + interfaceSortie.getNom() + 
                          " -> " + suivant.getId();
         circuit.add(connexion);
     }
     
     return circuit;
 }
}