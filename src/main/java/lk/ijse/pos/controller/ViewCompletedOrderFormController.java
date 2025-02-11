package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.pos.bo.BOFactory;
import lk.ijse.pos.bo.custom.OrderBO;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.util.Mail;
import lk.ijse.pos.view.tdm.AdvanceSearchTm;
import lk.ijse.pos.view.tdm.OrderTm;
import net.sf.jasperreports.engine.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static lk.ijse.pos.util.GenerateBill.getPDFFile;
import static lk.ijse.pos.util.GenerateBill.viewBill;

public class ViewCompletedOrderFormController {
    public AnchorPane rootNode;
    public TableColumn<?,?> colFurnId;
    public  TableColumn<?,?> colFurnDesc;
    public  TableColumn<?,?> colQty;
    public  TableColumn<?,?> colUnitPrice;
    public TableColumn<?,?> colTotal;
    public Label lblNetTotal;
    public TableView<AdvanceSearchTm> tblAdSearch;
    public Label lblOrderId;
    public Label lblCusName;
    public Label lblCusNum;
    public Label lblPaymentType;
    public Label lblCusId;
    public Label lblOrderDate;
    public JFXButton btnSendBill;
    private ObservableList<AdvanceSearchTm> obList;
    private List<AdvanceSearchTm> list;
    private OrderTm orderTm;
    private CustomerDTO customer;

    OrderBO orderBO = (OrderBO) BOFactory.getInstance().getBO(BOFactory.BOType.ORDER);

    public void initialize() {
        setCellValueFactory();
    }
    public void initialize(OrderTm orderTm) {
        this.orderTm = orderTm;
        getCustomer();

        loadItemTable();
        initializeOtherDetails();
    }

    private void getCustomer() {
        try {
            this.customer = orderBO.getCustomer(orderTm.getCusId());
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.WARNING,"Customer Not Found!").show();
            throw new RuntimeException(e);
        }
    }

    private void loadItemTable() {
        obList = FXCollections.observableArrayList();
        try {
            list = orderBO.getOrderItems(orderTm.getOrderId());
            if (!list.isEmpty()) {
                obList.addAll(list);
                tblAdSearch.setItems(obList);
            }else {
                new Alert(Alert.AlertType.WARNING, "Missing Item Details! May Be Item Deleted!").show();
            }

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeOtherDetails() {
        lblCusId.setText(orderTm.getCusId());
        lblOrderId.setText(orderTm.getOrderId());
        lblOrderDate.setText(orderTm.getOrderDate());
        lblPaymentType.setText(String.valueOf(orderTm.getPaymentType()));
        lblNetTotal.setText(String.valueOf(orderTm.getTotalPayment()));

        lblCusName.setText(customer.getName());
        lblCusNum.setText(customer.getContact());

        if (customer.getEmail() == null) btnSendBill.setVisible(false);

    }

    private void setCellValueFactory() {
        colFurnId.setCellValueFactory(new PropertyValueFactory<>("furnId"));
        colFurnDesc.setCellValueFactory(new PropertyValueFactory<>("furnDesc"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
    }

    public void btnGenerateBillClickOnAction(ActionEvent actionEvent) throws JRException, SQLException, ClassNotFoundException {
        viewBill(lblOrderId.getText());
    }

    public void btnRefundClickOnAction(ActionEvent actionEvent) {
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        Optional<ButtonType> type = new Alert(Alert.AlertType.CONFIRMATION, "This action cannot be undone. Do you want to Refund?", yes, no).showAndWait();

        if (type.orElse(no) == yes) {
            String id = lblOrderId.getText();
            try {
                if (orderBO.refundOrder(id, list)) {
                    new Alert(Alert.AlertType.INFORMATION, "Refunded Successfully!").show();
                    Stage stage = (Stage) rootNode.getScene().getWindow();
                    stage.close();
                }else {
                    new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void btnSendBillClickOnAction(ActionEvent actionEvent) {
        String title = "Order Receipt - " + lblOrderId.getText();
        String subject = "Order Receipt";
        String msg = "Your Order Receipt for Order Id: " + lblOrderId.getText() + " is attached with this email.";
        String email = customer.getEmail();

        try {
            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

            Optional<ButtonType> type = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to send bill again?", yes, no).showAndWait();

            if (type.orElse(no) == yes) {
                Mail.sendMail(title, subject, msg, email, getPDFFile(lblOrderId.getText()));
            }
        } catch (JRException | SQLException | MessagingException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
