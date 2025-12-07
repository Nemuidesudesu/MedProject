package com.medicalsystem.model;

public class Doctor {
    private int id;
    private String firstName;
    private String lastName;
    private String specialization;
    private String phone;
    private String email;
    private String iin;

    public Doctor() {}

    public Doctor(String firstName, String lastName, String specialization, String phone, String email, String iin) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.phone = phone;
        this.email = email;
        this.iin = iin;
    }

    public Doctor(int id, String firstName, String lastName, String specialization, String phone, String email, String iin) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.phone = phone;
        this.email = email;
        this.iin = iin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIin() {
        return iin;
    }

    public void setIin(String iin) {
        this.iin = iin;
    }

    public String getFullName() {
        return (firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName);
    }

    @Override
    public String toString() {
        return getFullName() + " (" + specialization + ")";
    }
}
