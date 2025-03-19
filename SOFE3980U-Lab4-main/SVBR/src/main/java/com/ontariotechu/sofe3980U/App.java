package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import java.util.Arrays;
import com.opencsv.*;

public class App {
    public static void main(String[] args) {
        // List of model CSV files to evaluate
        String[] csvFiles = {"model_1.csv", "model_2.csv", "model_3.csv"};
        
        // Tracking best values for each metric
        double lowestBCE = Double.MAX_VALUE, highestAUC = 0, highestAccuracy = 0;
        double highestPrecision = 0, highestRecall = 0, highestF1 = 0;
        String bestBCEModel = "", bestAUCModel = "", bestAccuracyModel = "";
        String bestPrecisionModel = "", bestRecallModel = "", bestF1Model = "";
        
        // Iterate over each CSV file and evaluate model performance
        for (String fileName : csvFiles) {
            List<String[]> dataset = readCSVFile(fileName);
            if (dataset == null) continue;
            
            // Compute all evaluation metrics
            double bceScore = computeBCE(dataset);
            int[][] confMatrix = getConfusionMatrix(dataset, 0.5);
            double acc = computeAccuracy(confMatrix);
            double prec = computePrecision(confMatrix);
            double rec = computeRecall(confMatrix);
            double f1 = computeF1Score(prec, rec);
            double aucScore = computeAUC(dataset);
            
            // Display the evaluation results for the current model
            System.out.println("Results for " + fileName);
            System.out.printf("BCE = %.7f\n", bceScore);
            System.out.println("Confusion Matrix");
            System.out.println("\ty=1\ty=0");
            System.out.println("y^=1\t" + confMatrix[1][1] + "\t" + confMatrix[1][0]);
            System.out.println("y^=0\t" + confMatrix[0][1] + "\t" + confMatrix[0][0]);
            System.out.printf("Accuracy = %.4f\n", acc);
            System.out.printf("Precision = %.7f\n", prec);
            System.out.printf("Recall = %.7f\n", rec);
            System.out.printf("F1 Score = %.7f\n", f1);
            System.out.printf("AUC ROC = %.7f\n\n", aucScore);
            
            // Update best models based on metric values
            if (bceScore < lowestBCE) { lowestBCE = bceScore; bestBCEModel = fileName; }
            if (aucScore > highestAUC) { highestAUC = aucScore; bestAUCModel = fileName; }
            if (acc > highestAccuracy) { highestAccuracy = acc; bestAccuracyModel = fileName; }
            if (prec > highestPrecision) { highestPrecision = prec; bestPrecisionModel = fileName; }
            if (rec > highestRecall) { highestRecall = rec; bestRecallModel = fileName; }
            if (f1 > highestF1) { highestF1 = f1; bestF1Model = fileName; }
        }
        
        // Print out best models for each metric
        System.out.println("According to BCE, the best model is " + bestBCEModel);
        System.out.println("According to Accuracy, the best model is " + bestAccuracyModel);
        System.out.println("According to Precision, the best model is " + bestPrecisionModel);
        System.out.println("According to Recall, the best model is " + bestRecallModel);
        System.out.println("According to F1 Score, the best model is " + bestF1Model);
        System.out.println("According to AUC ROC, the best model is " + bestAUCModel);
    }

    // Reads a CSV file and returns the data as a List of String arrays
    private static List<String[]> readCSVFile(String fileName) {
        try {
            FileReader fileReader = new FileReader(fileName);
            CSVReader csvReader = new CSVReaderBuilder(fileReader).withSkipLines(1).build();
            return csvReader.readAll();
        } catch (Exception e) {
            System.out.println("Error reading file: " + fileName);
            return null;
        }
    }

    // Computes Binary Cross-Entropy (BCE) loss
    private static double computeBCE(List<String[]> dataset) {
        double totalLoss = 0;
        for (String[] row : dataset) {
            int actual = Integer.parseInt(row[0]);
            double predicted = Double.parseDouble(row[1]);
            totalLoss += actual * Math.log(predicted) + (1 - actual) * Math.log(1 - predicted);
        }
        return -totalLoss / dataset.size();
    }

    // Computes confusion matrix for a given threshold
    private static int[][] getConfusionMatrix(List<String[]> dataset, double threshold) {
        int[][] matrix = new int[2][2];
        for (String[] row : dataset) {
            int actual = Integer.parseInt(row[0]);
            int predicted = Double.parseDouble(row[1]) >= threshold ? 1 : 0;
            matrix[predicted][actual]++;
        }
        return matrix;
    }

    private static double computeAccuracy(int[][] matrix) {
        return (double) (matrix[1][1] + matrix[0][0]) / Arrays.stream(matrix).flatMapToInt(Arrays::stream).sum();
    }

    private static double computePrecision(int[][] matrix) {
        return (double) matrix[1][1] / (matrix[1][1] + matrix[1][0]);
    }

    private static double computeRecall(int[][] matrix) {
        return (double) matrix[1][1] / (matrix[1][1] + matrix[0][1]);
    }

    private static double computeF1Score(double precision, double recall) {
        return 2 * (precision * recall) / (precision + recall);
    }

    private static double computeAUC(List<String[]> dataset) {
        double[] xVals = new double[101], yVals = new double[101];
        int posCount = (int) dataset.stream().filter(row -> row[0].equals("1")).count();
        int negCount = dataset.size() - posCount;
        
        for (int i = 0; i <= 100; i++) {
            double thresh = i / 100.0;
            int[][] matrix = getConfusionMatrix(dataset, thresh);
            yVals[i] = (double) matrix[1][1] / posCount;
            xVals[i] = (double) matrix[1][0] / negCount;
        }
        
        double aucValue = 0;
        for (int i = 1; i <= 100; i++) {
            aucValue += (yVals[i - 1] + yVals[i]) * Math.abs(xVals[i - 1] - xVals[i]) / 2;
        }
        return aucValue;
    }
}
