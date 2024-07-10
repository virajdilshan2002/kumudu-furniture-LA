package lk.ijse.pos.bo.custom;

import javafx.scene.control.Alert;
import lk.ijse.pos.bo.SuperBO;
import lk.ijse.pos.dto.FurnitureDTO;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface FurnitureBo extends SuperBO {
     List<FurnitureDTO> getAll() throws SQLException, ClassNotFoundException;

     boolean delete(String code) throws SQLException, ClassNotFoundException;

     boolean add(FurnitureDTO dto) throws SQLException, ClassNotFoundException;

     boolean update(FurnitureDTO dto) throws SQLException, ClassNotFoundException;

     String generateNewID() throws SQLException, ClassNotFoundException;
     FurnitureDTO search(String code) throws SQLException, ClassNotFoundException;

     boolean updateImage(File file, String furnId) throws SQLException, ClassNotFoundException;
}
