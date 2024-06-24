package lk.ijse.pos.dao.custom.impl;

import lk.ijse.pos.dao.SQLUtil;
import lk.ijse.pos.dao.custom.CustomerDAO;
import lk.ijse.pos.entity.Customer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAOImpl implements CustomerDAO {
    @Override
    public List<Customer> getAll() throws SQLException, ClassNotFoundException {
        List<Customer> customerList = new ArrayList<>();
        ResultSet rst = SQLUtil.execute("SELECT * FROM Customer");

        while(rst.next()){
            String id = rst.getString(1);
            String name = rst.getString(2);
            String address = rst.getString(3);
            String email = rst.getString(4);
            String contact = rst.getString(5);

            Customer customer = new Customer(id,name,address,email, contact);
            customerList.add(customer);
        }
        return customerList;
    }

    @Override
    public boolean add(Customer customer) throws SQLException, ClassNotFoundException {
        return SQLUtil.execute("INSERT INTO customer VALUES(?, ?, ?, ?, ?)",
                customer.getId(),
                customer.getName(),
                customer.getAddress(),
                customer.getEmail(),
                customer.getContact()
        );
    }

    @Override
    public boolean update(Customer customer) throws SQLException, ClassNotFoundException {
        return SQLUtil.execute("UPDATE customer SET cusName = ?, cusAddress = ?, cusEmail = ?, cusContact = ? WHERE cusId = ?",
                customer.getName(),
                customer.getAddress(),
                customer.getEmail(),
                customer.getContact(),
                customer.getId()
        );
    }

    @Override
    public boolean exist(String id) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public String generateNewID() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean delete(String id) throws SQLException, ClassNotFoundException {
        return SQLUtil.execute("DELETE FROM customer WHERE cusId =?",id);
    }

    @Override
    public Customer search(String id) throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public String getCurrentCusId() throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.execute("SELECT cusId FROM customer ORDER BY CAST(SUBSTRING(cusId, 2) AS UNSIGNED) DESC LIMIT 1");
        if (rst.next()){
            return rst.getString(1);
        }
        return null;
    }

    @Override
    public String getNextCusId(String currentId) {
        if (currentId != null) {
            String[] split = currentId.split("C");
            int id = Integer.parseInt(split[1], 10); // Explicitly specify radix 10
            return "C" + String.format("%05d", ++id);
        }else {
            return "C00001";
        }
    }

    @Override
    public int getCustomersCount() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = SQLUtil.execute("SELECT COUNT(*) FROM customer");
        if (resultSet.next()){
            return resultSet.getInt(1);
        }
        return 0;
    }
}
