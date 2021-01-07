package com.unipd.bragato.louvain;

import java.util.Comparator;

public class ComparatorMetaCommunity implements Comparator<MetaCommunity> {
    public int compare(MetaCommunity c1, MetaCommunity c2){
        int out =0;
        if(c1.getNodes().get(0).getX()<c2.getNodes().get(0).getX()){
            out = -1;
        }
        if(c1.getNodes().get(0).getX()>c2.getNodes().get(0).getX()){
            out = 1;
        }
        if(c1.getNodes().get(0).getX()==c2.getNodes().get(0).getX()){
            if(c1.getNodes().get(0).getY()<c2.getNodes().get(0).getY()){
                out =-1;
            }
            if(c1.getNodes().get(0).getY()>c2.getNodes().get(0).getY()){
                out =1;
            }
            if(c1.getNodes().get(0).getY()==c2.getNodes().get(0).getY()){
                out =0;
            }
        }
        return out;
    }
}