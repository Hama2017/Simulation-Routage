package tp.reseaux;

import java.util.*;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.SingleGraph;

public class Reseau {
    private List<Noeud> noeuds;
    private List<Lien> liens;
    private Graph graphStream; 
    
    public Reseau() {
        this.noeuds = new ArrayList<>();
        this.liens = new ArrayList<>();
        this.graphStream = new SingleGraph("Reseau");
    }
    
    public List<Noeud> getNoeuds() {
        return noeuds;
    }
    
    public List<Lien> getLiens() {
        return liens;
    }
    
    public void ajouterNoeud(Noeud noeud) {
        noeuds.add(noeud);
        if (graphStream.getNode(noeud.getId()) == null) {
            graphStream.addNode(noeud.getId());
        }
    }
    
    public Machine ajouterMachine(String id) {
        Machine machine = new Machine(id);
        noeuds.add(machine);
        
        if (graphStream.getNode(id) == null) {
            Node gsNode = graphStream.addNode(id);
            gsNode.setAttribute("type", "machine");
        }
        
        return machine;
    }
    
    public Commutateur ajouterCommutateur(String id) {
        Commutateur commutateur = new Commutateur(id);
        noeuds.add(commutateur);
        
        if (graphStream.getNode(id) == null) {
            Node gsNode = graphStream.addNode(id);
            gsNode.setAttribute("type", "commutateur");
        }
        
        return commutateur;
    }
    
    public Lien ajouterLien(Noeud noeud1, String interfaceNom1, 
                       Noeud noeud2, String interfaceNom2, int poids) {
        Interface interface1 = noeud1.getInterfaceByName(interfaceNom1);
        if (interface1 == null) {
            interface1 = noeud1.ajouterInterface(interfaceNom1);
        }
        
        Interface interface2 = noeud2.getInterfaceByName(interfaceNom2);
        if (interface2 == null) {
            interface2 = noeud2.ajouterInterface(interfaceNom2);
        }
        
        Lien lien = new Lien(interface1, interface2, poids);
        liens.add(lien);
        
        String edgeId = noeud1.getId() + "_" + noeud2.getId();
        if (graphStream.getEdge(edgeId) == null) {
            org.graphstream.graph.Edge gsEdge = graphStream.addEdge(edgeId, noeud1.getId(), noeud2.getId(), false); // non dirigé
            gsEdge.setAttribute("length", poids); // "length" est l'attribut standard pour Dijkstra
            gsEdge.setAttribute("interface1", interfaceNom1);
            gsEdge.setAttribute("interface2", interfaceNom2);
        }
        
        return lien;
    }
    
    public Noeud trouverNoeud(String id) {
        for (Noeud noeud : noeuds) {
            if (noeud.getId().equals(id)) {
                return noeud;
            }
        }
        return null;
    }
    
    public Lien trouverLien(Noeud noeud1, Noeud noeud2) {
        for (Lien lien : liens) {
            if ((lien.getNoeud1().equals(noeud1) && lien.getNoeud2().equals(noeud2)) ||
                (lien.getNoeud1().equals(noeud2) && lien.getNoeud2().equals(noeud1))) {
                return lien;
            }
        }
        return null;
    }
    
    public Interface trouverInterfaceVers(Noeud source, Noeud destination) {
        Lien lien = trouverLien(source, destination);
        if (lien != null) {
            if (lien.getNoeud1().equals(source)) {
                return lien.getInterface1();
            } else {
                return lien.getInterface2();
            }
        }
        return null;
    }
    
    public List<Noeud> trouverVoisins(Noeud noeud) {
        List<Noeud> voisins = new ArrayList<>();
        for (Interface interf : noeud.getInterfaces()) {
            if (interf.getNoeudConnecte() != null) {
                voisins.add(interf.getNoeudConnecte());
            }
        }
        return voisins;
    }
    
    public List<Noeud> trouverCheminPlusCourt(Noeud source, Noeud destination) {
        Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "length");
        dijkstra.init(graphStream);
        dijkstra.setSource(graphStream.getNode(source.getId()));
        dijkstra.compute();
        
        Node destNode = graphStream.getNode(destination.getId());
        if (dijkstra.getPathLength(destNode) == Double.POSITIVE_INFINITY) {
            return new ArrayList<>();
        }
        
        // Convertir le chemin GraphStream en notre format
        List<Noeud> chemin = new ArrayList<>();
        Path path = dijkstra.getPath(destNode);
        
        for (Node node : path.getNodePath()) {
            chemin.add(trouverNoeud(node.getId()));
        }
        
        return chemin;
    }
    
    public void genererTablesRoutage() {
        for (Noeud noeud : noeuds) {
            if (noeud instanceof Commutateur) {
                Commutateur commutateur = (Commutateur) noeud;
                
                // Initialiser Dijkstra pour ce commutateur
                Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "length");
                dijkstra.init(graphStream);
                dijkstra.setSource(graphStream.getNode(noeud.getId()));
                dijkstra.compute();
                
                // Generer les entres de table de routage
                for (Noeud destination : noeuds) {
                    if (!destination.equals(noeud)) {
                        Node destNode = graphStream.getNode(destination.getId());
                        if (dijkstra.getPathLength(destNode) != Double.POSITIVE_INFINITY) {
                            // Recuperer le chemin complet
                            Path path = dijkstra.getPath(destNode);
                            List<Node> nodePath = path.getNodePath();
                            
                            // S'il y a au moins un saut
                            if (nodePath.size() > 1) {
                                // Le prochain saut est le deuxieme nœud du chemin
                                Node nextHopNode = nodePath.get(1);
                                Noeud nextHop = trouverNoeud(nextHopNode.getId());
                                Interface interfaceSortie = trouverInterfaceVers(noeud, nextHop);
                                
                                if (interfaceSortie != null) {
                                    commutateur.ajouterEntreeTableRoutage(
                                        destination.getId(), interfaceSortie.getNom());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void afficherTablesRoutage() {
        for (Noeud noeud : noeuds) {
            if (noeud instanceof Commutateur) {
                ((Commutateur) noeud).afficherTableRoutage();
            }
        }
    }
    
    public List<String> genererCircuitVirtuel(Noeud source, Noeud destination) {
        List<Noeud> chemin = trouverCheminPlusCourt(source, destination);
        List<String> circuit = new ArrayList<>();
        
        if (chemin.size() <= 1) {
            return circuit;
        }
        
        for (int i = 0; i < chemin.size() - 1; i++) {
            Noeud courant = chemin.get(i);
            Noeud suivant = chemin.get(i + 1);
            Interface interfaceSortie = trouverInterfaceVers(courant, suivant);
            
            String connexion = courant.getId() + ":" + interfaceSortie.getNom() + 
                             " -> " + suivant.getId();
            circuit.add(connexion);
        }
        
        return circuit;
    }
    
    public Graph getGraphStream() {
        return graphStream;
    }
}