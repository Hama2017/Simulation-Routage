/**
 * TP4 - Simulation de routage dans un reseau informatique
 * Cette classe FormatFichierException gere les exceptions liees au format des fichiers
 * Elle etend IOException pour signaler les problemes lors de la lecture et du parsing
 * des fichiers DGS, comme des lignes mal formatees ou des valeurs manquantes
 * 
 * @author HAMADOU BA
 * @github https://github.com/Hama2017
 * @git https://www-apps.univ-lehavre.fr/forge/bh243413/reseaux-tp-4-routage.git
 */

package tp.reseaux;

import java.io.IOException;

public class FormatFichierException extends IOException {
    public FormatFichierException(String message) {
        super(message);
    }
}
