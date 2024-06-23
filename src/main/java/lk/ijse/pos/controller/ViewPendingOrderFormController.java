package lk.ijse.pos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.kumudufurniture.database.DatabaseConnection;
import lk.ijse.kumudufurniture.model.Customer;
import lk.ijse.kumudufurniture.model.Order;
import lk.ijse.kumudufurniture.model.tablemodel.AdvanceSearchTm;
import lk.ijse.kumudufurniture.model.tablemodel.OrderTm;
import lk.ijse.kumudufurniture.repository.OrderDetailRepo;
import lk.ijse.kumudufurniture.repository.OrderRepo;
import lk.ijse.kumudufurniture.util.Mail;
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
            this.customer = OrderRepo.getCustomer(orderTm.getCusId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeOtherDetails() {
        lblCusId.setText(orderTm.getCusId());
        lblOrderId.setText(orderTm.getOrderId());
        lblOrderDate.setText(orderTm.getOrderDate());
        lblPaymentType.setText(orderTm.getPaymentType());
        lblNetTotal.setText(String.valueOf(orderTm.getTotalPayment()));

        try {
            Customer customer = OrderRepo.getCustomer(orderTm.getCusId());
            Order order = OrderRepo.getOrder(orderTm.getOrderId());
            if (customer != null) {
                lblCusName.setText(customer.getName());
                lblCusNum.setText(customer.getContact());
            }
            if (order != null) {
                double toBePaidAmount = order.getTotalPayment() - order.getAdvancedPayment();
                lblAmountToBePaid.setText(String.valueOf(toBePaidAmount));
                lblPaid.setText(String.valueOf(order.getAdvancedPayment()));
            }
        } catch (SQLException e) {
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
            purchaseList = OrderDetailRepo.getOrderItems(orderTm.getOrderId());
            obList.addAll(purchaseList);
            tblAdSearch.setItems(obList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnPayClickOnAction(ActionEvent event) {
        try {
            boolean isPaid = OrderRepo.pay(orderTm.getOrderId());
            if (isPaid) {
                new Alert(Alert.AlertType.INFORMATION, "Paid Successfully").show();
                Stage stage = (Stage) rootNode.getScene().getWindow();
                stage.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnGenerateBillClickOnAction(ActionEvent actionEvent) throws JRException, SQLException {
        JasperDesign jasperDesign = JRXmlLoader.load("src/main/resources/report/Order_Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        Map<String, Object> data = new HashMap<>();
        data.put("ORDERID",lblOrderId.getText());

        JasperPrint jasperPrint =
                JasperFillManager.fillReport(
                        jasperReport,
                        data,
                        DatabaseConnection.getInstance().getConnection());

        JasperViewer.viewReport(jasperPrint,false);
    }

    public void btnRefundClickOnAction(ActionEvent actionEvent) {
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        Optional<ButtonType> type = new Alert(Alert.AlertType.CONFIRMATION, "This action cannot be undone. Do you want to Refund?", yes, no).showAndWait();

        if (type.orElse(no) == yes) {
            String id = lblOrderId.getText();
            try {
                boolean isRefunded = OrderRepo.refund(id, purchaseList);
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
        } catch (JRException | SQLException | MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File getBill() throws JRException, SQLException {
        JasperDesign jasperDesign = JRXmlLoader.load("src/main/resources/report/Order_Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        Map<String, Object> data = new HashMap<>();
        data.put("ORDERID", lblOrderId.getText());

        JasperPrint jasperPrint =
                JasperFillManager.fillReport(
                        jasperReport,
                        data,
                        DatabaseConnection.getInstance().getConnection());

        // Export the report to a PDF file
        File pdfFile = new File("Order Receipt.pdf");
        JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFile.getAbsolutePath());

        return pdfFile;
    }
}
