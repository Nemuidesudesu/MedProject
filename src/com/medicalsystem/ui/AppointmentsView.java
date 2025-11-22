package com.medicalsystem.ui;

import com.medicalsystem.dao.AppointmentDAO;
import com.medicalsystem.dao.DoctorDAO;
import com.medicalsystem.dao.PatientDAO;
import com.medicalsystem.model.Appointment;
import com.medicalsystem.model.Doctor;
import com.medicalsystem.model.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentsView extends BorderPane {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();

    private final ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    private final TableView<Appointment> table = new TableView<>();
    private ComboBox<Patient> patientCombo;
    private ComboBox<Doctor> doctorCombo;

    public AppointmentsView() {
        setPadding(new Insets(10));

        // Top: form to create appointment
        patientCombo = new ComboBox<>();
        doctorCombo = new ComboBox<>();
        TextField dateField = new TextField(); dateField.setPromptText("YYYY-MM-DD HH:mm");
        TextField diagnosisField = new TextField(); diagnosisField.setPromptText("Диагноз");
        Button addBtn = new Button("Добавить приём");
        Button refreshBtn = new Button("Обновить");

        HBox form = new HBox(8, new Label("Пациент:"), patientCombo, new Label("Доктор:"), doctorCombo, dateField, diagnosisField, addBtn, refreshBtn);
        form.setPadding(new Insets(0,0,8,0));
        HBox.setHgrow(patientCombo, Priority.ALWAYS);
        HBox.setHgrow(doctorCombo, Priority.ALWAYS);
        setTop(form);

        // Table columns
        TableColumn<Appointment, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(a -> new javafx.beans.property.SimpleIntegerProperty(a.getValue().getId()));

        TableColumn<Appointment, String> colPatient = new TableColumn<>("Пациент");
        colPatient.setCellValueFactory(a -> new javafx.beans.property.SimpleStringProperty(getPatientName(a.getValue().getPatientId())));

        TableColumn<Appointment, String> colDoctor = new TableColumn<>("Доктор");
        colDoctor.setCellValueFactory(a -> new javafx.beans.property.SimpleStringProperty(getDoctorName(a.getValue().getDoctorId())));

        TableColumn<Appointment, String> colDate = new TableColumn<>("Дата/время");
        colDate.setCellValueFactory(a -> new javafx.beans.property.SimpleStringProperty(a.getValue().getDateTime()));

        TableColumn<Appointment, String> colDiag = new TableColumn<>("Диагноз");
        colDiag.setCellValueFactory(a -> new javafx.beans.property.SimpleStringProperty(a.getValue().getDiagnosis()));

        table.getColumns().addAll(colId, colPatient, colDoctor, colDate, colDiag);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setCenter(table);

        // Initial load and refresh
        refreshCombos();
        refresh();

        refreshBtn.setOnAction(e -> refreshCombos());

        addBtn.setOnAction(e -> {
            Patient p = patientCombo.getValue();
            Doctor d = doctorCombo.getValue();
            if (p == null || d == null) { showAlert("Ошибка","Выберите пациента и доктора"); return; }
            String dt = dateField.getText().trim();
            String diag = diagnosisField.getText().trim();
            Appointment a = new Appointment(p.getId(), d.getId(), dt, diag);
            appointmentDAO.addAppointment(a);
            dateField.clear(); diagnosisField.clear();
            refresh();
        });
    }

    private void refreshCombos() {
        List<Patient> patients = patientDAO.getAllPatients();
        List<Doctor> doctors = doctorDAO.getAllDoctors();
        patientCombo.getItems().setAll(patients);
        doctorCombo.getItems().setAll(doctors);
    }

    private Map<Integer, String> patientNameCache = new HashMap<>();
    private Map<Integer, String> doctorNameCache = new HashMap<>();

    private String getPatientName(int id) {
        if (!patientNameCache.containsKey(id)) {
            for (Patient p : patientDAO.getAllPatients()) if (p.getId() == id) patientNameCache.put(id, p.getFullName());
        }
        return patientNameCache.getOrDefault(id, "-");
    }

    private String getDoctorName(int id) {
        if (!doctorNameCache.containsKey(id)) {
            for (Doctor d : doctorDAO.getAllDoctors()) if (d.getId() == id) doctorNameCache.put(id, d.getFullName());
        }
        return doctorNameCache.getOrDefault(id, "-");
    }

    private void refresh() {
        appointments.setAll(appointmentDAO.getAllAppointments());
        table.setItems(appointments);
        // clear caches
        patientNameCache.clear(); doctorNameCache.clear();
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}
