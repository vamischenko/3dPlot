package analysis;

public class MathExt { //Signum function; google "sgn" for more info.
    public static int sgn(int x) {
        if (x < 0) {
            return -1;
        }
        if (x > 0) {
            return 1;
        }
        return 0;
    }

    public static long sgn(long x) {
        if (x < 0L) {
            return -1;
        }
        if (x > 0L) {
            return 1;
        }
        return 0L;
    }

    public static double sgn(double x) {
        if (x < 0.0D) {
            return -1.0D;
        }
        if (x > 0.0D) {
            return 1.0D;
        }
        return 0.0D;
    }

    public static float sgn(float x) {
        if (x < 0.0F) {
            return -1.0F;
        }
        if (x > 0.0F) {
            return 1.0F;
        }
        return 0.0F;
    }
}
