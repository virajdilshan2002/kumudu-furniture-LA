package lk.ijse.pos.dao.custom.impl;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import lk.ijse.pos.dao.SQLUtil;
import lk.ijse.pos.dao.custom.OrderDAO;
import lk.ijse.pos.db.DBConnection;
import lk.ijse.pos.entity.Customer;
import lk.ijse.pos.entity.Order;
import lk.ijse.pos.util.PaymentType;
import lk.ijse.pos.view.tdm.AdvanceSearchTm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDAOImpl implements OrderDAO {

    @Override
    public boolean pay(String orderId) throws SQLException, ClassNotFoundException {
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No",ButtonBar.ButtonData.CANCEL_CLOSE);

        Optional<ButtonType> type = new Alert(Alert.AlertType.INFORMATION,"Are You Sure To Pay This Order ?",yes,no).showAndWait();
        if (type.orElse(no)==yes){
            return SQLUtil.execute("UPDATE orders SET advancePayment = NULL WHERE orderId = ?", orderId);
        }
        return false;
    }

    @Override
    public boolean refund(String id, List<AdvanceSearchTm> purchaseList) throws SQLException {
        Connection connection;
        try {
            connection = DBConnection.getDbConnection().getConnection();
            connection.setAutoCommit(false);

            String updateFurnQtySql = "UPDATE furniture SET furnQty = furnQty + ? WHERE furnId = ?";

            for (AdvanceSearchTm tm : purchaseList) {
                boolean isUpdated = SQLUtil.execute(updateFurnQtySql, tm.getQty(), tm.getFurnId());
                if (!isUpdated) {
                    connection.rollback();
                    return false;
                }
            }

            String deleteOrderSql = "DELETE FROM orders WHERE orderId = ?";
            boolean isDeleted = SQLUtil.execute(deleteOrderSql, id);
            if (!isDeleted) {
                connection.rollback();
                return false;
            }
            connection.commit();
            connection.setAutoCommit(true);
            return true;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isExists(String id) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = SQLUtil.execute("SELECT * FROM orders WHERE orderId = ?", id);
        if (resultSet.next()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean add(Order order) throws SQLException, ClassNotFoundException {
        return SQLUtil.execute("INSERT INTO orders VALUES (?,?,?,?,?,?)",
                order.getOrderId(),
                order.getCusId(),
                order.getOrderDate(),
                String.valueOf(order.getPaymentType()),
                order.getAdvancedPayment(),
                order.getTotalPayment()
        );
    }

    @Override
    public List<Order> getCompletedOrdersList() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = SQLUtil.execute("SELECT * FROM orders WHERE advancePayment IS NULL ORDER BY CAST(SUBSTRING(orderId, 2) AS UNSIGNED) DESC");
        List<Order> odList = new ArrayList<>();

        while (resultSet.next()) {
            String orderId = resultSet.getString(1);
            String cusId = resultSet.getString(2);
            String orderDate = resultSet.getString(3);
            PaymentType paymentType = PaymentType.valueOf(resultSet.getString(4));
            double totalPayment = resultSet.getDouble(6);

            Order order = new Order(orderId, cusId, orderDate, paymentType,null, totalPayment);
            odList.add(order);
        }
        return odList;
    }

    @Override
    public List<Order> getToBePaidOrders() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = SQLUtil.execute("SELECT * FROM orders WHERE advancePayment IS NOT NULL ORDER BY CAST(SUBSTRING(orderId, 2) AS UNSIGNED) DESC");
        List<Order> list = new ArrayList<>();

        while (resultSet.next()) {
            String orderId = resultSet.getString(1);
            String cusId = resultSet.getString(2);
            String orderDate = resultSet.getString(3);
            PaymentType paymentType = PaymentType.valueOf(resultSet.getString(4));
            double advancePayment = resultSet.getDouble(5);
            double totalPayment = resultSet.getDouble(6);

            Order order = new Order(orderId, cusId, orderDate, paymentType, advancePayment, totalPayment );
            list.add(order);
        }
        return list;
    }

    @Override
    public Order getOrder(String orderId) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = SQLUtil.execute("SELECT * FROM orders WHERE orderId = ?", orderId);

        if (resultSet.next()) {
            String cusId = resultSet.getString(2);
            String orderDate = resultSet.getString(3);
            PaymentType paymentType = PaymentType.valueOf(resultSet.getString(4));
            double advancePayment = resultSet.getDouble(5);
            double totalPayment = resultSet.getDouble(6);

            return new Order(orderId, cusId, orderDate, paymentType, advancePayment, totalPayment);
        };
        return null;
    }

    @Override
    public Customer getCustomer(String id) throws SQLException, ClassNotFoundException {
        ResultSet cusResult = SQLUtil.execute("SELECT * FROM customer WHERE cusId = ?", id);

        if (cusResult.next()) {
            String cusId = cusResult.getString(1);
            String cusName = cusResult.getString(2);
            String address = cusResult.getString(3);
            String email = cusResult.getString(4);
            String contact = cusResult.getString(5);

            return new Customer(cusId, cusName, address, email, contact);
        }
        return null;
    }

    @Override
    public int getToBePaidOrdersCount() throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.execute("SELECT COUNT(*) FROM orders WHERE advancePayment IS NOT NULL");

        if (rst.next()) {
            return rst.getInt(1);
        }
        return 0;
    }

    @Override
    public int getCompletedOrdersCount() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = SQLUtil.execute("SELECT COUNT(*) FROM orders WHERE advancePayment IS NULL");

        if (resultSet.next()) {
            return resultSet.getInt(1);
        }
        return 0;
    }

    @Override
    public List<Order> getAll() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean update(Order entity) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public String generateNewID() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean delete(String id) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public Order search(String id) throws SQLException, ClassNotFoundException {
        return null;
    }
}
