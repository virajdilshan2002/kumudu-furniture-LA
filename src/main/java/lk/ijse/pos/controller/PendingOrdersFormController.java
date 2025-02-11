package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.pos.bo.BOFactory;
import lk.ijse.pos.bo.custom.OrderBO;
import lk.ijse.pos.dto.OrderDTO;
import lk.ijse.pos.entity.Order;
import lk.ijse.pos.util.PaymentType;
import lk.ijse.pos.view.tdm.OrderTm;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class PendingOrdersFormController {
    public AnchorPane rootNode;
    public TableView<OrderTm> tblToBePaidOrders;
    public TableColumn<?,?> colOrderId;
    public TableColumn<?,?> colCusId;
    public TableColumn<?,?> colDate;
    public TableColumn<?,?> colPaymentType;
    public TableColumn<?,?> colTotalPrice;
    public TableColumn<?,?> colDetails;
    public TextField txtSearch;

    public List<OrderDTO> orderList;
    ObservableList<OrderTm> obList;

    OrderBO orderBo = (OrderBO) BOFactory.getInstance().getBO(BOFactory.BOType.ORDER);

    public void initialize() {
        setCellValueFactory();
        loadToBePaidOrdersTable();
    }

    private void setCellValueFactory() {
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colCusId.setCellValueFactory(new PropertyValueFactory<>("cusId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        colPaymentType.setCellValueFactory(new PropertyValueFactory<>("paymentType"));
        colTotalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPayment"));
        colDetails.setCellValueFactory(new PropertyValueFactory<>("Details"));
    }

    private void loadToBePaidOrdersTable() {
        obList = FXCollections.observableArrayList();

        try {
            orderList = orderBo.getToBePaidOrders();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        for (OrderDTO order : orderList) {
            String orderId = order.getOrderId();
            String cusId = order.getCusId();
            String orderDate = order.getOrderDate();
            PaymentType paymentType = order.getPaymentType();
            double totalPayment = order.getTotalPayment();

            JFXButton btnDetails = new JFXButton("DETAILS");
            btnDetails.setCursor(Cursor.HAND);
            btnDetails.setButtonType(JFXButton.ButtonType.RAISED);
            btnDetails.setStyle("-fx-background-color: #00e68a; -fx-background-radius: 20; -fx-text-fill: white; -fx-font-weight: bold;");
            btnDetails.setPrefSize(70, 20);

            btnDetails.setOnAction((e) ->{
                FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/lk/ijse/pos/view/ViewPendingOrderForm.fxml"));
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
                stage.setTitle("Order Details");

                if (tblToBePaidOrders.getSelectionModel().getSelectedItem() == null) {
                    new Alert(Alert.AlertType.ERROR,"Select A Order First").show();
                }else {
                    ViewPendingOrderFormController controller = loader.getController();
                    controller.initialize(tblToBePaidOrders.getSelectionModel().getSelectedItem());
                    stage.show();
                }
            });

            obList.add(new OrderTm(orderId, cusId, orderDate, paymentType, totalPayment, btnDetails));
        }
        tblToBePaidOrders.setItems(obList);
    }

    public void btnSearchClickOnAction(ActionEvent actionEvent) {
        String orderId = txtSearch.getText();

        for (OrderTm order : obList) {
            if (orderId.equals(order.getOrderId())) {
                tblToBePaidOrders.getSelectionModel().select(order);
                tblToBePaidOrders.getFocusModel().focus(tblToBePaidOrders.getSelectionModel().getSelectedIndex());
                tblToBePaidOrders.scrollTo(order);
                return;
            }
        }
        new Alert(Alert.AlertType.INFORMATION, "Order Not Found").show();
    }

    public void btnRefreshClickOnAction(ActionEvent actionEvent) {
        txtSearch.clear();
        loadToBePaidOrdersTable();
    }

    public void txtSearchClickOnAction(ActionEvent actionEvent) {
        btnSearchClickOnAction(actionEvent);
    }
}
