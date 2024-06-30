package lk.ijse.pos.dao;

import lk.ijse.pos.dao.custom.impl.*;

public class DAOFactory {
    private static DAOFactory daoFactory;
    private static CustomerDAOImpl customerDAO;
    private static FurnitureDAOImpl furnitureDAO;
    private static OrderDAOImpl orderDAO;
    private static OrderDetailDAOImpl orderDetailDAO;
    private static UserDAOImpl userDAO;
    private DAOFactory(){
        customerDAO = new CustomerDAOImpl();
        furnitureDAO = new FurnitureDAOImpl();
        orderDAO = new OrderDAOImpl();
        orderDetailDAO = new OrderDetailDAOImpl();
        userDAO = new UserDAOImpl();
    }

    public static DAOFactory getInstance(){
        return daoFactory = daoFactory == null ? new DAOFactory() : daoFactory;
    }

    //enum
    public enum DAOType{
        USER,CUSTOMER,FURNITURE,ORDER,ORDER_DETAIL
    }

    public CrudDAO getDAO(DAOType type){
        return switch (type){
            case USER -> userDAO;
            case CUSTOMER -> customerDAO;
            case FURNITURE -> furnitureDAO;
            case ORDER -> orderDAO;
            case ORDER_DETAIL -> orderDetailDAO;
        };
    }
}
