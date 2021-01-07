package com.unipd.bragato.louvain;

import java.util.function.Function;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class ModularityCalcolation implements Function<DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge>,Double> {

    public Double apply (DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> network) {
        double modularity = 0;
        //Uso una nomenclatura analoga a https://en.wikipedia.org/wiki/Modularity_(networks)
        double m = 0;
        for (DefaultWeightedEdge arco : network.edgeSet()) {
            m += network.getEdgeWeight(arco);
        }
        for (MetaCommunity v : network.vertexSet()) {
            double k_v = v.getPesoSuiNodi();
            for (MetaCommunity w : network.vertexSet()) {
                double k_w = w.getPesoSuiNodi();
                if (network.getEdge(v, w) != null) {
                    if(v.getComm() == w.getComm()) {
                        double A_vw = network.getEdgeWeight(network.getEdge(v, w));
                        modularity += A_vw - (k_v*k_w)/(2*m);
                    }
                }
            }
        }
        modularity = modularity/(2*m);
        return modularity;
    }
}    