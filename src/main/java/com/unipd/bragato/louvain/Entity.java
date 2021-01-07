package com.unipd.bragato.louvain;


/*   La classe serve come nodo del grafo su cui andremo ad applicare l'algoritmo di Louvain
 *   Ogni nodo del grafo avrà una stringa a identificarlo e avrà un campo int che indicherà la
 *   community a cui appartiene, se questa non è ancora nota il campo avrà valore -1
 */

public class Entity {

    //Campi
    private double x;
    private double y;
    private int positionInOrderedX;
    private int positionInOrderedY;
    private int community;

    //Costruttori 
    public Entity() {
        this.community = -1;
    }

    public Entity(double x, double y) {
        this.x = x;
        this.y = y;
        this.community = -1;
    }

    public Entity(double x, double y, int community) {
        this.x = x;
        this.y = y;
        this.community = community;
    }

    //get e set
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    public int getOrderX() {
        return positionInOrderedX;
    }
    
    public int getOrderY() {
        return positionInOrderedY;
    }

    public int getComm() {
        return community;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public void setY(Double y) {
        this.y = y;
    }
    public void setOrderX(int x) {
        this.positionInOrderedX = x;
    }

    public void setOrderY(int y) {
        this.positionInOrderedY= y;
    }
    public void setComm(int comm){
        this.community = comm; 
    }

    //sovrascrivo anche il metodo toString
    public String toString() {
        String s = "["+x+"; "+y+"]";
        return s;
    }

    public boolean equals(Entity cfr){
        boolean out = false;
        if(this.x==cfr.x &&this.y ==cfr.y){
            out =true;
        }
        return out;
    }
}
