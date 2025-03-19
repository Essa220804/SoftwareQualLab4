package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.*;

/**
 * Program to evaluate regression models using MSE, MAE, and MARE.
 */
public class App {
    public static void main(String[] args) {
        // List of CSV files to process
        String[] modelFiles = {"model_1.csv", "model_2.csv", "model_3.csv"};
        
        // Variables to track the best models for each metric
        double lowestMSE = Double.MAX_VALUE, lowestMAE = Double.MAX_VALUE, lowestMARE = Double.MAX_VALUE;
        String optimalMSEModel = "", optimalMAEModel = "", optimalMAREModel = "";
        
        // Iterate through each model file and compute metrics
        for (String fileName : modelFiles) {
            double totalMSE = 0, totalMAE = 0, totalMARE = 0;
            int totalCount = 0;
            
            try {
                // Open and read the CSV file
                FileReader fileReader = new FileReader(fileName);
                CSVReader csvReader = new CSVReaderBuilder(fileReader).withSkipLines(1).build();
                List<String[]> dataRows = csvReader.readAll();
                csvReader.close();
                
                // Process each row in the dataset
                for (String[] data : dataRows) {
                    double actualValue = Double.parseDouble(data[0]);
                    double predictedValue = Double.parseDouble(data[1]);
                    
                    // Calculate errors
                    double difference = actualValue - predictedValue;
                    totalMSE += difference * difference; // Squared error
                    totalMAE += Math.abs(difference); // Absolute error
                    totalMARE += Math.abs(difference) / (Math.abs(actualValue) + 1e-10); // Relative error
                    totalCount++;
                }
                
                // Compute average error metrics
                totalMSE /= totalCount;
                totalMAE /= totalCount;
                totalMARE = (totalMARE / totalCount) * 100; // Convert to percentage
                
                // Display computed metrics
                System.out.println("Results for " + fileName);
                System.out.println("    MSE = " + totalMSE);
                System.out.println("    MAE = " + totalMAE);
                System.out.println("    MARE = " + totalMARE);
                
                // Update best model for each metric
                if (totalMSE < lowestMSE) {
                    lowestMSE = totalMSE;
                    optimalMSEModel = fileName;
                }
                if (totalMAE < lowestMAE) {
                    lowestMAE = totalMAE;
                    optimalMAEModel = fileName;
                }
                if (totalMARE < lowestMARE) {
                    lowestMARE = totalMARE;
                    optimalMAREModel = fileName;
                }
                
            } catch (Exception e) {
                System.out.println("Error reading file: " + fileName);
            }
        }
        
        // Output the best model selection results
        System.out.println("\nBest model based on MSE: " + optimalMSEModel);
        System.out.println("Best model based on MAE: " + optimalMAEModel);
        System.out.println("Best model based on MARE: " + optimalMAREModel);
    }
}
