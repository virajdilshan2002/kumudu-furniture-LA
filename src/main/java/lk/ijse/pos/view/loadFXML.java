package lk.ijse.pos.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class loadFXML {
    public static void newStage(String path, String title) throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(loadFXML.class.getResource(path))));

        stage.setTitle(title);
        stage.centerOnScreen();
        stage.show();
    }
}
