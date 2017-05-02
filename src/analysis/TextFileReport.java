package analysis;

import java.io.*;
import java.util.Arrays;
import java.util.stream.Collectors;

/*
 * TextFileReport class.
 * Saves report to text file.
 * Process of creation report similar to CSVDataSource class.
 */

public class TextFileReport {
    DataAnalyzer analyzer;
    String report = "";

    public TextFileReport(DataAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    public boolean buildReport() {
        if (analyzer == null) {
            return false;
        }
        if (analyzer.getDataSource() == null) {
            return false;
        }
        if (analyzer.getDataSource().getData() == null) {
            return false;
        }
        if (analyzer.getAnalysis() == null) {
            return false;
        }
        report = "Source data: ";
        int[] source = analyzer.getDataSource().getData();
        report += Arrays.stream(source) //Source data out; Google "java streams api" or "java 8 streams"
                .boxed()
                .map(Object::toString)
                .collect(Collectors.joining("; "));
        report += ".\n";
        report += "Analysis:\n";
        int[][][] analysis = analyzer.getAnalysis();
        for (int element = 0; element < analysis.length; element++) { //Analysis out
            report += "---------------------\n";
            report += "Element ".concat(String.valueOf(source[element])).concat(":\n");
            for (int y = 0; y < analysis[element].length; y++) {
                for (int x = 0; x < analysis[element][y].length; x++) {
                    report += String.valueOf(analysis[element][y][x]).concat(" ");
                }
                report += "\n";
            }
        }
        return true;
    }

    public String getReport() {
        return report;
    }

    public boolean saveToFile(File file) {
        try (OutputStream outputStream = new FileOutputStream(file)) {
            String output = report.replace("\n", System.lineSeparator()); //System.lineSeparator() - line separator for current OS
            outputStream.write(output.getBytes());
            outputStream.flush();
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public boolean saveToFile(String filename) {
        return saveToFile(new File(filename));
    }
}
