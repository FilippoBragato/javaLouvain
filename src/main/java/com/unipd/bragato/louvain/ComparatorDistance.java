package com.unipd.bragato.louvain;

import java.util.Comparator;

public class ComparatorDistance implements Comparator<Distance>{
    public int compare(Distance d1, Distance d2){
        int out = 0;
        if(d1.getDistance()<d2.getDistance()){
            out = -1;
        }
        if(d1.getDistance()>d2.getDistance()){
            out = 1;
        }
        return out;
    }
}
