package com.unipd.bragato.louvain;


public class Distance {
    private double distance;
    private MetaCommunity community;

    public Distance(double distance, MetaCommunity community){
        this.distance = distance;
        this.community = community;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
    public void setEntity(MetaCommunity community) {
        this.community = community;
    }
    public double getDistance() {
        return distance;
    }
    public MetaCommunity getMetaCommunity() {
        return community;
    }
    
}
