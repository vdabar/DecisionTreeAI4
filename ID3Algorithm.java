package com.company;
import java.util.Map.Entry;
import java.io.*;
import java.util.*;
/**
 * Created by Vytautas on 4/5/2016.
 */
public class ID3Algorithm {
    private String[] attributes;
    private int targetAttributeIndex = -1;
    private Set<String> targetAttributeValues = new HashSet<String>();
    String separator = ",";


    public DecisionTree runID3Algorithm(String input) throws IOException {
        DecisionTree tree = new DecisionTree();
        BufferedReader reader = new BufferedReader(new FileReader(input));
        String line = reader.readLine();
        attributes = line.split(separator);
        int[] remainingAttributes = new int[attributes.length - 1];
        int pos = 0;
        //sepperate target attribute and remaining attributes
        targetAttributeIndex = attributes.length-1;
        for (int i = 0; i < attributes.length-2; i++) {
                remainingAttributes[pos++] = i;
        }

        // add test examples to collection
        List<String[]> examples = new ArrayList<String[]>();
        while (((line = reader.readLine()) != null)) {
            String[] lineSplit = line.split(separator);
            examples.add(lineSplit);
            targetAttributeValues.add(lineSplit[targetAttributeIndex]); // collect target attribute possible values
        }
        reader.close();

        // create the tree
        tree.root = id3(remainingAttributes, examples);
        tree.attributes = attributes;

        return tree;
    }

    private Node id3(int[] remainingAttributes, List<String[]> examples) {


        //search attribue with highest gain
        int attributeWithHighestGain = 0;
        double highestGain = -99999;
        for (int attribute : remainingAttributes) {
            if(attribute !=0) {
                double gain = calculateGain(attribute, examples);
                if (gain >= highestGain) {
                    highestGain = gain;
                    attributeWithHighestGain = attribute;
                }
            }
        }

        // if the highest gain is 0 take the most frequent classes
        if (highestGain == 0) {
            ClassNode classNode = new ClassNode();
            int topFrequency = 0;
            String className = null;
            // Calculate target value frequency
            Map<String, Integer> targetValuesFrequencyCount = countAttributeValuesFrequency(examples, targetAttributeIndex);
            for(Entry<String, Integer> entry: targetValuesFrequencyCount.entrySet()) {
                if(entry.getValue() > topFrequency) {
                    topFrequency = entry.getValue();
                    className = entry.getKey();
                }
            }
            classNode.count = examples.size();
            classNode.className = className;
            return classNode;
        }

        DecisionNode decisionNode = new DecisionNode();
        decisionNode.attribute = attributeWithHighestGain;

        // Delete attribute with the highest gain
        int[] newRemainingAttribute = new int[remainingAttributes.length - 1];
        int pos = 0;
        for (int i = 0; i < remainingAttributes.length; i++) {
            if (remainingAttributes[i] != attributeWithHighestGain) {
                newRemainingAttribute[pos++] = remainingAttributes[i];
            }
        }

        // Split the dataset into partitions according to the selected attribute
        Map<String, List<String[]>> partitions = new HashMap<String, List<String[]>>();
        for (String[] example : examples) {
            String value = example[attributeWithHighestGain];
            List<String[]> listExamples = partitions.get(value);
            if (listExamples == null) {
                listExamples = new ArrayList<String[]>();
                partitions.put(value, listExamples);
            }
            listExamples.add(example);
        }

        // Initialize nodes and attributeValues for the subnodes
        decisionNode.nodes = new Node[partitions.size()];
        decisionNode.attributeValues = new String[partitions.size()];

        //calls id3 recursively for all branches
        int index = 0;
        for (Entry<String, List<String[]>> partition : partitions.entrySet()) {
            decisionNode.attributeValues[index] = partition.getKey();
            decisionNode.nodes[index] = id3(newRemainingAttribute,
                    partition.getValue()); // recursive call
            index++;
        }
        return decisionNode;
    }

    private double calculateGain(int attributePos, List<String[]> examples) {

        // Count the frequency of target attribute
        Map<String, Integer> targetValuesFrequencyCount = countAttributeValuesFrequency(examples, targetAttributeIndex);
        
        double globalEntropy = 0d;
        
        for (String value : targetAttributeValues) {
            Integer frequencyInt = targetValuesFrequencyCount.get(value);
            if(frequencyInt != null) {
                double frequencyRatio = frequencyInt / (double) examples.size();
                globalEntropy -= frequencyRatio * Math.log(frequencyRatio) / Math.log(2);
            }
        }
        // Count the frequency of each value for the attribute
        Map<String, Integer> valuesFrequencyCount = countAttributeValuesFrequency(
                examples, attributePos);

        // Calculate the gain
        double sum = 0;
        for (Entry<String, Integer> entry : valuesFrequencyCount.entrySet()) {
            sum += entry.getValue()/ ((double) examples.size())* calculateEntropy(examples, attributePos,entry.getKey());
        }
        return globalEntropy - sum;
    }


    private double calculateEntropy(List<String[]> Examples,
                                    int attributeIF, String valueIF) {

        // examplesCount calculates how many examples with valueIF found
        // valuesFrequency calculates examples with different targetValue
        // frequency calculates ratio betwen valuesFrequency values and examplesCount
        int examplesCount = 0;
        Map<String, Integer> valuesFrequency = new HashMap<String, Integer>();
        for (String[] example : Examples) {
            if (example[attributeIF].equals(valueIF)) {
                String targetValue = example[targetAttributeIndex];
                if (valuesFrequency.get(targetValue) == null) {
                    valuesFrequency.put(targetValue, 1);
                } else {
                    valuesFrequency.put(targetValue,
                            valuesFrequency.get(targetValue) + 1);
                }
                examplesCount++;
            }
        }
        // calculate entropy
        double entropy = 0;
        for (String value : targetAttributeValues) {
            Integer count = valuesFrequency.get(value);
            if (count != null) {
                double frequency = count / (double) examplesCount;
                entropy -= frequency * Math.log(frequency) / Math.log(2);
            }
        }
        return entropy;
    }

    private Map<String, Integer> countAttributeValuesFrequency(
            List<String[]> examples, int indexAttribute) {
        Map<String, Integer> targetValuesFrequency = new HashMap<String, Integer>(); // Key: a string indicating a value Value:  the frequency
        for (String[] example:examples) {
            String targetValue = example[indexAttribute];
            if (targetValuesFrequency.get(targetValue) == null) {
                targetValuesFrequency.put(targetValue, 1);
            } else {
                targetValuesFrequency.put(targetValue,
                        targetValuesFrequency.get(targetValue) + 1);
            }
        }
        return targetValuesFrequency;
    }
}
