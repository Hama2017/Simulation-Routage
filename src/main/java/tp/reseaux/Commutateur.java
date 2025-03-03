package tp.reseaux;
import java.util.HashMap;
import java.util.Map;

public class Commutateur extends Noeud {
 private Map<String, String> tableRoutage;
 
 public Commutateur(String id) {
     super(id);
     this.tableRoutage = new HashMap<>();
 }
 
 public Map<String, String> getTableRoutage() {
     return tableRoutage;
 }
 
 public void ajouterEntreeTableRoutage(String destination, String interfaceSortie) {
     tableRoutage.put(destination, interfaceSortie);
 }
 
 public String trouverInterfaceSortie(String destination) {
     return tableRoutage.get(destination);
 }
 
 public void afficherTableRoutage() {
     System.out.println("Table de routage pour " + getId() + ":");
     System.out.println("Destination\tInterface de sortie");
     System.out.println("---------------------------------");
     for (Map.Entry<String, String> entry : tableRoutage.entrySet()) {
         System.out.println(entry.getKey() + "\t\t" + entry.getValue());
     }
     System.out.println();
 }
}