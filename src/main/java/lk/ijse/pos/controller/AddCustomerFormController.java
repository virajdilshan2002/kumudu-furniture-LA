package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import lk.ijse.pos.dao.DAOFactory;
import lk.ijse.pos.dao.custom.CustomerDAO;
import lk.ijse.pos.dao.custom.impl.CustomerDAOImpl;
import lk.ijse.pos.entity.Customer;
import lk.ijse.pos.util.Regex;
import lk.ijse.pos.util.TextField;

import java.sql.SQLException;
import java.util.Optional;

import static lk.ijse.pos.util.EmailVerifier.checkIsValidMail;

public class AddCustomerFormController {
    public AnchorPane rootNode;

    public Label lblCusId;
    public JFXTextField txtName;
    public JFXTextField txtContact;
    public JFXTextField txtAddress;
    public JFXTextField txtEmail;
    public ImageView imgEmailOk;
    public ImageView imgContactOk;
    public ImageView imgContactError;
    public ImageView imgEmailError;

    CustomerDAO customerDAO = (CustomerDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.CUSTOMER);

    public void initialize() {
        setNextCusId();
        setDisable();
    }

    private void setDisable() {
        imgEmailOk.setVisible(false);
        imgContactOk.setVisible(false);
        imgContactError.setVisible(false);
        imgEmailError.setVisible(false);
    }

    private void setNextCusId() {
        try {
            String newCusId = customerDAO.generateNewID();
            lblCusId.setText(newCusId);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnClearClickOnAction(ActionEvent actionEvent) {
        clearTextFields();
        setDisable();
    }

    private void clearTextFields() {
        txtAddress.clear();
        txtName.clear();
        txtEmail.clear();
        txtContact.clear();
    }

    public void btnSaveClickOnAction(ActionEvent actionEvent) {
        String id = lblCusId.getText();
        String name = txtName.getText();
        String address = txtAddress.getText();
        String email = txtEmail.getText();
        String contact = txtContact.getText();

        boolean isEmpty = checkDetails(name, address, contact);

        if (!isEmpty){
            boolean isValid = isValid();
            if (isValid) {
                ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
                ButtonType no = new ButtonType("No",ButtonBar.ButtonData.CANCEL_CLOSE);

                Optional<ButtonType> addType = new Alert(Alert.AlertType.INFORMATION,"Are You Sure To Add This Customer?",yes,no).showAndWait();

                if (addType.orElse(no) == yes){
                    if (email.isEmpty()) email = null;
                    Customer customer = new Customer(id, name, address, email, contact);

                    try {
                        boolean isSaved = customerDAO.add(customer);
                        if (isSaved) {
                            new Alert(Alert.AlertType.INFORMATION, "Customer Saved Successfully!").show();
                            Stage stage = (Stage) rootNode.getScene().getWindow();
                            stage.close();
                        }else {
                            new Alert(Alert.AlertType.ERROR, "Customer Not Saved!").show();
                        }
                    } catch (SQLException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public boolean checkDetails(String name, String address, String contact) {
        if (name.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Customer Name cannot be empty").show();
            return true;
        } else if (address.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Customer Address cannot be empty").show();
            return true;
        } else if (contact.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Customer Contact cannot be empty").show();
            return true;
        }
        return false;
    }

    public void txtAddressClickOnAction(ActionEvent actionEvent) {
        txtEmail.requestFocus();
    }

    public void txtNameClickOnAction(ActionEvent actionEvent) {
        txtAddress.requestFocus();
    }

    public void txtContactClickOnAction(ActionEvent actionEvent) {
        btnSaveClickOnAction(actionEvent);
    }

    public void txtContactOnKeyRelesed(KeyEvent keyEvent) {
        if (Regex.setTextColor(TextField.PHONE,txtContact)){
            imgContactError.setVisible(false);
            imgContactOk.setVisible(true);
        }else {
            imgContactError.setVisible(true);
            imgContactOk.setVisible(false);
        }
    }

    private boolean isValid() {
        if (!Regex.setTextColor(TextField.PHONE,txtContact)) {
            new Alert(Alert.AlertType.INFORMATION,"Invalid Contact Number!").show();
            imgEmailError.setVisible(false);
            imgEmailOk.setVisible(false);
            imgContactOk.setVisible(false);
            imgContactError.setVisible(true);
            return false;
        } else if(txtEmail.getText().isEmpty()) {
            imgEmailError.setVisible(false);
            imgEmailOk.setVisible(false);
            imgContactOk.setVisible(true);
            imgContactError.setVisible(false);
            return true;
        } else if (!checkIsValidMail(txtEmail.getText())) {
            imgEmailOk.setVisible(false);
            imgEmailError.setVisible(true);
            imgContactOk.setVisible(true);
            imgContactError.setVisible(false);
            return false;
        }
        imgEmailError.setVisible(false);
        imgContactError.setVisible(false);
        imgEmailOk.setVisible(true);
        imgContactOk.setVisible(true);
        return true;
    }

    public void txtEmailClickOnAction(ActionEvent actionEvent) {
        txtContact.requestFocus();
    }

    public void txtEmailOnKeyRelesedAction(KeyEvent keyEvent) {
        if (!txtEmail.getText().isEmpty()) {
            Regex.setTextColor(TextField.EMAIL,txtEmail);
        }else {
            txtEmail.setFocusColor(Paint.valueOf("Blue"));
            txtEmail.setUnFocusColor(Paint.valueOf("Blue"));
        }
    }
}
