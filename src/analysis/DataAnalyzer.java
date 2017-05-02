package analysis;

/**
 * Data analyzer class.
 * Reads data from DataSource and analyses only.
 */
public class DataAnalyzer {
    //Data source (interface)
    private DataSource dataSource;
    //Analysis
    private int[][][] analysis; //Resulting array
    //Progress
    private volatile double analysisProgress = 0.0;
    private volatile boolean analysingInProgress = false;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public int[][][] getAnalysis() {
        return analysis;
    }

    //Generates 3d-array with data analyze
    public boolean analyze() {
        synchronized (this) {
            analysingInProgress = true;
        }
        analysisProgress = 0.0;
        if (dataSource == null) { //If data source didn't set, we can't perform this
            synchronized (this) {
                analysingInProgress = false;
            }
            return false;
        }
        int[] sourceData = dataSource.getData(); //Getting array with data from data source
        if (sourceData == null) { //null check
            synchronized (this) {
                analysingInProgress = false;
            }
            return false;
        }
        analysis = new int[sourceData.length][][]; //Creating 3d-array; allocating only for first dimension (array of 2d-arrays);
        for (int i = 0; i < sourceData.length; i++) { //For each number in the data array
            int[][] numberAnalysis = analyzeNumber(i); //Getting analysis for i-th number only
            if (numberAnalysis == null) { //We can't perform analysis anymore, something has gone wrong...
                synchronized (this) {
                    analysingInProgress = false;
                }
                return false;
            }
            analysis[i] = numberAnalysis; //Ok, we have an analysis for i-th number
            synchronized (this) {
                analysisProgress = (double) (i + 1) / sourceData.length;
            }
        }
        synchronized (this) {
            analysingInProgress = false;
        }
        return true;
    }

    //Progress getter
    public double getAnalysisProgress() {
        synchronized (this) {
            return analysisProgress;
        }
    }

    public boolean isAnalysingInProgress() {
        synchronized (this) {
            return analysingInProgress;
        }
    }

    //Analyzes i-th number ONLY in the source data and returns data analyze in matrix
    private int[][] analyzeNumber(int i) {
        //External data source null check
        if (dataSource == null) {
            return null;
        }
        //Array data source
        int[] sourceData = dataSource.getData();
        if (sourceData == null) { //More null checks
            return null;
        }
        if (i >= sourceData.length) { //i check
            return null;
        }
        int[][] analysis = new int[sourceData.length][sourceData.length]; //Matrix for analysis
        for (int j = 0; j < sourceData.length; j++) { //First loop
            for (int k = 0; k < sourceData.length; k++) { //Second loop
                if (i != j && i != k && j != k) { //checking: i != j != k
                    // Corrected function
                    int xi = sourceData[i], xj = sourceData[j], xk = sourceData[k]; //Xi, Xj and Xk
                    int sgn = MathExt.sgn((xi - xk) * (xi - xk) - (xj - xk)  * (xj - xk)); //Signum function
                    // /Corrected function
                    if (sgn > 0) { //Checking signum func value
                        analysis[j][k] = 1;
                        analysis[k][j] = 0;
                    } else if (sgn < 0) {
                        analysis[k][j] = 1;
                        analysis[j][k] = 0;
                    } else {
                        analysis[j][k] = 1;
                        analysis[k][j] = 1;
                    }
                } else {
                    analysis[j][k] = 0;
                    analysis[k][j] = 0;
                }
            }
        }
        return analysis;
    }
}
