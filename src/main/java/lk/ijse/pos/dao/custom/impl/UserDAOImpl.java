package lk.ijse.pos.dao.custom.impl;

import lk.ijse.pos.dao.SQLUtil;
import lk.ijse.pos.dao.custom.UserDAO;
import lk.ijse.pos.entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDAOImpl implements UserDAO {
    @Override
    public List<User> getAll() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean add(User user) throws SQLException, ClassNotFoundException {
        return SQLUtil.execute("INSERT INTO credential VALUES(?, ?, ?, ?)",
                user.getUserName(),
                user.getFullName(),
                user.getEmail(),
                user.getPassword()
        );
    }

    @Override
    public boolean update(User user) throws SQLException, ClassNotFoundException {
        return SQLUtil.execute("UPDATE credential SET password = ? WHERE userName = ?",
                user.getUserName(),
                user.getPassword()
        );
    }

    @Override
    public boolean isExists(String userName) throws SQLException, ClassNotFoundException {
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
    public User search(String id) throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public User isUserExist(String name) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = SQLUtil.execute("SELECT * FROM credential WHERE userName = ?", name);
        if (resultSet.next()){
            return new User(
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
