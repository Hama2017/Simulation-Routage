/**
 * TP4 - Simulation de routage dans un reseau informatique
 * Cette classe Ordinateur represente un noeud (ordinateur) dans le reseau
 * Elle contient un identifiant unique et des coordonnees optionnelles
 * Les coordonnees sont utilisees pour positionner les noeuds dans la visualisation graphique
 * 
 * @author HAMADOU BA
 * @github https://github.com/Hama2017
 * @git https://www-apps.univ-lehavre.fr/forge/bh243413/reseaux-tp-4-routage.git
 */

package tp.reseaux;

import java.awt.Point;
import java.util.Objects;
import java.util.Optional;

public class Ordinateur {
	
	protected String id;
    protected Optional<Point> position;


	public Ordinateur(String id, Point position) {
		this.id = id;
		this.position = Optional.ofNullable(position);
	}
	
	public Ordinateur(String id) {
		this.id = id;
		this.position = Optional.empty();
	}


	public String getId() {
		return id;
	}


	public Optional<Point> getPosition() {
		return position;
	}

	public void setPosition(Optional<Point> position) {
		this.position = position;
	}

	@Override
	public String toString() {
		  return "Ordinateur [id=" + id + ", position=" + 
	               position.map(p -> p.x + "," + p.y).orElse("null") + "]";
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ordinateur other = (Ordinateur) obj;
		return Objects.equals(id, other.id);
	}


}
