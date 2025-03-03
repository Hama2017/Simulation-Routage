package tp.reseaux;

public class Machine extends Noeud {
 private String adresseIP;
 
 public Machine(String id) {
     super(id);
     ajouterInterface("eth0");
 }
 
 public Machine(String id, String adresseIP) {
     this(id);
     this.adresseIP = adresseIP;
 }
 
 public String getAdresseIP() {
     return adresseIP;
 }
 
 public void setAdresseIP(String adresseIP) {
     this.adresseIP = adresseIP;
 }
}