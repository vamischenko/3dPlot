import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Matrix view class.
 * Allows to view matrix.
 */

public class MatrixView {
    private static final int DEFAULT_WIDTH = 640;
    private static final int DEFAULT_HEIGHT = 480;
    private static final int SIZE_PER_ELEMENT = 20;
    Stage stage;
    GridPane gpMatrix = new GridPane();

    public MatrixView(Window parent) {
        stage = new Stage(); //New stage (window)
        stage.initOwner(parent); //Parent - main window
        stage.initModality(Modality.WINDOW_MODAL); //Setting stage to modal
        initWindow();
    }

    public void viewMatrix(int[][] matrix) { //Show matrix method
        if (matrix == null) {
            return;
        }
        gpMatrix.getChildren().clear(); //Cleaning grid pane
        setupGridPane(matrix); //Setting up grid pane
        for (int y = 0; y < matrix.length; y++) { //Out to grid pane
            for (int x = 0; x < matrix[y].length; x++) {
                gpMatrix.add(new Label(String.valueOf(matrix[y][x])), x + 1, y + 1);
            }
        }
        stage.show();
    }

    private void setupGridPane(int[][] matrix) { //Setups grid pane's cells
        if (matrix == null) {
            return;
        }
        //Cleaning
        gpMatrix.getChildren().clear();
        gpMatrix.getColumnConstraints().clear();
        gpMatrix.getRowConstraints().clear();
        //By X
        for (int x = 0; x < matrix[0].length; x++) {
            Label label = new Label(String.valueOf(x + 1));
            label.setAlignment(Pos.CENTER);
            gpMatrix.add(label, x + 1, 0);
            //...
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth((1.0 / (matrix[0].length + 1)) * 100);
            columnConstraints.setHalignment(HPos.CENTER);
            gpMatrix.getColumnConstraints().add(columnConstraints);
        }
        //By Y
        for (int y = 0; y < matrix.length; y++) {
            Label label = new Label(String.valueOf(y + 1));
            label.setAlignment(Pos.CENTER);
            gpMatrix.add(label, 0, y + 1);
            //...
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight((1.0 / (matrix.length + 1)) * 100);
            rowConstraints.setValignment(VPos.CENTER);
            gpMatrix.getRowConstraints().add(rowConstraints);
        }
        //Last column
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHalignment(HPos.CENTER);
        columnConstraints.setPercentWidth((1.0 / (matrix[0].length + 1)) * 100);
        gpMatrix.getColumnConstraints().add(columnConstraints);
        //Last row
        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setValignment(VPos.CENTER);
        rowConstraints.setPercentHeight((1.0 / (matrix.length + 1)) * 100);
        gpMatrix.getRowConstraints().add(rowConstraints);
        //Grid lines
        gpMatrix.setGridLinesVisible(true);
    }

    public void setTitle(String title) {
        stage.setTitle(title);
    }

    public void setResizable(boolean resizable) {
        stage.setResizable(resizable);
    }

    private void initWindow() {
        BorderPane root = new BorderPane();
        root.setCenter(gpMatrix);
        gpMatrix.setAlignment(Pos.CENTER);
        stage.setScene(new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }
}
