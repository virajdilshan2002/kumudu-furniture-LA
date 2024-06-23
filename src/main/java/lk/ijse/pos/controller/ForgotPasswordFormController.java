package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import lk.ijse.kumudufurniture.model.Credential;
import lk.ijse.kumudufurniture.repository.CredentialRepo;
import lk.ijse.kumudufurniture.util.Mail;

import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Random;

public class ForgotPasswordFormController {

    public JFXButton btnGetOtp;
    public JFXButton btnCheckOtp;
    public JFXButton btnConfirmChanges;
    public ImageView imgUserNameOk;
    public JFXPasswordField txtPassword;
    public JFXPasswordField txtReEnterPw;
    public ImageView imgPwError;
    @FXML
    private ImageView imgOk;

    @FXML
    private ImageView imgOtpError;

    @FXML
    private ImageView imgUserNameError;

    @FXML
    private Label lblEmail;

    @FXML
    private AnchorPane leftNode;

    @FXML
    private TextField txtOtp;

    @FXML
    private JFXTextField txtUserName;

    private Credential user = null;
    private Integer otp = null;

    public void initialize() {
        setDisable();
    }

    private void setDisable() {
        imgOk.setVisible(false);
        imgUserNameOk.setVisible(false);
        imgOtpError.setVisible(false);
        imgUserNameError.setVisible(false);
        btnCheckOtp.setDisable(true);
        btnGetOtp.setDisable(true);
        txtOtp.setDisable(true);
        txtReEnterPw.setDisable(true);
        txtPassword.setDisable(true);
        imgPwError.setVisible(false);
    }

    @FXML
    void btnCheckOtpClickOnAction(ActionEvent event) {
        if (txtOtp.getText().equals(String.valueOf(otp))) {
            imgOtpError.setVisible(false);
            txtPassword.setDisable(false);
            txtReEnterPw.setDisable(false);
            imgOk.setVisible(true);
            txtOtp.setDisable(true);
            btnCheckOtp.setDisable(true);

            txtPassword.requestFocus();
        } else {
            imgOtpError.setVisible(true);
            imgOk.setVisible(false);
        }
    }

    @FXML
    void btnConfirmChangesClickOnAction(ActionEvent event) {
        boolean isValid = isValid();
        if (isValid){

            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            ButtonType no = new ButtonType("No",ButtonBar.ButtonData.CANCEL_CLOSE);

            Optional<ButtonType> addType = new Alert(Alert.AlertType.INFORMATION,"Are you sure you want to change your password ?",yes,no).showAndWait();

            if (addType.orElse(no) == yes){
                String newPassword = txtPassword.getText();
                try {
                    boolean isUpdated = CredentialRepo.updatePassword(user.getUserName(), newPassword);
                    if (isUpdated) {
                        new Alert(Alert.AlertType.INFORMATION, "Password Updated Successfully!").show();
                        loadLoginForm();
                    }else {
                        new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
                        btnGetOtp.setDisable(false);
                        txtOtp.setDisable(false);
                        txtPassword.setDisable(false);
                        txtReEnterPw.setDisable(false);
                    }
                } catch (SQLException | IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }

    private void loadLoginForm() throws IOException {
        AnchorPane rootNode = FXMLLoader.load(this.getClass().getResource("/view/LoginForm.fxml"));
        Scene scene = new Scene(rootNode);

        Stage stage = (Stage) this.leftNode.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setTitle("KUMUDU FURNITURE");
    }

    @FXML
    void btnGetOtpClickOnAction(ActionEvent event) {
        otp = 1000 + new Random().nextInt(9000);
        try {
            Mail.setOtpMail(user.getEmail(), String.valueOf(otp));
            btnGetOtp.setDisable(true);
            txtOtp.setDisable(false);
            btnCheckOtp.setDisable(false);
        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void txtOtpClickOnAction(ActionEvent event) {
        btnCheckOtpClickOnAction(event);
    }

    @FXML
    void txtPasswordClickOnAction(ActionEvent event) {
        txtReEnterPw.requestFocus();
    }

    @FXML
    void txtReEnterPwClickOnAction(ActionEvent event) {
        btnConfirmChangesClickOnAction(event);
    }

    @FXML
    void txtReEnterPwOnKeyRelesedAction(KeyEvent event) {
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

    @FXML
    void txtUserNameClickOnAction(ActionEvent event) {
        btnSearchUserNameClickOnAction(event);
    }

    @FXML
    public void btnSearchUserNameClickOnAction(ActionEvent actionEvent) {
        String userName = txtUserName.getText();
        try {
            user = CredentialRepo.isUserExist(userName);
            if (user != null) {
                lblEmail.setText(user.getEmail());

                imgUserNameOk.setVisible(true);
                imgUserNameError.setVisible(false);
                btnGetOtp.setDisable(false);
            }else {
                imgUserNameOk.setVisible(false);
                imgUserNameError.setVisible(true);
                new Alert(Alert.AlertType.ERROR, "User Not Found").show();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isValid() {
        if (!txtPassword.getText().equals(txtReEnterPw.getText())){
            new Alert(Alert.AlertType.ERROR, "Password does not match!").show();
            txtReEnterPw.setFocusColor(Paint.valueOf("Red"));
            txtReEnterPw.setUnFocusColor(Paint.valueOf("Red"));
            return false;
        }
        return true;
    }

    public void imgBackOnMouseClick(MouseEvent mouseEvent) throws IOException {
        loadLoginForm();
    }
}
