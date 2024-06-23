package lk.ijse.pos.bo.custom;

import lk.ijse.pos.bo.SuperBO;
import lk.ijse.pos.dto.FurnitureDTO;

import java.sql.SQLException;
import java.util.ArrayList;

public interface ItemBo extends SuperBO {
     ArrayList<FurnitureDTO> getAll() throws SQLException, ClassNotFoundException;

     boolean delete(String code) throws SQLException, ClassNotFoundException;

     boolean add(FurnitureDTO dto) throws SQLException, ClassNotFoundException;

     boolean update(FurnitureDTO dto) throws SQLException, ClassNotFoundException;

     boolean exist(String code) throws SQLException, ClassNotFoundException;

     String generateNewID() throws SQLException, ClassNotFoundException;
     FurnitureDTO search(String code) throws SQLException, ClassNotFoundException;
}
