package tp.reseaux;

import tp.reseaux.Reseau;
import java.io.IOException;

public interface ChargeurTopologie {
    /**
     * Charge une topologie réseau depuis une source
     * @param reseau Le réseau à remplir
     * @param source La source de la topologie (chemin de fichier ou autre)
     * @throws IOException En cas d'erreur de lecture
     */
    void chargerTopologie(Reseau reseau, String source) throws IOException;
    
    /**
     * Vérifie si ce chargeur peut traiter le format spécifié
     * @param source La source à vérifier
     * @return true si ce chargeur peut traiter cette source
     */
    boolean peutCharger(String source);
}