package com.unipd.bragato.louvain;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class MatriceDelleAdiacenze {
    public static void main(String[] args) {
        GraphGenerator gen = new GraphGenerator();
        for (int i = 1; i < 9; i++) {
            String path = "C:/Users/Filippo/Desktop/communitydetection/src/main/java/com/unipd/bragato/communitydetection/Data/Raw/Shape"+i+"/";
            String file = "Shape";
            int n = 0;
            try {
                Scanner scan = new Scanner(new File(path + file + ".txt"));

                while (scan.hasNextLine()) {
                    n++;
                    scan.nextLine();
                }
                scan.close();
            }
            catch (Exception e) {
            }
            DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> g = gen.graphFromTxtFull(path, file);
            double[][] adj = new double[n][n];
            MetaCommunity[] vertex=g.vertexSet().toArray(new MetaCommunity[0]);
            for (int j = 0; j < vertex.length; j++) {
                for (int j2 = 0; j2 < vertex.length; j2++) {
                    if (g.containsEdge(vertex[j], vertex[j2])) {
                        adj[j][j2]=g.getEdgeWeight(g.getEdge(vertex[j], vertex[j2]));
                    }
                }
            }
            try {
                PrintStream fileOut = new PrintStream("C:/Users/Filippo/Desktop/communitydetection/src/main/java/com/unipd/bragato/communitydetection/Data/Raw/Shape"+i+"/adj"+i+".txt");
                System.setOut(fileOut);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            for (int j = 0; j < vertex.length; j++) {
                for (int j2 = 0; j2 < vertex.length; j2++) {
                    if(j2==vertex.length-1){
                        System.out.println(adj[j][j2]+" ");
                    }
                    else{
                        System.out.print(adj[j][j2]+" ");
                    }
                }
            }
            try {
                PrintStream fileOut = new PrintStream("C:/Users/Filippo/Desktop/communitydetection/src/main/java/com/unipd/bragato/communitydetection/Data/Raw/Shape"+i+"/sol"+i+".txt");
                System.setOut(fileOut);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            try {
                Scanner scan = new Scanner(new File(path + file + ".txt"));
                ArrayList<Integer> visti= new ArrayList<Integer>(0);
                ArrayList<Integer> corrispondenti= new ArrayList<Integer>(0);
                int traslazione = 1;
                while (scan.hasNextLine()) {
                    scan.next();
                    scan.next();
                    int actual = Integer.parseInt(scan.next());
                    if(!visti.contains(actual)){
                        visti.add(actual);
                        actual = traslazione;
                        corrispondenti.add(traslazione++);
                    }
                    else{
                        actual=corrispondenti.get(visti.indexOf(actual));
                        
                    }
                    System.out.println(actual);
                }
                scan.close();
            }
            catch (Exception e) {
            }
        }
    }
}
