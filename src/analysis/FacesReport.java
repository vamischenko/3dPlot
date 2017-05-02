package analysis;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FacesReport {
    private DataAnalyzer dataAnalyzer;

    public FacesReport(DataAnalyzer dataAnalyzer) {
        this.dataAnalyzer = dataAnalyzer;
    }

    public boolean saveReport(String filename, FaceConverter.FaceConverterProc proc) {
        if (dataAnalyzer == null) {
            return false;
        }
        if (dataAnalyzer.getAnalysis() == null) {
            return false;
        }
        int totalFaces = dataAnalyzer.getAnalysis().length;
        for (int faceNumber = 0; faceNumber < totalFaces; faceNumber++) {
            int[][] face = proc.faceProc(dataAnalyzer.getAnalysis(), faceNumber);
            String faceFilename = filename
                    .concat("_face")
                    .concat(String.valueOf(faceNumber));
            if (dataAnalyzer.getDataSource() != null && dataAnalyzer.getDataSource().getData() != null) {
                faceFilename = faceFilename.concat("_").concat(String.valueOf(dataAnalyzer.getDataSource().getData()[faceNumber]));
            }
            faceFilename = faceFilename.concat(".txt");
            try (OutputStream outputStream = new FileOutputStream(faceFilename)) {
                String output = "";
                if (dataAnalyzer.getDataSource() != null && dataAnalyzer.getDataSource().getData() != null) {
                    output = output
                            .concat("Element ")
                            .concat(String.valueOf(dataAnalyzer.getDataSource().getData()[faceNumber])).concat(":\n");
                }
                for (int[] row : face) {
                    for (int atom : row) {
                        output = output.concat(String.valueOf(atom)).concat(" ");
                    }
                    output = output.concat("\n");
                }
                output = output.replace("\n", System.lineSeparator());
                outputStream.write(output.getBytes());
                outputStream.flush();
            } catch (IOException e) {
                return false;
            }
        }
        return true;
    }
}
