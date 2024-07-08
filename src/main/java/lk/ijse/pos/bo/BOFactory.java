package lk.ijse.pos.bo;

import lk.ijse.pos.bo.custom.OrderBO;
import lk.ijse.pos.bo.custom.impl.*;

public class BOFactory implements SuperBO{
    private static BOFactory boFactory;
    private static CustomerBoImpl customerBo;
    private static FurnitureBoImpl furnitureBo;
    private static PlaceOrderBoImpl placeOrderBo;
    private static UserBOImpl userBO;
    private static OrderBOImpl orderBO;
    private static DashboardBoImpl dashboardBo;

    private BOFactory(){
        userBO = new UserBOImpl();
        customerBo = new CustomerBoImpl();
        furnitureBo = new FurnitureBoImpl();
        placeOrderBo = new PlaceOrderBoImpl();
        orderBO = new OrderBOImpl();
        dashboardBo = new DashboardBoImpl();
    }

    public static BOFactory getInstance(){
        return boFactory = boFactory == null ? new BOFactory() : boFactory;
    }

    public enum BOType {
        USER,CUSTOMER,FURNITURE,PLACEORDER,ORDER,DASHBOARD
    }

    public SuperBO getBO(BOType type){
        return switch (type) {
            case ORDER -> orderBO;
            case USER -> userBO;
            case CUSTOMER -> customerBo;
            case FURNITURE -> furnitureBo;
            case PLACEORDER -> placeOrderBo;
            case DASHBOARD -> dashboardBo;
        };
    }

}
