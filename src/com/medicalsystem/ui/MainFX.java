package com.medicalsystem.ui;

import com.medicalsystem.dao.PatientDAO;
import com.medicalsystem.model.Patient;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainFX extends Application {

    private final PatientDAO dao = new PatientDAO();
    private final ObservableList<Patient> patients = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) {

        stage.setTitle("Медицинская система");

        // Таблица пациентов
        TableView<Patient> table = new TableView<>(patients);

        TableColumn<Patient, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());

        TableColumn<Patient, String> colFirstName = new TableColumn<>("Имя");
        colFirstName.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getFirstName()));

        TableColumn<Patient, String> colLastName = new TableColumn<>("Фамилия");
        colLastName.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getLastName()));

        TableColumn<Patient, String> colBirthDate = new TableColumn<>("Дата рождения");
        colBirthDate.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getBirthDate()));

        TableColumn<Patient, String> colPhone = new TableColumn<>("Телефон");
        colPhone.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));

        table.getColumns().addAll(colId, colFirstName, colLastName, colBirthDate, colPhone);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Поля ввода
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("Имя");

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Фамилия");

        TextField birthDateField = new TextField();
        birthDateField.setPromptText("Дата рождения (гггг-мм-дд)");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Телефон");

        Button addButton = new Button("Добавить");
        Button deleteButton = new Button("Удалить");
        Button editButton = new Button("Редактировать");

        //-----------------------------------------
        // 1. Оригинальный обработчик для addButton
        //-----------------------------------------
        addButton.setOnAction(e -> {
            String fName = firstNameField.getText().trim();
            String lName = lastNameField.getText().trim();
            String birth = birthDateField.getText().trim();
            String phone = phoneField.getText().trim();

            if (fName.isEmpty() || lName.isEmpty()) {
                showAlert("Ошибка", "Имя и фамилия обязательны!");
                return;
            }

            Patient newPatient = new Patient(fName, lName, birth, phone);
            dao.addPatient(newPatient);
            refreshTable();

            firstNameField.clear();
            lastNameField.clear();
            birthDateField.clear();
            phoneField.clear();
        });

        // Запоминаем оригинальный обработчик,
        // чтобы потом вернуть его после редактирования
        var originalAddHandler = addButton.getOnAction();

        //-----------------------------------------
        // 2. Удаление
        //-----------------------------------------
        deleteButton.setOnAction(e -> {
            Patient selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Ошибка", "Выберите пациента для удаления.");
                return;
            }
            if (dao.deletePatientById(selected.getId())) {
                showAlert("Успех", "Пациент удалён.");
                refreshTable();
            } else {
                showAlert("Ошибка", "Не удалось удалить пациента.");
            }
        });

        //-----------------------------------------
        // 3. Редактирование
        //-----------------------------------------
        editButton.setOnAction(e -> {
            Patient selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Ошибка", "Выберите пациента для редактирования.");
                return;
            }

            // Заполнить поля текущими значениями
            firstNameField.setText(selected.getFirstName());
            lastNameField.setText(selected.getLastName());
            birthDateField.setText(selected.getBirthDate());
            phoneField.setText(selected.getPhone());

            // Меняем кнопку
            addButton.setText("Сохранить");

            addButton.setOnAction(saveEvent -> {
                selected.setFirstName(firstNameField.getText().trim());
                selected.setLastName(lastNameField.getText().trim());
                selected.setBirthDate(birthDateField.getText().trim());
                selected.setPhone(phoneField.getText().trim());

                dao.updatePatient(selected);
                refreshTable();

                firstNameField.clear();
                lastNameField.clear();
                birthDateField.clear();
                phoneField.clear();

                addButton.setText("Добавить");
                addButton.setOnAction(originalAddHandler);
            });
        });

        //-----------------------------------------
        // Интерфейс
        //-----------------------------------------
        HBox inputBox = new HBox(10,
                firstNameField, lastNameField, birthDateField, phoneField, addButton, editButton, deleteButton);
        inputBox.setPadding(new Insets(10));

        VBox root = new VBox(10, table, inputBox);
        root.setPadding(new Insets(10));

        stage.setScene(new Scene(root, 900, 500));
        stage.show();

        refreshTable();
    }

    //------------------------------------------------------------
    private void refreshTable() {
        patients.setAll(dao.getAllPatients());
    }

    //------------------------------------------------------------
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}
