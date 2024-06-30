package lk.ijse.pos.bo.custom.impl;

import lk.ijse.pos.bo.custom.UserBO;
import lk.ijse.pos.dao.DAOFactory;
import lk.ijse.pos.dao.custom.UserDAO;
import lk.ijse.pos.dto.UserDTO;
import lk.ijse.pos.entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserBOImpl implements UserBO {

    UserDAO userDAO = (UserDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.USER);
    @Override
    public UserDTO isUserExist(String userName) throws SQLException, ClassNotFoundException {
        User user = userDAO.isUserExist(userName);
        return new UserDTO(user.getUserName(),
                user.getFullName(),
                user.getEmail(),
                user.getPassword()
        );
    }

    @Override
    public boolean updatePassword(String userName, String newPassword) throws SQLException, ClassNotFoundException {
        return userDAO.updatePassword(userName, newPassword);
    }

    @Override
    public ResultSet checkCredential(String userName) throws SQLException, ClassNotFoundException {
        return userDAO.checkCredential(userName);
    }
}
