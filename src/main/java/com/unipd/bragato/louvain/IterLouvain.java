package com.unipd.bragato.louvain;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class IterLouvain {
    public static void main(String[] args) {
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
        ModularityCalcolation mod = new ModularityCalcolation();

        for (int i = 1; i < 2; i++) {
            double proportion = 0;
            System.out.println("Per shape" + i + " ho trovato modularità:");
            int iterations = 10;
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
                    graphics2d.fillRoundRect((int) x, (int) y, 20, 20, 10, 10);
                    if(soluzioniPresenti){
                        String third = scan2.next();
                        int c = Integer.parseInt(third);
                        MetaCommunity entrata = new MetaCommunity(c, new Entity(x, y));
                        nodi.add(entrata);
                        solGraph.addVertex(entrata);
                        grasol.setColor(colori[c * 157 % colori.length]);
                        grasol.fillRoundRect((int) x, (int) y, 20, 20, 10, 10);
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
            for (int pippo = 0; pippo < iterations; pippo++) {

                DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> g = gen.graphFromTxtFull(path, file);
                DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> smaller = louvain.apply(g);
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


            boolean end= false;
            double oldModularity=0;
            while(end==false){
                int numberOfCommunities = communities.size();
                end= true;
                //calcolo la modularità del grafo
                DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> old = gen.graphFromTxtFull(path, file);
                for (MetaCommunity oldNode : old.vertexSet()) {
                    for (int j = 0; j < communities.size(); j++) {
                        for (Entity oldEntity : communities.get(j).getNodes()) {
                            if(oldEntity.equals(oldNode.getNodes().get(0))){
                                oldNode.setComm(j);
                            }
                        }
                    }
                }
                oldModularity = mod.apply(old);
                System.out.println("old = "+oldModularity);
                for (int j = 0; j < numberOfCommunities; j++) {
                    DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> commj = gen.graphFromArraylist(communities.get(j).getNodes());
                    DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> smallerj = louvain.apply(commj);
                    MetaCommunity[] newComm= smallerj.vertexSet().toArray(new MetaCommunity[0]);
                    for (int k = 1; k < newComm.length; k++) {
                        for (Entity e : newComm[k].getNodes()) {
                            for (MetaCommunity oldNode : old.vertexSet()) {
                                if (e.equals(oldNode.getNodes().get(0))){
                                    oldNode.setComm(numberOfCommunities+k-1);
                                }
                            }
                        }
                    }
                    double newModularity = mod.apply(old);
                    System.out.println("new = "+newModularity);
                    if(newModularity>oldModularity){
                        end=false;
                        for (int k = 0; k < newComm.length; k++) {
                            int i_c = j;
                            if(k!=0){
                                i_c= numberOfCommunities+k-1;
                            }
                            File commTxt = new File(path + "Community" + i_c + ".txt");
                            try {
                                commTxt.createNewFile();
                            } 
                            catch (IOException e) {
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
                        communities.set(j,newComm[0]);
                        for (int k = 1; k < newComm.length; k++) {
                            communities.add(newComm[k]);
                        }
                        oldModularity = newModularity;
                        numberOfCommunities= numberOfCommunities+newComm.length-1;
                    }
                    else{
                        for (Entity e: communities.get(j).getNodes()) {
                            for (MetaCommunity metaCommunity : old.vertexSet()) {
                                if(metaCommunity.getNodes().get(0).equals(e)){
                                    metaCommunity.setComm(j);
                                }
                            }
                        }
                    }
                }
            }

            BufferedImage img = new BufferedImage((int) maxValueX, (int) maxValueY, BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D graphics2d = img.createGraphics();
            graphics2d.setColor(Color.black);
            graphics2d.fillRect(0, 0, (int) maxValueX, (int) maxValueY);
            try {
                for (int j = 0; j < communities.size(); j++) {
                    graphics2d.setColor(colori[j * 157 % colori.length]);
                    for (Entity entity : communities.get(j).getNodes()) {
                        int x = (int) (entity.getX() * 100);
                        int y = (int) (entity.getY() * 100);
                        graphics2d.fillRoundRect(x, y, 20, 20, 10, 10);
                    }
                }
                ImageIO.write(img, "jpg", new File(path + file + "Mod" + oldModularity + ".jpg"));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
