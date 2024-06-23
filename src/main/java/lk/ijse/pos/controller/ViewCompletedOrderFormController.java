package lk.ijse.pos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.kumudufurniture.database.DatabaseConnection;
import lk.ijse.kumudufurniture.model.Customer;
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

public class ViewCompletedOrderFormController {
    public AnchorPane rootNode;
    public  TableColumn<?,?> colFurnId;
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
            this.customer = OrderRepo.getCustomer(orderTm.getCusId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadCOItemTable() {
        purchaseList = FXCollections.observableArrayList();
        try {
            List<AdvanceSearchTm> list = OrderDetailRepo.getOrderItems(orderTm.getOrderId());
            if (!list.isEmpty()) {
                purchaseList.addAll(list);
                tblAdSearch.setItems(purchaseList);
            }else {
                new Alert(Alert.AlertType.ERROR, "Missing Item Details! May Be Item Deleted!").show();
            }

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
            if (customer != null) {
                lblCusName.setText(customer.getName());
                lblCusNum.setText(customer.getContact());
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
}
