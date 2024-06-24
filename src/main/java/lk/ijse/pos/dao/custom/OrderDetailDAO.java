package lk.ijse.pos.dao.custom;

import lk.ijse.pos.entity.OrderDetail;
import lk.ijse.pos.view.tdm.AdvanceSearchTm;

import java.sql.SQLException;
import java.util.List;

public interface OrderDetailDAO {
    boolean save(List<OrderDetail> odList) throws SQLException, ClassNotFoundException;
    boolean save(OrderDetail od) throws SQLException, ClassNotFoundException;
    List<AdvanceSearchTm> getOrderItems(String id) throws SQLException, ClassNotFoundException;
}
