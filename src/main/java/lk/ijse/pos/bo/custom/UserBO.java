package lk.ijse.pos.bo.custom;

import lk.ijse.pos.bo.SuperBO;
import lk.ijse.pos.dto.UserDTO;
import lk.ijse.pos.entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface UserBO extends SuperBO {
    boolean add(UserDTO user) throws SQLException, ClassNotFoundException;
    UserDTO isUserExist(String userName) throws SQLException, ClassNotFoundException;
    boolean updatePassword(String userName, String newPassword) throws SQLException, ClassNotFoundException;
    ResultSet checkCredential(String userName) throws SQLException, ClassNotFoundException;
}
