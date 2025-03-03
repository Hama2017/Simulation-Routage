package tp.reseaux;

public class Interface {
 private String nom;
 private Noeud noeud;
 private Noeud noeudConnecte;
 
 public Interface(String nom, Noeud noeud) {
     this.nom = nom;
     this.noeud = noeud;
 }
 
 public String getNom() {
     return nom;
 }
 
 public Noeud getNoeud() {
     return noeud;
 }
 
 public Noeud getNoeudConnecte() {
     return noeudConnecte;
 }
 
 public void connecter(Noeud noeudConnecte) {
     this.noeudConnecte = noeudConnecte;
 }
 
 @Override
 public String toString() {
     return nom;
 }
}