package lk.ijse.pos.dao.custom.impl;

import lk.ijse.pos.dao.SQLUtil;
import lk.ijse.pos.dao.custom.OrderDetailDAO;
import lk.ijse.pos.entity.OrderDetail;
import lk.ijse.pos.view.tdm.AdvanceSearchTm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailDAOImpl implements OrderDetailDAO {
    @Override
    public boolean save(List<OrderDetail> odList) throws SQLException, ClassNotFoundException {
        for (OrderDetail od : odList) {
            if(!save(od)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean save(OrderDetail od) throws SQLException, ClassNotFoundException {
        return SQLUtil.execute("INSERT INTO orderdetail VALUES (?,?,?,?)",
                od.getOrderId(),
                od.getFurnId(),
                od.getQty(),
                od.getUnitPrice()
        );
    }

    @Override
    public List<AdvanceSearchTm> getOrderItems(String id) throws SQLException, ClassNotFoundException {
        ResultSet odResultSet = SQLUtil.execute("SELECT * FROM orderDetail WHERE orderId = ?",id);

        List<AdvanceSearchTm> list = new ArrayList<>();

        while (odResultSet.next()) {
            String furnId = odResultSet.getString(2);
            String qty = odResultSet.getString(3);
            String unitPrice = odResultSet.getString(4);
            double total = Integer.parseInt(qty) * Double.parseDouble(unitPrice);

            ResultSet furnResultSet = SQLUtil.execute("SELECT furnDescription FROM furniture WHERE furnId = ?", furnId);

            if (furnResultSet.next()) {
                String furnDesc = furnResultSet.getString(1);
                AdvanceSearchTm tm = new AdvanceSearchTm(furnId, furnDesc, Integer.parseInt(qty), Double.parseDouble(unitPrice), total);
                list.add(tm);
            }
        }
        return list;
    }

    @Override
    public List<OrderDetail> getAll() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean add(OrderDetail entity) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean update(OrderDetail entity) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean isExists(String id) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public String generateNewID() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean delete(String id) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public OrderDetail search(String id) throws SQLException, ClassNotFoundException {
        return null;
    }
}
