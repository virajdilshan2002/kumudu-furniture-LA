package lk.ijse.pos.bo.custom.impl;

import lk.ijse.pos.bo.custom.UserBO;
import lk.ijse.pos.dao.DAOFactory;
import lk.ijse.pos.dao.custom.CredentialDAO;
import lk.ijse.pos.dto.CredentialDTO;
import lk.ijse.pos.entity.Credential;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserBOImpl implements UserBO {

    CredentialDAO credentialDAO = (CredentialDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.CREDENTIAL);
    @Override
    public CredentialDTO isUserExist(String userName) throws SQLException, ClassNotFoundException {
        Credential credential =credentialDAO.isUserExist(userName);
        return new CredentialDTO(credential.getUserName(),
                credential.getFullName(),
                credential.getEmail(),
                credential.getPassword()
        );
    }

    @Override
    public boolean updatePassword(String userName, String newPassword) throws SQLException, ClassNotFoundException {
        return credentialDAO.updatePassword(userName, newPassword);
    }

    @Override
    public ResultSet checkCredential(String userName) throws SQLException, ClassNotFoundException {
        return credentialDAO.checkCredential(userName);
    }
}
