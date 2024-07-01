package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXTextField;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import lk.ijse.pos.bo.BOFactory;
import lk.ijse.pos.bo.custom.CustomerBo;
import lk.ijse.pos.bo.custom.FurnitureBo;
import lk.ijse.pos.bo.custom.OrderBO;
import lk.ijse.pos.db.DBConnection;
import lk.ijse.pos.util.GenerateBill;
import lk.ijse.pos.util.NavigateTo;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class DashboardFormController {
    public AnchorPane rootNode;
    public Label lblCustomersCount;
    public Label lblFurnCount;
    public Label lblTime;
    public Label lblDate;
    public Label lblToBePaidOrdersCount;
    public Label lblCompletedOrdersCount;
    public Label lblDayStatus;
    public JFXTextField txtSearchReceipt;

    FurnitureBo furnitureBo = (FurnitureBo) BOFactory.getInstance().getBO(BOFactory.BOType.FURNITURE);
    CustomerBo customerBo = (CustomerBo) BOFactory.getInstance().getBO(BOFactory.BOType.CUSTOMER);
    OrderBO orderBo = (OrderBO) BOFactory.getInstance().getBO(BOFactory.BOType.ORDER);

    public void initialize() {
        setTime();
        setDate();
        setCustomersCount();
        setFurnitureCount();
        setToBePaidOrdersCount();
        setCompletedOrdersCount();
        setDayStatus();
    }

    private void setCompletedOrdersCount() {
        try {
            int count = orderBo.getCompletedOrdersCount();
            lblCompletedOrdersCount.setText(String.valueOf(count));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    private void setToBePaidOrdersCount() {
        try {
            int count = orderBo.getToBePaidOrdersCount();
            lblToBePaidOrdersCount.setText(String.valueOf(count));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void setTime() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    // Update the label with the current time
                    LocalDateTime currentTime = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                    lblTime.setText(formatter.format(currentTime));
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE); // Repeat indefinitely
        timeline.play();
    }

    private void setDate() {
        LocalDate localDate = LocalDate.now();
        lblDate.setText(String.valueOf(localDate));
    }

    private void setDayStatus() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        String status;

        if (hour >= 5 && hour < 12) {
            status = "Good Morning!";
        } else if (hour >= 12 && hour < 18) {
            status = "Good Afternoon!";
        } else {
            status = "Good Evening!";
        }
        lblDayStatus.setText(status);
    }

    private void setFurnitureCount() {
        try {
            int count = furnitureBo.getFurnitureCount();
            lblFurnCount.setText(String.valueOf(count));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void setCustomersCount() {
        try {
            int count = customerBo.getCustomersCount();
            lblCustomersCount.setText(String.valueOf(count));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnCompletedOrdersClickOnAction(ActionEvent actionEvent) throws IOException {
        String path = "/lk/ijse/pos/view/CompletedOrdersForm.fxml";
        NavigateTo.children(path, this.rootNode);
    }

    public void btnOrdersToBePaidClickOnAction(ActionEvent actionEvent) throws IOException {
        String path = "/lk/ijse/pos/view/PendingOrdersForm.fxml";
        NavigateTo.children(path, this.rootNode);
    }

    public void btnGenerateClickOnAction(ActionEvent actionEvent) {
        String id = txtSearchReceipt.getText();
        try {
            boolean isFound = orderBo.isExistsOrder(id);
            if (isFound) {
                txtSearchReceipt.setFocusColor(Paint.valueOf("Green"));
                txtSearchReceipt.setUnFocusColor(Paint.valueOf("Green"));
                GenerateBill.viewBill(id);
            } else {
                txtSearchReceipt.setFocusColor(Paint.valueOf("Red"));
                txtSearchReceipt.setUnFocusColor(Paint.valueOf("Red"));
                new Alert(Alert.AlertType.ERROR, "Order Not Found!").show();
            }
        } catch (SQLException | JRException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void txtSearchReceiptClickOnAction(ActionEvent actionEvent) {
        btnGenerateClickOnAction(actionEvent);
    }

    public void txtSearchReceiptOnKeyRelesedAction(KeyEvent keyEvent) {
        String id = txtSearchReceipt.getText();
        try {
            boolean isFound = orderBo.isExistsOrder(id);
            if (isFound) {
                txtSearchReceipt.setFocusColor(Paint.valueOf("Green"));
                txtSearchReceipt.setUnFocusColor(Paint.valueOf("Green"));
            } else {
                txtSearchReceipt.setFocusColor(Paint.valueOf("Red"));
                txtSearchReceipt.setUnFocusColor(Paint.valueOf("Red"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
