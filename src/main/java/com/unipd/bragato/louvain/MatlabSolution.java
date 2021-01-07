package com.unipd.bragato.louvain;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;


public class MatlabSolution {
    public static void main(String[] args) {
        Color[] colori = new Color[125];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                for (int j2 = 0; j2 < 5; j2++) {
                    colori[i*25+j*5+j2]= new Color(51*i+51,51*j+51,51*j2+51);
                }
            }
        }
        for (int i = 1; i < 9; i++) {
        String path ="C:/Users/Filippo/Desktop/communitydetection/src/main/java/com/unipd/bragato/communitydetection/Data/Raw/Shape"+i+"/";

            double maxValueX = 0;
            double maxValueY = 0;
            try {
                Scanner scan = new Scanner(new File(path + "Shape.txt"));

                while (scan.hasNextLine()) {
                    String first = scan.next();
                    String second = scan.next();
                    scan.next();
                    double x = Double.parseDouble(first);
                    double y = Double.parseDouble(second);
                    if (x > maxValueX) {
                        maxValueX = x;
                    }
                    if (y > maxValueY) {
                        maxValueY = y;
                    }
                }

                maxValueX = maxValueX *100;
                maxValueY = maxValueY *100;
                scan.close();
            }
            catch (Exception e) {
            }
            
            try{
                Scanner scan2 = new Scanner(new File(path + "Shape.txt"));
                Scanner scan3 = new Scanner(new File(path+"SolByMat"+i+".txt"));
                BufferedImage sol = new BufferedImage((int)maxValueX, (int)maxValueY, BufferedImage.TYPE_3BYTE_BGR);
                Graphics2D grasol = sol.createGraphics();
                grasol.setColor(Color.black);
                grasol.fillRect(0, 0, (int)maxValueX, (int)maxValueY);
                DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge> solGraph = new DefaultUndirectedWeightedGraph<MetaCommunity, DefaultWeightedEdge>(DefaultWeightedEdge.class);
                ArrayList<MetaCommunity> nodi= new ArrayList<MetaCommunity>(0);

                while (scan2.hasNextLine()) {
                    String first = scan2.next();
                    String second = scan2.next();
                    scan2.next();
                    String third = scan3.next();
                    double x = Double.parseDouble(first);
                    double y = Double.parseDouble(second);
                    int c = Integer.parseInt(third);
                    MetaCommunity entrata =new MetaCommunity(c, new Entity(x, y));
                    nodi.add(entrata);
                    solGraph.addVertex(entrata);
                    grasol.setColor(colori[c*23 % colori.length]);
                    x = x *100;
                    y = y *100;
                    grasol.fillRoundRect((int)x,(int) y, 20, 20, 10, 10);
                }
                scan2.close();
                ImageIO.write(sol, "jpg", new File(path +"Matlab.jpg"));
            }
            catch(Exception e ){
                System.out.println(e.getMessage());
            }
        }
            
    }
}
