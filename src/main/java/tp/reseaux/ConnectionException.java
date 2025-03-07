/**
 * TP4 - Simulation de routage dans un reseau informatique
 * Cette classe ConnectionException gere les exceptions liees aux connexions
 * Elle etend RuntimeException pour permettre la gestion des erreurs de connexion
 * comme des connexions entre deux memes ordinateurs ou des ordinateurs manquants
 * 
 * @author HAMADOU BA
 * @github https://github.com/Hama2017
 * @git https://www-apps.univ-lehavre.fr/forge/bh243413/reseaux-tp-4-routage.git
 */

package tp.reseaux;

public class ConnectionException extends RuntimeException {
	
	 public ConnectionException(String message) {
	        super(message);
	    }
}
