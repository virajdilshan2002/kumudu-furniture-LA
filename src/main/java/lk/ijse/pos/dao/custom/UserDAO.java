package lk.ijse.pos.dao.custom;

import lk.ijse.pos.dao.CrudDAO;
import lk.ijse.pos.entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface UserDAO extends CrudDAO<User> {
    User isUserExist(String userName) throws SQLException, ClassNotFoundException;

    boolean updatePassword(String userName, String newPassword) throws SQLException, ClassNotFoundException;

    ResultSet checkCredential(String userName) throws SQLException, ClassNotFoundException;
}
