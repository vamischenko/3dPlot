package analysis;

import java.util.Random;

/*
 * RandomDataSource class.
 * Implements RANDOM data source.
 * It generates array with random numbers.
 */

public class RandomDataSource implements DataSource {
    private int[] data;

    public void generate(int length, int max) { //Generation method
        data = new int[length]; //Array for data
        Random random = new Random(); //Random object
        for (int i = 0; i < length; i++) {
            data[i] = random.nextInt(max); //...generation...
        }
    }

    @Override
    public int[] getData() { //Getter; implementation of DataSource
        return data;
    }
}
