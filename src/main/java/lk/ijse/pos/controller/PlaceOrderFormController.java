package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lk.ijse.kumudufurniture.database.DatabaseConnection;
import lk.ijse.kumudufurniture.model.*;
import lk.ijse.kumudufurniture.model.tablemodel.CartTm;
import lk.ijse.kumudufurniture.repository.CustomerRepo;
import lk.ijse.kumudufurniture.repository.FurnitureRepo;
import lk.ijse.kumudufurniture.repository.PlaceOrderRepo;
import lk.ijse.kumudufurniture.util.Mail;
import lk.ijse.kumudufurniture.util.Regex;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class PlaceOrderFormController {
    public AnchorPane rootNode;
    public Label lblDate;
    public Label lblOrderId;
    public Label lblDesc;
    public Label lblMaterial;
    public Label lblColor;
    public JFXComboBox<String> cmbItems;
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
    public JFXComboBox<String> cmbPaymentType;
    public TextField txtPayment;
    public static String paymentType;
    public TextField txtPrice;
    public Label lblCusId;
    public Label lblPayment;
    public ScrollPane ItemScrollPane;
    private static List<Furniture> itemList;
    public Label lblStock;
    public ImageView imgLoading;
    public JFXTextField txtContactNo;
    public JFXButton btnPlaceOrder;
    @FXML
    private ImageView imgSelectedItem;
    private Furniture selectedItem;

    private Customer customer;

    private ObservableList<CartTm> cartList = FXCollections.observableArrayList();

    public void initialize(){
        setCellValueFactory();

        setNextOrderId();
        setDate();
        setPaymentTypes();

        setDisable();
        getItemList();
        initializeItemScrollPane();
    }

    private void setDisable() {
        lblStock.setVisible(false);
        lblPayment.setDisable(true);
        txtPayment.setDisable(true);
        imgLoading.setVisible(false);
    }

    private void getItemList() {
        try {
            itemList = FurnitureRepo.getAllItems();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeItemScrollPane() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(0));
        gridPane.setHgap(0);
        gridPane.setVgap(10);

        for (int i = 0; i < itemList.size(); i++) {
            Pane pane = new Pane();
            ImageView imageView = createImageView(itemList.get(i).getImageFile());
            Label desc = descLabel(itemList.get(i).getFurnDescription() , itemList.get(i).getFurnPrice());
            Label qty = qtyLabel(itemList.get(i).getFurnQty());
            Label id = idLabel(itemList.get(i).getFurnId());
            JFXButton btnSelectItem = creatButton();

            int finalI = i;
            btnSelectItem.setOnAction(event -> {
               Image image = imageView.getImage();
               imgSelectedItem.setImage(image);
               lblFurnId.setText(itemList.get(finalI).getFurnId());
               lblDesc.setText(itemList.get(finalI).getFurnDescription());
               txtPrice.setText(String.valueOf(itemList.get(finalI).getFurnPrice()));
               lblStock.setText(String.valueOf(itemList.get(finalI).getFurnQty()));
               lblStock.setVisible(true);
               txtQty.clear();
               txtQty.requestFocus();

               for (Furniture item : itemList){
                   if (item.getFurnId().equals(lblFurnId.getText())){
                       selectedItem = item;
                   }
               }
            });
            pane.getChildren().addAll(imageView, qty, id, desc, btnSelectItem);
            gridPane.add(pane, i % 4, i / 4);
        }

        ScrollPane sp = new ScrollPane(gridPane);
        ItemScrollPane.setContent(sp);
        ItemScrollPane.setFitToHeight(true);
        ItemScrollPane.setFitToWidth(true);
    }

    private Label idLabel(String furnId) {
        Label lbl = new Label(furnId);
        lbl.setAlignment(Pos.CENTER);
        lbl.setPrefHeight(10);
        lbl.setPrefWidth(60);
        lbl.setStyle("-fx-background-color: #33001a; -fx-background-radius: 0px 10px 10px 0px; -fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        lbl.setLayoutX(10);
        lbl.setLayoutY(150);
        return lbl;
    }

    private Label qtyLabel(int qty) {
        Label lbl = new Label(Integer.toString(qty));
        lbl.setPrefWidth(30);
        lbl.setPrefHeight(30);
        lbl.setAlignment(Pos.CENTER);
        lbl.setStyle("-fx-background-color:  #00e68a; -fx-background-radius: 50; -fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        lbl.setLayoutX(145);
        lbl.setLayoutY(5);
        return lbl;
    }

    private Label descLabel(String furnDescription , double price) {
        Label lbl = new Label(furnDescription + " : " + String.valueOf(price) + "/=");
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #00cc7a;");
        lbl.setLayoutX(10);
        lbl.setLayoutY(180);
        return lbl;
    }

    private JFXButton creatButton() {
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
        ObservableList<String> obList = FXCollections.observableArrayList("Cash", "Advance");
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
        LocalDate localDate = LocalDate.now();
        lblDate.setText(String.valueOf(localDate));
    }

    private void setNextOrderId() {
        try {
            String currentOrderId = PlaceOrderRepo.getCurrentOrderId();
            String nextOrderId = PlaceOrderRepo.getNextOrderId(currentOrderId);

            lblOrderId.setText(nextOrderId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void txtContactNoClickOnAction(ActionEvent actionEvent) {
        if (Regex.setTextColor(lk.ijse.kumudufurniture.util.TextField.PHONE,txtContactNo)){
            getCustomer();
            btnPlaceOrder.requestFocus();
        }else {
            new Alert(Alert.AlertType.ERROR, "Invalid Contact No!").show();
            txtContactNo.requestFocus();
        }
    }

    private void getCustomer() {
        try {
            customer = CustomerRepo.getCustomer(txtContactNo.getText());
            if (customer != null) {
                lblCusId.setText(customer.getId());
                lblCusName.setText(customer.getName());
            }else {
                new Alert(Alert.AlertType.ERROR, "Customer Not Found!").show();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnAddNewCustomerClickOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(this.getClass().getResource("/view/AddCustomerForm.fxml"))));

        stage.setTitle("Add New Customer Form");
        stage.centerOnScreen();
        stage.show();
    }

    public void btnAddToCartClickOnAction(ActionEvent actionEvent) {
        boolean isValid = checkDetailsBeforeAddingToCart();

        if (isValid) {

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
        } else if (!Regex.checkTextField(lk.ijse.kumudufurniture.util.TextField.PRICE,txtPrice)) {
            new Alert(Alert.AlertType.WARNING, "Invalid Price").show();
            txtPrice.requestFocus();
            return false;
        }else if (txtQty.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please Enter Quantity!").show();
            txtQty.requestFocus();
            return false;
        } else if (!Regex.checkTextField(lk.ijse.kumudufurniture.util.TextField.QTY,txtQty)) {
            new Alert(Alert.AlertType.WARNING, "Invalid Quantity!").show();
            txtQty.requestFocus();
            return false;
        }else {
            try {
                boolean isAvailable = FurnitureRepo.checkQty(lblFurnId.getText(), Integer.parseInt(txtQty.getText()));
                if (isAvailable) {
                    return true;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    private void calculateTotalPrice() {
        double total = 0.0;
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
        if (paymentType.equals("Cash")){
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
        } else if (paymentType.equals("Advance")) {
            if (txtPayment.getText().trim().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please Enter Payment Amount!").show();
                txtPayment.requestFocus();
                return false;
            } else if (!Regex.checkTextField(lk.ijse.kumudufurniture.util.TextField.PRICE,txtPayment)) {
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
            String paymentType = cmbPaymentType.getValue();
            double payment;

            if (paymentType.equals("Cash")){
                payment = Double.parseDouble(lblTotal.getText());
            }else {
                payment = Double.parseDouble(txtPayment.getText());
            }
            double total = Double.parseDouble(lblTotal.getText());

            Order order;
            if (paymentType.equals("Advance")) {
                order = new Order(orderId, cusId, orderDate, paymentType, payment, total);
            } else {
                order = new Order(orderId, cusId, orderDate, paymentType, null, total);
            }

            List<OrderDetail> odList = new ArrayList<>();
            for (int i = 0; i < tblOrderCart.getItems().size(); i++) {
                CartTm cartTm = cartList.get(i);

                OrderDetail od = new OrderDetail(
                        orderId,
                        cartTm.getItemId(),
                        cartTm.getQty(),
                        cartTm.getUnitPrice()
                );
                odList.add(od);
            }

            PlaceOrder po = new PlaceOrder(order, odList);

            try {
                boolean isPlaced = PlaceOrderRepo.placeOrder(po);
                if (isPlaced) {
                    new Alert(Alert.AlertType.INFORMATION, "Order Placed Successfully!").show();

                    cartList.clear();
                    tblOrderCart.refresh();
                    txtQty.clear();
                    txtPayment.clear();
                    lblTotal.setText("0.0");

                    if (customer.getEmail() != null){
                        sendReceipt();
                    }

                } else {
                    new Alert(Alert.AlertType.WARNING, "Order Not Placed!").show();
                }
            } catch (SQLException i) {
                throw new RuntimeException(i);
            }
        }
    }

    private void sendReceipt() {
        String title = "Order Placed Successfully! - " + lblOrderId.getText();
        String subject = "Order Receipt";
        String msg = "Your Order Receipt for Order Id: " + lblOrderId.getText() + " is attached with this email. Thank you for shopping with us!";
        String email = customer.getEmail();

        try {
            Mail.setMail(title, subject, msg, email, getBill());
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

    public void txtPaymentClickOnAction(ActionEvent actionEvent) {
        btnPlaceOrderClickOnAction(actionEvent);
    }

    public void txtPriceClickOnAction(ActionEvent actionEvent) {
        btnAddToCartClickOnAction(actionEvent);
    }

    public void txtContactOnKeyRelesed(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.kumudufurniture.util.TextField.PHONE,txtContactNo);
    }

}
