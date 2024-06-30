package lk.ijse.pos.bo.custom;

import lk.ijse.pos.bo.SuperBO;
import lk.ijse.pos.dto.UserDTO;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface UserBO extends SuperBO {
    UserDTO isUserExist(String userName) throws SQLException, ClassNotFoundException;

    boolean updatePassword(String userName, String newPassword) throws SQLException, ClassNotFoundException;

    ResultSet checkCredential(String userName) throws SQLException, ClassNotFoundException;
}
