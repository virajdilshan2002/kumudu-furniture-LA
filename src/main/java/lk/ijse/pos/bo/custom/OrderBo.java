package lk.ijse.pos.bo.custom;

import java.sql.SQLException;

public interface OrderBo {
    int getToBePaidOrdersCount() throws SQLException, ClassNotFoundException;
}
