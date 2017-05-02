import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Arrays;
import java.util.stream.Collectors;

/*
 * Allows to view array in text area.
 */

public class ArrayView {
    private static final int DEFAULT_WIDTH = 320;
    private static final int DEFAULT_HEIGHT = 240;
    Stage stage;
    TextArea taArray = new TextArea();

    public ArrayView(Window parent) {
        stage = new Stage();
        stage.initOwner(parent);
        stage.initModality(Modality.WINDOW_MODAL);
        initWindow();
    }

    public void viewArray(int[] array) {
        viewArray(array, ", ");
    }

    public void viewArray(int[] array, String delimiter) {
        String str = Arrays.stream(array)
                .boxed()
                .map(Object::toString)
                .collect(Collectors.joining(delimiter));
        taArray.setText("[" + str + "]");
        stage.show();
    }

    public void setTitle(String title) {
        stage.setTitle(title);
    }

    private void initWindow() {
        BorderPane borderPane = new BorderPane(taArray);
        stage.setScene(new Scene(borderPane, DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }
}
