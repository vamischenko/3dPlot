package analysis;

import csv.CSV;

import java.io.*;

/*
 * CSV report creator.
 * Creates report to CSV object.
 */

public class CSVReport {
    DataAnalyzer dataAnalyzer;
    CSV csv;

    public CSVReport(DataAnalyzer dataAnalyzer) {
        this.dataAnalyzer = dataAnalyzer;
    }

    public boolean buildReport() {
        if (dataAnalyzer == null) {
            return false;
        }
        csv = new CSV();
        //Data source
        if (dataAnalyzer.getDataSource() != null) { //Adding source data if present
            if (dataAnalyzer.getDataSource().getData() != null) {
                csv.set(0, 0, "Source:");
                for (int i = 0; i < dataAnalyzer.getDataSource().getData().length; i++) {
                    csv.set(i + 1, 0, String.valueOf(dataAnalyzer.getDataSource().getData()[i]));
                }
            }
        }
        //Analysis
        if (dataAnalyzer.getAnalysis() != null) { //Adding analysis if present
            int analysisLength = dataAnalyzer.getDataSource().getData().length + 1;
            for (int element = 0; element < dataAnalyzer.getAnalysis().length; element++) { //Each source data element...
                int startY = analysisLength * element + 1; //Position in CSV-table
                csv.set(0, startY, "Element ".concat(String.valueOf(dataAnalyzer.getDataSource().getData()[element])).concat(":"));
                for (int y = 0; y < dataAnalyzer.getAnalysis()[element].length; y++) {
                    for (int x = 0; x < dataAnalyzer.getAnalysis()[element][y].length; x++) {
                        csv.set(x, y + startY + 1, String.valueOf(dataAnalyzer.getAnalysis()[element][y][x]));
                    }
                }
            }
        }
        return true;
    }

    public boolean saveToFile(File file) {
        return csv != null && csv.saveToFile(file);
    }

    public boolean saveToFile(String filename) {
        return csv != null && csv.saveToFile(filename);
    }
}
