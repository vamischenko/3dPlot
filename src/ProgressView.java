import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ProgressView {
    private DoubleProperty progressProperty = new SimpleDoubleProperty(0.0);
    private Stage stage = new Stage();
    private ProgressBar progressBar = new ProgressBar();
    private Label progressTitle = new Label("Progress:");

    public ProgressView(Window owner) {
        this(owner, true);
    }

    public ProgressView(Window owner, boolean progressBarPresent) {
        this(owner, 350.0, 75.0, progressBarPresent);
    }

    public ProgressView(Window owner, double width, double height, boolean progressBarPresent) {
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);
        progressBar.progressProperty().bind(progressProperty);
        initWindow(progressBarPresent);
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setResizable(false);
        progressBar.setMinWidth(width / 2.0);
        progressBar.setPrefWidth(width * 0.75);
        progressBar.setMaxWidth(width);
    }

    public double getProgressProperty() {
        return progressProperty.get();
    }

    public DoubleProperty progressPropertyProperty() {
        return progressProperty;
    }

    public void setProgressProperty(double progressProperty) {
        this.progressProperty.set(progressProperty);
    }

    public void setTitle(String title) {
        stage.setTitle(title);
    }

    public void setProgressTitle(String title) {
        progressTitle.setText(title);
    }

    public void show() {
        stage.show();
    }

    public void close() {
        stage.close();
    }

    private void initWindow(boolean withProgressBar) {
        VBox vBox;
        if (withProgressBar) {
            vBox = new VBox(progressTitle, progressBar);
        } else {
            vBox = new VBox(progressTitle);
        }
        vBox.setPadding(new Insets(5.0));
        vBox.setSpacing(5.0);
        stage.setScene(new Scene(vBox));
    }
}
