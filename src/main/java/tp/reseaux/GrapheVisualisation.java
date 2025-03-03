package tp.reseaux;

import java.util.Iterator;
import java.util.List;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

public class GrapheVisualisation {
    private Reseau reseau;
    private Graph graph;
    private boolean interactif;
    
    public GrapheVisualisation(Reseau reseau, boolean interactif) {
        this.reseau = reseau;
        this.interactif = interactif;
        System.setProperty("org.graphstream.ui", "swing");
    }
    
    public void afficher() {
        graph = new SingleGraph("Réseau");
        graph.setAttribute("ui.stylesheet", 
            "node { size: 30px; text-size: 12; fill-color: #cccccc; } " +
            "node.commutateur { fill-color: #ff6666; } " +
            "node.machine { fill-color: #6666ff; } " +
            "edge { text-size: 12; }");
        
        for (Noeud noeud : reseau.getNoeuds()) {
            Node node = graph.addNode(noeud.getId());
            node.setAttribute("ui.label", noeud.getId());
            
            if (noeud instanceof Machine) {
                node.setAttribute("ui.class", "machine");
            } else if (noeud instanceof Commutateur) {
                node.setAttribute("ui.class", "commutateur");
            }
        }
        
        int edgeId = 0;
        for (Lien lien : reseau.getLiens()) {
            Noeud noeud1 = lien.getNoeud1();
            Noeud noeud2 = lien.getNoeud2();
            
            Edge edge = graph.addEdge("e" + edgeId++, 
                                    noeud1.getId(), 
                                    noeud2.getId());
            edge.setAttribute("ui.label", String.valueOf(lien.getPoids()));
        }
        
        Viewer viewer = graph.display();
        
        if (interactif) {
            ViewerPipe pipe = viewer.newViewerPipe();
            pipe.addViewerListener(new ViewerListener() {
                @Override
                public void viewClosed(String viewName) {
                    System.exit(0);
                }
                
                @Override
                public void buttonPushed(String id) {
                    System.out.println("Noeud sélectionné: " + id);
                }
                
                @Override
                public void buttonReleased(String id) {
                }

                @Override
                public void mouseOver(String id) {
                }

                @Override
                public void mouseLeft(String id) {
                }
            });
            
            while (true) {
                try {
                    pipe.pump();
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void afficherChemin(List<Noeud> chemin) {
        for (Node node : graph) {
            node.removeAttribute("ui.style");
        }
        
        Iterator<Edge> edgeIterator = graph.edges().iterator();
        while (edgeIterator.hasNext()) {
            Edge edge = edgeIterator.next();
            edge.removeAttribute("ui.style");
        }
        
        for (int i = 0; i < chemin.size(); i++) {
            Node node = graph.getNode(chemin.get(i).getId());
            node.setAttribute("ui.style", "fill-color: #66ff66;");
            
            if (i < chemin.size() - 1) {
                Noeud courant = chemin.get(i);
                Noeud suivant = chemin.get(i + 1);
                
                String edgeId1 = courant.getId() + suivant.getId();
                String edgeId2 = suivant.getId() + courant.getId();
                
                Edge edge = graph.getEdge(edgeId1);
                if (edge == null) {
                    edge = graph.getEdge(edgeId2);
                }
                
                if (edge == null) {
                    edgeIterator = graph.edges().iterator();
                    while (edgeIterator.hasNext()) {
                        Edge e = edgeIterator.next();
                        if ((e.getNode0().getId().equals(courant.getId()) && 
                             e.getNode1().getId().equals(suivant.getId())) ||
                            (e.getNode0().getId().equals(suivant.getId()) && 
                             e.getNode1().getId().equals(courant.getId()))) {
                            edge = e;
                            break;
                        }
                    }
                }
                
                if (edge != null) {
                    edge.setAttribute("ui.style", "fill-color: #66ff66; size: 2px;");
                }
            }
        }
    }
}