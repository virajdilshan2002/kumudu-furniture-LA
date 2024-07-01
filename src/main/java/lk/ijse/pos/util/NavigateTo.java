package lk.ijse.pos.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigateTo {
    public static void newStage(String path, String title) throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(NavigateTo.class.getResource(path))));

        stage.setTitle(title);
        stage.centerOnScreen();
        stage.show();
    }

    public static void children(String path, AnchorPane centerNode) throws IOException {
        AnchorPane root = FXMLLoader.load(NavigateTo.class.getResource(path));

        centerNode.getChildren().clear();
        centerNode.getChildren().add(root);
    }
    public static void parent(String path, AnchorPane parentNode) throws IOException {
        AnchorPane rootNode = FXMLLoader.load(NavigateTo.class.getResource(path));
        Scene scene = new Scene(rootNode);

        Stage stage = (Stage) parentNode.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
    }
}
