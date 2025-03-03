package tp.reseaux;

public class Lien {
 private Interface interface1;
 private Interface interface2;
 private int poids;
 
 public Lien(Interface interface1, Interface interface2, int poids) {
     this.interface1 = interface1;
     this.interface2 = interface2;
     this.poids = poids;
     
     interface1.connecter(interface2.getNoeud());
     interface2.connecter(interface1.getNoeud());
 }
 
 public Interface getInterface1() {
     return interface1;
 }
 
 public Interface getInterface2() {
     return interface2;
 }
 
 public int getPoids() {
     return poids;
 }
 
 public Noeud getNoeud1() {
     return interface1.getNoeud();
 }
 
 public Noeud getNoeud2() {
     return interface2.getNoeud();
 }
 
 @Override
 public String toString() {
     return interface1.getNoeud().getId() + ":" + interface1.getNom() + 
            " <--> " + 
            interface2.getNoeud().getId() + ":" + interface2.getNom() +
            " (poids: " + poids + ")";
 }
}