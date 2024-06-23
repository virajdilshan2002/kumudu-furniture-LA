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
import lk.ijse.kumudufurniture.model.Order;
import lk.ijse.kumudufurniture.model.tablemodel.OrderTm;
import lk.ijse.kumudufurniture.repository.OrderRepo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CompletedOrdersFormController {
    public AnchorPane rootNode;
    public TableView<OrderTm> tblCompletedOrders;
    public TableColumn<?,?> colOrderId;
    public TableColumn<?,?> colDate;
    public TableColumn<?,?> colPaymentType;
    public TableColumn<?,?> colCusId;
    public TableColumn<?,?> colTotalPrice;
    public TextField txtSearch;

    public AnchorPane centerNode;
    public TableColumn<?,?> colDetails;
    List<Order> orderList;
    ObservableList<OrderTm> obList;

    public void initialize(){
        setCellValueFactory();
        loadCompletedOrdersTable();
    }

    private void setCellValueFactory() {
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colCusId.setCellValueFactory(new PropertyValueFactory<>("cusId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        colPaymentType.setCellValueFactory(new PropertyValueFactory<>("paymentType"));
        colTotalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPayment"));
        colDetails.setCellValueFactory(new PropertyValueFactory<>("Details"));
    }

    private void loadCompletedOrdersTable() {
        obList = FXCollections.observableArrayList();

        //get all completed orders
        try {
            orderList = OrderRepo.getCompletedOrdersList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //add all orders to obList and table
        for (Order order : orderList) {
            String orderId = order.getOrderId();
            String cusId = order.getCusId();
            String orderDate = order.getOrderDate();
            String paymentType = order.getPaymentType();
            double totalPayment = order.getTotalPayment();

            JFXButton btnDetails = new JFXButton("DETAILS");
            btnDetails.setCursor(Cursor.HAND);
            btnDetails.setButtonType(JFXButton.ButtonType.RAISED);
            btnDetails.setStyle("-fx-background-color: #00e68a; -fx-background-radius: 20; -fx-text-fill: white; -fx-font-weight: bold;");
            btnDetails.setPrefSize(70, 20);

            btnDetails.setOnAction((e) ->{
                FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/view/ViewCompletedOrderForm.fxml"));
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

                if (tblCompletedOrders.getSelectionModel().getSelectedItem() == null) {
                    new Alert(Alert.AlertType.ERROR,"Select A Order First").show();
                }else {
                    ViewCompletedOrderFormController controller = loader.getController();
                    controller.initialize(tblCompletedOrders.getFocusModel().getFocusedItem());
                    stage.show();
                }
            });

            OrderTm orderTm = new OrderTm(orderId, cusId, orderDate, paymentType, totalPayment, btnDetails);
            obList.add(orderTm);
        }
        tblCompletedOrders.setItems(obList);
    }

    public void btnSearchClickOnAction(ActionEvent actionEvent) {
        String orderId = txtSearch.getText();

        for (OrderTm order : obList) {
            if (orderId.equals(order.getOrderId())) {
                tblCompletedOrders.getSelectionModel().select(order);
                tblCompletedOrders.getFocusModel().focus(tblCompletedOrders.getSelectionModel().getSelectedIndex());
                tblCompletedOrders.scrollTo(order);
                return;
            }
        }
        new Alert(Alert.AlertType.INFORMATION, "Order Not Found").show();
    }

    public void btnRefreshClickOnAction(ActionEvent actionEvent) {
        txtSearch.clear();
        loadCompletedOrdersTable();
    }

    public void txtSearchClickOnAction(ActionEvent actionEvent) {
        btnSearchClickOnAction(actionEvent);
    }
}
