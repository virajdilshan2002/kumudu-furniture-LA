package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lk.ijse.pos.bo.BOFactory;
import lk.ijse.pos.bo.custom.FurnitureBo;
import lk.ijse.pos.dto.FurnitureDTO;
import lk.ijse.pos.entity.Furniture;
import lk.ijse.pos.view.tdm.FurnitureTm;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class ViewItemController {
    public AnchorPane centerNode;
    public Label lblItemTitle;
    public ImageView imgItem;
    public Label lblItemId;
    public TextField txtItemDesc;
    public TextField txtItemType;
    public TextField txtUnitPrice;
    public TextField txtStock;
    public JFXButton btnUpdateDetails;
    public JFXToggleButton togBtn;
    public TextField txtColor;
    public JFXButton btnDelete;
    private FurnitureDTO item;
    private final Desktop desktop = Desktop.getDesktop();

    FurnitureBo furnitureBo = (FurnitureBo) BOFactory.getInstance().getBO(BOFactory.BOType.FURNITURE);
    public void initialize(FurnitureTm furnitureTm) {
        getItem(furnitureTm.getFurnId());
        setDetails();
        setDisable();
    }

    private void setDisable() {
        txtItemDesc.setDisable(true);
        txtItemType.setDisable(true);
        txtUnitPrice.setDisable(true);
        txtColor.setDisable(true);
        txtStock.setDisable(true);
        btnUpdateDetails.setDisable(true);
        btnDelete.setDisable(true);
    }

    private void setEnable() {
        txtItemDesc.setDisable(false);
        txtItemType.setDisable(false);
        txtUnitPrice.setDisable(false);
        txtColor.setDisable(false);
        txtStock.setDisable(false);
        btnUpdateDetails.setDisable(false);
        btnDelete.setDisable(false);
    }

    private void setDetails() {
        lblItemTitle.setText(item.getFurnDescription());
        lblItemId.setText(item.getFurnId());
        txtItemDesc.setText(item.getFurnDescription());
        txtItemType.setText(item.getFurnWoodType());
        txtColor.setText(item.getFurnColor());
        txtUnitPrice.setText(String.valueOf(item.getFurnPrice()));
        txtStock.setText(String.valueOf(item.getFurnQty()));

        setImage(item.getImageFile());
    }

    private void setImage(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            imgItem.setImage(new Image(fis));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void getItem(String id) {
        try {
            item = furnitureBo.search(id);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnOpenImage(ActionEvent actionEvent) {
        try {
            desktop.open(item.getImageFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnUpdateImage(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));
        fileChooser.setTitle("Select Image *.jpg *.png");
        Stage stage = new Stage();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            setImage(file);

            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            ButtonType no = new ButtonType("No",ButtonBar.ButtonData.CANCEL_CLOSE);

            Optional<ButtonType> addType = new Alert(Alert.AlertType.INFORMATION,"Are You Sure To Update Image ?",yes,no).showAndWait();

            if (addType.orElse(no)==yes){
                try {
                    boolean isUpdated = furnitureBo.updateImage(file, item.getFurnId());
                    if (isUpdated){
                        item.setImageFile(file);
                        new Alert(Alert.AlertType.INFORMATION,"Item Image Updated Successfully!").show();
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }else {
                setImage(item.getImageFile());
            }
        }
    }

    public void txtItemDescClickOnAction(ActionEvent actionEvent) {
        txtItemType.requestFocus();
    }

    public void txtItemTypeClickOnAction(ActionEvent actionEvent) {
        txtUnitPrice.requestFocus();
    }

    public void txtUnitPriceClickOnAction(ActionEvent actionEvent) {
        txtStock.requestFocus();
    }

    public void txtStockClickOnAction(ActionEvent actionEvent) {
        btnUpdateDetailsClickOnAction(actionEvent);
    }

    public void togBtnClickOnAction(ActionEvent actionEvent) {
        if (togBtn.isSelected()){
            setEnable();
        }else {
            setDisable();
        }
    }

    public void btnUpdateDetailsClickOnAction(ActionEvent actionEvent) {
        String id = lblItemId.getText();
        String desc = txtItemDesc.getText();
        String woodType = txtItemType.getText();
        String color = txtColor.getText();
        double price = Double.parseDouble(txtUnitPrice.getText());
        int qty = Integer.parseInt(txtStock.getText());

        FurnitureDTO updatedItem = new FurnitureDTO(id,null,desc,woodType,color,price,qty);
        try {
            ButtonType yes = new ButtonType("Yes",ButtonBar.ButtonData.OK_DONE);
            ButtonType no = new ButtonType("No",ButtonBar.ButtonData.CANCEL_CLOSE);

            Optional<ButtonType> addType = new Alert(Alert.AlertType.CONFIRMATION,"Are You Sure To Update Item ?",yes,no).showAndWait();
            if (addType.orElse(no)==yes){
                boolean isUpdated = furnitureBo.update(updatedItem);
                if (isUpdated){
                    new Alert(Alert.AlertType.INFORMATION,"Item Updated Successfull!").show();
                    getItem(id);
                    setDetails();
                    setDisable();
                }
            }else {
                new Alert(Alert.AlertType.ERROR,"Cancelled!").show();
                setDetails();
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void txtColorClickOnAction(ActionEvent actionEvent) {
        txtUnitPrice.requestFocus();
    }

    public void btnDeleteClickOnAction(ActionEvent actionEvent) {
        ButtonType yes = new ButtonType("Yes",ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No",ButtonBar.ButtonData.CANCEL_CLOSE);

        Optional<ButtonType> type = new Alert(Alert.AlertType.CONFIRMATION,"Are You Sure To Delete This Item?",yes,no).showAndWait();
        if (type.orElse(no)==yes){
            String id = lblItemId.getText();
            try {
                boolean isDeleted = furnitureBo.delete(id);
                if (isDeleted){
                    new Alert(Alert.AlertType.INFORMATION,"Item Deleted Successfully!").show();
                    Stage stage = (Stage) centerNode.getScene().getWindow();
                    stage.close();
                }else {
                    new Alert(Alert.AlertType.ERROR,"Item Not Deleted!").show();
                }
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }else {
            new Alert(Alert.AlertType.INFORMATION,"Cancelled!").show();
        }
    }
}
