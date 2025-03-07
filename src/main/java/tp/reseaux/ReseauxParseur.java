/**
 * TP4 - Simulation de routage dans un reseau informatique
 * Cette interface ReseauxParseur definit le contrat pour les parseurs de fichiers
 * Elle permet de standardiser la methode d'importation des differents formats
 * de fichiers contenant la description d'un reseau informatique
 * 
 * @author HAMADOU BA
 * @github https://github.com/Hama2017
 * @git https://www-apps.univ-lehavre.fr/forge/bh243413/reseaux-tp-4-routage.git
 */

package tp.reseaux;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface ReseauxParseur {

    public Reseaux importDGS() throws FileNotFoundException, IOException;
}
