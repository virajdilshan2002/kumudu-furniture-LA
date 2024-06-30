package lk.ijse.pos.dao.custom;

import lk.ijse.pos.dao.CrudDAO;
import lk.ijse.pos.entity.Credential;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface CredentialDAO extends CrudDAO<Credential> {
    Credential isUserExist(String userName) throws SQLException, ClassNotFoundException;

    boolean updatePassword(String userName, String newPassword) throws SQLException, ClassNotFoundException;

    ResultSet checkCredential(String userName) throws SQLException, ClassNotFoundException;
}
