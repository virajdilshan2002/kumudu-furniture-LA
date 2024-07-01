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
import lk.ijse.pos.bo.BOFactory;
import lk.ijse.pos.bo.custom.CustomerBo;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.entity.Customer;
import lk.ijse.pos.util.Regex;
import lk.ijse.pos.util.TextField;

import java.sql.SQLException;
import java.util.Optional;

import static lk.ijse.pos.util.EmailVerifier.checkIsValidMail;

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

    CustomerBo customerBo = (CustomerBo) BOFactory.getInstance().getBO(BOFactory.BOType.CUSTOMER);

    public void initialize(CustomerDTO customer){
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
                boolean isDeleted = customerBo.delete(cusId);
                if (isDeleted) {
                    new Alert(Alert.AlertType.INFORMATION,"Customer Deleted!").show();
                    clearTextFields();
                }
            }catch (SQLException | ClassNotFoundException e) {
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

        if (!isEmpty()){
            if (isValid()) {
                if (email.isEmpty()) email = null;
                ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
                ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

                Optional<ButtonType> result = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure To Update This Customer?", yes, no).showAndWait();
                if (result.orElse(no) == yes) {
                    CustomerDTO customer = new CustomerDTO(id,name,address, email, contact);
                    try {
                        if (customerBo.update(customer)) {
                            new Alert(Alert.AlertType.INFORMATION,"Customer Updated!").show();
                            clearTextFields();
                        }
                    }catch (SQLException | ClassNotFoundException e) {
                        new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
                    }
                }
            }
        }
    }

    public boolean isEmpty() {
        if (txtName.getText().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Customer Name cannot be empty").show();
            return true;
        } else if (txtAddress.getText().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Customer Address cannot be empty").show();
            return true;
        } else if (txtContact.getText().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Customer Contact cannot be empty").show();
            return true;
        }
        return false;
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
        if (Regex.setTextColor(TextField.PHONE,txtContact)){
            imgContactError.setVisible(false);
            imgContactOk.setVisible(true);
        }else {
            imgContactError.setVisible(true);
            imgContactOk.setVisible(false);
        }
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
