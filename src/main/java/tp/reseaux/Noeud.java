package tp.reseaux;

import java.util.ArrayList;
import java.util.List;

public abstract class Noeud {
 private String id;
 private List<Interface> interfaces;
 
 public Noeud(String id) {
     this.id = id;
     this.interfaces = new ArrayList<>();
 }
 
 public String getId() {
     return id;
 }
 
 public List<Interface> getInterfaces() {
     return interfaces;
 }
 
 public Interface ajouterInterface(String nom) {
     Interface interf = new Interface(nom, this);
     interfaces.add(interf);
     return interf;
 }
 
 public Interface getInterfaceByName(String nom) {
     for (Interface interf : interfaces) {
         if (interf.getNom().equals(nom)) {
             return interf;
         }
     }
     return null;
 }
 
 @Override
 public boolean equals(Object o) {
     if (this == o) return true;
     if (o == null || getClass() != o.getClass()) return false;
     Noeud noeud = (Noeud) o;
     return id.equals(noeud.id);
 }
 
 @Override
 public int hashCode() {
     return id.hashCode();
 }
 
 @Override
 public String toString() {
     return id;
 }
}