package lk.ijse.pos.bo.custom.impl;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import lk.ijse.pos.bo.custom.PlaceOrderBo;
import lk.ijse.pos.dao.SQLUtil;
import lk.ijse.pos.dao.custom.FurnitureDAO;
import lk.ijse.pos.dao.custom.OrderDAO;
import lk.ijse.pos.dao.custom.OrderDetailDAO;
import lk.ijse.pos.dao.custom.impl.FurnitureDAOImpl;
import lk.ijse.pos.dao.custom.impl.OrderDAOImpl;
import lk.ijse.pos.dao.custom.impl.OrderDetailDAOImpl;
import lk.ijse.pos.db.DBConnection;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.dto.FurnitureDTO;
import lk.ijse.pos.dto.OrderDetailDTO;
import lk.ijse.pos.entity.Order;
import lk.ijse.pos.entity.OrderDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlaceOrderBoImpl implements PlaceOrderBo {

    OrderDAO orderDAO = new OrderDAOImpl();
    OrderDetailDAO orderDetailDAO = new OrderDetailDAOImpl();
    FurnitureDAO furnitureDAO = new FurnitureDAOImpl();

    @Override
    public CustomerDTO searchCustomer(String id) throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public FurnitureDTO searchItem(String code) throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean existItem(String code) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean existCustomer(String code) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public String generateOrderID() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public ArrayList<CustomerDTO> getAllCustomers() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public ArrayList<FurnitureDTO> getAllItems() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean placeOrder(String orderId, LocalDate orderDate, String customerId, List<OrderDetailDTO> orderDetails) {
        return false;
    }

    @Override
    public FurnitureDTO findItem(String code) {
        return null;
    }

    @Override
    public String getCurrentOrderId() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = SQLUtil.execute("SELECT orderId FROM orders ORDER BY CAST(SUBSTRING(orderId, 2) AS UNSIGNED) DESC LIMIT 1");
        if (resultSet.next()) {
            return resultSet.getString(1);
        }
        return null;
    }

    @Override
    public String getNextOrderId(String currentOrderId) {
        if (currentOrderId != null) {
            String[] split = currentOrderId.split("#");
            int id = Integer.parseInt(split[1],10);
            return "#" + String.format("%05d", ++id);
        }
        return "#00001";
    }

    @Override
    public boolean placeOrder(Order order, List<OrderDetail> odList){

        Connection connection = null;
        try {
            connection = DBConnection.getDbConnection().getConnection();
            connection.setAutoCommit(false);
            boolean isOrderSaved = orderDAO.save(order);
            if (isOrderSaved) {
                System.out.println("Order Query Executed To Pool");
                boolean isOrderDetailSaved = orderDetailDAO.save(odList);
                if (isOrderDetailSaved) {
                    System.out.println("Order Detail Query Executed To Pool");
                    boolean isFurnQtyUpdate = furnitureDAO.updateQty(odList);
                    if (isFurnQtyUpdate) {
                        System.out.println("Furniture Query Executed To Pool");

                        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
                        ButtonType no = new ButtonType("No",ButtonBar.ButtonData.CANCEL_CLOSE);

                        Optional<ButtonType> type = new Alert(Alert.AlertType.INFORMATION,"Are You Sure To Place This Order ?",yes,no).showAndWait();
                        if (type.orElse(no) == yes){
                            connection.commit();
                            connection.setAutoCommit(true);
                            return true;
                        }
                        connection.rollback();
                        connection.setAutoCommit(true);
                        return false;
                    }
                }
            }
            connection.rollback();
            connection.setAutoCommit(true);
            return false;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
