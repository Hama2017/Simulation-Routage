package tp.reseaux;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GestionnaireChargeurs {
    private List<ChargeurTopologie> chargeurs = new ArrayList<>();
    
    public GestionnaireChargeurs() {
        enregistrerChargeur(new ChargeurFormatTexte());
        enregistrerChargeur(new ChargeurFormatDGS());
    }
    
    public void enregistrerChargeur(ChargeurTopologie chargeur) {
        chargeurs.add(chargeur);
    }
    
    public boolean chargerTopologie(Reseau reseau, String source) {
        for (ChargeurTopologie chargeur : chargeurs) {
            if (chargeur.peutCharger(source)) {
                try {
                    chargeur.chargerTopologie(reseau, source);
                    return true;
                } catch (IOException e) {
                    System.err.println("Erreur lors du chargement avec " + 
                                      chargeur.getClass().getSimpleName() + ": " + e.getMessage());
                }
            }
        }
        
        return false;
    }
}