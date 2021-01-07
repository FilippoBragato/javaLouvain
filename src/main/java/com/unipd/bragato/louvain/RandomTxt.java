package com.unipd.bragato.louvain;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class RandomTxt {
    public static void main(String[] args) {
        int n = 10000;
        String path = "./src/main/java/com/unipd/bragato/louvain/Data/G8/";
        String file = "G";
        File commTxt = new File(path + file +  ".txt");
        try {
            commTxt.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileWriter myWriter;
        Random rng = new Random();
        try {
            myWriter = new FileWriter(commTxt);
            for (int i = 0; i<n; i++) {
                if(i==n-1){
                    myWriter.write(rng.nextInt(100000000)+" "+rng.nextInt(100000000));
                }
                else{
                    myWriter.write(rng.nextInt(100000000)+" "+rng.nextInt(100000000)+"\n");
                }
            }
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
