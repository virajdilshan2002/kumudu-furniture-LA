package lk.ijse.pos.bo.custom;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import lk.ijse.pos.bo.SuperBO;
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

public interface PlaceOrderBo extends SuperBO {
    CustomerDTO searchCustomer(String id) throws SQLException, ClassNotFoundException;
    FurnitureDTO searchItem(String code) throws SQLException, ClassNotFoundException;
    boolean existItem(String code) throws SQLException, ClassNotFoundException;
    boolean existCustomer(String code) throws SQLException, ClassNotFoundException;
    String generateOrderID() throws SQLException, ClassNotFoundException ;
    ArrayList<CustomerDTO> getAllCustomers() throws SQLException, ClassNotFoundException;
    ArrayList<FurnitureDTO> getAllItems() throws SQLException, ClassNotFoundException;
    boolean placeOrder(String orderId, LocalDate orderDate, String customerId, List<OrderDetailDTO> orderDetails);
    FurnitureDTO findItem(String code);
    String getCurrentOrderId() throws SQLException, ClassNotFoundException;
    String getNextOrderId(String currentOrderId);
    boolean placeOrder(Order order, List<OrderDetail> odList);
}
