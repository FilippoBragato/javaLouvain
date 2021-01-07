package com.unipd.bragato.louvain;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class GraphGenerator {
    //Dato un file txt crea un grafo in cui i nodi sono caratterizzati da un'ascissa e un'ordinata
    //e ogni nodo è collegato con un arco ai numberOfArch nodi più vicino. Il peso dell'arco è inversamente
    //proporzionale al quadrato della distanza
    public DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> graphFromTxtFixedDegree(String path, String filename, int numberOfArch){
        DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> net = new DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        
        //Creo un array con i nodi
        ArrayList<MetaCommunity> nodi= new ArrayList<MetaCommunity>(0);
        try {
            Scanner scan = new Scanner(new File(path + filename + ".txt"));
            int i = 0;
            while(scan.hasNextLine()){
                String first = scan.next();
                String second = scan.next();
                //Sscan.next();
                double x = Double.parseDouble(first);
                double y = Double.parseDouble(second);
                nodi.add( new MetaCommunity(i, new Entity(x,y)));
                net.addVertex(nodi.get(i++));
            }
            scan.close();
        } 
        catch (Exception e) {
            System.out.println("An exception has occurred: " + e.getMessage());
        }
        //Mi occupo di scrivere gli archi
        for (int i = 0; i < nodi.size() -1; i++) {
            ArrayList<Distance> distanzaicostj= new ArrayList<Distance>(nodi.size()-i-1);
            MetaCommunity commi = nodi.get(i);
            for (int j = i+1; j < nodi.size(); j++) {
                MetaCommunity commj = nodi.get(j);
                if(i!=j) {
                        double dist=Math.pow((commi.getNodes().get(0).getX()-commj.getNodes().get(0).getX()), 2)+Math.pow((commi.getNodes().get(0).getY()-commj.getNodes().get(0).getY()), 2);
                        distanzaicostj.add(new Distance(dist, commj));
                }
                else {
                    distanzaicostj.add(new Distance(Double.MAX_VALUE, commj));
                }
            }
            distanzaicostj.sort(new ComparatorDistance());
            int k=0;
            while(net.degreeOf(commi)<numberOfArch && k<distanzaicostj.size()) {
                net.addEdge(commi, distanzaicostj.get(k).getMetaCommunity());
                DefaultWeightedEdge arco = net.getEdge(commi, distanzaicostj.get(k).getMetaCommunity());
                net.setEdgeWeight(arco, 1/distanzaicostj.get(k).getDistance());
                k++;
            }
        }
        
        return net;
    }
    public DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> graphFromTxtFixedDistance(String path, String filename, double maxDistance){
        DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> net = new DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        
        ArrayList<MetaCommunity> nodi= new ArrayList<MetaCommunity>(0);
        try {
            Scanner scan = new Scanner(new File(path + filename + ".txt"));
            int i = 0;
            while(scan.hasNextLine()){
                String first = scan.next();
                String second = scan.next();
                //scan.next();
                double x = Double.parseDouble(first);
                double y = Double.parseDouble(second);
                nodi.add( new MetaCommunity(i, new Entity(x,y)));
                net.addVertex(nodi.get(i++));
            }
            scan.close();
        } 
        catch (Exception e) {
            System.out.println(e.getCause());
        }
        double quadDistanzamax = Math.pow(maxDistance, 2);
        for (int i = 0; i < nodi.size() -1; i++) {
            Entity nodo1 = nodi.get(i).getNodes().get(0);
            for (int j = i+1; j < nodi.size(); j++) {
                Entity nodo2 = nodi.get(j).getNodes().get(0);
                double dist=Math.pow((nodo1.getX()-nodo2.getX()), 2)+Math.pow((nodo1.getY()-nodo2.getY()), 2);
                if(dist < quadDistanzamax){
                    net.addEdge(nodi.get(i), nodi.get(j));
                    DefaultWeightedEdge arco = net.getEdge(nodi.get(i), nodi.get(j));
                    net.setEdgeWeight(arco, 1/dist);                 
                }
            }
        }
        return net;
    }


    public DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> graphFromTxtFull(String path, String filename){
        DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> net = new DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        
        ArrayList<MetaCommunity> nodi= new ArrayList<MetaCommunity>(0);
        try {
            Scanner scan = new Scanner(new File(path + filename + ".txt"));
            int i = 0;
            while(scan.hasNextLine()){
                String first = scan.next();
                String second = scan.next();
                //scan.next();
                double x = Double.parseDouble(first);
                double y = Double.parseDouble(second);
                nodi.add(new MetaCommunity(i, new Entity(x,y)));
                net.addVertex(nodi.get(i++));
            }
            scan.close();
        } 
        catch (Exception e) {
            System.out.println("An exception has occurred: " + e.getMessage());
        }

        for (int i = 0; i < nodi.size() -1; i++) {
            Entity nodo1 = nodi.get(i).getNodes().get(0);
            for (int j = i+1; j < nodi.size(); j++) {
                Entity nodo2 = nodi.get(j).getNodes().get(0);
                double dist=Math.pow((nodo1.getX()-nodo2.getX()), 2)+Math.pow((nodo1.getY()-nodo2.getY()), 2);
                net.addEdge(nodi.get(i), nodi.get(j));
                DefaultWeightedEdge arco = net.getEdge(nodi.get(i), nodi.get(j));
                net.setEdgeWeight(arco, 1/dist);
            }
        }
        
        return net;
    }

    public DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> graphFromTxtSafe(String path, String filename){
        DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> net = new DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        
        ArrayList<MetaCommunity> nodi= new ArrayList<MetaCommunity>(0);
        try {
            Scanner scan = new Scanner(new File(path + filename + ".txt"));
            int i = 0;
            while(scan.hasNextLine()){
                String first = scan.next();
                String second = scan.next();
                scan.next();
                double x = Double.parseDouble(first);
                double y = Double.parseDouble(second);
                Entity ingresso = new Entity(x,y);
                Boolean presente = false;
                for (MetaCommunity metaCommunity : nodi) {
                    if(metaCommunity.getNodes().get(0).equals(ingresso)){
                        presente = true;
                    }
                }
                if(!presente){
                    nodi.add(new MetaCommunity(i, ingresso));
                    net.addVertex(nodi.get(i++));
                }
            }
            scan.close();
        } 
        catch (Exception e) {
            System.out.println("An exception has occurred: " + e.getMessage());
        }

        for (int i = 0; i < nodi.size() -1; i++) {
            Entity nodo1 = nodi.get(i).getNodes().get(0);
            for (int j = i+1; j < nodi.size(); j++) {
                Entity nodo2 = nodi.get(j).getNodes().get(0);
                double dist=Math.pow((nodo1.getX()-nodo2.getX()), 2)+Math.pow((nodo1.getY()-nodo2.getY()), 2);
                net.addEdge(nodi.get(i), nodi.get(j));
                DefaultWeightedEdge arco = net.getEdge(nodi.get(i), nodi.get(j));
                net.setEdgeWeight(arco, 1/dist);
            }
        }
        
        return net;
    }
    public DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> graphFromTxtSafeScaled(String path, String filename){
        DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> net = new DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        
        ArrayList<MetaCommunity> nodi= new ArrayList<MetaCommunity>(0);
        double maxX = 0;
        double maxY = 0;
        try {
            Scanner scan = new Scanner(new File(path + filename + ".txt"));
            int i = 0;
            while(scan.hasNextLine()){
                String first = scan.next();
                String second = scan.next();
                scan.next();
                double x = Double.parseDouble(first);
                double y = Double.parseDouble(second);
                if(x>maxX){
                    maxX = x;
                }
                if(y>maxY){
                    maxY=y;
                }
                Entity ingresso = new Entity(x,y);
                Boolean presente = false;
                for (MetaCommunity metaCommunity : nodi) {
                    if(metaCommunity.getNodes().get(0).equals(ingresso)){
                        presente = true;
                    }
                }
                if(!presente){
                    nodi.add(new MetaCommunity(i, ingresso));
                    net.addVertex(nodi.get(i++));
                }
            }
            scan.close();
        } 
        catch (Exception e) {
            System.out.println("An exception has occurred: " + e.getMessage());
        }
        double diagonal =(Math.pow(maxX, 2)+Math.pow(maxY, 2))/1024;
        for (int i = 0; i < nodi.size() -1; i++) {
            Entity nodo1 = nodi.get(i).getNodes().get(0);
            for (int j = i+1; j < nodi.size(); j++) {
                Entity nodo2 = nodi.get(j).getNodes().get(0);
                double dist=Math.pow((nodo1.getX()-nodo2.getX()), 2)+Math.pow((nodo1.getY()-nodo2.getY()), 2)/diagonal;
                net.addEdge(nodi.get(i), nodi.get(j));
                DefaultWeightedEdge arco = net.getEdge(nodi.get(i), nodi.get(j));
                net.setEdgeWeight(arco, 1/dist);
            }
        }
        
        return net;
    }
    public DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> graphFromTxtScaled(String path, String filename){
        DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> net = new DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        
        ArrayList<MetaCommunity> nodi= new ArrayList<MetaCommunity>(0);
        double maxX = 0;
        double maxY = 0;
        try {
            Scanner scan = new Scanner(new File(path + filename + ".txt"));
            int i = 0;
            while(scan.hasNextLine()){
                String first = scan.next();
                String second = scan.next();
                scan.next();
                double x = Double.parseDouble(first);
                double y = Double.parseDouble(second);
                if(x>maxX){
                    maxX = x;
                }
                if(y>maxY){
                    maxY=y;
                }
                nodi.add(new MetaCommunity(i, new Entity(x,y)));
                net.addVertex(nodi.get(i++));
            }
            scan.close();
        } 
        catch (Exception e) {
            System.out.println("An exception has occurred: " + e.getMessage());
        }
        double diagonal =(Math.pow(maxX, 2)+Math.pow(maxY, 2))/1024;
        for (int i = 0; i < nodi.size() -1; i++) {
            Entity nodo1 = nodi.get(i).getNodes().get(0);
            for (int j = i+1; j < nodi.size(); j++) {
                Entity nodo2 = nodi.get(j).getNodes().get(0);
                double dist=Math.pow((nodo1.getX()-nodo2.getX()), 2)+Math.pow((nodo1.getY()-nodo2.getY()), 2)/diagonal;
                net.addEdge(nodi.get(i), nodi.get(j));
                DefaultWeightedEdge arco = net.getEdge(nodi.get(i), nodi.get(j));
                net.setEdgeWeight(arco, 1/dist);
            }
        }
        
        return net;
    }
    public DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> graphFromArraylist(ArrayList<Entity> ind){
        DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> net = new DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        
        ArrayList<MetaCommunity> nodi= new ArrayList<MetaCommunity>(0);
        for (int i = 0; i< ind.size();i++) {
            MetaCommunity nuovo = new MetaCommunity(i, new Entity(ind.get(i).getX(), ind.get(i).getY()));
            nodi.add(nuovo);
            net.addVertex(nuovo);
        }

        for (int i = 0; i < nodi.size() -1; i++) {
            Entity nodo1 = nodi.get(i).getNodes().get(0);
            for (int j = i+1; j < nodi.size(); j++) {
                Entity nodo2 = nodi.get(j).getNodes().get(0);
                double dist=Math.pow((nodo1.getX()-nodo2.getX()), 2)+Math.pow((nodo1.getY()-nodo2.getY()), 2);
                net.addEdge(nodi.get(i), nodi.get(j));
                DefaultWeightedEdge arco = net.getEdge(nodi.get(i), nodi.get(j));
                net.setEdgeWeight(arco, 1/dist);
            }
        }
        return net;
    }
    public DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> graphFromTxtFixedArc(String path, String filename, int arc){
        DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> net = new DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        
        ArrayList<MetaCommunity> nodi= new ArrayList<MetaCommunity>(0);
        ArrayList<MetaCommunity> nodi2= new ArrayList<MetaCommunity>(0);
        try {
            Scanner scan = new Scanner(new File(path + filename + ".txt"));
            int i = 0;
            while(scan.hasNextLine()){
                String first = scan.next();
                String second = scan.next();
                //scan.next();
                double x = Double.parseDouble(first);
                double y = Double.parseDouble(second);
                nodi.add( new MetaCommunity(i, new Entity(x,y)));
                nodi2.add( new MetaCommunity(i, new Entity(x,y)));
                net.addVertex(nodi.get(i++));
            }
            scan.close();
        } 
        catch (Exception e) {
            System.out.println(e.getCause());
        }
        Random rng = new Random();
        Collections.shuffle(nodi, rng);
        Random rng2 = new Random();
        Collections.shuffle(nodi2, rng2);
        int actual=0;
        while (actual<arc){
            int i=rng.nextInt(nodi.size());
            int j=rng2.nextInt(nodi.size());
            Entity nodo1 = nodi.get(i).getNodes().get(0);
            Entity nodo2 = nodi2.get(j).getNodes().get(0);
            double dist=Math.pow((nodo1.getX()-nodo2.getX()), 2)+Math.pow((nodo1.getY()-nodo2.getY()), 2);
            var add =net.addEdge(nodi.get(i), nodi.get(j));
            DefaultWeightedEdge arco = net.getEdge(nodi.get(i), nodi.get(j));
            net.setEdgeWeight(arco, 1/dist);
            if(!(add==null)){
                actual++;
                System.out.println(actual);
            }
        }
        return net;
    }

}
