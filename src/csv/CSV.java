package csv;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CSV {
    private List<List<String>> table = new ArrayList<>();
    private final char delimiter;
    private final char strDelimiter;

    public CSV(char delimiter, char strDelimiter) {
        this.delimiter = delimiter;
        this.strDelimiter = strDelimiter;
    }

    public CSV(char delimiter) {
        this(delimiter, '"');
    }

    public CSV() {
        this(';', '"');
    }

    public boolean saveToFile(String filename) {
        return saveToFile(new File(filename));
    }

    public boolean saveToFile(File file) {
        try (OutputStream outputStream = new FileOutputStream(file)) {
            String output = out().replace("\n", System.lineSeparator());
            outputStream.write(output.getBytes());
            outputStream.flush();
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public boolean openFromFile(String filename) {
        return openFromFile(new File(filename));
    }

    public boolean openFromFile(File file) {
        try (InputStream inputStream = new FileInputStream(file)) {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            String source = new String(bytes);
            read(source);
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public String get(int x, int y) {
        if (y >= table.size()) {
            return "";
        }
        if (x >= table.get(y).size()) {
            return "";
        }
        return table.get(y).get(x);
    }

    public boolean set(int x, int y, String value) {
        if (y >= table.size()) {
            int addSize = y - (table.size() - 1);
            for (int i = 0; i < addSize; i++) {
                table.add(new ArrayList<>());
            }
        }
        if (x >= table.get(y).size()) {
            int addSize = x - (table.get(y).size() - 1);
            for (int i = 0; i < addSize; i++) {
                table.get(y).add("");
            }
        }
        table.get(y).set(x, value);
        return true;
    }

    public void read(String source) { //Reading CSV-table from String object
        erase();
        Scanner scanner = new Scanner(source);
        while (scanner.hasNextLine()) { //One line - one table row
            //New row
            table.add(new ArrayList<>());
            int currentRow = table.size() - 1; //It's number
            //Row from string
            String row = scanner.nextLine();
            String cell = ""; //Cell
            boolean stringCell = false; //If ';' met
            for (int charNumber = 0; charNumber < row.length(); charNumber++) { //Row scanning
                char current = row.charAt(charNumber); //Current token
                if (current == strDelimiter) { // If String delimiter is met
                    stringCell = !stringCell; // Turning on String cell reading
                } else {
                    if (stringCell) { // String cell
                        cell += current;
                    } else { // no String cell
                        if (current == delimiter) { // Cell delimiter met, adding cell to table
                            table.get(currentRow).add(cell);
                            cell = "";
                        } else {
                            cell += current; // Adding char to cell string
                        }
                    }
                }
            }
            table.get(currentRow).add(cell);
        }
    }

    public String out() { //Saves table to string
        String csvOut = "";
        for (List<String> row : table) { //For each row...
            if (row.size() == 0) {
                csvOut = csvOut.concat(String.valueOf(delimiter)); //If row is empty
            }
            for (String cell : row) { //For each cell...
                if (cell.contains(String.valueOf(delimiter))) { //If cell contains cell delimiter...
                    csvOut = csvOut.concat(String.valueOf(strDelimiter)).concat(cell).concat(String.valueOf(strDelimiter));
                } else {
                    csvOut = csvOut.concat(cell); //If cell is OK
                }
                csvOut = csvOut.concat(String.valueOf(delimiter));
            }
            csvOut = csvOut.concat("\n"); //Row end;
        }
        return csvOut;
    }

    public void erase() {
        table = new ArrayList<>();
    }

    @Override
    public String toString() {
        String result = "";
        for (List<String> row : table) {
            for (String cell : row) {
                result = result.concat("|").concat(cell).concat("|\t");
            }
            result += ":\n";
        }
        return result;
    }
}
