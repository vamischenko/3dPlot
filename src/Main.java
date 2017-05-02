import analysis.*;
import csv.CSV;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main class.
 */

public class Main extends Application {
    DataAnalyzer analyzer = new DataAnalyzer();
    Plot3D plot3D = new Plot3D();
    Scene3D scene3D = new Scene3D(plot3D.getGroup3d());

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //ALL CODE WHICH LOCATED HERE MOSTLY RELATES TO UI. GOOGLE "JAVAFX" FOR MORE INFO.

        //Root pane
        BorderPane rootPane = new BorderPane();
        Scene scene = new Scene(rootPane, 800, 600);
        primaryStage.setScene(scene);

        //Side accordion
        Accordion accordion = new Accordion();
        rootPane.setRight(accordion);

        //Data source pane
        //Generation
        TextField tfCount = new TextField();
        TextField tfMaxValue = new TextField();
        Button btnGenerate = new Button("Generate data");

        //CSV
        TextField tfFileName = new TextField(); //Filename
        TextField tfRow = new TextField("0"); //Start row
        TextField tfCol = new TextField("0"); //Start column
        TextField tfCSVCount = new TextField(""); //Elements count; if not specified will be read to first empty cell

        RadioButton rbByRow = new RadioButton("Read by ROW"); //Reading by row...
        RadioButton rbByCol = new RadioButton("Read by COL"); //or by column
        ToggleGroup tgCSVOrientation = new ToggleGroup(); //RadioButtons' toggle group
        rbByRow.setToggleGroup(tgCSVOrientation); //...setting it...
        rbByCol.setToggleGroup(tgCSVOrientation); //...
        rbByRow.setSelected(true); //Default - by row

        Button btnReadFromCSV = new Button("Read from CSV");

        //View source data
        Button btnViewSourceData = new Button("View source data");

        VBox vBoxGeneration = new VBox( //Data source's VBox
                new Label("Generate data"),
                new Label("Count"),
                tfCount,
                new Label("Max value"),
                tfMaxValue,
                btnGenerate,
                new Separator(Orientation.HORIZONTAL),
                new Label("Read from CSV"),
                new Label("File name"),
                tfFileName,
                new Label("Col"),
                tfCol,
                new Label("Row"),
                tfRow,
                new Label("Count"),
                tfCSVCount,
                new Label("Parse orientation"),
                rbByCol,
                rbByRow,
                btnReadFromCSV,
                new Separator(Orientation.HORIZONTAL),
                btnViewSourceData
        );
        vBoxGeneration.setSpacing(3.0);
        accordion.getPanes().add(new TitledPane("Data source", vBoxGeneration));

        //GENERATE button
        btnGenerate.setOnAction(event -> {
            if (!(textFieldCheckNumeral(tfCount) && textFieldCheckNumeral(tfMaxValue))) {
                return;
            }
            int n = Integer.parseInt(tfCount.getText()); //Numbers count
            int max = Integer.parseInt(tfMaxValue.getText()); //Max number value
            RandomDataSource dataSource = new RandomDataSource();
            dataSource.generate(n, max);
            analyzer.setDataSource(dataSource);
        });

        //READ FROM CSV button
        btnReadFromCSV.setOnAction(event -> {
            //CSV file
            CSV csv = new CSV();
            if (!textFieldEmptyCheck(tfFileName)) {
                return;
            }
            if (!csv.openFromFile(tfFileName.getText())) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "File open error!");
                alert.showAndWait();
                return;
            }
            //...
            if (!(textFieldCheckNumeral(tfCol) && textFieldCheckNumeral(tfRow))) {
                return;
            }
            int col = Integer.parseInt(tfCol.getText());
            int row = Integer.parseInt(tfRow.getText());
            int count;
            if (tfCount.getText().length() == 0 && col == 0 && row == 0) {
                count = -1;
            } else {
                if (!textFieldCheckNumeral(tfCount)) {
                    return;
                }
                count = Integer.parseInt(tfCSVCount.getText());
            }
            //CSV data source
            CSVDataSource csvDataSource = new CSVDataSource(csv);
            //Direction - parse by row
            if (rbByRow.isSelected()) {
                if (!csvDataSource.parseRow(row, col, count)) {
                    return;
                }
            }
            //Direction - parse by column
            if (rbByCol.isSelected()) {
                if (!csvDataSource.parseCol(col, row, count)) {
                    return;
                }
            }
            if (csvDataSource.getData() != null) {
                analyzer.setDataSource(csvDataSource);
                tfCSVCount.setText(String.valueOf(csvDataSource.getData().length));
            }
        });

        //VIEW SOURCE DATA button
        btnViewSourceData.setOnAction(event -> {
            DataSource dataSource = analyzer.getDataSource();
            if (dataSource == null) {
                return;
            }
            if (dataSource.getData() == null) {
                return;
            }
            ArrayView sourceDataView = new ArrayView(primaryStage);
            sourceDataView.setTitle("Input data");
            sourceDataView.viewArray(dataSource.getData());
        });

        //Analysis
        VBox vBoxAnalysis = new VBox(5.0);
        //Analyse
        Button btnAnalyse = new Button("Analyse");
        Button btnBuildPlot = new Button("Build plot");
        //Face
        //Number
        TextField tfFaceNumber = new TextField();
        //Face plane
        ToggleGroup facePlaneToggleGroup = new ToggleGroup();
        RadioButton layerRadioButton = new RadioButton("Layer (Y-axis, plane xOz)");
        layerRadioButton.setToggleGroup(facePlaneToggleGroup);
        RadioButton xAxisRadioButton = new RadioButton("X-axis (plane yOz)");
        xAxisRadioButton.setToggleGroup(facePlaneToggleGroup);
        RadioButton zAxisRadioButton = new RadioButton("Z-axis (plane xOy)");
        zAxisRadioButton.setToggleGroup(facePlaneToggleGroup);
        layerRadioButton.setSelected(true);
        //Face view
        Button btnFaceView = new Button("View face");
        //Face build
        Button btnBuildFace = new Button("Build face");

        vBoxAnalysis.getChildren().addAll(
                new Label("Analyse and build plot"),
                btnAnalyse,
                btnBuildPlot,
                new Separator(Orientation.HORIZONTAL),
                new Label("View face"),
                new Label("Axis"),
                layerRadioButton,
                xAxisRadioButton,
                zAxisRadioButton,
                new Label("Item number"),
                tfFaceNumber,
                btnFaceView,
                btnBuildFace
        );
        accordion.getPanes().add(new TitledPane("Analysis and plot", vBoxAnalysis));

        //ANALYSE button
        btnAnalyse.setOnAction(event -> {
            //Source data check
            if (analyzer.getDataSource() == null || analyzer.getDataSource().getData() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Generate source data!");
                alert.setHeaderText("No source data!");
                alert.showAndWait();
                return;
            }
            //JavaFX thread for analysis; JavaFX thread because we need to use JavaFX features
            //JavaFX features don't allowed (the just don't work) in simple non-javafx threads
            //Progress view
            ProgressView progressView = new ProgressView(primaryStage);
            progressView.setProgressTitle("Analysing in progress...");
            progressView.show();
            //Analysis
            new Thread(() -> {
                analyzer.analyze();
                Platform.runLater(progressView::close);
            }).start();
            //Progress indication
            new Thread(() -> {
                while (analyzer.isAnalysingInProgress()) {
                    progressView.setProgressProperty(analyzer.getAnalysisProgress());
                }
            }).start();
        });

        //BUILD PLOT button
        btnBuildPlot.setOnAction(event -> {
            if (analyzer.getAnalysis() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Nothing to build!");
                alert.setHeaderText("Buld error!");
                alert.showAndWait();
                return;
            }
            ProgressView progressView = new ProgressView(primaryStage, false);
            progressView.setTitle("Building...");
            progressView.setProgressTitle("Building plot, please wait...");
            progressView.show();
            Platform.runLater(() -> {
                plot3D.build(analyzer.getAnalysis());
                progressView.close();
            });
        });

        //FACE VIEW button
        btnFaceView.setOnAction(event -> {
            if (analyzer.getAnalysis() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Nothing to view!");
                alert.setHeaderText("Face view error!");
                alert.showAndWait();
                return;
            }
            if (!textFieldCheckNumeral(tfFaceNumber)) {
                return;
            }
            int faceIndex = Integer.parseInt(tfFaceNumber.getText());
            if (faceIndex >= analyzer.getDataSource().getData().length) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "No such face!");
                alert.setHeaderText("Face view error!");
                alert.showAndWait();
                return;
            }
            MatrixView faceView = new MatrixView(primaryStage);
            faceView.setTitle("Element: ".concat(String.valueOf(analyzer.getDataSource().getData()[faceIndex])));
            int[][] face = null; //Here will be face to show
            //Selection face from analysis
            if (layerRadioButton.isSelected()) {
                face = FaceConverter.faceByY(analyzer.getAnalysis(), faceIndex);
            }
            if (xAxisRadioButton.isSelected()) {
                face = FaceConverter.faceByX(analyzer.getAnalysis(), faceIndex);
            }
            if (zAxisRadioButton.isSelected()) {
                face = FaceConverter.faceByZ(analyzer.getAnalysis(), faceIndex);
            }
            if (face == null) {
                return;
            }
            //Showing
            faceView.viewMatrix(face);
        });

        //BUILD FACE button
        btnBuildFace.setOnAction(event -> {
            if (analyzer.getAnalysis() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Nothing to build!");
                alert.setHeaderText("Face build error!");
                alert.showAndWait();
                return;
            }
            int selected = 0;
            try {
                selected = Integer.parseInt(tfFaceNumber.getText());
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Wrong number format!");
                alert.setHeaderText("Face build error!");
                alert.showAndWait();
                return;
            }
            if (selected >= analyzer.getAnalysis().length) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "No such face!");
                alert.setHeaderText("Face build error!");
                alert.showAndWait();
                return;
            }
            if (layerRadioButton.isSelected()) {
                plot3D.build(analyzer.getAnalysis(), selected, -1, -1);
            }
            if (xAxisRadioButton.isSelected()) {
                plot3D.build(analyzer.getAnalysis(), -1, selected, -1);
            }
            if (zAxisRadioButton.isSelected()) {
                plot3D.build(analyzer.getAnalysis(), -1, -1, selected);
            }
        });

        //Report save
        VBox vBoxReportSave = new VBox(5.0);
        //File
        TextField tfReportFilename = new TextField();
        //Save to text file
        Button btnSaveReportToTextFile = new Button("Save to TXT");
        //Save to CSV file
        Button btnSaveReportToCSV = new Button("Save to CSV");
        //Save to multiple text files
        Button btnSaveReportToMultipleTxt = new Button("Save to multiple TXT");
        //Save faces
        Button btnSaveYaxis = new Button("Save faces by Y (layers)");
        Button btnSaveXaxis = new Button("Save faces by X");
        Button btnSaveZaxis = new Button("Save faces by Z");

        vBoxReportSave.getChildren().addAll(
                new Label("File name"),
                tfReportFilename,
                new Separator(Orientation.HORIZONTAL),
                btnSaveReportToTextFile,
                new Separator(Orientation.HORIZONTAL),
                btnSaveReportToCSV,
                new Separator(Orientation.HORIZONTAL),
                btnSaveReportToMultipleTxt,
                new Separator(Orientation.HORIZONTAL),
                btnSaveYaxis,
                btnSaveXaxis,
                btnSaveZaxis
        );

        accordion.getPanes().add(new TitledPane("Report", vBoxReportSave));

        //SAVE REPORT TO TEXT FILE button
        btnSaveReportToTextFile.setOnAction(event -> {
            if (!textFieldEmptyCheck(tfReportFilename)) {
                return;
            }
            TextFileReport textFileReport = new TextFileReport(analyzer);
            if (!textFileReport.buildReport()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Report wasn't build. Maybe, you forgot to analyse?");
                alert.setHeaderText("Text report generation error!");
                alert.showAndWait();
                return;
            }
            textFileReport.saveToFile(tfReportFilename.getText().concat(".txt"));
        });

        //SAVE REPORT TO CSV button
        btnSaveReportToCSV.setOnAction(event -> {
            if (!textFieldEmptyCheck(tfReportFilename)) {
                return;
            }
            CSVReport csvReport = new CSVReport(analyzer);
            if (!csvReport.buildReport()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Report wasn't build. Maybe, you forgot to analyse?");
                alert.setHeaderText("CSV Report generation error!");
                alert.showAndWait();
                return;
            }
            csvReport.saveToFile(tfReportFilename.getText().concat(".csv"));
        });

        //SAVE REPORT TO MULTIPLE TXT button
        btnSaveReportToMultipleTxt.setOnAction(event -> {
            if (!textFieldEmptyCheck(tfReportFilename)) {
                return;
            }
            MultipleTextFileReport multipleTextFileReport = new MultipleTextFileReport(analyzer);
            if (!multipleTextFileReport.saveReport(tfReportFilename.getText())) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Report wasn't build. Maybe, you forgot to analyse?");
                alert.setHeaderText("Multiple Text File Report generation error!");
                alert.showAndWait();
            }
        });

        //SAVE X-AXIS faces
        btnSaveXaxis.setOnAction(event -> {
            if (!textFieldEmptyCheck(tfReportFilename)) {
                return;
            }
            FacesReport facesReport = new FacesReport(analyzer);
            if (!facesReport.saveReport(tfReportFilename.getText().concat("_x"), FaceConverter::faceByX)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Report wasn't build. Maybe, you forgot to analyse?");
                alert.setHeaderText("X-axis report generation error!");
                alert.showAndWait();
            }
        });

        //SAVE Y-AXIS faces
        btnSaveYaxis.setOnAction(event -> {
            if (!textFieldEmptyCheck(tfReportFilename)) {
                return;
            }
            FacesReport facesReport = new FacesReport(analyzer);
            if (!facesReport.saveReport(tfReportFilename.getText().concat("_y"), FaceConverter::faceByY)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Report wasn't build. Maybe, you forgot to analyse?");
                alert.setHeaderText("Y-axis report generation error!");
                alert.showAndWait();
            }
        });

        //SAVE Z-AXIS faces
        btnSaveZaxis.setOnAction(event -> {
            if (!textFieldEmptyCheck(tfReportFilename)) {
                return;
            }
            FacesReport facesReport = new FacesReport(analyzer);
            if (!facesReport.saveReport(tfReportFilename.getText().concat("_z"), FaceConverter::faceByZ)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Report wasn't build. Maybe, you forgot to analyse?");
                alert.setHeaderText("Z-axis report generation error!");
                alert.showAndWait();
            }
        });

        //View settings controls
        Slider cameraDistance = new Slider(-500.0, 500.0, 0.0);
        scene3D.getCameraDistance().zProperty().bind(cameraDistance.valueProperty());

        Slider zRotation = new Slider(-180.0, 180.0, 0.0);
        scene3D.getRz().angleProperty().bind(zRotation.valueProperty());

        Slider xRotation = new Slider(-180.0, 180.0, 0.0);
        scene3D.getRx().angleProperty().bind(xRotation.valueProperty());

        Slider yRotation = new Slider(-180.0, 180.0, 0.0);
        scene3D.getRy().angleProperty().bind(yRotation.valueProperty());

        //To the plot's top
        Button btnToTop = new Button("Plot top");
        //Reset rotations
        Button btnResetRotations = new Button("Reset rotations");

        VBox vBoxView = new VBox();
        vBoxView.getChildren().addAll(
                new Label("Camera"),
                new Label("Distance by Z"),
                cameraDistance,
                new Separator(Orientation.HORIZONTAL),
                new Label("Scene rotation"),
                new Label("By Z:"),
                zRotation,
                new Label("By X:"),
                xRotation,
                new Label("By Y:"),
                yRotation,
                btnToTop,
                btnResetRotations
        );
        vBoxView.setSpacing(5.0);
        accordion.getPanes().add(new TitledPane("View", vBoxView));

        btnToTop.setOnAction(event -> {
            zRotation.setValue(0.0);
            xRotation.setValue(90.0);
            yRotation.setValue(90.0);
        });

        btnResetRotations.setOnAction(event -> {
            zRotation.setValue(0.0);
            xRotation.setValue(0.0);
            yRotation.setValue(0.0);
        });

        //SubScene...
        Pane subScenePane = new Pane(scene3D.getSubScene());
        scene3D.getSubScene().widthProperty().bind(subScenePane.widthProperty());
        scene3D.getSubScene().heightProperty().bind(subScenePane.heightProperty());
        rootPane.setCenter(subScenePane);

        //ALL text fields normally is ok
        textFieldOk(
                tfCol,
                tfCount,
                tfCSVCount,
                tfFaceNumber,
                tfFileName,
                tfMaxValue,
                tfReportFilename
        );

        //:)
        primaryStage.show();
    }

    //All methods below - colorizing text fields
    private void textFieldOk(TextField ... textFields) {
        for (TextField textField : textFields) {
            textFieldOk(textField);
        }
    }

    private void textFieldOk(TextField textField) {
        textField.setStyle("-fx-focus-color: #00BFFF; -fx-faint-focus-color: rgba(0, 191, 255, 0.06);");
    }

    private void textFieldError(TextField textField, String title, String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR, error);
        alert.setHeaderText(title);
        alert.showAndWait();
        textField.setStyle("-fx-focus-color: #FF0055; -fx-faint-focus-color: rgba(255, 0, 88, 0.11);");
        textField.requestFocus();
    }

    private boolean textFieldEmptyCheck(TextField textField) {
        if (textField.getText().contentEquals("")) {
            textFieldError(textField, "Bad value!", "Can't be empty!");
            return false;
        }
        textFieldOk(textField);
        return true;
    }

    private boolean textFieldCheckNumeral(TextField textField) {
        if (!textFieldEmptyCheck(textField)) {
            return false;
        }
        try {
            Integer.parseInt(textField.getText());
        } catch (NumberFormatException e) {
            textFieldError(textField, "Bad value!", "Wrong number format!");
            return false;
        }
        textFieldOk(textField);
        return true;
    }
}
