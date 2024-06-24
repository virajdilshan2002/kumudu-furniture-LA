package lk.ijse.pos.bo.custom.impl;

import lk.ijse.pos.bo.custom.OrderBo;
import lk.ijse.pos.dao.SQLUtil;
import lk.ijse.pos.dao.custom.OrderDAO;
import lk.ijse.pos.dao.custom.impl.OrderDAOImpl;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderBoImpl implements OrderBo {

    OrderDAO orderDAO = new OrderDAOImpl();
    @Override
    public int getToBePaidOrdersCount() throws SQLException, ClassNotFoundException {
        return orderDAO.getToBePaidOrdersCount();
    }
}
