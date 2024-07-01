package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import lk.ijse.pos.bo.BOFactory;
import lk.ijse.pos.bo.custom.CustomerBo;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.entity.Customer;
import lk.ijse.pos.util.Regex;
import lk.ijse.pos.view.tdm.CustomerTm;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static lk.ijse.pos.util.EmailVerifier.checkIsValidMail;

public class CustomerFormController {
    public AnchorPane rootNode;
    public TableView<CustomerTm> tblCustomer;
    public TableColumn<?,?> colId;
    public TableColumn<?,?> colName;
    public TableColumn<?,?> colAddress;
    public TableColumn<?,?> colContact;
    public TableColumn<?,?> colAction;


    public JFXTextField txtName;
    public JFXTextField txtAddress;
    public Label lblCusId;
    public JFXTextField txtContact;
    public JFXTextField txtEmail;
    public ImageView imgEmailOk;
    public ImageView imgContactOk;
    public ImageView imgContactError;
    public ImageView imgEmailError;
    public TextField txtSearch;
    private List<CustomerDTO> customerList = new ArrayList<>();
    private ObservableList<CustomerTm> tmList;

    CustomerBo customerBo = (CustomerBo) BOFactory.getInstance().getBO(BOFactory.BOType.CUSTOMER);

    public void initialize() {
        setCellValueFactory();
        loadCustomerTable();
        setNextCusId();
        setDisable();
    }

    private void setDisable() {
        imgEmailOk.setVisible(false);
        imgContactOk.setVisible(false);
        imgContactError.setVisible(false);
        imgEmailError.setVisible(false);
    }

    private void loadCustomerTable() {
        try {
            customerList = customerBo.getAll();
        }catch (SQLException | ClassNotFoundException e){
            throw new RuntimeException(e);
        }

        tmList = FXCollections.observableArrayList();

        for(CustomerDTO customer : customerList){
            String id = customer.getId();
            String name = customer.getName();
            String address = customer.getAddress();
            String contact = customer.getContact();

            JFXButton btnUpdate = new JFXButton("UPDATE");
            btnUpdate.setCursor(Cursor.HAND);
            btnUpdate.setButtonType(JFXButton.ButtonType.RAISED);
            btnUpdate.setStyle("-fx-background-color: #00e68a; -fx-background-radius: 20; -fx-text-fill: white; -fx-font-weight: bold;");
            btnUpdate.setPrefSize(70, 20);

            btnUpdate.setOnAction((e) ->{

                FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/lk/ijse/pos/view/UpdateOrDeleteCustomerForm.fxml"));
                AnchorPane rootNode = null;
                try {
                    rootNode = loader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                Scene scene = new Scene(rootNode);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.setTitle("Update Customer");

                if (tblCustomer.getSelectionModel().getSelectedItem() == null) {
                    new Alert(Alert.AlertType.ERROR,"Select A Customer First").show();
                }else {
                    UpdateOrDeleteCustomerFormController controller = loader.getController();
                    controller.initialize(customer);
                    stage.show();
                }
            });
            CustomerTm tm = new CustomerTm(id,name,address,contact,btnUpdate);
            tmList.add(tm);
        }
        tblCustomer.setItems(tmList);
    }

    private void setCellValueFactory() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("Action"));
    }

    private void clearTextFields() {
        txtAddress.clear();
        txtName.clear();
        txtContact.clear();
        txtEmail.clear();
    }

    private void setNextCusId() {
        try {
            String newCusId = customerBo.generateNewID();
            lblCusId.setText(newCusId);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void txtContactClickOnAction(ActionEvent actionEvent) {
        btnSaveClickOnAction(actionEvent);
    }

    public void btnSearchClickOnAction(ActionEvent actionEvent) {
        if (Regex.checkTextField(lk.ijse.pos.util.TextField.PHONE,txtSearch)){
            String contact = txtSearch.getText();
            for (CustomerTm customer : tmList) {
                if (customer.getContact().equals(contact)) {
                    tblCustomer.getSelectionModel().select(customer);
                    tblCustomer.scrollTo(customer);
                    return;
                }
            }
            new Alert(Alert.AlertType.ERROR,"No Customer Found!").show();
        }else {
            new Alert(Alert.AlertType.INFORMATION,"Invalid Contact Number!").show();
        }
    }

    public void btnSaveClickOnAction(ActionEvent actionEvent) {
        String id = lblCusId.getText();
        String name = txtName.getText();
        String address = txtAddress.getText();
        String email = txtEmail.getText();
        String contact = txtContact.getText();

        if (!isEmpty()){
            if (isValid()) {
                if (email.isEmpty()) email = null;
                CustomerDTO customer = new CustomerDTO(id, name, address, email, contact);
                try {
                    ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
                    ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

                    Optional<ButtonType> result = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure To Add This Customer?", yes, no).showAndWait();

                    if (result.orElse(no) == yes) {
                        boolean isSaved = customerBo.add(customer);
                        if (isSaved) {
                            new Alert(Alert.AlertType.INFORMATION, "Customer Saved Successfully!").show();
                            clearTextFields();
                            txtName.requestFocus();
                            setNextCusId();
                            loadCustomerTable();
                            setDisable();
                        }else {
                            new Alert(Alert.AlertType.ERROR, "Customer Not Saved").show();
                        }
                    }
                }catch (SQLException | ClassNotFoundException e){
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
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
        if (!Regex.setTextColor(lk.ijse.pos.util.TextField.PHONE,txtContact)) {
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

    public void txtNameClickOnAction(ActionEvent actionEvent) {
        txtAddress.requestFocus();
    }

    public void txtAddressClickOnAction(ActionEvent actionEvent) {
        txtEmail.requestFocus();
    }

    public void btnRefreshClickOnAction(ActionEvent actionEvent) {
        loadCustomerTable();
        setNextCusId();
    }

    public void txtSearchClickOnAction(ActionEvent actionEvent) {
        btnSearchClickOnAction(actionEvent);
    }

    public void btnClearClickOnAction(ActionEvent actionEvent) {
        clearTextFields();
        setDisable();
    }

    public void txtContactOnKeyRelesed(KeyEvent keyEvent) {
        if (Regex.setTextColor(lk.ijse.pos.util.TextField.PHONE,txtContact)){
            imgContactError.setVisible(false);
            imgContactOk.setVisible(true);
        }else {
            imgContactError.setVisible(true);
            imgContactOk.setVisible(false);
        }
    }

    public void txtEmailClickOnAction(ActionEvent actionEvent) {
        txtContact.requestFocus();
    }

    public void txtEmailOnKeyRelesedAction(KeyEvent keyEvent) {
        if (!txtEmail.getText().isEmpty()) {
            Regex.setTextColor(lk.ijse.pos.util.TextField.EMAIL,txtEmail);
        }else {
            txtEmail.setFocusColor(Paint.valueOf("Blue"));
            txtEmail.setUnFocusColor(Paint.valueOf("Blue"));
        }
    }
}
