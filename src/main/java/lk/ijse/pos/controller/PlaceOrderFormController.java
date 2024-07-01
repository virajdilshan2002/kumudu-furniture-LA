package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import lk.ijse.pos.bo.BOFactory;
import lk.ijse.pos.bo.custom.PlaceOrderBo;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.dto.FurnitureDTO;
import lk.ijse.pos.dto.OrderDTO;
import lk.ijse.pos.dto.OrderDetailDTO;

import lk.ijse.pos.util.Mail;
import lk.ijse.pos.util.NavigateTo;
import lk.ijse.pos.util.PaymentType;
import lk.ijse.pos.util.Regex;
import lk.ijse.pos.view.tdm.CartTm;
import net.sf.jasperreports.engine.*;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import static lk.ijse.pos.util.GenerateBill.getPDFFile;
import static lk.ijse.pos.util.NavigateTo.newStage;

public class PlaceOrderFormController {

    public AnchorPane rootNode;
    public Label lblDate;
    public Label lblOrderId;
    public Label lblDesc;
    public TableView<CartTm> tblOrderCart;
    public Label lblTotal;
    public Label lblCusName;
    public TextField txtQty;
    public Label lblFurnId;
    public TableColumn<?, ?> colItemId;
    public TableColumn<?, ?> colDescription;
    public TableColumn<?, ?> colUnitPrice;
    public TableColumn<?, ?> colQty;
    public TableColumn<?, ?> colTotal;
    public TableColumn<?, ?> colAction;
    public JFXComboBox<PaymentType> cmbPaymentType;
    public TextField txtPayment;
    public PaymentType paymentType;
    public TextField txtPrice;

    public Label lblCusId;
    public Label lblPayment;
    public ScrollPane ItemScrollPane;
    public Label lblStock;
    public ImageView imgLoading;
    public JFXTextField txtContactNo;
    public JFXButton btnPlaceOrder;
    @FXML
    private ImageView imgSelectedItem;

    private List<FurnitureDTO> itemList;
    private FurnitureDTO selectedItem;
    private CustomerDTO customer;

    private ObservableList<CartTm> cartList = FXCollections.observableArrayList();

    PlaceOrderBo placeOrderBo = (PlaceOrderBo) BOFactory.getInstance().getBO(BOFactory.BOType.PLACEORDER);

    public void initialize(){
        setCellValueFactory();
        loadItemList();

        generateNewOrderId();
        setDate();
        setPaymentTypes();

        setDisable();
        initializeItemScrollPane();
    }

    private void setDisable() {
        lblStock.setVisible(false);
        lblPayment.setDisable(true);
        txtPayment.setDisable(true);
        imgLoading.setVisible(false);
    }

    private void loadItemList() {
        try {
            itemList = placeOrderBo.getAllFurnitureItems();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeItemScrollPane() {
        //Create a GridPane for all items
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(0));
        gridPane.setHgap(0);
        gridPane.setVgap(10);

        //Create a pane for each item
        for (int i = 0; i < itemList.size(); i++) {
            Pane pane = new Pane();

            ImageView imageView = createImageView(itemList.get(i).getImageFile());
            Label desc = createDescLabel(itemList.get(i).getFurnDescription() , itemList.get(i).getFurnPrice());
            Label qty = createQtyLabel(itemList.get(i).getFurnQty());
            Label id = createIdLabel(itemList.get(i).getFurnId());

            //Create a button to select the item
            JFXButton btnSelectItem = creatItemButton();
            final int curentItemIndex = i; // Create a variable to store the index of the item
            //Add an event handler to the button
            btnSelectItem.setOnAction(event -> {
                Image image = imageView.getImage();
                imgSelectedItem.setImage(image);
                lblFurnId.setText(itemList.get(curentItemIndex).getFurnId());
                lblDesc.setText(itemList.get(curentItemIndex).getFurnDescription());
                txtPrice.setText(String.valueOf(itemList.get(curentItemIndex).getFurnPrice()));
                lblStock.setText(String.valueOf(itemList.get(curentItemIndex).getFurnQty()));

                lblStock.setVisible(true);
                txtQty.clear();
                txtQty.requestFocus();

                selectedItem = itemList.get(curentItemIndex);
            });

            //Add the pane to the GridPane
            pane.getChildren().addAll(imageView, qty, id, desc, btnSelectItem);
            gridPane.add(pane, i % 4, i / 4);
        }

        //Set the GridPane as the content of the ScrollPane
        ItemScrollPane.setContent(new ScrollPane(gridPane));
        ItemScrollPane.setFitToHeight(true);
        ItemScrollPane.setFitToWidth(true);
    }

    private Label createIdLabel(String furnId) {
        Label lbl = new Label(furnId);
        lbl.setAlignment(Pos.CENTER);
        lbl.setPrefHeight(10);
        lbl.setPrefWidth(60);
        lbl.setStyle("-fx-background-color: #33001a; -fx-background-radius: 0px 10px 10px 0px; -fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        lbl.setLayoutX(10);
        lbl.setLayoutY(150);
        return lbl;
    }

    private Label createQtyLabel(int qty) {
        Label lbl = new Label(Integer.toString(qty));
        lbl.setPrefWidth(30);
        lbl.setPrefHeight(30);
        lbl.setAlignment(Pos.CENTER);
        lbl.setStyle("-fx-background-color:  #00e68a; -fx-background-radius: 50; -fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        lbl.setLayoutX(145);
        lbl.setLayoutY(5);
        return lbl;
    }

    private Label createDescLabel(String furnDescription , double price) {
        Label lbl = new Label(furnDescription + " : " + String.valueOf(price) + "/=");
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #00cc7a;");
        lbl.setLayoutX(10);
        lbl.setLayoutY(180);
        return lbl;
    }

    private JFXButton creatItemButton() {
        JFXButton btn = new JFXButton("SELECT");
        btn.setCursor(Cursor.HAND);
        btn.setButtonType(JFXButton.ButtonType.RAISED);
        btn.setStyle("-fx-background-color: #00e68a; -fx-background-radius: 20; -fx-text-fill: white; -fx-font-weight: bold;");
        btn.setLayoutX(10);
        btn.setLayoutY(200);
        btn.setPrefWidth(160);
        btn.setPrefHeight(30);
        return btn;
    }

    private ImageView createImageView(File file) {
        Image img = new Image(file.toURI().toString());
        ImageView imageView = new ImageView(img);
        imageView.setFitHeight(160);
        imageView.setFitWidth(160);
        imageView.setLayoutX(10);
        imageView.setLayoutY(10);
        return imageView;
    }

    private void setPaymentTypes() {
        ObservableList<PaymentType> obList = FXCollections.observableArrayList(PaymentType.CASH, PaymentType.ADVANCE);
        cmbPaymentType.setItems(obList);
    }

    private void setCellValueFactory() {
        colItemId.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("btnRemove"));
    }

    private void setDate() {
        lblDate.setText(String.valueOf(LocalDate.now()));
    }

    private void generateNewOrderId() {
        try {
            lblOrderId.setText(placeOrderBo.generateNewOrderId());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void txtContactNoClickOnAction(ActionEvent actionEvent) {
        if (Regex.setTextColor(lk.ijse.pos.util.TextField.PHONE,txtContactNo)){
            searchCustomer();
            btnPlaceOrder.requestFocus();
        }else {
            new Alert(Alert.AlertType.ERROR, "Invalid Contact No!").show();
            txtContactNo.requestFocus();
        }
    }

    private void searchCustomer() {
        try {
            customer = placeOrderBo.searchCustomerByContact(txtContactNo.getText());
            if (customer != null) {
                lblCusId.setText(customer.getId());
                lblCusName.setText(customer.getName());
            }else {
                new Alert(Alert.AlertType.ERROR, "Customer Not Found!").show();
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnAddNewCustomerClickOnAction(ActionEvent actionEvent) throws IOException {
        NavigateTo.newStage("/lk/ijse/pos/view/AddCustomerForm.fxml", "Add New Customer Form");
    }

    public void btnAddToCartClickOnAction(ActionEvent actionEvent) {
        if (checkDetailsBeforeAddingToCart()) {

            ButtonType yes = new ButtonType("Yes",ButtonBar.ButtonData.OK_DONE);
            ButtonType no = new ButtonType("No",ButtonBar.ButtonData.CANCEL_CLOSE);

            //Optional<ButtonType> addType = new Alert(Alert.AlertType.INFORMATION,"Are You Sure To Add ?",yes,no).showAndWait();

            //if (addType.orElse(no) == yes){
            String furnId = selectedItem.getFurnId();
            String description = selectedItem.getFurnDescription();
            double unitPrice = Double.parseDouble(txtPrice.getText());
            int qty = Integer.parseInt(txtQty.getText());
            double total = unitPrice * qty;

            JFXButton btnRemove = new JFXButton("REMOVE");
            btnRemove.setCursor(Cursor.HAND);
            btnRemove.setButtonType(JFXButton.ButtonType.RAISED);
            btnRemove.setPrefWidth(80);
            btnRemove.setStyle("-fx-background-color: #ff0066;" +
                    "-fx-background-radius: 20;" +
                    " -fx-text-fill: white;"+
                    "-fx-font-weight: bold;");

            btnRemove.setOnAction((e) ->{
                Optional<ButtonType> removeType = new Alert(Alert.AlertType.INFORMATION,"Are You Sure To Remove ?",yes,no).showAndWait();
                if (removeType.orElse(no)==yes){
                    cartList.remove(tblOrderCart.getFocusModel().getFocusedItem());
                    tblOrderCart.refresh();
                    calculateTotalPrice();

                    if (cartList.isEmpty()){
                        imgLoading.setVisible(false);
                    }
                }
            });

            for (int i = 0; i < tblOrderCart.getItems().size(); i++){
                if (furnId.equals(colItemId.getCellData(i))){
                    qty += cartList.get(i).getQty();
                    total = unitPrice * qty;

                    cartList.get(i).setQty(qty);
                    cartList.get(i).setUnitPrice(unitPrice);
                    cartList.get(i).setTotal(total);

                    tblOrderCart.refresh();
                    txtQty.clear();

                    calculateTotalPrice();
                    return;
                }
            }

            CartTm cartTm = new CartTm(furnId, description, unitPrice, qty, total, btnRemove);
            cartList.add(cartTm);
            tblOrderCart.setItems(cartList);

            calculateTotalPrice();
            imgLoading.setVisible(true);
            txtQty.clear();
            //}
        }
    }

    private boolean checkDetailsBeforeAddingToCart() {
        if (selectedItem == null) {
            new Alert(Alert.AlertType.WARNING, "Please Select Item!").show();
            return false;
        } else if (txtPrice.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please Enter Price!").show();
            txtPrice.requestFocus();
            return false;
        } else if (!Regex.checkTextField(lk.ijse.pos.util.TextField.PRICE,txtPrice)) {
            new Alert(Alert.AlertType.WARNING, "Invalid Price").show();
            txtPrice.requestFocus();
            return false;
        }else if (txtQty.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please Enter Quantity!").show();
            txtQty.requestFocus();
            return false;
        } else if (!Regex.checkTextField(lk.ijse.pos.util.TextField.QTY,txtQty)) {
            new Alert(Alert.AlertType.WARNING, "Invalid Quantity!").show();
            txtQty.requestFocus();
            return false;
        }else {
            try {
                String furnId = lblFurnId.getText();
                int orderQty = Integer.parseInt(txtQty.getText());

                int availableQty = placeOrderBo.availableItemQty(furnId, orderQty);
                if (availableQty > 0) {
                    return true;
                }
                new Alert(Alert.AlertType.ERROR, "Quantity Not Available! Available Quantity Is : " + availableQty).show();
                return false;

            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void calculateTotalPrice() {
        double total = 0;
        for (CartTm cartTm : cartList){
            total += cartTm.getTotal();
        }
        lblTotal.setText(String.valueOf(total));
    }

    public void txtQtyClickOnAction(ActionEvent actionEvent) {
        txtPrice.requestFocus();
    }

    public void cmbPaymentTypeClickOnAction(ActionEvent actionEvent) {
        paymentType = cmbPaymentType.getValue();
        if (paymentType.equals(PaymentType.CASH)) {
            lblPayment.setDisable(true);
            txtPayment.setDisable(true);
            btnPlaceOrder.requestFocus();
        }else {
            lblPayment.setDisable(false);
            txtPayment.setDisable(false);
            btnPlaceOrder.requestFocus();
        }
    }

    private boolean checkDetailsBeforePlaceOrder() {
        String netTotal = lblTotal.getText();
        String payment = txtPayment.getText();

        if (cartList.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Cart Is Empty!").show();
            return false;
        }else if (lblCusId.getText() == null) {
            new Alert(Alert.AlertType.WARNING, "Please Add A Customer!").show();
            txtContactNo.requestFocus();
            return false;
        } else if (paymentType == null) {
            new Alert(Alert.AlertType.WARNING, "Please Select A Payment Type!").show();
            cmbPaymentType.requestFocus();
            return false;
        } else if (paymentType.equals(PaymentType.ADVANCE)) {
            if (txtPayment.getText().trim().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please Enter Payment Amount!").show();
                txtPayment.requestFocus();
                return false;
            } else if (!Regex.checkTextField(lk.ijse.pos.util.TextField.PRICE,txtPayment)) {
                new Alert(Alert.AlertType.WARNING, "Invalid Payment Amount!").show();
                txtPayment.requestFocus();
                return false;
            } else if (Double.parseDouble(netTotal) < Double.parseDouble(payment)) {
                new Alert(Alert.AlertType.WARNING, "Payment Is Greater Than Net Total!").show();
                txtPayment.requestFocus();
                return false;
            }
        }
        return true;
    }

    public void btnPlaceOrderClickOnAction(ActionEvent actionEvent) {
        boolean isOkay = checkDetailsBeforePlaceOrder();

        if (isOkay) {
            String orderId = lblOrderId.getText();
            String cusId = lblCusId.getText();
            String orderDate = lblDate.getText();
            paymentType = cmbPaymentType.getValue();

            double payment = paymentType.equals(PaymentType.CASH) ? Double.parseDouble(lblTotal.getText()) : Double.parseDouble(txtPayment.getText());
            double total = Double.parseDouble(lblTotal.getText());

            OrderDTO order;
            if (paymentType.equals(PaymentType.ADVANCE)) {
                order = new OrderDTO(orderId, cusId, orderDate, paymentType, payment, total);
            } else {
                order = new OrderDTO(orderId, cusId, orderDate, paymentType, null, total);
            }

            List<OrderDetailDTO> odList = new ArrayList<>();
            for (int i = 0; i < tblOrderCart.getItems().size(); i++) {
                CartTm cartTm = cartList.get(i);
                odList.add(new OrderDetailDTO(
                        orderId,
                        cartTm.getItemId(),
                        cartTm.getQty(),
                        cartTm.getUnitPrice()
                ));
            }

            //Transaction
            if (placeOrderBo.placeOrder(order,odList)) {
                new Alert(Alert.AlertType.INFORMATION, "Order Placed Successfully!").show();

                //Send Email if customer has an email
                if (customer.getEmail() != null){
                    sendReceipt();
                }

                cartList.clear();
                tblOrderCart.refresh();
                txtQty.clear();
                txtPayment.clear();
                lblTotal.setText("0.00");
            } else {
                new Alert(Alert.AlertType.WARNING, "Order Not Placed!").show();
            }
        }
    }

    private void sendReceipt() {
        String title = "Order Placed Successfully! - " + lblOrderId.getText();
        String subject = "Order Receipt";
        String msg = "Your Order Receipt for Order Id: " + lblOrderId.getText() + " is attached with this email. Thank you for shopping with us!";
        String email = customer.getEmail();

        try {
            Mail.sendMail(title, subject, msg, email, getPDFFile(lblOrderId.getText()));
        } catch (JRException | SQLException | MessagingException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void txtPaymentClickOnAction(ActionEvent actionEvent) {
        btnPlaceOrderClickOnAction(actionEvent);
    }

    public void txtPriceClickOnAction(ActionEvent actionEvent) {
        btnAddToCartClickOnAction(actionEvent);
    }

    public void txtContactOnKeyRelesed(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.pos.util.TextField.PHONE,txtContactNo);
    }
}
