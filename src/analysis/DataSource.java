package analysis;

/*
 * DataSource interface.
 *
 * Implementations of this interface must provide (for analyser class) array with the source data.
 *
 */

public interface DataSource {
    int[] getData();
}
