package lk.ijse.pos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.pos.dao.custom.OrderDAO;
import lk.ijse.pos.dao.custom.OrderDetailDAO;
import lk.ijse.pos.dao.custom.impl.OrderDAOImpl;
import lk.ijse.pos.dao.custom.impl.OrderDetailDAOImpl;
import lk.ijse.pos.db.DBConnection;
import lk.ijse.pos.entity.Customer;
import lk.ijse.pos.entity.Order;
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

public class ViewPendingOrderFormController {
    public Label lblAmountToBePaid;
    public Label lblCusId;
    public Label lblOrderDate;
    public Label lblPaid;
    @FXML
    private TableColumn<?, ?> colFurnId;

    @FXML
    private TableColumn<?, ?> colFurnDesc;

    @FXML
    private TableColumn<?, ?> colQty;

    @FXML
    private TableColumn<?, ?> colTotal;

    @FXML
    private TableColumn<?, ?> colUnitPrice;

    @FXML
    private Label lblCusName;

    @FXML
    private Label lblCusNum;

    @FXML
    private Label lblNetTotal;

    @FXML
    private Label lblOrderId;

    @FXML
    private Label lblPaymentType;

    @FXML
    private AnchorPane rootNode;

    @FXML
    private TableView<AdvanceSearchTm> tblAdSearch;

    private List<AdvanceSearchTm> purchaseList;

    private OrderTm orderTm;
    private ObservableList<AdvanceSearchTm> obList;

    private Customer customer;

    OrderDAO orderDAO = new OrderDAOImpl();
    OrderDetailDAO orderDetailDAO = new OrderDetailDAOImpl();

    public void initialize() {
        setCellValueFactory();
    }

    public void initialize(OrderTm orderTm) {
        this.orderTm = orderTm;
        getCustomer();
        loadAOItemTable();
        initializeOtherDetails();
    }

    private void getCustomer() {
        try {
            this.customer = orderDAO.getCustomer(orderTm.getCusId());
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
            Order order = orderDAO.getOrder(orderTm.getOrderId());
            if (customer != null) {
                lblCusName.setText(customer.getName());
                lblCusNum.setText(customer.getContact());
            }
            if (order != null) {
                double toBePaidAmount = order.getTotalPayment() - order.getAdvancedPayment();
                lblAmountToBePaid.setText(String.valueOf(toBePaidAmount));
                lblPaid.setText(String.valueOf(order.getAdvancedPayment()));
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

    public void loadAOItemTable() {
        obList = FXCollections.observableArrayList();
        try {
            purchaseList = orderDetailDAO.getOrderItems(orderTm.getOrderId());
            obList.addAll(purchaseList);
            tblAdSearch.setItems(obList);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnPayClickOnAction(ActionEvent event) {
        try {
            boolean isPaid = orderDAO.pay(orderTm.getOrderId());
            if (isPaid) {
                new Alert(Alert.AlertType.INFORMATION, "Paid Successfully").show();
                Stage stage = (Stage) rootNode.getScene().getWindow();
                stage.close();
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnGenerateBillClickOnAction(ActionEvent actionEvent) throws JRException, SQLException, ClassNotFoundException {
        JasperDesign jasperDesign = JRXmlLoader.load("src/main/resources/report/Order_Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        Map<String, Object> data = new HashMap<>();
        data.put("ORDERID",lblOrderId.getText());

        JasperPrint jasperPrint =
                JasperFillManager.fillReport(
                        jasperReport,
                        data,
                        DBConnection.getDbConnection().getConnection());

        JasperViewer.viewReport(jasperPrint,false);
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
                Mail.setMail(title, subject, msg, email, getBill());
            }
        } catch (JRException | SQLException | MessagingException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private File getBill() throws JRException, SQLException, ClassNotFoundException {
        JasperDesign jasperDesign = JRXmlLoader.load("src/main/resources/report/Order_Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        Map<String, Object> data = new HashMap<>();
        data.put("ORDERID", lblOrderId.getText());

        JasperPrint jasperPrint =
                JasperFillManager.fillReport(
                        jasperReport,
                        data,
                        DBConnection.getDbConnection().getConnection());

        // Export the report to a PDF file
        File pdfFile = new File("Order Receipt.pdf");
        JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFile.getAbsolutePath());

        return pdfFile;
    }
}
