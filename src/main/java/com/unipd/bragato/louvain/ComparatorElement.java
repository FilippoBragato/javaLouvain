package com.unipd.bragato.louvain;

import java.util.Comparator;

public class ComparatorElement implements Comparator<Entity> {
    public int compare(Entity e1, Entity e2){
        int out =0;
        if(e1.getX()<e2.getX()){
            out = -1;
        }
        if(e1.getX()>e2.getX()){
            out = 1;
        }
        if(e1.getX()==e2.getX()){
            if(e1.getY()<e2.getY()){
                out =-1;
            }
            if(e1.getY()>e2.getY()){
                out =1;
            }
            if(e1.getY()==e2.getY()){
                out =0;
            }
        }
        return out;
    }
}
