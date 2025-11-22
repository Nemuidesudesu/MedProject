package com.medicalsystem.ui;

import com.medicalsystem.dao.DoctorDAO;
import com.medicalsystem.model.Doctor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class DoctorsView extends BorderPane {

    private final DoctorDAO dao = new DoctorDAO();
    private final ObservableList<Doctor> list = FXCollections.observableArrayList();
    private final TableView<Doctor> table = new TableView<>();

    public DoctorsView() {
        setPadding(new Insets(10));

        TextField search = new TextField();
        search.setPromptText("Поиск по имени");
        HBox top = new HBox(8, new Label("Поиск:"), search);
        top.setPadding(new Insets(0,0,8,0));
        setTop(top);

        TableColumn<Doctor, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getId()));

        TableColumn<Doctor, String> colName = new TableColumn<>("Имя");
        colName.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getFullName()));

        TableColumn<Doctor, String> colSpec = new TableColumn<>("Специальность");
        colSpec.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getSpecialization()));

        table.getColumns().addAll(colId, colName, colSpec);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setCenter(table);

        Button add = new Button("Добавить");
        Button delete = new Button("Удалить");
        HBox bottom = new HBox(8, add, delete);
        bottom.setPadding(new Insets(8,0,0,0));
        setBottom(bottom);

        refresh();

        add.setOnAction(e -> showAddDialog());
        delete.setOnAction(e -> {
            Doctor sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showAlert("Ошибка","Выберите доктора"); return; }
            if (dao.deleteDoctorById(sel.getId())) refresh(); else showAlert("Ошибка","Не удалось удалить");
        });
    }

    private void refresh() {
        list.setAll(dao.getAllDoctors());
        table.setItems(list);
    }

    private void showAddDialog() {
        Dialog<Doctor> dlg = new Dialog<>();
        dlg.setTitle("Добавить доктора");
        ButtonType addType = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(addType, ButtonType.CANCEL);

        TextField first = new TextField(); first.setPromptText("Имя");
        TextField last = new TextField(); last.setPromptText("Фамилия");
        TextField spec = new TextField(); spec.setPromptText("Специальность");
        TextField phone = new TextField(); phone.setPromptText("Телефон");
        HBox body = new HBox(8, first, last, spec, phone);
        HBox.setHgrow(first, Priority.ALWAYS);
        dlg.getDialogPane().setContent(body);

        dlg.setResultConverter(bt -> {
            if (bt == addType) return new Doctor(first.getText().trim(), last.getText().trim(), spec.getText().trim(), phone.getText().trim(), "");
            return null;
        });

        dlg.showAndWait().ifPresent(d -> { dao.addDoctor(d); refresh(); });
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}
