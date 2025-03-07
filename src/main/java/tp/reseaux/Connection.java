/**
 * TP4 - Simulation de routage dans un reseau informatique
 * Cette classe Connection represente un lien entre deux ordinateurs du reseau
 * Elle contient l'identifiant de la connexion, les deux ordinateurs connectes et le cout
 * Le cout represente le poids de l'arete dans le graphe pour le calcul des chemins
 * 
 * @author HAMADOU BA
 * @github https://github.com/Hama2017
 * @git https://www-apps.univ-lehavre.fr/forge/bh243413/reseaux-tp-4-routage.git
 */

package tp.reseaux;

public class Connection {
	
	protected String id;
	protected Ordinateur ord1;
	protected Ordinateur ord2;
	protected double cout;
	
	public Connection(String id, Ordinateur ord1, Ordinateur ord2, int cout) {
		if(ord1 == null || ord2==null) {
			throw new ConnectionException("Les deux ordinateurs doivent etre fournis"); 
		}
		if(ord1.getId().equals(ord2.getId())) {
			throw new ConnectionException("Les ordinateurs ne peuvent pas etre les memes"); 
		}
		this.id = id;
		this.ord1 = ord1;
		this.ord2 = ord2;
		this.cout = cout;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public Ordinateur getOrd1() {
		return ord1;
	}
	
	public void setOrd1(Ordinateur ord1) {
		this.ord1 = ord1;
	}
	
	public Ordinateur getOrd2() {
		return ord2;
	}
	
	public void setOrd2(Ordinateur ord2) {
		this.ord2 = ord2;
	}
	
	public double getCout() {
		return cout;
	}
	
	public void setCout(int cout) {
		this.cout = cout;
	}

	@Override
	public String toString() {
		return "Connection [id=" + id + ", ord1=" + ord1 + ", ord2=" + ord2 + ", cout=" + cout + "]";
	}
	
	
	

}
