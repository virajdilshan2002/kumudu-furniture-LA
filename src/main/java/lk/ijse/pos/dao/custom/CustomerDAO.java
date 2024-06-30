package lk.ijse.pos.dao.custom;

import lk.ijse.pos.dao.CrudDAO;
import lk.ijse.pos.entity.Customer;

import java.sql.SQLException;

public interface CustomerDAO extends CrudDAO<Customer> {

    Customer searchByContact(String contact) throws SQLException, ClassNotFoundException;

    String getCurrentCusId() throws SQLException, ClassNotFoundException;

    int getCustomersCount() throws SQLException, ClassNotFoundException;
}
