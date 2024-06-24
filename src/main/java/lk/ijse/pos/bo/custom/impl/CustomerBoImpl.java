package lk.ijse.pos.bo.custom.impl;

import lk.ijse.pos.bo.custom.CustomerBo;
import lk.ijse.pos.dao.custom.CustomerDAO;
import lk.ijse.pos.dao.custom.impl.CustomerDAOImpl;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.entity.Customer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerBoImpl implements CustomerBo {
    CustomerDAO customerDAO = new CustomerDAOImpl();

    @Override
    public List<CustomerDTO> getAll() throws SQLException, ClassNotFoundException {
        List<CustomerDTO> customerDTOS = new ArrayList<>();

        List<Customer> customers = customerDAO.getAll();
        for (Customer customer : customers){
            CustomerDTO customerDTO = new CustomerDTO(customer.getId(),customer.getName(),customer.getAddress(),customer.getEmail(),customer.getContact());
            customerDTOS.add(customerDTO);
        }
        return customerDTOS;
    }

    @Override
    public boolean add(CustomerDTO dto) throws SQLException, ClassNotFoundException {
        Customer customer = new Customer(dto.getId(), dto.getName(), dto.getAddress(), dto.getEmail(), dto.getContact());
        return customerDAO.add(customer);
    }

    @Override
    public boolean update(CustomerDTO dto) throws SQLException, ClassNotFoundException {
        Customer customer = new Customer(dto.getId(), dto.getName(), dto.getAddress(), dto.getEmail(), dto.getContact());
        return customerDAO.update(customer);
    }

    @Override
    public boolean exist(String id) throws SQLException, ClassNotFoundException {
        return customerDAO.exist(id);
    }

    @Override
    public String generateNewID() throws SQLException, ClassNotFoundException {
        return customerDAO.generateNewID();
    }

    @Override
    public boolean delete(String id) throws SQLException, ClassNotFoundException {
        return customerDAO.delete(id);
    }

    @Override
    public CustomerDTO search(String id) throws SQLException, ClassNotFoundException {
        Customer customer = customerDAO.search(id);
        return new CustomerDTO(customer.getId(), customer.getName(), customer.getAddress(), customer.getEmail(), customer.getContact());    }
}
