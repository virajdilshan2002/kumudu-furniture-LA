package lk.ijse.pos.dao;

import java.sql.SQLException;
import java.util.List;

public interface CrudDAO<T> {
    List<T> getAll() throws SQLException, ClassNotFoundException;
    boolean add(T entity) throws SQLException, ClassNotFoundException;
    boolean update(T entity) throws SQLException, ClassNotFoundException;
    boolean exist(String id) throws SQLException, ClassNotFoundException;
    String generateNewID() throws SQLException, ClassNotFoundException;
    boolean delete(String id) throws SQLException, ClassNotFoundException;
    T search(String id) throws SQLException, ClassNotFoundException;
}
