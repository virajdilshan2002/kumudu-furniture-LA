package lk.ijse.pos.bo.custom.impl;

import lk.ijse.pos.bo.custom.FurnitureBo;
import lk.ijse.pos.dao.custom.FurnitureDAO;
import lk.ijse.pos.dao.custom.impl.FurnitureDAOImpl;
import lk.ijse.pos.dto.FurnitureDTO;
import lk.ijse.pos.entity.Furniture;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FurnitureBoImpl implements FurnitureBo {

    FurnitureDAO furnitureDAO = new FurnitureDAOImpl();
    @Override
    public List<FurnitureDTO> getAll() throws SQLException, ClassNotFoundException {
        List<Furniture> furnitures = furnitureDAO.getAll();

        List<FurnitureDTO> furnitureDTOS = new ArrayList<>();
        for (Furniture furniture : furnitures) {
            furnitureDTOS.add(new FurnitureDTO(
                    furniture.getFurnId(),
                    furniture.getImageFile(),
                    furniture.getFurnDescription(),
                    furniture.getFurnWoodType(),
                    furniture.getFurnColor(),
                    furniture.getFurnPrice(),
                    furniture.getFurnQty()
            ));
        }
        return furnitureDTOS;
    }

    @Override
    public boolean delete(String code) throws SQLException, ClassNotFoundException {
        return furnitureDAO.delete(code);
    }

    @Override
    public boolean add(FurnitureDTO dto) throws SQLException, ClassNotFoundException {
        Furniture furniture = new Furniture(
                dto.getFurnId(),
                dto.getImageFile(),
                dto.getFurnDescription(),
                dto.getFurnWoodType(),
                dto.getFurnColor(),
                dto.getFurnPrice(),
                dto.getFurnQty()
        );
        return furnitureDAO.add(furniture);
    }

    @Override
    public boolean update(FurnitureDTO dto) throws SQLException, ClassNotFoundException {
        Furniture furniture = new Furniture(
                dto.getFurnId(),
                dto.getImageFile(),
                dto.getFurnDescription(),
                dto.getFurnWoodType(),
                dto.getFurnColor(),
                dto.getFurnPrice(),
                dto.getFurnQty()
        );
        return furnitureDAO.update(furniture);
    }

    @Override
    public boolean exist(String code) throws SQLException, ClassNotFoundException {
        return furnitureDAO.exist(code);
    }

    @Override
    public String generateNewID() throws SQLException, ClassNotFoundException {
        return furnitureDAO.generateNewID();
    }

    @Override
    public FurnitureDTO search(String code) throws SQLException, ClassNotFoundException {
        Furniture furniture = furnitureDAO.search(code);
        FurnitureDTO furnitureDTO = new FurnitureDTO(
                furniture.getFurnId(),
                furniture.getImageFile(),
                furniture.getFurnDescription(),
                furniture.getFurnWoodType(),
                furniture.getFurnColor(),
                furniture.getFurnPrice(),
                furniture.getFurnQty()
        );
        return furnitureDTO;
    }
}
