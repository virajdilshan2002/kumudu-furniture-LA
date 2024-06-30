package lk.ijse.pos.bo.custom.impl;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import lk.ijse.pos.bo.custom.PlaceOrderBo;
import lk.ijse.pos.dao.DAOFactory;
import lk.ijse.pos.dao.SQLUtil;
import lk.ijse.pos.dao.custom.FurnitureDAO;
import lk.ijse.pos.dao.custom.OrderDAO;
import lk.ijse.pos.dao.custom.OrderDetailDAO;
import lk.ijse.pos.db.DBConnection;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.dto.FurnitureDTO;
import lk.ijse.pos.dto.OrderDTO;
import lk.ijse.pos.dto.OrderDetailDTO;
import lk.ijse.pos.entity.Order;
import lk.ijse.pos.entity.OrderDetail;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlaceOrderBoImpl implements PlaceOrderBo {

    OrderDAO orderDAO = (OrderDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.ORDER);
    OrderDetailDAO orderDetailDAO = (OrderDetailDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.ORDER_DETAIL);
    FurnitureDAO furnitureDAO = (FurnitureDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.FURNITURE);

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
    public String getLastOrderId() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = SQLUtil.execute("SELECT orderId FROM orders ORDER BY CAST(SUBSTRING(orderId, 2) AS UNSIGNED) DESC LIMIT 1");
        if (resultSet.next()) {
            return resultSet.getString(1);
        }
        return null;
    }

    @Override
    public String generateNewOrderId() throws SQLException, ClassNotFoundException {
        String lastOrderId = getLastOrderId();
        if (lastOrderId != null) {
            String[] split = lastOrderId.split("#");
            int id = Integer.parseInt(split[1],10);
            return "#" + String.format("%05d", ++id);
        }
        return "#00001";
    }

    @Override
    public boolean placeOrder(OrderDTO orderDTO, List<OrderDetailDTO> dtoList){
        //Convert DTO to Entity
        Order order = new Order(
                orderDTO.getOrderId(),
                orderDTO.getCusId(),
                orderDTO.getOrderDate(),
                orderDTO.getPaymentType(),
                orderDTO.getAdvancedPayment(),
                orderDTO.getTotalPayment()
        );

        //Convert DTO List to Entity List
        ArrayList<OrderDetail> entityList = new ArrayList<>();
        for (OrderDetailDTO dto : dtoList){
            entityList.add(new OrderDetail(
                    dto.getOrderId(),
                    dto.getFurnId(),
                    dto.getQty(),
                    dto.getUnitPrice()
            ));
        }

        //Transaction
        Connection connection;
        try {
            connection = DBConnection.getDbConnection().getConnection();
            connection.setAutoCommit(false);

            if (orderDAO.save(order)) {
                System.out.println("Order Query Executed To Pool!");

                if (orderDetailDAO.save(entityList)) {
                    System.out.println("Order Detail Query Executed To Pool!");

                    if (furnitureDAO.updateQty(entityList)) {
                        System.out.println("Furniture Query Executed To Pool!");

                        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
                        ButtonType no = new ButtonType("No",ButtonBar.ButtonData.CANCEL_CLOSE);

                        Optional<ButtonType> type = new Alert(Alert.AlertType.INFORMATION,"Are You Sure To Place This Order ?",yes,no).showAndWait();
                        if (type.orElse(no) == yes){
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
            throw new RuntimeException(e);
        }
    }
}
