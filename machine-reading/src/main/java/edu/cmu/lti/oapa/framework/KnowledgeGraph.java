package edu.cmu.lti.oapa.framework;

import java.util.HashMap;
import java.util.HashSet;

public class KnowledgeGraph {
	private String name;
	private HashSet<KnowledgeGraph> neighbors;
	private HashMap<KnowledgeGraph, String> relationships;
	
	public KnowledgeGraph(){
		this.name = "";
		neighbors = new HashSet<KnowledgeGraph>();
		relationships = new HashMap<KnowledgeGraph, String>();
	}
	
	public KnowledgeGraph(String name){
		this.name = name;
		neighbors = new HashSet<KnowledgeGraph>();
		relationships = new HashMap<KnowledgeGraph, String>();
	}
	
	
	
	public void addNeighbor(KnowledgeGraph n, String r) {
			neighbors.add(n);
			relationships.put(n, r);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashSet<KnowledgeGraph> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(HashSet<KnowledgeGraph> neighbors) {
		this.neighbors = neighbors;
	}

	public HashMap<KnowledgeGraph, String> getRelationships() {
		return relationships;
	}

	public void setRelationships(HashMap<KnowledgeGraph, String> relationships) {
		this.relationships = relationships;
	}
	
	
}
