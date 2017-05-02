package analysis;
/*
Converts 3-dimensional ANALYSIS arrays to 2-dimensional arrays.
 */

public class FaceConverter {
    /*
    Analysis 3-dimensional array "structure":
        int[k(number_analysis)][i][j]:
        Projections:
            k -> y --NUMBER IN SOURCE ARRAY
            i -> x
            j -> z
        --->
        int[y][x][z]
    */
    public static int[][] faceByY(int[][][] analysis, int faceNumber) { // by layer, analysis for ONE SOURCE NUMBER, xOz plane
        if (analysis.length > faceNumber) {
            return analysis[faceNumber];
        } else {
            return null;
        }
    }

    public static int[][] faceByZ(int[][][] analysis, int faceNumber) { //xOy plane, axis [z]
        int[][] xOy = new int[analysis.length][analysis.length];
        for (int j = 0; j < xOy.length; j++) {
            for (int i = 0; i < xOy[j].length; i++) {
                xOy[j][i] = analysis[j][i][faceNumber];
            }
        }
        return xOy;
    }

    public static int[][] faceByX(int[][][] analysis, int faceNumber) { //yOz plane axis[x]
        int[][] yOz = new int[analysis.length][analysis.length];
        for (int j = 0; j < yOz.length; j++) {
            for (int k = 0; k < yOz[j].length; k++) {
                yOz[j][k] = analysis[j][faceNumber][k];
            }
        }
        return yOz;
    }

    public interface FaceConverterProc {
        int[][] faceProc(int[][][] analysis, int faceNumber);
    }
}
