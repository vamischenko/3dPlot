import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 * 3D scene class.
 * Helper class, which contains camera, it's movement by by Z and root's rotations.
 */

public class Scene3D {
    //Camera settings
    private final static double CAMERA_NEAR_CLIP = 0.1;
    private final static double CAMERA_FAR_CLIP = 10000.0;
    //Camera
    Camera camera = new PerspectiveCamera(true);
    Translate cameraDistance = new Translate();
    //Subscene
    SubScene subScene = null;
    //Root rotations
    Rotate rx = new Rotate(0.0, Rotate.X_AXIS);
    Rotate ry = new Rotate(0.0, Rotate.Y_AXIS);
    Rotate rz = new Rotate(0.0, Rotate.Z_AXIS);

    public Scene3D(Parent root) {
        //SubScene creation
        subScene = new SubScene(root, 500, 500, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.LIGHTCYAN);
        subScene.setCamera(camera);
        //Camera setting up
        camera.getTransforms().add(cameraDistance);
        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        //Root
        root.getTransforms().addAll(rx, ry, rz); //Applying transforms to root node; google "javafx 3d".
    }

    //Camera distance translate
    public Translate getCameraDistance() {
        return cameraDistance;
    }

    //Returns subscene (which can be added as a simple node)
    public SubScene getSubScene() {
        return subScene;
    }

    public void setBackgroundColor(Color backgroundColor) {
        subScene.setFill(backgroundColor);
    }

    //RX, RY, RZ - objects rotations in scene (rotates 3d scene)

    public Rotate getRx() {
        return rx;
    }

    public Rotate getRy() {
        return ry;
    }

    public Rotate getRz() {
        return rz;
    }
}
