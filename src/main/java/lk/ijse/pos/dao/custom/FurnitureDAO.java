package lk.ijse.pos.dao.custom;

import lk.ijse.pos.dao.CrudDAO;
import lk.ijse.pos.entity.Furniture;
import lk.ijse.pos.entity.OrderDetail;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

public interface FurnitureDAO extends CrudDAO<Furniture> {
    boolean updateQty(List<OrderDetail> odList) throws SQLException, ClassNotFoundException;

    boolean updateQty(OrderDetail od) throws SQLException, ClassNotFoundException;

    int checkAvailableQty(String furnId, int qty) throws SQLException, ClassNotFoundException;

    boolean updateImage(File file, String furnId) throws SQLException, ClassNotFoundException;

    int getFurnitureCount() throws SQLException, ClassNotFoundException;

    String getCurrentFurnId() throws SQLException, ClassNotFoundException;

    File convertInputStreamToFile(InputStream inputStream);
}
