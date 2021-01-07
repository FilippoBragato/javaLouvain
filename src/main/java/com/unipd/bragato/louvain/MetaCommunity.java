package com.unipd.bragato.louvain;
/*  All'interno dell'algoritmo di Louvian utilizziamo un grafo i cui nodi rappresentano le community.
    questa classe rappresenterà i nodi di questo grafo.
    All'interno delle meta-community vengono salvati anche i nodi di cui questa community è composta.
*/

import java.util.ArrayList;

public class MetaCommunity {
    // Campi
    private int community;
    private ArrayList<Entity> nodes;
    private double pesoSuiNodi;

    //Costruttori
    public MetaCommunity() {
    }

    public MetaCommunity(int community) {
        this.community = community;
    }

    public MetaCommunity(int community, ArrayList<Entity> nodes) {
        this.community = community;
        this.nodes = nodes;
        for (Entity entity : nodes) {
            entity.setComm(community);
        }
    }

    public MetaCommunity(int community, Entity node) {
        this.community = community;
        this.nodes = new ArrayList<Entity>(0);
        this.nodes.add(node);
        node.setComm(community);
    }

    //get e set
    public int getComm() {
        return community;
    }

    public void setComm(int comm){
        this.community = comm; 
        for (Entity entity : this.nodes) {
            entity.setComm(comm);
        }
    }

    public ArrayList<Entity> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<Entity> nodes){
        this.nodes = nodes; 
    }

    public int numberOfNodes(){
        return this.nodes.size();
    }
    public double getPesoSuiNodi() {
        return pesoSuiNodi;
    }
    public void setPesoSuiNodi(double pesoSuiNodi) {
        this.pesoSuiNodi = pesoSuiNodi;
    }

    //Metodi per aggiungere nodi
    public void addNode(Entity node) {
        this.nodes.add(node);
        node.setComm(this.community);
    }

    public void addNode(ArrayList<Entity> node) {
        this.nodes.addAll(node);
        for (Entity entity : node) {
            entity.setComm(this.community);
        }
    }

    //unire due meta-community, l'istanza su cui viene invocato sarà quella di cui si terrà la community 
    public void merge(MetaCommunity toBeDepleted){
        this.addNode(toBeDepleted.nodes);
        toBeDepleted.nodes.clear();
    }
}
