package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import lk.ijse.pos.dao.custom.CredentialDAO;
import lk.ijse.pos.dao.custom.impl.CredentialDAOImpl;
import lk.ijse.pos.entity.Credential;
import lk.ijse.pos.util.Regex;
import lk.ijse.pos.util.TextField;

import java.io.IOException;

import static lk.ijse.pos.util.EmailVerifier.checkIsValidMail;

public class SignUpFormController {
    public JFXTextField txtUserName;
    public JFXTextField txtFullName;

    public JFXTextField txtEmail;
    public AnchorPane leftNode;
    public JFXPasswordField txtReEnterPw;
    public JFXPasswordField txtPassword;
    public ImageView imgPwError;
    public ImageView imgEmailError;

    CredentialDAO credentialDAO = new CredentialDAOImpl();

    public void initialize(){
        imgPwError.setVisible(false);
        imgEmailError.setVisible(false);
    }


    public void btnSignUpClickOnAction(ActionEvent actionEvent) {
        String userName = txtUserName.getText();
        String fullName = txtFullName.getText();
        String password = txtPassword.getText();
        String email = txtEmail.getText();

        if (isValid()) {
            try {
                boolean isSaved = credentialDAO.add(new Credential(userName,
                        fullName,
                        email,
                        password)
                );
                if (isSaved) {
                    new Alert(Alert.AlertType.INFORMATION, "SignUp Successful! Please Login Again!").show();
                    loadLoginForm();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        }
    }


    public void txtFullNameClickOnAction(ActionEvent actionEvent) {
        txtPassword.requestFocus();
    }

    public void txtPasswordClickOnAction(ActionEvent actionEvent) {
        btnSignUpClickOnAction(actionEvent);
    }

    public void txtUserNameClickOnAction(ActionEvent actionEvent) {
        txtFullName.requestFocus();
    }

    public void loadLoginForm() throws IOException {
        AnchorPane rootNode = FXMLLoader.load(this.getClass().getResource("/view/LoginForm.fxml"));
        Scene scene = new Scene(rootNode);

        Stage stage = (Stage) this.leftNode.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setTitle("KUMUDU FURNITURE");
    }

    public void txtReEnterPwClickOnAction(ActionEvent actionEvent) {
        btnSignUpClickOnAction(actionEvent);
    }

    public void txtReEnterPwOnKeyRelesedAction(KeyEvent keyEvent) {
        if (txtPassword.getText().equals(txtReEnterPw.getText())){
            txtReEnterPw.setFocusColor(Paint.valueOf("Green"));
            txtReEnterPw.setUnFocusColor(Paint.valueOf("Green"));
            txtPassword.setFocusColor(Paint.valueOf("Green"));
            txtPassword.setUnFocusColor(Paint.valueOf("Green"));
            imgPwError.setVisible(false);
        }else {
            txtReEnterPw.setFocusColor(Paint.valueOf("Red"));
            txtReEnterPw.setUnFocusColor(Paint.valueOf("Red"));
            txtPassword.setFocusColor(Paint.valueOf("Red"));
            txtPassword.setUnFocusColor(Paint.valueOf("Red"));
            imgPwError.setVisible(true);
        }
    }

    public boolean isValid() {
        if (txtUserName.getText().isEmpty() || txtPassword.getText().isEmpty() || txtFullName.getText().isEmpty() || txtPassword.getText().isEmpty() || txtEmail.getText().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "All fields are required!").show();
            imgPwError.setVisible(true);
            imgEmailError.setVisible(true);
            return false;
        }else if (!Regex.setTextColor(TextField.EMAIL,txtEmail)){
            new Alert(Alert.AlertType.ERROR, "Invalid Email").show();
            imgEmailError.setVisible(true);
            return false;
        } else if (!checkIsValidMail(txtEmail.getText())) {
            imgEmailError.setVisible(true);
            return false;
        }else if (!txtPassword.getText().equals(txtReEnterPw.getText())){
            new Alert(Alert.AlertType.ERROR, "Password does not match!").show();
            imgPwError.setVisible(true);
            imgEmailError.setVisible(false);
            return false;
        }
        return true;
    }

    public void txtEmailClickOnAction(ActionEvent actionEvent) {
        txtPassword.requestFocus();
    }

    public void txtEmailOnKeyRelesedAction(KeyEvent keyEvent) {
        Regex.setTextColor(TextField.EMAIL,txtEmail);
    }

    public void imgBackOnMouseClick(MouseEvent mouseEvent) throws IOException {
        loadLoginForm();
    }
}
