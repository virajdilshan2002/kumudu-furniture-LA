package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import lk.ijse.pos.util.NavigateTo;

import java.io.IOException;
import java.util.Optional;

public class MainFormController {
    @FXML
    private AnchorPane rootNode;
    @FXML
    private AnchorPane centerNode;
    @FXML
    public JFXButton mainBtnDashboard;
    public JFXButton mainBtnPlaceOrder;
    public JFXButton mainBtnFurniture;
    public JFXButton mainBtnCustomer;
    public JFXButton mainBtnCompletedOrders;
    public JFXButton mainBtnPendingOrders;
    public ImageView imgBusinessLogo;

    private JFXButton selectedButton;

    public void initialize() throws IOException {
        loadDashboard();
        handleSelection(mainBtnDashboard);
    }

    private void loadDashboard() throws IOException {
        String path = "/lk/ijse/pos/view/DashboardForm.fxml";
        NavigateTo.children(path, this.centerNode);
    }

    public void btnlogOutClickOnAction(ActionEvent actionEvent) throws IOException {
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No",ButtonBar.ButtonData.CANCEL_CLOSE);

        Optional<ButtonType> addType = new Alert(Alert.AlertType.INFORMATION,"Are You Sure To Log Out ?",yes,no).showAndWait();

        if (addType.orElse(no) == yes){
            String path = "/lk/ijse/pos/view/LoginForm.fxml";
            NavigateTo.parent(path, rootNode);
        }
    }

    public void btnPlaceOrderClickOnAction(ActionEvent actionEvent) throws IOException {
        String path = "/lk/ijse/pos/view/PlaceOrderForm.fxml";
        NavigateTo.children(path, this.centerNode);
        handleSelection(mainBtnPlaceOrder);
    }

    public void btnFurnitureClickOnAction(ActionEvent actionEvent) throws IOException {
        String path = "/lk/ijse/pos/view/FurnitureForm.fxml";
        NavigateTo.children(path, this.centerNode);
        handleSelection(mainBtnFurniture);
    }

    private void handleSelection(JFXButton button) {
        if(selectedButton != null){
            selectedButton.setStyle(""); // Deselect the previously selected button
        }
        selectedButton = button; // Set the new selected button
        selectedButton.setStyle("-fx-background-radius: 100px;\n" +
                "-fx-border-color: #00cc88;\n" +
                "    -fx-border-width: 1px;\n" +
                "    -fx-border-radius: 100px;\n" +
                "    -fx-background-color: #00ffaa;\n" +
                "    -fx-text-fill: #ffffff;"); // Apply the selected style
    }

    public void btnCustomerClickOnAction(ActionEvent actionEvent) throws IOException {
        String path = "/lk/ijse/pos/view/CustomerForm.fxml";
        NavigateTo.children(path, this.centerNode);
        handleSelection(mainBtnCustomer);
    }

    public void btnDashboardClickOnAction(ActionEvent actionEvent) throws IOException {
        loadDashboard();
        handleSelection(mainBtnDashboard);
    }

    public void mainBtnCompletedOrdersClickOnAction(ActionEvent actionEvent) throws IOException {
        String path = "/lk/ijse/pos/view/CompletedOrdersForm.fxml";
        NavigateTo.children(path, this.centerNode);
        handleSelection(mainBtnCompletedOrders);
    }

    public void mainBtnPendingOrdersClickOnAction(ActionEvent actionEvent) throws IOException {
        String path = "/lk/ijse/pos/view/PendingOrdersForm.fxml";
        NavigateTo.children(path, this.centerNode);
        handleSelection(mainBtnPendingOrders);
    }
}
