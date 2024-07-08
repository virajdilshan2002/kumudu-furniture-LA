package lk.ijse.pos.bo.custom.impl;

import lk.ijse.pos.bo.custom.DashboardBO;
import lk.ijse.pos.dao.DAOFactory;
import lk.ijse.pos.dao.custom.CustomerDAO;
import lk.ijse.pos.dao.custom.FurnitureDAO;
import lk.ijse.pos.dao.custom.OrderDAO;

import java.sql.SQLException;

public class DashboardBoImpl implements DashboardBO {

    OrderDAO orderDAO = (OrderDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.ORDER);
    FurnitureDAO furnitureDAO = (FurnitureDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.FURNITURE);
    CustomerDAO customerDAO = (CustomerDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.CUSTOMER);
    @Override
    public int getCompletedOrdersCount() throws SQLException, ClassNotFoundException {
        return orderDAO.getCompletedOrdersCount();
    }

    @Override
    public int getToBePaidOrdersCount() throws SQLException, ClassNotFoundException {
        return orderDAO.getToBePaidOrdersCount();
    }

    @Override
    public int getFurnitureCount() throws SQLException, ClassNotFoundException {
        return furnitureDAO.getFurnitureCount();
    }

    @Override
    public int getCustomersCount() throws SQLException, ClassNotFoundException {
        return customerDAO.getCustomersCount();
    }

    @Override
    public boolean isExistsOrder(String id) throws SQLException, ClassNotFoundException {
        return orderDAO.isExistsOrder(id);
    }
}
