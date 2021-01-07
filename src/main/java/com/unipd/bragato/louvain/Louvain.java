package com.unipd.bragato.louvain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.function.Function;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class Louvain implements Function<DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge>, DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge>> {

    @Override
    public DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> apply(DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> net){
        MetaCommunity [] mc = net.vertexSet().toArray(new MetaCommunity[0]);
        ArrayList<MetaCommunity> metaCom = new ArrayList<MetaCommunity>(0);
        for (int i = 0; i < mc.length; i++) {
            metaCom.add(mc[i]);
        }
        for (int i = 0; i < mc.length; i++) {
            double peso = 0;
            for (DefaultWeightedEdge archi : net.edgesOf(mc[i]).toArray(new DefaultWeightedEdge[0])) {
                peso += net.getEdgeWeight(archi);
            }
            mc[i].setPesoSuiNodi(peso);
        }
        boolean ending = false;
        double m = 0;
        for (DefaultWeightedEdge arc : net.edgeSet()) {
            m += net.getEdgeWeight(arc);
        }
        while (ending == false) {
            //Anzitutto resetto la condizione iniziale
            ending=true;
            Random rng = new Random();
            Collections.shuffle(metaCom, rng);
            //Per ogni metacommunity di metacom indicizzata con i_mc viene calcolata come cambia la modularità se il nodo viene attribuito alla community di un suo vicino
            for (int i_mc = 0; i_mc < metaCom.size(); i_mc++) {
                int deg =net.degreeOf(metaCom.get(i_mc));
                double selfLoopWeight = 0;
                DefaultWeightedEdge selfLoop = null;
                DefaultWeightedEdge[] archiDeiVicini = net.edgesOf(metaCom.get(i_mc)).toArray(new DefaultWeightedEdge[0]);

                //Tratto sempre il self loop a parte
                if(net.getEdge(metaCom.get(i_mc), metaCom.get(i_mc))!=null) {
                    deg = deg-2;
                    selfLoop = net.getEdge(metaCom.get(i_mc), metaCom.get(i_mc));
                    selfLoopWeight = net.getEdgeWeight(selfLoop);
                    DefaultWeightedEdge[] adv = new DefaultWeightedEdge[archiDeiVicini.length - 1];
                    int ret = 0;
                    for(int i=0; i<archiDeiVicini.length; i++){
                        if(archiDeiVicini[i] == selfLoop) {
                            ret++;
                        }
                        else{
                            adv[i-ret]=archiDeiVicini[i]; 
                        }
                    }
                    archiDeiVicini = adv;
                }
                if(deg!=0){
                    double[] variationOfModularity = new double[deg];
                    MetaCommunity[] vicini = new MetaCommunity[deg];
                    for(int i_archi = 0; i_archi < archiDeiVicini.length; i_archi ++) {
                        if (net.getEdgeSource(archiDeiVicini[i_archi]).equals(metaCom.get(i_mc))){
                            vicini[i_archi]=net.getEdgeTarget(archiDeiVicini[i_archi]);
                        } 
                        else{
                            vicini[i_archi]=net.getEdgeSource(archiDeiVicini[i_archi]);
                        }
                    }

                    //Uso nomi in accordo con quanto descritto da https://en.wikipedia.org/wiki/Louvain_method
                    for(int i_vicino = 0; i_vicino < vicini.length; i_vicino++ ){


                        double sigma_in = 0;
                        if(net.getEdge(vicini[i_vicino], vicini[i_vicino])!=null){
                            sigma_in += net.getEdgeWeight(net.getEdge(vicini[i_vicino], vicini[i_vicino]));
                        }
                        double sigma_tot = vicini[i_vicino].getPesoSuiNodi();
                        double k_i = metaCom.get(i_mc).getPesoSuiNodi();
                        double k_i_in = net.getEdgeWeight(archiDeiVicini[i_vicino]);
                        variationOfModularity[i_vicino] =((sigma_in+2*k_i_in)/(2*m)-(Math.pow(((sigma_tot+k_i)/(2*m)), 2))) - ((sigma_in/(2*m))-Math.pow((sigma_tot/(2*m)), 2) - Math.pow(k_i/(2*m), 2));
                    }
                    //Trovo il massimo della modularità
                    int max_position = 0;
                    double max_modularity = variationOfModularity[0];
                    for (int i = 1; i < variationOfModularity.length; i ++ ) {
                        if (variationOfModularity[i]>max_modularity) {
                            max_position = i;
                            max_modularity = variationOfModularity[i];
                        }
                    }
                    //Se è possibile aumentare la modularità comincio a spostare i metanodi
                    if (max_modularity >0) {
                        //Salvo il peso degli archi
                        double[] weightOfEdge = new double[archiDeiVicini.length];
                        for(int i_archi = 0; i_archi < archiDeiVicini.length; i_archi ++){
                            weightOfEdge[i_archi] = net.getEdgeWeight(archiDeiVicini[i_archi]); 
                        }
                        //Rimuovo tutti gli archi che mi connettono ai vicini
                        for(int i_archi = 0; i_archi < archiDeiVicini.length; i_archi ++){
                            net.removeEdge(archiDeiVicini[i_archi]); 
                        }
                        //Rimuovo anche l'arco di selfloop
                        net.removeEdge(metaCom.get(i_mc), metaCom.get(i_mc));
                        //Comincio a sistemare gli altri archi
                        for(int i_archi = 0; i_archi < archiDeiVicini.length; i_archi ++){
                            //Tratto a parte il caso in cui vado nel nodo che rappresenta la community di destinazione (per il self loop da aggiungere) 
                            if(i_archi==max_position){
                                vicini[max_position].setPesoSuiNodi(vicini[max_position].getPesoSuiNodi()+metaCom.get(i_mc).getPesoSuiNodi()-net.getEdgeWeight(archiDeiVicini[i_archi]));
                                if (net.getEdge(vicini[max_position], vicini[max_position]) == null) {
                                    net.addEdge(vicini[max_position], vicini[max_position]);
                                    net.setEdgeWeight(net.getEdge(vicini[max_position], vicini[max_position]), selfLoopWeight + weightOfEdge[max_position]);
                                }
                                else{
                                    DefaultWeightedEdge arcoInQuestione =net.getEdge(vicini[max_position], vicini[max_position]);
                                    net.setEdgeWeight(arcoInQuestione, selfLoopWeight + weightOfEdge[max_position] + net.getEdgeWeight(arcoInQuestione));
                                }
                            }
                            //Considero ora gli altri casi
                            else{
                                if(net.getEdge(vicini[i_archi], vicini[max_position]) == null){
                                    net.addEdge(vicini[i_archi], vicini[max_position]);
                                    net.setEdgeWeight(net.getEdge(vicini[i_archi], vicini[max_position]), weightOfEdge[i_archi]);
                                }
                                else{
                                    DefaultWeightedEdge arcoInQuestione =net.getEdge(vicini[i_archi], vicini[max_position]);
                                    net.setEdgeWeight(arcoInQuestione, weightOfEdge[i_archi] + net.getEdgeWeight(arcoInQuestione));
                                }
                            }
                        }
                        //Mi salvo quali nodi finiscono nella nuova community
                        vicini[max_position].merge(metaCom.get(i_mc));
                        //Modifico la community nei nodi che passano da altre parti
                        ArrayList<Entity> nodesChangingCom =metaCom.get(i_mc).getNodes();
                        for (Entity entity : nodesChangingCom) {
                            entity.setComm(vicini[max_position].getComm());
                        }
                        //Rimuovo il nodo dal grafo e dalla lista di nodi
                        net.removeVertex(metaCom.get(i_mc));
                        metaCom.remove(i_mc);
                        //Resetto la condizione del ciclo
                        ending = false;
                    }
                }
            }
        }
        return net;
    }
}
