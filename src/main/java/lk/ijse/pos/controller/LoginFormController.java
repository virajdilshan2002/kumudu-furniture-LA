package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.pos.dao.custom.CredentialDAO;
import lk.ijse.pos.dao.custom.impl.CredentialDAOImpl;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFormController {
    public AnchorPane rootNode;
    public Label lblWarningPw;
    public Label lblWarningUserName;
    public ImageView imgWarning1;
    public ImageView imgWarning2;
    public AnchorPane leftNode;
    public JFXTextField txtUserName;
    public JFXPasswordField txtPassword;
    public ImageView imgBackground;

    CredentialDAO credentialDAO = new CredentialDAOImpl();

    public void initialize() {
        lblWarningUserName.setVisible(false);
        lblWarningPw.setVisible(false);
        imgWarning1.setVisible(false);
        imgWarning2.setVisible(false);
        txtUserName.requestFocus();

        setLoginBackgroundImage();
    }

    private void setLoginBackgroundImage() {
        Image image = new Image(getClass().getResourceAsStream("/lk/ijse/pos/assets/pic/loginImage.jpg"));
        imgBackground.setImage(image);
    }

    private void setSignUpBackgroundImage() {
        Image image = new Image(getClass().getResourceAsStream("/lk/ijse/pos/assets/pic/signup.jpg"));
        imgBackground.setImage(image);
    }

    private void setForgotPwBackgroundImage() {
        Image image = new Image(getClass().getResourceAsStream("/lk/ijse/pos/assets/pic/forgotPwImage.jpg"));
        imgBackground.setImage(image);
    }

    public void btnLogInClickOnAction(ActionEvent actionEvent) throws IOException {
        String userName = txtUserName.getText();
        String password = txtPassword.getText();

        try {
            if (userName.isEmpty() && password.isEmpty()){
                imgWarning1.setVisible(true);
                imgWarning2.setVisible(true);
                lblWarningUserName.setVisible(false);
                lblWarningPw.setVisible(false);
            }else if (userName.isEmpty()){
                imgWarning2.setVisible(false);
                imgWarning1.setVisible(true);
                lblWarningUserName.setVisible(false);
                lblWarningPw.setVisible(false);
            }else if (password.isEmpty()){
                imgWarning1.setVisible(false);
                imgWarning2.setVisible(true);
                lblWarningUserName.setVisible(false);
                lblWarningPw.setVisible(false);
            }else {
                imgWarning1.setVisible(false);
                imgWarning2.setVisible(false);
                lblWarningUserName.setVisible(false);
                lblWarningPw.setVisible(false);

                ResultSet rst = credentialDAO.checkCredential(userName);

                if (rst.next()){
                    lblWarningUserName.setVisible(false);
                    lblWarningPw.setVisible(false);
                    imgWarning1.setVisible(false);
                    imgWarning2.setVisible(true);
                    String dbPassword = rst.getString(2);

                    if (dbPassword.equals(password)){
                        navigateToDashboard();
                    }else {
                        imgWarning1.setVisible(false);
                        imgWarning2.setVisible(false);
                        lblWarningUserName.setVisible(false);
                        lblWarningPw.setVisible(true);
                        txtPassword.requestFocus();
                    }
                }else {
                    imgWarning1.setVisible(false);
                    imgWarning2.setVisible(false);
                    lblWarningUserName.setVisible(true);
                    lblWarningPw.setVisible(false);

                    txtUserName.clear();
                    txtPassword.clear();
                    txtUserName.requestFocus();
                }
            }
        }catch (SQLException | ClassNotFoundException e){
            /*new Alert(Alert.AlertType.ERROR, "OOPS! something went wrong!").show();*/
            e.printStackTrace();
        }
    }

    private void navigateToDashboard() throws IOException {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/lk/ijse/pos/view/MainForm.fxml"));
        AnchorPane rootNode = loader.load();
        Scene scene = new Scene(rootNode);

        Stage stage = (Stage) this.rootNode.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
        /*stage.setTitle("KUMUDU FURNITURE");*/
    }

    public void linkSignUpClickOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane rootNode = FXMLLoader.load(this.getClass().getResource("/lk/ijse/pos/view/SignUpForm.fxml"));

        this.leftNode.getChildren().clear();
        this.leftNode.getChildren().add(rootNode);

        setSignUpBackgroundImage();
    }

    public void txtUserNameClickOnAction(ActionEvent actionEvent) {
        txtPassword.requestFocus();
    }

    public void txtPasswordClickOnAction(ActionEvent actionEvent) throws IOException {
        btnLogInClickOnAction(actionEvent);
    }

    public void linkForgotPwClickOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane rootNode = FXMLLoader.load(this.getClass().getResource("/lk/ijse/pos/view/ForgotPasswordForm.fxml"));

        this.leftNode.getChildren().clear();
        this.leftNode.getChildren().add(rootNode);

        setForgotPwBackgroundImage();
    }

    public void imgCloseAppOnMouseClicked(MouseEvent mouseEvent) {
        System.exit(0);
    }
}
