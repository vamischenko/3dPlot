package analysis;

import csv.CSV;

import java.util.ArrayList;
import java.util.List;

/*
 * CSVDataSource class.
 * Provides data from CSV-table.
 * Constructor accepts CSV-object (CSV can also be setted later) and with parseRow() (parses specified row) or parseCol() (parses specified column)
 * parses data from CSV-table to source data array.
 */

public class CSVDataSource implements DataSource {
    private CSV csv;
    private int[] data; //Source data array

    public CSVDataSource() { //Constructor w/o accepting CSV
    }

    public CSVDataSource(CSV csv) {
        this.csv = csv;
    }

    public void setCsv(CSV csv) { //Sets CSV
        this.csv = csv;
    }

    public CSV getCsv() {
        return csv;
    }

    public boolean parseRow(int row, int startCol, int count) { //Row parsing method, accepts: row number, start column and count of numbers
        return parse(row, startCol, count, true);
    }

    public boolean parseCol(int col, int startRow, int count) { //Column parsing method, accepts: column number, start row and count of numbers
        return parse(startRow, col, count, false);
    }

    private boolean parse(int row, int col, int count, boolean parseByRow) { //Parsing method; if count will be <= 0 then numbers will be parsed automatically to first empty cell
        if (csv == null) {
            return false;
        }
        if (count > 0) { //If count specified
            int[] data = new int[count]; //source data array
            int startI = (parseByRow)?col:row; //if we'll parse by row we choosing start column and vice versa
            for (int i = startI; i < (startI + count); i++) { //Pasrsing...
                String cellValue = (parseByRow)?csv.get(i, row):csv.get(col, i); //Getting cell value depending on parse direction
                try {
                    data[i - startI] = Integer.parseInt(cellValue); //Trying to parse number from cell
                } catch (NumberFormatException e) {
                    return false; //Failed (maybe cell contains non-number chars). Data source won't be setted
                }
            }
            this.data = data;
        } else { //count NOT specified
            List<Integer> inputData = new ArrayList<>(); //Data list
            int currentCell = (parseByRow)?col:row; //Start column or row (depending on parse direction)
            String cellValue = "";
            do {
                cellValue = (parseByRow)?csv.get(currentCell, row):csv.get(col, currentCell); //Parsing cell
                currentCell++;
                if (!cellValue.contentEquals("")) { //If cell not empty
                    try {
                        int value = Integer.parseInt(cellValue); //Trysing to parse integer from cell
                        inputData.add(value); //If parsed ok then adding it to the list
                    } catch (NumberFormatException e) {
                        return false; //Failed. Data source won't be setted
                    }
                }
            } while (!cellValue.contentEquals("")); //Reading until empty cell met
            this.data = new int[inputData.size()]; //Source data array
            for (int i = 0; i < inputData.size(); i++) { //When toArray() can't be useful
                this.data[i] = inputData.get(i);
            }
        }
        return true;
    }

    @Override
    public int[] getData() { //Getter; implementation of DataSource
        return data;
    }
}
