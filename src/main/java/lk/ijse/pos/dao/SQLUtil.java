package lk.ijse.pos.dao;

import lk.ijse.pos.db.DBConnection;
import lk.ijse.pos.entity.Furniture;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLUtil {
    public static <T>T execute(String sql, Object... args) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getDbConnection().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        for (int i = 0; i < args.length; i++){
            pstm.setObject((i+1),args[i]);
        }

        if(sql.startsWith("SELECT")){
            return (T) pstm.executeQuery();
        }else {
            return (T) (Boolean) (pstm.executeUpdate() > 0);
        }
    }

    public static boolean addFurnitureItem(String sql, Furniture item) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getDbConnection().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        File file = item.getImageFile();
        try (FileInputStream fis = new FileInputStream(file)) {
            pstm.setObject(1, item.getFurnId());
            pstm.setBinaryStream(2, fis, (int) file.length());
            pstm.setObject(3, item.getFurnDescription());
            pstm.setObject(4, item.getFurnWoodType());
            pstm.setObject(5, item.getFurnColor());
            pstm.setObject(6, item.getFurnPrice());
            pstm.setObject(7, item.getFurnQty());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return pstm.executeUpdate() > 0;
    }

    public static boolean updateItemImageFile(String sql, File file, String furnId) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getDbConnection().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        try (FileInputStream fis = new FileInputStream(file)) {
            pstm.setBinaryStream(1, fis, (int) file.length());
            pstm.setObject(2, furnId);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }

        return pstm.executeUpdate() > 0;
    }
}
