package lk.ijse.pos.bo.custom.impl;

import lk.ijse.pos.bo.custom.OrderBO;
import lk.ijse.pos.dao.DAOFactory;
import lk.ijse.pos.dao.custom.OrderDAO;
import lk.ijse.pos.dao.custom.OrderDetailDAO;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.dto.OrderDTO;
import lk.ijse.pos.entity.Customer;
import lk.ijse.pos.entity.Order;
import lk.ijse.pos.view.tdm.AdvanceSearchTm;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderBOImpl implements OrderBO {

    OrderDAO orderDAO = (OrderDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.ORDER);
    OrderDetailDAO orderDetailDAO = (OrderDetailDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.ORDER_DETAIL);
    @Override
    public boolean payOrder(String orderId) throws SQLException, ClassNotFoundException {
        return orderDAO.pay(orderId);
    }

    @Override
    public boolean refundOrder(String id, List<AdvanceSearchTm> purchaseList) throws SQLException {
        return orderDAO.refund(id, purchaseList);
    }

    @Override
    public boolean isExistsOrder(String id) throws SQLException, ClassNotFoundException {
        return orderDAO.isExistsOrder(id);
    }

    @Override
    public List<OrderDTO> getCompletedOrdersList() throws SQLException, ClassNotFoundException {
        List<Order> orders = orderDAO.getCompletedOrdersList();

        List<OrderDTO> orderDTOS = new ArrayList<>();
        for (Order order : orders) {
            orderDTOS.add(new OrderDTO(order.getOrderId(),
                    order.getCusId(),
                    order.getOrderDate(),
                    order.getPaymentType(),
                    order.getAdvancedPayment(),
                    order.getTotalPayment()
            ));
        }
        return orderDTOS;
    }

    @Override
    public List<OrderDTO> getToBePaidOrders() throws SQLException, ClassNotFoundException {
        List<Order> orders = orderDAO.getToBePaidOrders();

        List<OrderDTO> orderDTOS = new ArrayList<>();
        for (Order order : orders) {
            orderDTOS.add(new OrderDTO(order.getOrderId(),
                    order.getCusId(),
                    order.getOrderDate(),
                    order.getPaymentType(),
                    order.getAdvancedPayment(),
                    order.getTotalPayment()
            ));
        }
        return orderDTOS;
    }

    @Override
    public OrderDTO getOrder(String orderId) throws SQLException, ClassNotFoundException {
        Order order = orderDAO.getOrder(orderId);
        return new OrderDTO(order.getOrderId(),
                order.getCusId(),
                order.getOrderDate(),
                order.getPaymentType(),
                order.getAdvancedPayment(),
                order.getTotalPayment()
        );
    }

    @Override
    public CustomerDTO getCustomer(String id) throws SQLException, ClassNotFoundException {
        Customer customer = orderDAO.getCustomer(id);
        return new CustomerDTO(customer.getId(),
                customer.getName(),
                customer.getAddress(),
                customer.getEmail(),
                customer.getContact()
        );
    }

    @Override
    public int getToBePaidOrdersCount() throws SQLException, ClassNotFoundException {
        return orderDAO.getToBePaidOrdersCount();
    }

    @Override
    public int getCompletedOrdersCount() throws SQLException, ClassNotFoundException {
        return orderDAO.getCompletedOrdersCount();
    }

    @Override
    public List<AdvanceSearchTm> getOrderItems(String id) throws SQLException, ClassNotFoundException {
        return orderDetailDAO.getOrderItems(id);
    }
}
