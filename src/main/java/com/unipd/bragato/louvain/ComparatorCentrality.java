package com.unipd.bragato.louvain;

import java.util.Comparator;

public class ComparatorCentrality implements Comparator<MetaCommunity> {
    public int compare(MetaCommunity c1, MetaCommunity c2){
        int out =0;
        if(c1.getPesoSuiNodi()<c2.getPesoSuiNodi()){
            out = -1;
        }
        if(c1.getPesoSuiNodi()>c2.getPesoSuiNodi()){
            out = 1;
        }
        return out;
    }
}
