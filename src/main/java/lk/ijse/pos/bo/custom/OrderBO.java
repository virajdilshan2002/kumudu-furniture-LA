package lk.ijse.pos.bo.custom;

import lk.ijse.pos.bo.SuperBO;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.dto.OrderDTO;
import lk.ijse.pos.entity.Customer;
import lk.ijse.pos.entity.Order;
import lk.ijse.pos.entity.OrderDetail;
import lk.ijse.pos.view.tdm.AdvanceSearchTm;

import java.sql.SQLException;
import java.util.List;

public interface OrderBO extends SuperBO {
    boolean payOrder(String orderId) throws SQLException, ClassNotFoundException;

    boolean refundOrder(String id, List<AdvanceSearchTm> purchaseList) throws SQLException;

    boolean isExistsOrder(String id) throws SQLException, ClassNotFoundException;

    List<OrderDTO> getCompletedOrdersList() throws SQLException, ClassNotFoundException;

    List<OrderDTO> getToBePaidOrders() throws SQLException, ClassNotFoundException;

    OrderDTO getOrder(String orderId) throws SQLException, ClassNotFoundException;

    CustomerDTO getCustomer(String id) throws SQLException, ClassNotFoundException;
    List<AdvanceSearchTm> getOrderItems(String id) throws SQLException, ClassNotFoundException;
}
