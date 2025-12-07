package com.medicalsystem.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

// Appointment model. Field `dateTime` stores ISO date or datetime (e.g. 2025-11-15 or 2025-11-15T14:30).
public class Appointment {
    private int id;
    private int patientId;
    private int doctorId; // optional: id of doctor (used by web UI)
    private String doctor; // optional: doctor name (stored in DB as text)
    private String dateTime;
    private String diagnosis;
    private int typeId; // appointment type (1=Платная, 2=По страховке, 3=Экстренная, 4=По месту закрепления)
    private String typeName; // appointment type name

    public Appointment() {}

    // constructor used by web handlers: patientId, doctorId, dateTime, diagnosis
    public Appointment(int patientId, int doctorId, String dateTime, String diagnosis) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.dateTime = dateTime;
        this.diagnosis = diagnosis;
    }

    // constructor used by web handlers with id
    public Appointment(int id, int patientId, int doctorId, String dateTime, String diagnosis) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.dateTime = dateTime;
        this.diagnosis = diagnosis;
    }

    // constructor with type
    public Appointment(int id, int patientId, int doctorId, String dateTime, String diagnosis, int typeId) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.dateTime = dateTime;
        this.diagnosis = diagnosis;
        this.typeId = typeId;
    }

    // constructor used when DAO returns doctor name instead of id
    public Appointment(int id, int patientId, String doctorName, String dateTime, String diagnosis) {
        this.id = id;
        this.patientId = patientId;
        this.doctor = doctorName;
        this.dateTime = dateTime;
        this.diagnosis = diagnosis;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    public String getDoctor() { return doctor; }
    public void setDoctor(String doctor) { this.doctor = doctor; }

    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public int getTypeId() { return typeId; }
    public void setTypeId(int typeId) { this.typeId = typeId; }

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

    // Try parse as LocalDateTime, fallback to LocalDate. Handles both "YYYY-MM-DD HH:MM" and "YYYY-MM-DDTHH:MM" formats.
    public LocalDateTime toLocalDateTimeOrNull() {
        if (dateTime == null) return null;
        try { 
            // Try ISO format with T separator first (from datetime-local input)
            return LocalDateTime.parse(dateTime); 
        }
        catch (DateTimeParseException e) {
            try { 
                // Try format with space separator (YYYY-MM-DD HH:MM)
                String normalized = dateTime.replace(" ", "T");
                return LocalDateTime.parse(normalized); 
            }
            catch (DateTimeParseException ex) {
                try { 
                    // Fallback to date only
                    LocalDate d = LocalDate.parse(dateTime); 
                    return d.atStartOfDay(); 
                }
                catch (DateTimeParseException ex2) { 
                    return null; 
                }
            }
        }
    }

    // true if appointment date/time is more than 10 minutes in the past
    public boolean isOverdue() {
        LocalDateTime dt = toLocalDateTimeOrNull();
        if (dt == null) return false;
        // Check if appointment time is more than 10 minutes before now
        return dt.isBefore(LocalDateTime.now().minusMinutes(10));
    }

    @Override
    public String toString() {
        return "Appointment{" + "id=" + id + ", patientId=" + patientId + ", doctorId=" + doctorId + ", doctor='" + doctor + '\'' + ", dateTime='" + dateTime + '\'' + ", diagnosis='" + diagnosis + '\'' + ", typeId=" + typeId + ", typeName='" + typeName + '\'' + '}';
    }
}
