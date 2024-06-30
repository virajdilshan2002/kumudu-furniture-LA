package lk.ijse.pos.dao.custom.impl;

import lk.ijse.pos.dao.SQLUtil;
import lk.ijse.pos.dao.custom.CredentialDAO;
import lk.ijse.pos.entity.Credential;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CredentialDAOImpl implements CredentialDAO {
    @Override
    public List<Credential> getAll() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean add(Credential credential) throws SQLException, ClassNotFoundException {
        return SQLUtil.execute("INSERT INTO credential VALUES(?, ?, ?, ?)",
                credential.getUserName(),
                credential.getFullName(),
                credential.getEmail(),
                credential.getPassword()
        );
    }

    @Override
    public boolean update(Credential credential) throws SQLException, ClassNotFoundException {
        return SQLUtil.execute("UPDATE credential SET password = ? WHERE userName = ?",
                credential.getUserName(),
                credential.getPassword()
        );
    }

    @Override
    public boolean exist(String userName) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public String generateNewID() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean delete(String id) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public Credential search(String id) throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public Credential isUserExist(String name) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = SQLUtil.execute("SELECT * FROM credential WHERE userName = ?", name);
        if (resultSet.next()){
            return new Credential(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4)
            );
        }
        return null;
    }

    @Override
    public boolean updatePassword(String userName, String newPassword) throws SQLException, ClassNotFoundException {
        return SQLUtil.execute("UPDATE credential SET password = ? WHERE userName = ?",
                userName,
                newPassword
        );
    }

    @Override
    public ResultSet checkCredential(String userName) throws SQLException, ClassNotFoundException {
        return SQLUtil.execute("SELECT userName,password from credential where BINARY userName = ?", userName);
    }
}
