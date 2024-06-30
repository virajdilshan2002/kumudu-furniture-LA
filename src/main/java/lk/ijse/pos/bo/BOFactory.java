package lk.ijse.pos.bo;

import lk.ijse.pos.bo.custom.impl.CustomerBoImpl;
import lk.ijse.pos.bo.custom.impl.FurnitureBoImpl;
import lk.ijse.pos.bo.custom.impl.PlaceOrderBoImpl;

public class BOFactory implements SuperBO{
    private static BOFactory boFactory;
    private static CustomerBoImpl customerBo;
    private static FurnitureBoImpl furnitureBo;
    private static PlaceOrderBoImpl placeOrderBo;
    private BOFactory(){
        customerBo = new CustomerBoImpl();
        furnitureBo = new FurnitureBoImpl();
        placeOrderBo = new PlaceOrderBoImpl();
    }

    public static BOFactory getInstance(){
        return boFactory = boFactory == null ? new BOFactory() : boFactory;
    }

    public enum BOType {
        CUSTOMER,FURNITURE,PLACEORDER
    }

    public SuperBO getBO(BOType type){
        return switch (type) {
            case CUSTOMER -> customerBo;
            case FURNITURE -> furnitureBo;
            case PLACEORDER -> placeOrderBo;
        };
    }

}
