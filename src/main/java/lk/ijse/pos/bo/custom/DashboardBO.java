package lk.ijse.pos.bo.custom;

import lk.ijse.pos.bo.SuperBO;

import java.sql.SQLException;

public interface DashboardBO extends SuperBO {
    int getCompletedOrdersCount() throws SQLException, ClassNotFoundException;

    int getToBePaidOrdersCount() throws SQLException, ClassNotFoundException;

    int getFurnitureCount() throws SQLException, ClassNotFoundException;

    int getCustomersCount() throws SQLException, ClassNotFoundException;

    boolean isExistsOrder(String id) throws SQLException, ClassNotFoundException;
}
