package com.company;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Main {

    public static ArrayList<String> attrMap;
    public static void main(String[] args) throws IOException {
        ID3Algorithm id3 = new ID3Algorithm();
        DecisionTree tree1 = new DecisionTree();
        Scanner scanner = new Scanner(System.in);
        String input,output;
        System.out.println("Enter input file:");
        input=(scanner.nextLine());
        System.out.println("Enter utput file:");
        output=(scanner.nextLine());
        tree1 = id3.runID3Algorithm(input);

        BufferedReader reader = new BufferedReader(new FileReader(output));
        String line;
        List<String[]> examples = new ArrayList<String[]>();
        while (((line = reader.readLine()) != null)) {
            String[] lineSplit = line.split(",");
            examples.add(lineSplit);
        }
        reader.close();
        for (String[] s: examples){
            for (String a:s){System.out.print(a);}
            System.out.println(" "+tree1.predictTargetAttributeValue(s));
        }

        tree1.print(output);

    }


}
