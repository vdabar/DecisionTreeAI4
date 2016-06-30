package com.company;

import java.io.*;

/**
 * Created by Vytautas on 4/5/2016.
 */
public class DecisionTree {
    String []attributes;
    Node root = null;

    /**
     * Print the tree to System.out.
     */
    public void print(String output) throws IOException {
        String indent = " ";
        File file = new File(output);
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        writer.write("DECISION TREE");
        print(root, indent, "", writer);
        writer.flush();
        writer.close();
        System.out.println("Answer succesfully written to files");
    }

    private void print(Node nodeToPrint, String indent, String value, FileWriter writer) throws IOException {

        String temp = " ";
        if(value.isEmpty() == false)
            temp =(indent + value + ": ");
        String newIndent = indent + "  ";
        if(nodeToPrint instanceof ClassNode){
            ClassNode node = (ClassNode) nodeToPrint;
            temp += ( node.className + " (" + node.count + ")\n");
            writer.write(temp);
        }else{
            writer.write(temp+"\n");
            DecisionNode node = (DecisionNode) nodeToPrint;
            writer.write(newIndent + "<"+ attributes[node.attribute] + "> \n");

            newIndent = newIndent + "  ";
            for(int i=0; i< node.nodes.length; i++){
                print(node.nodes[i], newIndent, node.attributeValues[i],writer);
            }
        }

    }
    public String predictTargetAttributeValue(String[] newExample) {
        return predict(root, newExample);
    }

    private String predict(Node currentNode, String[] newExample) {
        if(currentNode instanceof ClassNode){
            ClassNode node = (ClassNode) currentNode;
            return node.className;
        }else{
            DecisionNode node = (DecisionNode) currentNode;
            String value = newExample[node.attribute];
            for(int i=0; i< node.attributeValues.length; i++){
                if(node.attributeValues[i].equals(value)){
                    return predict(node.nodes[i], newExample);
                }
            }
        }
        return null;
    }




}