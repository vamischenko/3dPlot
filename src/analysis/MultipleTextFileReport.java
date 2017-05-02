package analysis;

/*
 *
 * Multiple .txt report builder
 *
 */

import java.io.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MultipleTextFileReport {
    private DataAnalyzer dataAnalyzer;

    public MultipleTextFileReport(DataAnalyzer dataAnalyzer) {
        this.dataAnalyzer = dataAnalyzer;
    }

    public boolean saveReport(String textFilePrefix) {
        if (dataAnalyzer.getAnalysis() == null) {
            return false;
        }
        try {
            //Source
            OutputStream outputStream = new FileOutputStream(new File(textFilePrefix.concat("_source.txt")));
            byte[] buffer;
            buffer = Arrays.stream(dataAnalyzer.getDataSource().getData())
                    .boxed()
                    .map(String::valueOf)
                    .collect(Collectors.joining(";"))
                    .concat(".\n").getBytes();
            outputStream.write(buffer);
            outputStream.flush();
            outputStream.close();
            //Analysis
            for (int number = 0; number < dataAnalyzer.getAnalysis().length; number++) {
                String numberAnalysis = "Element "
                        .concat(String.valueOf(dataAnalyzer.getDataSource().getData()[number]))
                        .concat(":\n");
                for (int[] analysisRow : dataAnalyzer.getAnalysis()[number]) {
                    String stringAnalysisRow = Arrays.stream(analysisRow)
                            .boxed()
                            .map(String::valueOf)
                            .collect(Collectors.joining(" "))
                            .concat("\n");
                    numberAnalysis = numberAnalysis.concat(stringAnalysisRow);
                }
                String filename = textFilePrefix
                        .concat("Analysis").concat(String.valueOf(number))
                        .concat("_").concat(String.valueOf(dataAnalyzer.getDataSource().getData()[number]))
                        .concat(".txt");
                outputStream = new FileOutputStream(filename);
                outputStream.write(numberAnalysis.getBytes());
                outputStream.flush();
                outputStream.close();
            }
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
