package com.unipd.bragato.louvain;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class App {

    // Main
    public static void main(String[] args) {
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        boolean soluzioniPresenti = false;
        // Inizializzazione dell'array dei colori
        Color[] colori = new Color[125];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                for (int j2 = 0; j2 < 5; j2++) {
                    colori[i * 25 + j * 5 + j2] = new Color(51 * i + 51, 51 * j + 51, 51 * j2 + 51);
                }
            }
        }
        //I colori i cui indici sono multipli di 31 sono grigi, li rimuovo
        Color[] temp = new Color[120];
        int i2=0;
        for (int j = 0; j < colori.length; j++) {
            if(j%31!=0){
                temp[j-i2]=colori[j];
            }
            else{
                i2++;
            }
        }
        colori =temp;

        // Istanzio un'istanza delle classi che mi seviranno
        GraphGenerator gen = new GraphGenerator();
        Louvain louvain = new Louvain();

        for (int i = 3; i < 4; i++) {
            double proportion = 0;
            int iterations = 5;
            ArrayList<DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge>> output = new ArrayList<DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge>>(
                    0);
            ArrayList<Double> modularityOfOutput = new ArrayList<Double>(0);
            String path = "./src/main/java/com/unipd/bragato/louvain/Data/G"
                    + i + "/";
            String file = "G";
            double maxValueX = 0;
            double maxValueY = 0;
            try {
                Scanner scan = new Scanner(new File(path + file + ".txt"));

                while (scan.hasNextLine()) {
                    String first = scan.next();
                    String second = scan.next();
                    if(soluzioniPresenti){
                        scan.next();
                    }
                    double x = Double.parseDouble(first);
                    double y = Double.parseDouble(second);
                    if (x > maxValueX) {
                        maxValueX = x;
                    }
                    if (y > maxValueY) {
                        maxValueY = y;
                    }
                }
                scan.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
                proportion = 1080/maxValueX;
                maxValueX = maxValueX * proportion;
                maxValueY = maxValueY * proportion;
            try {
                Scanner scan2 = new Scanner(new File(path + file + ".txt"));
                BufferedImage img = new BufferedImage((int) maxValueX, (int) maxValueY, BufferedImage.TYPE_3BYTE_BGR);
                Graphics2D graphics2d = img.createGraphics();
                graphics2d.setColor(Color.black);
                graphics2d.fillRect(0, 0, (int) maxValueX, (int) maxValueY);
                graphics2d.setColor(Color.white);
                BufferedImage sol = new BufferedImage((int) maxValueX, (int) maxValueY, BufferedImage.TYPE_3BYTE_BGR);
                Graphics2D grasol = sol.createGraphics();
                grasol.setColor(Color.black);
                grasol.fillRect(0, 0, (int) maxValueX, (int) maxValueY);
                DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> solGraph = new DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge>(
                        DefaultWeightedEdge.class);
                ArrayList<MetaCommunity> nodi = new ArrayList<MetaCommunity>(0);
                while (scan2.hasNextLine()) {
                    String first = scan2.next();
                    String second = scan2.next();   
                    double x = Double.parseDouble(first);
                    double y = Double.parseDouble(second);
                    x = x * proportion;
                    y = y * proportion;
                    graphics2d.fillRoundRect((int) x, (int) y, 2, 2, 1, 1);
                    if(soluzioniPresenti){
                        String third = scan2.next();
                        int c = Integer.parseInt(third);
                        MetaCommunity entrata = new MetaCommunity(c, new Entity(x, y));
                        nodi.add(entrata);
                        solGraph.addVertex(entrata);
                        grasol.setColor(colori[c * 157 % colori.length]);
                        grasol.fillRoundRect((int) x, (int) y, 2, 2, 1, 1);
                    }
                    
                }
                scan2.close();
                if(soluzioniPresenti){
                    for (int l = 0; l < nodi.size() - 1; l++) {
                        Entity nodo1 = nodi.get(l).getNodes().get(0);
                        for (int j = l + 1; j < nodi.size(); j++) {
                            Entity nodo2 = nodi.get(j).getNodes().get(0);
                            double dist = Math.pow((nodo1.getX() - nodo2.getX()), 2)
                                    + Math.pow((nodo1.getY() - nodo2.getY()), 2);
                            solGraph.addEdge(nodi.get(l), nodi.get(j));
                            DefaultWeightedEdge arco = solGraph.getEdge(nodi.get(l), nodi.get(j));
                            solGraph.setEdgeWeight(arco, 1 / dist);
                        }
                    }
                    for (int l = 0; l < nodi.size() - 1; l++) {
                        double peso = 0;
                        for (DefaultWeightedEdge archi : solGraph.edgesOf(nodi.get(l))
                                .toArray(new DefaultWeightedEdge[0])) {
                            peso += solGraph.getEdgeWeight(archi);
                        }
                        nodi.get(l).setPesoSuiNodi(peso);
                    }
                    ModularityCalcolation q = new ModularityCalcolation();
                    double modularity = q.apply(solGraph);
                    ImageIO.write(sol, "jpg", new File(path + file + "Mod" + modularity + "Solution.jpg"));
                }
                ImageIO.write(img, "jpg", new File(path + file + "White.jpg"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int l = 0; l < iterations; l++) {
                DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> g = gen.graphFromTxtFull(path, file);
                int a = g.edgeSet().size();
                double beginTime = System.currentTimeMillis();
                DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> smaller = louvain.apply(g);
                double endTime = System.currentTimeMillis();
                System.out.println("archi " + a + " tempo "+(endTime-beginTime)/1000);
                output.add(smaller);
                MetaCommunity[] mc = smaller.vertexSet().toArray(new MetaCommunity[0]);
                ArrayList<MetaCommunity> communities = new ArrayList<MetaCommunity>(0);
                for (int k = 0; k < mc.length; k++) {
                    communities.add(mc[k]);
                }
                ModularityCalcolation m = new ModularityCalcolation();
                double q = m.apply(smaller);
                modularityOfOutput.add(q);
                System.out.println(q);

            }
            int positionOfMaxQ = 0;
            double maxQ = modularityOfOutput.get(0);
            for (int index = 1; index < modularityOfOutput.size(); index++) {
                if (modularityOfOutput.get(index) > maxQ) {
                    maxQ = modularityOfOutput.get(index);
                    positionOfMaxQ = index;
                }
            }
            DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> smaller = output.get(positionOfMaxQ);
            MetaCommunity[] mc = smaller.vertexSet().toArray(new MetaCommunity[0]);
            ArrayList<MetaCommunity> communities = new ArrayList<MetaCommunity>(0);
            for (int k = 0; k < mc.length; k++) {
                communities.add(mc[k]);
            }
            BufferedImage img = new BufferedImage((int) maxValueX, (int) maxValueY, BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D graphics2d = img.createGraphics();
            graphics2d.setColor(Color.black);
            graphics2d.fillRect(0, 0, (int) maxValueX, (int) maxValueY);
            try {
                for (int j = 0; j < communities.size(); j++) {
                    graphics2d.setColor(colori[j * 157 % colori.length]);
                    for (Entity entity : communities.get(j).getNodes()) {
                        int x = (int) (entity.getX() * proportion);
                        int y = (int) (entity.getY() * proportion);
                        graphics2d.fillRoundRect(x, y, 2, 2, 1, 1);
                    }
                }
                ImageIO.write(img, "jpg", new File(path + file + "Mod" + maxQ + ".jpg"));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            for (int i_c = 0; i_c < communities.size(); i_c++) {
                File commTxt = new File(path + "Community" + i_c + ".txt");
                try {
                    commTxt.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileWriter myWriter;
                try {
                    myWriter = new FileWriter(commTxt);
                    for (Entity e : communities.get(i_c).getNodes()) {
                        myWriter.write(e.getX()+" "+e.getY()+" "+i_c+"\n");
                    }
                    myWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
