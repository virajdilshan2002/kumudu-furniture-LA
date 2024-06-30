package lk.ijse.pos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.pos.dao.DAOFactory;
import lk.ijse.pos.dao.custom.OrderDAO;
import lk.ijse.pos.dao.custom.OrderDetailDAO;
import lk.ijse.pos.dao.custom.impl.OrderDAOImpl;
import lk.ijse.pos.dao.custom.impl.OrderDetailDAOImpl;
import lk.ijse.pos.db.DBConnection;
import lk.ijse.pos.entity.Customer;
import lk.ijse.pos.util.Mail;
import lk.ijse.pos.view.tdm.AdvanceSearchTm;
import lk.ijse.pos.view.tdm.OrderTm;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private ObservableList<AdvanceSearchTm> purchaseList;
    private OrderTm orderTm;
    private Customer customer;

    OrderDAO orderDAO = (OrderDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.ORDER);
    OrderDetailDAO orderDetailDAO = (OrderDetailDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.ORDER_DETAIL);

    public void initialize() {
        setCellValueFactory();
    }
    public void initialize(OrderTm orderTm) {
        this.orderTm = orderTm;
        getCustomer();

        loadCOItemTable();
        initializeOtherDetails();
    }

    private void getCustomer() {
        try {
            this.customer = orderDAO.getCustomer(orderTm.getCusId());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadCOItemTable() {
        purchaseList = FXCollections.observableArrayList();
        try {
            List<AdvanceSearchTm> list = orderDetailDAO.getOrderItems(orderTm.getOrderId());
            if (!list.isEmpty()) {
                purchaseList.addAll(list);
                tblAdSearch.setItems(purchaseList);
            }else {
                new Alert(Alert.AlertType.ERROR, "Missing Item Details! May Be Item Deleted!").show();
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

        try {
            Customer customer = orderDAO.getCustomer(orderTm.getCusId());
            if (customer != null) {
                lblCusName.setText(customer.getName());
                lblCusNum.setText(customer.getContact());
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
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
                boolean isRefunded = orderDAO.refund(id, purchaseList);
                if (isRefunded) {
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
