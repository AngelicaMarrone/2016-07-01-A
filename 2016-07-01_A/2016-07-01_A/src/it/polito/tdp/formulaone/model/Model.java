package it.polito.tdp.formulaone.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.formulaone.db.FormulaOneDAO;

public class Model {
	
private FormulaOneDAO dao;

	

	//scelta valore mappa

	private Map<Integer,Driver> idMap;

	

	//scelta tipo valori lista

	private List<Driver> vertex;

	

	//scelta tra uno dei due edges

	private List<Adiacenza> edges;

	

	//scelta tipo vertici e tipo archi

	private SimpleDirectedWeightedGraph<Driver, DefaultWeightedEdge> graph;

	
	

	

	public Model() {

		

		//inserire tipo dao

		dao  = new FormulaOneDAO();

		//inserire tipo values

		idMap = new HashMap<Integer,Driver>();

	}
	
	
	
	
	public List<Integer> getYears() {
		List<Integer> anni= new ArrayList<Integer>(dao.getAllYearsOfRace());
		return anni;
	}




	public String creaGrafo(Integer anno) {
		
		graph= new SimpleDirectedWeightedGraph<Driver, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		vertex= new ArrayList<Driver>(dao.listDrivers(anno,idMap));
		
		Graphs.addAllVertices(this.graph,vertex);

		

		edges = new ArrayList<Adiacenza>(dao.getEdges(anno,idMap));

		

		for(Adiacenza a : edges) {


			Driver source = idMap.get(a.getId1());

			Driver target = idMap.get(a.getId2());

			double peso = a.getPeso();

			Graphs.addEdge(graph,source,target,peso);

			System.out.println("AGGIUNTO ARCO TRA: "+source.toString()+" e "+target.toString());

			

		}

		

		System.out.println("#vertici: "+ graph.vertexSet().size());

		System.out.println("#archi: "+ graph.edgeSet().size());
		
		String ris= trovaMigliore();
	
		return ris;
		
	}




	private String trovaMigliore() {
		
		String ris= "Il miglior pilota e' ";
		int peso=0;
		Driver best= null;
		
		
		for (Driver d: this.graph.vertexSet())
		{
			
			int temp= calcolaPeso(d);
			
			if (temp>peso)
			{
				peso=temp;
				best=d;
			}
		}
		
		
		ris+= best.getForename() + " " + best.getSurname()+ " "+ peso;
		
		
		return ris;
	}




	private int calcolaPeso(Driver d) {
		
		Set<DefaultWeightedEdge> incoming= this.graph.incomingEdgesOf(d);
		Set<DefaultWeightedEdge> outgoing= this.graph.outgoingEdgesOf(d);
		
		int inc=0;
		int out=0;
		
		for (DefaultWeightedEdge e: incoming) {
			
			inc+= this.graph.getEdgeWeight(e);
			
		}
		
		for (DefaultWeightedEdge e: outgoing) {
			
			out+= this.graph.getEdgeWeight(e);
			
		}
		
		int peso= out-inc;
		
		return peso;
		
	}


}
