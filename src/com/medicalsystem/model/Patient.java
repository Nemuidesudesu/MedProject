package com.medicalsystem.model;

public class Patient {
    private int id;
    private String firstName;
    private String lastName;
    private String birthDate;
    private String phone;
    private String iin;

    // Конструктор без ID (для добавления новых пациентов)
    public Patient(String firstName, String lastName, String birthDate, String phone, String iin) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.iin = iin;
    }

    // Конструктор с ID (для загрузки из БД)
    public Patient(int id, String firstName, String lastName, String birthDate, String phone, String iin) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.iin = iin;
    }

    // Геттеры и сеттеры
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

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIin() {
        return iin;
    }

    public void setIin(String iin) {
        this.iin = iin;
    }

    // Удобный метод для отображения имени
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return getFullName() + " (ID:" + id + ")";
    }
}
