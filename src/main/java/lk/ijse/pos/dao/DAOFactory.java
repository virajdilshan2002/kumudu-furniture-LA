package lk.ijse.pos.dao;

import lk.ijse.pos.bo.custom.impl.FurnitureBoImpl;
import lk.ijse.pos.dao.custom.impl.CustomerDAOImpl;
import lk.ijse.pos.dao.custom.impl.FurnitureDAOImpl;
import lk.ijse.pos.dao.custom.impl.OrderDAOImpl;
import lk.ijse.pos.dao.custom.impl.OrderDetailDAOImpl;

public class DAOFactory {
    private static DAOFactory daoFactory;
    private static CustomerDAOImpl customerDAO;
    private static FurnitureDAOImpl furnitureDAO;
    private static OrderDAOImpl orderDAO;
    private static OrderDetailDAOImpl orderDetailDAO;
    private DAOFactory(){
        customerDAO = new CustomerDAOImpl();
        furnitureDAO = new FurnitureDAOImpl();
        orderDAO = new OrderDAOImpl();
        orderDetailDAO = new OrderDetailDAOImpl();
    }

    public static DAOFactory getInstance(){
        return daoFactory = daoFactory == null ? new DAOFactory() : daoFactory;
    }

    //enum
    public enum DAOType{
        CUSTOMER,FURNITURE,ORDER,ORDER_DETAIL
    }

    public CrudDAO getDAO(DAOType type){
        return switch (type){
            case CUSTOMER -> customerDAO;
            case FURNITURE -> furnitureDAO;
            case ORDER -> orderDAO;
            case ORDER_DETAIL -> orderDetailDAO;
        };
    }
}
