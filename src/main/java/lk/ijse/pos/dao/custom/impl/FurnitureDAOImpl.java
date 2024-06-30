package lk.ijse.pos.dao.custom.impl;

import lk.ijse.pos.dao.SQLUtil;
import lk.ijse.pos.dao.custom.FurnitureDAO;
import lk.ijse.pos.entity.Furniture;
import lk.ijse.pos.entity.OrderDetail;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FurnitureDAOImpl implements FurnitureDAO {
    @Override
    public List<Furniture> getAll() throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.execute("SELECT * FROM furniture");
        List<Furniture> itemList = new ArrayList<>();

        while (rst.next()) {
            String furnId = rst.getString(1);

            InputStream fis = rst.getBinaryStream(2);
            File file = convertInputStreamToFile(fis);

            String furnDesc = rst.getString(3);
            String furnWoodType = rst.getString(4);
            String furnColor = rst.getString(5);
            double furnPrice = rst.getDouble(6);
            int furnQty = rst.getInt(7);

            Furniture item = new Furniture(furnId, file, furnDesc, furnWoodType, furnColor, furnPrice, furnQty);

            itemList.add(item);
        }
        return itemList;
    }

    @Override
    public boolean add(Furniture item) throws SQLException, ClassNotFoundException {
        return SQLUtil.addFurniture("INSERT INTO furniture VALUES (?, ?, ?, ?, ?, ?, ?)", item);
    }

    @Override
    public boolean update(Furniture item) throws SQLException, ClassNotFoundException {
        return SQLUtil.execute("UPDATE furniture SET furnDescription = ? , furnWoodType = ? , furnColor = ? , furnPrice = ? , furnQty = ? WHERE furnId = ?",
                item.getFurnDescription(),
                item.getFurnWoodType(),
                item.getFurnColor(),
                item.getFurnPrice(),
                item.getFurnQty(),
                item.getFurnId()
        );
    }

    @Override
    public boolean exist(String id) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public String generateNewID() throws SQLException, ClassNotFoundException {
        String currentFurnId = getCurrentFurnId();
        if (currentFurnId != null) {
            String[] split = currentFurnId.split("F");
            int id = Integer.parseInt(split[1], 10); // Explicitly specify radix 10
            return "F" + String.format("%05d", ++id);
        }else {
            return "F00001";
        }
    }

    @Override
    public boolean delete(String id) throws SQLException, ClassNotFoundException {
        return SQLUtil.execute("DELETE FROM furniture WHERE furnId = ?",id);
    }

    @Override
    public Furniture search(String id) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = SQLUtil.execute("SELECT * FROM furniture WHERE furnId = ?", id);

        if (resultSet.next()) {
            String furnId = resultSet.getString(1);

            InputStream fis = resultSet.getBinaryStream(2);
            File file = convertInputStreamToFile(fis);

            String furnDesc = resultSet.getString(3);
            String furnWoodType = resultSet.getString(4);
            String furnColor = resultSet.getString(5);
            double furnPrice = resultSet.getDouble(6);
            int furnQty = resultSet.getInt(7);

            return new Furniture(furnId, file, furnDesc, furnWoodType, furnColor, furnPrice, furnQty);
        }
        return null;
    }

    @Override
    public boolean updateQty(List<OrderDetail> odList) throws SQLException, ClassNotFoundException {
        for (OrderDetail od : odList) {
            if (!updateQty(od)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean updateQty(OrderDetail od) throws SQLException, ClassNotFoundException {
        return SQLUtil.execute("UPDATE furniture SET furnQty = furnQty - ? WHERE furnId = ?",
                od.getQty(),
                od.getFurnId()
        );
    }

    @Override
    public int checkAvailableQty(String furnId, int orderQty) throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.execute("SELECT furnQty FROM furniture WHERE furnId = ?", furnId);

        int qty = 0;
        if (rst.next()) {
            qty = rst.getInt(1);
            if (qty >= orderQty) {
                return qty;
            }
        }
        return qty;
    }

    @Override
    public boolean updateImage(File file, String furnId) throws SQLException, ClassNotFoundException {
        return SQLUtil.updateImageFile("UPDATE furniture SET imageFile = ? WHERE furnId = ?",
                file,
                furnId
        );
    }

    @Override
    public int getFurnitureCount() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = SQLUtil.execute("SELECT COUNT(*) FROM furniture");
        if (resultSet.next()) {
            return resultSet.getInt(1);
        }
        return 0;
    }

    @Override
    public String getCurrentFurnId() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = SQLUtil.execute("SELECT furnId FROM furniture ORDER BY CAST(SUBSTRING(furnId, 2) AS UNSIGNED) DESC LIMIT 1");

        if (resultSet.next()) {
            return resultSet.getString(1);
        }
        return null;
    }

    @Override
    public File convertInputStreamToFile(InputStream inputStream) {
        File tempFile;
        try {
            tempFile = File.createTempFile("tempImg", ".jpg");

            try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return tempFile;
    }
}
