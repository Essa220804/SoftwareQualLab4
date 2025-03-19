package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.*;
import java.util.Arrays;

/**
 * Evaluate Multiclass Classification Model
 */
public class App {
    public static void main(String[] args) {
        String filePath = "model.csv";
        List<String[]> allData;

        // Read CSV file
        try {
            FileReader filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            allData = csvReader.readAll();
        } catch (Exception e) {
            System.out.println("Error reading the CSV file");
            return;
        }

        int numClasses = 5; // Assume 5 classes in dataset
        int[][] confusionMatrix = new int[numClasses][numClasses];
        double totalCE = 0;
        int totalSamples = allData.size();

        // Process each row in the dataset
        for (String[] row : allData) {
            int actualLabel = Integer.parseInt(row[0]) - 1;
            double[] predictedProbs = new double[numClasses];
            
            // Read predicted probabilities
            for (int i = 0; i < numClasses; i++) {
                predictedProbs[i] = Double.parseDouble(row[i + 1]);
            }
            
            // Compute Cross-Entropy loss
            totalCE += Math.log(predictedProbs[actualLabel]);
            
            // Determine predicted class (argmax)
            int predictedLabel = argmax(predictedProbs);
            
            // Update confusion matrix
            confusionMatrix[predictedLabel][actualLabel]++;
        }

        // Compute final CE
        double crossEntropy = -totalCE / totalSamples;
        
        // Print results
        System.out.printf("CE = %.7f\n", crossEntropy);
        System.out.println("Confusion Matrix");
        System.out.println("\ty=1\ty=2\ty=3\ty=4\ty=5");
        for (int i = 0; i < numClasses; i++) {
            System.out.print("y^=" + (i + 1) + "\t");
            for (int j = 0; j < numClasses; j++) {
                System.out.print(confusionMatrix[i][j] + "\t");
            }
            System.out.println();
        }
    }

    // Function to find index of maximum value
    private static int argmax(double[] array) {
        int maxIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }
}
