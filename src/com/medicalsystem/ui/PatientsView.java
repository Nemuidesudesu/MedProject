package com.medicalsystem.ui;

import com.medicalsystem.dao.PatientDAO;
import com.medicalsystem.model.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

// A reusable Patients view: table + controls (add/edit/delete/search)
public class PatientsView extends BorderPane {

    private final PatientDAO dao = new PatientDAO();
    private final ObservableList<Patient> masterList = FXCollections.observableArrayList();
    private final TableView<Patient> table = new TableView<>();

    public PatientsView() {
        setPadding(new Insets(10));

        // Top: search bar
        TextField searchField = new TextField();
        searchField.setPromptText("Поиск по имени или фамилии");

        HBox topBox = new HBox(8, new Label("Поиск:"), searchField);
        topBox.setPadding(new Insets(4, 0, 8, 0));
        setTop(topBox);

        // Center: table
        setupTable();
        setCenter(table);

        // Bottom: controls
        Button addBtn = new Button("Добавить");
        Button editBtn = new Button("Редактировать");
        Button deleteBtn = new Button("Удалить");

        HBox controls = new HBox(8, addBtn, editBtn, deleteBtn);
        controls.setPadding(new Insets(8, 0, 0, 0));
        setBottom(controls);

        // Load data
        refreshTable();

        // Filtering
        FilteredList<Patient> filtered = new FilteredList<>(masterList, p -> true);
        searchField.textProperty().addListener((obs, oldV, newV) -> {
            String q = newV == null ? "" : newV.trim().toLowerCase();
            filtered.setPredicate(p -> {
                if (q.isEmpty()) return true;
                return p.getFirstName().toLowerCase().contains(q) || p.getLastName().toLowerCase().contains(q);
            });
        });

        SortedList<Patient> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);

        // Button actions
        addBtn.setOnAction(e -> showAddDialog());

        editBtn.setOnAction(e -> {
            Patient sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) {
                showAlert("Ошибка", "Выберите пациента для редактирования.");
                return;
            }
            showEditDialog(sel);
        });

        deleteBtn.setOnAction(e -> {
            Patient sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) {
                showAlert("Ошибка", "Выберите пациента для удаления.");
                return;
            }
            boolean ok = dao.deletePatientById(sel.getId());
            if (ok) refreshTable();
            else showAlert("Ошибка", "Не удалось удалить пациента.");
        });
    }

    private void setupTable() {
        TableColumn<Patient, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()));

        TableColumn<Patient, String> colFirst = new TableColumn<>("Имя");
        colFirst.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFirstName()));

        TableColumn<Patient, String> colLast = new TableColumn<>("Фамилия");
        colLast.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLastName()));

        TableColumn<Patient, String> colBirth = new TableColumn<>("Дата рождения");
        colBirth.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getBirthDate()));

        TableColumn<Patient, String> colPhone = new TableColumn<>("Телефон");
        colPhone.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));

        table.getColumns().addAll(colId, colFirst, colLast, colBirth, colPhone);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void refreshTable() {
        masterList.setAll(dao.getAllPatients());
    }

    private void showAddDialog() {
        Dialog<Patient> dlg = new Dialog<>();
        dlg.setTitle("Добавить пациента");

        ButtonType addType = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(addType, ButtonType.CANCEL);

        TextField first = new TextField();
        TextField last = new TextField();
        TextField birth = new TextField();
        TextField phone = new TextField();

        first.setPromptText("Имя");
        last.setPromptText("Фамилия");
        birth.setPromptText("гггг-мм-дд");
        phone.setPromptText("Телефон");

        HBox body = new HBox(8, first, last, birth, phone);
        HBox.setHgrow(first, Priority.ALWAYS);
        dlg.getDialogPane().setContent(body);

        dlg.setResultConverter(bt -> {
            if (bt == addType) {
                return new Patient(first.getText().trim(), last.getText().trim(), birth.getText().trim(), phone.getText().trim());
            }
            return null;
        });

        dlg.showAndWait().ifPresent(p -> {
            dao.addPatient(p);
            refreshTable();
        });
    }

    private void showEditDialog(Patient p) {
        Dialog<Patient> dlg = new Dialog<>();
        dlg.setTitle("Редактировать пациента");

        ButtonType saveType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        TextField first = new TextField(p.getFirstName());
        TextField last = new TextField(p.getLastName());
        TextField birth = new TextField(p.getBirthDate());
        TextField phone = new TextField(p.getPhone());

        HBox body = new HBox(8, first, last, birth, phone);
        dlg.getDialogPane().setContent(body);

        dlg.setResultConverter(bt -> {
            if (bt == saveType) {
                p.setFirstName(first.getText().trim());
                p.setLastName(last.getText().trim());
                p.setBirthDate(birth.getText().trim());
                p.setPhone(phone.getText().trim());
                return p;
            }
            return null;
        });

        dlg.showAndWait().ifPresent(updated -> {
            boolean ok = dao.updatePatient(updated);
            if (ok) refreshTable();
            else showAlert("Ошибка", "Не удалось сохранить изменения.");
        });
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
