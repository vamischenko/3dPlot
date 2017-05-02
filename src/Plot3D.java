import com.interactivemesh.jfx.importer.tds.TdsModelImporter;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Translate;

/**
 * 3D plot class.
 * Allows to build plot by 3d-array
 */

public class Plot3D {
    //Models
    //External models with "0" and "1"
    //JavaFX doesn't provide any tools for 3D text generation.
    //So, we need 3d models.
    private static final String MODEL_ZERO_RESNAME = "models/zero.3DS";
    private static final String MODELS_ONE_RESNAME = "models/one.3DS";
    //Axes
    private double axesLength = 10.0;
    private double axesWidth = 0.25;
    private Box axisX = new Box(axesLength, axesWidth, axesWidth);
    private Box axisY = new Box(axesWidth, axesLength, axesWidth);
    private Box axisZ = new Box(axesWidth, axesWidth, axesLength);
    //...
    private Group group3d = new Group();
    private double primitiveSize = 5.0;
    private double primitiveDistance = 1.0;
    //Grid
    private Color gridColor = Color.DARKGRAY;
    private double gridWidth = 0.0625;

    public Plot3D() {
        initAxes();
        showAxes(false);
    }

    //Builds plot
    public void build(int[][][] data) {
        build(data, -1, -1, -1);
    }

    public void build(int[][][] data, int layer) {
        build(data, layer, -1, -1);
    }

    public void build(int[][][] data, int layer, int xFace, int zFace) { //Builds plot for analysis; if layer > -1 then building specified layer;
        //ARRAY MUST BE NOT NULL! ALL ARRAY DIMENSIONS MUST BE NOT NULL AND IT'S LENGTH > 0!
        if (data == null) {
            return;
        }
        //-----------Preparing-----------
        group3d.getChildren().clear();
        addAxes();
        //-----------Plot building positions-----------
        //Calculating offsets by array dimensions
        int layersByY = data.length;
        int linesByX = data[0].length;
        int pointsByZ = data[0][0].length;
        //Y-offset
        double plotHeight = layersByY * primitiveSize + (layersByY - 1) * primitiveDistance;
        double yOffset = plotHeight / 2.0 - plotHeight;
        //X-offset
        double plotWidth = linesByX * primitiveSize + (linesByX - 1) * primitiveDistance;
        double xOffset = plotWidth / 2.0 - plotWidth;
        //Z-offset
        double plotDepth = pointsByZ * primitiveSize + (pointsByZ - 1) * primitiveDistance;
        double zOffset = plotDepth / 2.0 - plotDepth;
        //-----------Building Plot-----------
        //Y-axis //layer (by height)
        for (int j = 0; j < layersByY; j++) {
            //If the layer specified
            if (layer >= 0) {
                if (layer != j) {
                    continue;
                }
            }
            //X-axis //line
            for (int i = 0; i < linesByX; i++) {
                if (xFace >= 0) {
                    if (xFace != i) {
                        continue;
                    }
                }
                //Z-axis //point
                for (int k = 0; k < pointsByZ; k++) {
                    if (zFace >= 0) {
                        if (zFace != k) {
                            continue;
                        }
                    }
                    Shape3D shape; // = new Box(primitiveSize, primitiveSize, primitiveSize);

                    if (data[j][i][k] > 0) { //Choosing right model
                        shape = getOneModel();
                    } else {
                        shape = getZeroModel();
                    }

                    if (shape == null) { //If model wasn't load
                        return;
                    }

                    shape.setMaterial(new PhongMaterial( //Material for model
                            Color.rgb(255 / linesByX * i, 255 / layersByY * j, 255 / pointsByZ * k)
                    ));

                    shape.getTransforms().addAll( //Model placing
                            new Translate(
                                    i * (primitiveSize + primitiveDistance) + xOffset + primitiveSize / 2.0,
                                    j * (primitiveSize + primitiveDistance) + yOffset + primitiveSize / 2.0,
                                    k * (primitiveSize + primitiveDistance) + zOffset + primitiveSize / 2.0
                            )
                    );
                    group3d.getChildren().add(shape);
                }
            }
        }

        //-----------Grid-----------
        //XY plane by Z-axis
        for (int k = 0; k <= pointsByZ; k++) {
            for (int i = 0; i <= linesByX; i++) {
                Shape3D shape = new Box(gridWidth, layersByY * (primitiveSize + primitiveDistance) + gridWidth, gridWidth);
                shape.setMaterial(new PhongMaterial(gridColor));
                shape.getTransforms().add(
                        new Translate(
                                i * (primitiveSize + primitiveDistance) + xOffset - primitiveDistance / 2.0,
                                0.0,
                                k * (primitiveSize + primitiveDistance) + zOffset - primitiveDistance / 2.0
                        )
                );
                group3d.getChildren().add(shape);
            }
        }
        //XZ by X-axis -> Y-axis; XZ by Z-axis -> Y-axis
        for (int j = 0; j <= layersByY; j++) {
            for (int i = 0; i <= linesByX; i++) {
                Shape3D shape = new Box(gridWidth, gridWidth, pointsByZ * (primitiveSize + primitiveDistance));
                shape.setMaterial(new PhongMaterial(gridColor));
                shape.getTransforms().clear();
                shape.getTransforms().add(
                        new Translate(
                                i * (primitiveSize + primitiveDistance) + xOffset - primitiveDistance / 2.0,
                                j * (primitiveSize + primitiveDistance) + yOffset - primitiveDistance / 2.0,
                                0.0
                        )
                );
                group3d.getChildren().add(shape);
            }
            //Rotated 90deg
            for (int k = 0; k <= pointsByZ; k++) {
                Shape3D shape = new Box(linesByX * (primitiveSize + primitiveDistance) + gridWidth, gridWidth, gridWidth);
                shape.setMaterial(new PhongMaterial(gridColor));
                shape.getTransforms().clear();
                shape.getTransforms().add(
                        new Translate(
                                0.0,
                                j * (primitiveSize + primitiveDistance) + yOffset - primitiveDistance / 2.0,
                                k * (primitiveSize + primitiveDistance) + zOffset - primitiveDistance / 2.0
                        )
                );
                group3d.getChildren().add(shape);
            }
        }

        //-----------Start pointer (green ball)-----------
        Sphere sphere = new Sphere(primitiveSize / 4.0);
        sphere.setMaterial(new PhongMaterial(Color.GREENYELLOW));
        sphere.getTransforms().add(new Translate(
                xOffset,
                yOffset,
                zOffset
        ));
        group3d.getChildren().add(sphere);

        //-----------Big axes-----------
        Shape3D bigAxisX = new Box((linesByX + 1) * (primitiveSize + primitiveDistance), axesWidth, axesWidth);
        bigAxisX.setMaterial(new PhongMaterial(Color.BLUE));
        bigAxisX.getTransforms().clear();
        bigAxisX.getTransforms().add(
                new Translate(
                        0.0,
                        yOffset,
                        zOffset
                )
        );
        getGroup3d().getChildren().add(bigAxisX);

        Shape3D bigAxisY = new Box(axesWidth, (layersByY + 1) * (primitiveSize + primitiveDistance), axesWidth);
        bigAxisY.setMaterial(new PhongMaterial(Color.RED));
        bigAxisY.getTransforms().clear();
        bigAxisY.getTransforms().add(
                new Translate(
                        xOffset,
                        0.0,
                        yOffset
                )
        );
        getGroup3d().getChildren().add(bigAxisY);

        Shape3D bigAxisZ = new Box(axesWidth, axesWidth, (pointsByZ + 1) * (primitiveSize + primitiveDistance));
        bigAxisZ.setMaterial(new PhongMaterial(Color.GREEN));
        bigAxisZ.getTransforms().clear();
        bigAxisZ.getTransforms().add(
                new Translate(
                        xOffset,
                        yOffset,
                        0.0
                )
        );
        getGroup3d().getChildren().add(bigAxisZ);
    }

    //Returns group with 3d objects
    public Group getGroup3d() {
        return group3d;
    }

    //Axes methods
    public void showAxes(boolean show) {
        axisX.setVisible(show);
        axisY.setVisible(show);
        axisZ.setVisible(show);
    }

    private void initAxes() {
        //Colors
        axisX.setMaterial(new PhongMaterial(Color.BLUE));
        axisY.setMaterial(new PhongMaterial(Color.RED));
        axisZ.setMaterial(new PhongMaterial(Color.GREEN));
    }

    //Models
    private Shape3D getOneModel() {
        return getModel(MODELS_ONE_RESNAME);
    }

    private Shape3D getZeroModel() {
        return getModel(MODEL_ZERO_RESNAME);
    }

    private Shape3D getModel(String resourceName) { //Gets model with specified resource name
        TdsModelImporter modelImporter = new TdsModelImporter(); //Model importer; located in external library;
        modelImporter.read(getClass().getResource(resourceName)); //Read model from file
        if (modelImporter.getImport() == null) {
            return null;
        }
        Group modelGroup = (Group) modelImporter.getImport()[0]; //Model import...
        if (modelGroup.getChildren().size() < 1) {
            return null;
        }
        Shape3D modelShape = (Shape3D) modelGroup.getChildren().get(0); //Model import...
        modelShape.getTransforms().clear();
        return modelShape;
    }

    private void addAxes() {
        group3d.getChildren().addAll(axisX, axisY, axisZ);
    }
}
