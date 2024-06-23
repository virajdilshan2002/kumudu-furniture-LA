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
import lk.ijse.kumudufurniture.model.Customer;
import lk.ijse.kumudufurniture.repository.CustomerRepo;
import lk.ijse.kumudufurniture.util.Regex;

import java.sql.SQLException;
import java.util.Optional;

import static lk.ijse.kumudufurniture.util.EmailVerifier.checkIsValidMail;

public class UpdateOrDeleteCustomerFormController {


    public AnchorPane rootNode;
    public Label lblCusId;
    public JFXTextField txtEmail;
    public JFXTextField txtName;
    public JFXTextField txtAddress;
    public JFXTextField txtContact;
    public ImageView imgEmailOk;
    public ImageView imgContactOk;
    public ImageView imgContactError;
    public ImageView imgEmailError;

    public void initialize(Customer customer){
        lblCusId.setText(customer.getId());
        txtName.setText(customer.getName());
        txtAddress.setText(customer.getAddress());
        txtEmail.setText(customer.getEmail());
        txtContact.setText(customer.getContact());

        setDisable();
    }

    private void setDisable() {
        imgEmailOk.setVisible(false);
        imgContactOk.setVisible(false);
        imgContactError.setVisible(false);
        imgEmailError.setVisible(false);
    }

    public void btnClearClickOnAction(ActionEvent actionEvent) {
        clearTextFields();
        setDisable();
    }

    public void btnDeleteClickOnAction(ActionEvent actionEvent) {
        String cusId = lblCusId.getText();

        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        Optional<ButtonType> result = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure To Delete This Customer?", yes, no).showAndWait();

        if (result.orElse(no) == yes) {
            try {
                boolean isDeleted = CustomerRepo.deleteCustomer(cusId);
                if (isDeleted) {
                    new Alert(Alert.AlertType.INFORMATION,"Customer Deleted!").show();
                    clearTextFields();
                }
            }catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            }
        }

    }

    private void clearTextFields() {
        txtAddress.clear();
        txtName.clear();
        txtEmail.clear();
        txtContact.clear();
    }

    public void btnUpdateClickOnAction(ActionEvent actionEvent) {
        String id = lblCusId.getText();
        String name = txtName.getText();
        String address = txtAddress.getText();
        String email = txtEmail.getText();
        String contact = txtContact.getText();

        boolean isEmpty = CustomerRepo.checkDetails(name, address, contact);

        if (!isEmpty){
            boolean isValid = isValid();
            if (isValid) {
                ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
                ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

                Optional<ButtonType> result = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure To Update This Customer?", yes, no).showAndWait();
                if (result.orElse(no) == yes) {
                    if (email.isEmpty() || email == null) email = null;
                    Customer customer = new Customer(id,name,address, email, contact);

                    try {
                        boolean isUpdated = CustomerRepo.updateCustomer(customer);
                        if (isUpdated) {
                            new Alert(Alert.AlertType.INFORMATION,"Customer Updated!").show();
                            clearTextFields();
                        }
                    }catch (SQLException e) {
                        new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
                    }
                }
            }
        }
    }

    private boolean isValid() {
        if (!Regex.setTextColor(lk.ijse.kumudufurniture.util.TextField.PHONE,txtContact)) {
            new Alert(Alert.AlertType.INFORMATION,"Invalid Contact Number!").show();
            imgEmailError.setVisible(false);
            imgEmailOk.setVisible(false);
            imgContactOk.setVisible(false);
            imgContactError.setVisible(true);
            return false;
        } else if(txtEmail.getText() == null) {
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

    public void txtAddressClickOnAction(ActionEvent actionEvent) {
        txtContact.requestFocus();
    }

    public void txtNameClickOnAction(ActionEvent actionEvent) {
        txtAddress.requestFocus();
    }

    public void txtContactClickOnAction(ActionEvent actionEvent) {
    }

    public void txtEmailClickOnAction(ActionEvent actionEvent) {
        txtContact.requestFocus();
    }

    public void txtContactOnKeyRelesedAction(KeyEvent keyEvent) {
        if (Regex.setTextColor(lk.ijse.kumudufurniture.util.TextField.PHONE,txtContact)){
            imgContactError.setVisible(false);
            imgContactOk.setVisible(true);
        }else {
            imgContactError.setVisible(true);
            imgContactOk.setVisible(false);
        }
    }

    public void txtEmailOnKeyRelesedAction(KeyEvent keyEvent) {
        if (!txtEmail.getText().isEmpty()) {
            Regex.setTextColor(lk.ijse.kumudufurniture.util.TextField.EMAIL,txtEmail);
        }else {
            txtEmail.setFocusColor(Paint.valueOf("Blue"));
            txtEmail.setUnFocusColor(Paint.valueOf("Blue"));
        }
    }
}
