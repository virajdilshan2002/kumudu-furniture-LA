package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.pos.bo.BOFactory;
import lk.ijse.pos.bo.custom.OrderBO;
import lk.ijse.pos.db.DBConnection;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.dto.OrderDTO;
import lk.ijse.pos.util.Mail;
import lk.ijse.pos.view.tdm.AdvanceSearchTm;
import lk.ijse.pos.view.tdm.OrderTm;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static lk.ijse.pos.util.GenerateBill.getPDFFile;

public class ViewPendingOrderFormController {
    public Label lblAmountToBePaid;

    public Label lblCusId;
    public Label lblOrderDate;
    public Label lblPaid;
    @FXML
    public JFXButton btnSendBill;
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
    private OrderTm orderTm;
    private ObservableList<AdvanceSearchTm> obList;
    private List<AdvanceSearchTm> list;
    private CustomerDTO customer;
    private OrderDTO order;

    OrderBO orderBo = (OrderBO) BOFactory.getInstance().getBO(BOFactory.BOType.ORDER);

    public void initialize() {
        setCellValueFactory();
    }

    public void initialize(OrderTm orderTm) {
        this.orderTm = orderTm;
        getCustomerAndOrder();
        loadAOItemTable();
        initializeOtherDetails();
    }

    private void getCustomerAndOrder() {
        try {
            this.customer = orderBo.getCustomer(orderTm.getCusId());
            this.order = orderBo.getOrder(orderTm.getOrderId());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeOtherDetails() {
        //set order details
        lblCusId.setText(orderTm.getCusId());
        lblOrderId.setText(orderTm.getOrderId());
        lblOrderDate.setText(orderTm.getOrderDate());
        lblPaymentType.setText(String.valueOf(orderTm.getPaymentType()));
        lblNetTotal.setText(String.valueOf(orderTm.getTotalPayment()));

       //set customer details
        lblCusName.setText(customer.getName());
        lblCusNum.setText(customer.getContact());

        //set paid details
        double toBePaidAmount = order.getTotalPayment() - order.getAdvancedPayment();
        lblAmountToBePaid.setText(String.valueOf(toBePaidAmount));
        lblPaid.setText(String.valueOf(order.getAdvancedPayment()));

        if (customer.getEmail() == null) btnSendBill.setVisible(false);
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
            list = orderBo.getOrderItems(orderTm.getOrderId());
            if (!list.isEmpty()){
                obList.addAll(list);
                tblAdSearch.setItems(obList);
            }else {
                new Alert(Alert.AlertType.WARNING, "Missing Item Details! May Be Item Deleted!").show();
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnPayClickOnAction(ActionEvent event) {
        try {
            boolean isPaid = orderBo.payOrder(orderTm.getOrderId());
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
                boolean isRefunded = orderBo.refundOrder(id, list);
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
