package lk.ijse.pos.dao.custom;

import lk.ijse.pos.dao.CrudDAO;
import lk.ijse.pos.entity.Customer;
import lk.ijse.pos.entity.Order;
import lk.ijse.pos.view.tdm.AdvanceSearchTm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public interface OrderDAO extends CrudDAO<Order> {
    boolean pay(String orderId) throws SQLException, ClassNotFoundException;

    boolean refund(String id, List<AdvanceSearchTm> purchaseList) throws SQLException;

    boolean isExistsOrder(String id) throws SQLException, ClassNotFoundException;

    boolean save(Order order) throws SQLException, ClassNotFoundException;

    List<Order> getCompletedOrdersList() throws SQLException, ClassNotFoundException;

    List<Order> getToBePaidOrders() throws SQLException, ClassNotFoundException;

    Order getOrder(String orderId) throws SQLException, ClassNotFoundException;

    Customer getCustomer(String id) throws SQLException, ClassNotFoundException;

    int getToBePaidOrdersCount() throws SQLException, ClassNotFoundException;

    int getCompletedOrdersCount() throws SQLException, ClassNotFoundException;
}
