package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lk.ijse.pos.bo.BOFactory;
import lk.ijse.pos.bo.custom.FurnitureBo;
import lk.ijse.pos.dto.FurnitureDTO;
import lk.ijse.pos.util.Regex;
import lk.ijse.pos.view.tdm.FurnitureTm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class FurnitureFormController {
    public AnchorPane rootNode;

    public ImageView image;
    public TextField txtSearch;
    public TableView<FurnitureTm> tblItems;
    public TableColumn<?,?> colId;
    public TableColumn<?,?> colDesc;
    public TableColumn<?,?> colWoodType;
    public TableColumn<?,?> colColor;
    public TableColumn<?,?> colPrice;
    public TableColumn<?,?> colQty;
    public TableColumn<?,?> colViewItem;
    public TextField txtFurnDesc;
    public TextField txtMatType;
    public TextField txtFurnColor;
    public TextField txtFurnPrice;
    public TextField txtQty;
    public Label lblFurnId;
    private static List<FurnitureDTO> itemList;
    private File file;
    private FileInputStream fis;

    FurnitureBo furnitureBo = (FurnitureBo) BOFactory.getInstance().getBO(BOFactory.BOType.FURNITURE);

    public void initialize() throws IOException, SQLException {
        setCellValueFactory();
        getAllFurnitureItems();
        generateNewFurnId();
    }

    private void generateNewFurnId() {
        try {
            String newFurnId = furnitureBo.generateNewID();
            lblFurnId.setText(newFurnId);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void getAllFurnitureItems() {
        ObservableList<FurnitureTm> obList = FXCollections.observableArrayList();
        try {
            itemList = furnitureBo.getAll();
            for (FurnitureDTO item : itemList) {
                String id = item.getFurnId();
                String desc = item.getFurnDescription();
                String woodType = item.getFurnWoodType();
                String color = item.getFurnColor();
                double price = item.getFurnPrice();
                int qty = item.getFurnQty();

                JFXButton btnView = new JFXButton("VIEW");
                btnView.setStyle("-fx-background-color: #00e68a; -fx-background-radius: 20; -fx-text-fill: white; -fx-font-weight: bold;");
                btnView.setPrefSize(70, 20);
                btnView.setCursor(Cursor.HAND);
                btnView.setButtonType(JFXButton.ButtonType.RAISED);

                btnView.setOnAction((e) -> {
                    FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/lk/ijse/pos/view/ViewItem.fxml"));
                    AnchorPane centerNode;
                    try {
                        centerNode = loader.load();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    Scene scene = new Scene(centerNode);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.centerOnScreen();
                    stage.setTitle(id + " - " + desc);

                    if (tblItems.getFocusModel().getFocusedItem() != null) {
                        FurnitureTm focusedItem = tblItems.getFocusModel().getFocusedItem();
                        ViewItemController controller = loader.getController();
                        controller.initialize(focusedItem);
                        stage.show();
                    }else {
                        new Alert(Alert.AlertType.ERROR, "No item selected!").show();
                    }
                });

                FurnitureTm tm = new FurnitureTm(id, desc, woodType, color, price, qty, btnView);
                obList.add(tm);
            }
            tblItems.setItems(obList);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void setCellValueFactory() {
        colId.setCellValueFactory(new PropertyValueFactory<>("furnId"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("furnDesc"));
        colWoodType.setCellValueFactory(new PropertyValueFactory<>("furnWoodType"));
        colColor.setCellValueFactory(new PropertyValueFactory<>("furnColor"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("furnPrice"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("furnQty"));
        colViewItem.setCellValueFactory(new PropertyValueFactory<>("btnView"));
    }


    public void btnBrowseClickOnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));
        fileChooser.setTitle("Select Image *.jpg *.png");
        Stage stage = new Stage();
        file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                fis = new FileInputStream(file);
                javafx.scene.image.Image SelectedImage = new Image(fis);
                image.setImage(SelectedImage);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }else {
            new Alert(Alert.AlertType.INFORMATION, "Image not selected!").show();
        }

    }

    public void btnAddClickOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        boolean isValid = checkDetails();
        if (isValid) {
            String id = lblFurnId.getText();
            String desc = txtFurnDesc.getText();
            String woodType = txtMatType.getText();
            String color = txtFurnColor.getText();
            double price = Double.parseDouble(txtFurnPrice.getText());
            int qty = Integer.parseInt(txtQty.getText());

            FurnitureDTO furniture = new FurnitureDTO(id, file, desc, woodType, color, price, qty);

            boolean isSaved = furnitureBo.add(furniture);
            if (isSaved) {
                new Alert(Alert.AlertType.INFORMATION, "Item added successfully!").show();
                clearTextFields();
                getAllFurnitureItems();
                tblItems.refresh();
                generateNewFurnId();
            } else {
                new Alert(Alert.AlertType.ERROR, "Item not saved!").show();
            }
        }
    }

    private void clearTextFields() {
        txtFurnDesc.clear();
        txtFurnPrice.clear();
        txtFurnColor.clear();
        txtMatType.clear();
        txtQty.clear();
    }

    public void txtSearchClickOnAction(ActionEvent actionEvent) {
        btnSearchClickOnAction(actionEvent);
    }

    public void btnSearchClickOnAction(ActionEvent actionEvent) {
        String id = txtSearch.getText();
        for (FurnitureTm tm : tblItems.getItems()) {
            if (tm.getFurnId().equals(id)) {
                tblItems.getSelectionModel().select(tm);
                tblItems.scrollTo(tm);
                return;
            }
        }
        new Alert(Alert.AlertType.INFORMATION, "Item not found!").show();
    }

    public void btnRefreshClickOnAction(ActionEvent actionEvent) {
        txtSearch.clear();
        getAllFurnitureItems();
        tblItems.refresh();
    }

    private boolean checkDetails() {
        if (file == null) {
            new Alert(Alert.AlertType.ERROR, "Please select an image!").show();
            return false;
        } else if (txtFurnDesc.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Description cannot be empty!").show();
            txtFurnDesc.requestFocus();
            return false;
        } else if (txtMatType.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Wood type cannot be empty!").show();
            txtMatType.requestFocus();
            return false;
        } else if (txtFurnColor.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please fill color field!").show();
            txtFurnColor.requestFocus();
            return false;
        } else if (txtFurnPrice.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please enter price!").show();
            txtFurnPrice.requestFocus();
            return false;
        } else if (!Regex.checkTextField(lk.ijse.pos.util.TextField.PRICE,txtFurnPrice)) {
            new Alert(Alert.AlertType.ERROR, "Invalid price!").show();
            txtFurnPrice.requestFocus();
            return false;
        } else if (txtQty.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please enter quantity!").show();
            txtQty.requestFocus();
            return false;
        }else if (!Regex.checkTextField(lk.ijse.pos.util.TextField.QTY,txtQty)) {
            new Alert(Alert.AlertType.ERROR, "Invalid quantity!").show();
            txtQty.requestFocus();
            return false;
        }
        return true;
    }
}
