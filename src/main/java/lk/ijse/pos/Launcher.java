package lk.ijse.pos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Launcher extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(FXMLLoader.load(this.getClass().getResource("src/main/resources/lk/ijse/pos/view/LoginForm.fxml"))));

        stage.initStyle(StageStyle.UNDECORATED);
        /*stage.setTitle("KUMUDU FURNITURE");*/
        stage.centerOnScreen();
        stage.show();
    }
}
