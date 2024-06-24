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
import lk.ijse.pos.bo.custom.OrderBo;
import lk.ijse.pos.dao.custom.CustomerDAO;
import lk.ijse.pos.dao.custom.FurnitureDAO;
import lk.ijse.pos.dao.custom.OrderDAO;
import lk.ijse.pos.dao.custom.impl.CustomerDAOImpl;
import lk.ijse.pos.dao.custom.impl.FurnitureDAOImpl;
import lk.ijse.pos.dao.custom.impl.OrderDAOImpl;
import lk.ijse.pos.db.DBConnection;
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

    FurnitureDAO furnitureDAO = new FurnitureDAOImpl();
    CustomerDAO customerDAO = new CustomerDAOImpl();
    OrderDAO orderDAO = new OrderDAOImpl();

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
            int count = orderDAO.getCompletedOrdersCount();
            lblCompletedOrdersCount.setText(String.valueOf(count));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    private void setToBePaidOrdersCount() {
        try {
            int count = orderDAO.getToBePaidOrdersCount();
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
            int count = furnitureDAO.getFurnitureCount();
            lblFurnCount.setText(String.valueOf(count));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void setCustomersCount() {
        try {
            int count = customerDAO.getCustomersCount();
            lblCustomersCount.setText(String.valueOf(count));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnCompletedOrdersClickOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane dashboardPane = FXMLLoader.load(this.getClass().getResource("/view/CompletedOrdersForm.fxml"));

        this.rootNode.getChildren().clear();
        this.rootNode.getChildren().add(dashboardPane);
    }

    public void btnOrdersToBePaidClickOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane dashboardPane = FXMLLoader.load(this.getClass().getResource("/view/PendingOrdersForm.fxml"));

        this.rootNode.getChildren().clear();
        this.rootNode.getChildren().add(dashboardPane);
    }

    public void btnGenerateClickOnAction(ActionEvent actionEvent) {
        String id = txtSearchReceipt.getText();
        try {
            boolean isFound = orderDAO.isExistsOrder(id);
            if (isFound) {
                txtSearchReceipt.setFocusColor(Paint.valueOf("Green"));
                txtSearchReceipt.setUnFocusColor(Paint.valueOf("Green"));

                JasperDesign jasperDesign = JRXmlLoader.load("src/main/resources/report/Order_Report.jrxml");
                JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

                Map<String, Object> data = new HashMap<>();
                data.put("ORDERID",id);

                JasperPrint jasperPrint =
                        JasperFillManager.fillReport(
                                jasperReport,
                                data,
                                DBConnection.getDbConnection().getConnection());

                JasperViewer.viewReport(jasperPrint,false);
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
            boolean isFound = orderDAO.isExistsOrder(id);
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
