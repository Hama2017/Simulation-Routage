/**
 * TP4 - Simulation de routage dans un reseau informatique
 * Cette classe Reseaux represente le coeur du programme de simulation
 * Elle gere la structure du graphe, les ordinateurs et les connexions
 * Permet de visualiser le reseau, calculer les chemins les plus courts
 * et generer les tables de routage pour tous les ordinateurs
 * 
 * @author HAMADOU BA
 * @github https://github.com/Hama2017
 * @git https://www-apps.univ-lehavre.fr/forge/bh243413/reseaux-tp-4-routage.git
 */

package tp.reseaux;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.SingleGraph;

public class Reseaux {

	protected Graph graph;
	protected String label;
	protected List<Ordinateur> ordinateurs;
	protected List<Connection> connections;
	
	public Reseaux(String label, List<Ordinateur> ordinateurs, List<Connection> connections ) {
		
		this.label = label;
		this.ordinateurs = ordinateurs;
		this.connections = connections;
		System.setProperty("org.graphstream.ui", "swing"); 
		initGraph();
	}
	

	private void initGraph() {
		graph = new SingleGraph(label);
		initOrdinateurs();
		initConnections();
		
	}

	private void initOrdinateurs() {
		
		URL imageUrlIconOrdinateur = getClass().getClassLoader().getResource("ordinateurIcon.png");
	
		String imagePathIconOrdinateur = imageUrlIconOrdinateur.toString();
	
		Map<String,Object> mapAttributes = new HashMap<>();


		for(Ordinateur ord : ordinateurs) {
			
			mapAttributes.put("label", ord.getId());
			mapAttributes.put("ui.style",
					 "fill-color: blue; "
					+ "size: 50px, 50px;"
					+ "text-style:bold;"
					+ "text-offset:13px,-13px;"
					+ "text-color: white; "
					+ "text-background-mode: rounded-box;"
					+ "text-background-color: #222C;"
					+ "text-size:20px;"
					+ "text-padding:5px;"
					+ "fill-mode: image-scaled; fill-image: url('"+imagePathIconOrdinateur+"'); "
					+ "shape: box; "
					+ "stroke-mode: none;");
			

	
			if(ord.getPosition().isPresent()) {
				mapAttributes.put("x", ord.getPosition().get().getX());
				mapAttributes.put("y", ord.getPosition().get().getY());

			}
			
			graph.addNode(ord.getId())
				 .setAttributes(mapAttributes);
				 
		}
		
		mapAttributes.clear();
				
	}
	
	
	private void initConnections() {
		
		Map<String,Object> mapAttributes = new HashMap<>();
		

		for(Connection co : connections) {
			
			mapAttributes.put("label", co.getCout());
			mapAttributes.put("weight",co.getCout());
			
			mapAttributes.put("ui.style",
					""
					+ "text-style:bold;"
					+ "text-offset:13px,-13px;"
					+ "text-color: white; "
					+ "text-background-mode: rounded-box;"
					+ "text-background-color: #222C;"
					+ "text-size:20px;"
					+ "text-padding:5px;"
					);
			
			graph.addEdge(co.getId(), co.getOrd1().getId(), co.getOrd2().getId())
				 .setAttributes(mapAttributes);
			
				 
			
		}
		
		mapAttributes.clear();
				
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<Ordinateur> getOrdinateurs() {
		return ordinateurs;
	}

	public void setOrdinateurs(List<Ordinateur> ordinateurs) {
		this.ordinateurs = ordinateurs;
	}

	public List<Connection> getConnections() {
		return connections;
	}

	public void setConnections(List<Connection> connections) {
		this.connections = connections;
	}

	public String trouverCheminPlusCourt(String IdOrdinateurSource, String IdOrdinateurDestination) {
		
		for (Edge edge : graph.edges().toArray(Edge[]::new)) {
	            edge.setAttribute("ui.style", "fill-color: black;");
	        }
		
        Node ordinateurSource = graph.getNode(IdOrdinateurSource);
        Node ordinateurDestination  = graph.getNode(IdOrdinateurDestination);

		Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "weight");
	    dijkstra.init(graph);
	    dijkstra.setSource(ordinateurSource);
	    dijkstra.compute();
	    
        Path cheminLePlusCourt = dijkstra.getPath(ordinateurDestination);
    
         
        for (Edge edge : cheminLePlusCourt.getEdgeSet()) {
            edge.setAttribute("ui.style", "fill-color: green;");
        }

        return cheminLePlusCourt.toString();
		
	}
	
	
	
	
	public void genererTableDeRoutage() {
	    
	    for (Ordinateur ordiSite : ordinateurs) {
	        Node ordiSiteNoeud = graph.getNode(ordiSite.getId());
	        Node[] noeudsVoisin = ordiSiteNoeud.neighborNodes().toArray(Node[]::new);
	        
	        StringJoiner stringNoeudVoisin = new StringJoiner(",", "(", ")");
	        for (Node n : noeudsVoisin) {
	            stringNoeudVoisin.add(n.getId());
	        }
	        System.out.println("SITE " + ordiSite.getId() + " : " + stringNoeudVoisin);
	        
	        for (Ordinateur ordiDestination : ordinateurs) {
	            if (!ordiDestination.equals(ordiSite)) {
	                Node ordiDestinationNoeud = graph.getNode(ordiDestination.getId());
	                String meilleurVoisin = trouverCheminRoutage(ordiSiteNoeud, ordiDestinationNoeud, noeudsVoisin);
	                System.out.println(ordiDestination.getId() + " : " + meilleurVoisin);
	            }
	        }
	        System.out.println(); 
	    }
	}

	public String trouverCheminRoutage(Node ordinateurSource, Node ordiDestinationNoeud, Node[] noeudsVoisin) {

		Map<Node, Double> coutParVoisin = new HashMap<>();
	    
	    List<List<Node>> tousLesChemins = trouverTousLesCheminsEntreDeuxNoeuds(ordinateurSource, ordiDestinationNoeud);
	    
	    for (Node voisin : noeudsVoisin) {
	        if (voisin.equals(ordiDestinationNoeud)) {
	            Edge edge = ordinateurSource.getEdgeBetween(voisin);
	            double poids = edge != null ? edge.getNumber("weight") : 0.0;
	            coutParVoisin.put(voisin, poids);
	            continue;
	        }
	        
	        double coutMinimal = Double.MAX_VALUE;
	        List<List<Node>> tousLesChemins1 = trouverTousLesCheminsEntreDeuxNoeuds(ordinateurSource, ordiDestinationNoeud);
	        
	        for (Node voisin1 : noeudsVoisin) {
	        	
	            double coutMinimal1 = Double.MAX_VALUE;
	            
	            for (List<Node> chemin : tousLesChemins1) {
	                if (chemin.size() > 1 && chemin.get(1).equals(voisin1)) {
	                    try {
	                    	
	                        Path path = convertirListeNoeudsEnPath(chemin);
	                        double cout = path.getPathWeight("weight");
	                        
	                        if (cout < coutMinimal1) {
	                            coutMinimal1 = cout;
	                            
	                        }
	                    } catch (IllegalArgumentException e) {
	                        continue;
	                    }
	                }
	            }
	            
	            coutParVoisin.put(voisin1, coutMinimal1);
	        }
	    }
	    
	    List<Node> voisinsTriesParCout = Arrays.stream(noeudsVoisin)
	        .sorted((v1, v2) -> {
	        	
	            double cout1 = coutParVoisin.getOrDefault(v1, Double.MAX_VALUE);
	            double cout2 = coutParVoisin.getOrDefault(v2, Double.MAX_VALUE);
	            
	            if (Math.abs(cout1 - cout2) < 0.0001) {
	                return v1.getId().compareTo(v2.getId());
	            }
	            
	            return Double.compare(cout1, cout2);
	        })
	        .collect(Collectors.toList());
	    
	    StringBuilder result = new StringBuilder();
	    for (Node voisin : voisinsTriesParCout) {
	        if (result.length() > 0) {
	            result.append(" ");
	        }
	        result.append(voisin.getId());
	    }
	    
	    return result.toString();
	}

	private List<List<Node>> trouverTousLesCheminsEntreDeuxNoeuds(Node depart, Node arrivee) {
	    List<List<Node>> tousLesChemins = new ArrayList<>();
	    List<Node> cheminActuel = new ArrayList<>();
	    Set<Node> visites = new HashSet<>();
	    
	    trouverTousLesCheminsRec(depart, arrivee, visites, cheminActuel, tousLesChemins);
	    return tousLesChemins;
	}

	private void trouverTousLesCheminsRec(Node actuel, Node arrivee, Set<Node> visites, List<Node> cheminActuel, List<List<Node>> tousLesChemins) {
	    visites.add(actuel);
	    cheminActuel.add(actuel);

	    if (actuel.equals(arrivee)) {
	        tousLesChemins.add(new ArrayList<>(cheminActuel));
	    } else {
	        for (Edge arete : actuel) {
	            Node prochainNoeud = arete.getOpposite(actuel);
	            if (!visites.contains(prochainNoeud)) {
	                trouverTousLesCheminsRec(prochainNoeud, arrivee, visites, cheminActuel, tousLesChemins);
	            }
	        }
	    }
	    
	    cheminActuel.remove(cheminActuel.size() - 1);
	    visites.remove(actuel);
	}

	private Path convertirListeNoeudsEnPath(List<Node> noeuds) {
	    if (noeuds == null || noeuds.isEmpty()) {
	        throw new IllegalArgumentException("La liste de nœuds ne peut pas etre vide");
	    }
	    
	    Path path = new Path();
	    
	    path.setRoot(noeuds.get(0));
	    
	    for (int i = 0; i < noeuds.size() - 1; i++) {
	        Node noeudCourant = noeuds.get(i);
	        Node noeudSuivant = noeuds.get(i + 1);
	        
	        Edge edge = noeudCourant.getEdgeBetween(noeudSuivant);
	        
	        if (edge != null) {
	            path.add(edge);
	        } else {
	            throw new IllegalArgumentException(
	                "Aucune arete trouver entre " + noeudCourant.getId() + 
	                " et " + noeudSuivant.getId()
	            );
	        }
	    }
	    
	    return path;
	}
	
	
	
	
	
	
		
		

	
	

}
