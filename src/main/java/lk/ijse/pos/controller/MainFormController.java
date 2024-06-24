package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class MainFormController {
    public AnchorPane rootNode;
    public AnchorPane centerNode;

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
        AnchorPane dashboardPane = FXMLLoader.load(this.getClass().getResource("/view/DashboardForm.fxml"));

        this.centerNode.getChildren().clear();
        this.centerNode.getChildren().add(dashboardPane);
    }

    public void btnlogOutClickOnAction(ActionEvent actionEvent) throws IOException {
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No",ButtonBar.ButtonData.CANCEL_CLOSE);

        Optional<ButtonType> addType = new Alert(Alert.AlertType.INFORMATION,"Are You Sure To Log Out ?",yes,no).showAndWait();

        if (addType.orElse(no) == yes){
            AnchorPane rootNode = FXMLLoader.load(this.getClass().getResource("/view/LoginForm.fxml"));

            Scene scene = new Scene(rootNode);

            Stage stage = (Stage) this.rootNode.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setTitle("KUMUDU FURNITURE");
        }
    }

    public void btnPlaceOrderClickOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane placeOrderPane = FXMLLoader.load(this.getClass().getResource("/view/PlaceOrderForm.fxml"));

        this.centerNode.getChildren().clear();
        this.centerNode.getChildren().add(placeOrderPane);
        handleSelection(mainBtnPlaceOrder);
    }

    public void btnFurnitureClickOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane FurniturePane = FXMLLoader.load(this.getClass().getResource("/view/FurnitureForm.fxml"));

        this.centerNode.getChildren().clear();
        this.centerNode.getChildren().add(FurniturePane);
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
        AnchorPane customerPane = FXMLLoader.load(this.getClass().getResource("/view/CustomerForm.fxml"));

        this.centerNode.getChildren().clear();
        this.centerNode.getChildren().add(customerPane);
        handleSelection(mainBtnCustomer);
    }

    public void btnDashboardClickOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane dashboardPane = FXMLLoader.load(this.getClass().getResource("/view/DashboardForm.fxml"));

        this.centerNode.getChildren().clear();
        this.centerNode.getChildren().add(dashboardPane);
        handleSelection(mainBtnDashboard);
    }

    public void mainBtnCompletedOrdersClickOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane placeOrderPane = FXMLLoader.load(this.getClass().getResource("/view/CompletedOrdersForm.fxml"));

        this.centerNode.getChildren().clear();
        this.centerNode.getChildren().add(placeOrderPane);
        handleSelection(mainBtnCompletedOrders);
    }

    public void mainBtnPendingOrdersClickOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane placeOrderPane = FXMLLoader.load(this.getClass().getResource("/view/PendingOrdersForm.fxml"));

        this.centerNode.getChildren().clear();
        this.centerNode.getChildren().add(placeOrderPane);
        handleSelection(mainBtnPendingOrders);
    }
}
