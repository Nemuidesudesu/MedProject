package com.medicalsystem.model;

public class Doctor {
    private long id;
    private String firstName;
    private String lastName;
    private String specialization;
    private String phone;
    private String email;

    public Doctor() {}

    public Doctor(long id, String firstName, String lastName, String specialization,
                  String phone, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.phone = phone;
        this.email = email;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " - " + specialization;
    }
}
