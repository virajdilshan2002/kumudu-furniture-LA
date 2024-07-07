package lk.ijse.pos.bo.custom;

import lk.ijse.pos.bo.SuperBO;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.dto.FurnitureDTO;
import lk.ijse.pos.dto.OrderDTO;
import lk.ijse.pos.dto.OrderDetailDTO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface PlaceOrderBo extends SuperBO {
    CustomerDTO searchCustomerByContact(String contact) throws SQLException, ClassNotFoundException;
    List<FurnitureDTO> getAllFurnitureItems() throws SQLException, ClassNotFoundException;
    String getLastOrderId() throws SQLException, ClassNotFoundException;
    String generateNewOrderId() throws SQLException, ClassNotFoundException;
    boolean placeOrder(OrderDTO order, List<OrderDetailDTO> odList);
    int availableItemQty(String furnId, int orderQty) throws SQLException, ClassNotFoundException;
}
