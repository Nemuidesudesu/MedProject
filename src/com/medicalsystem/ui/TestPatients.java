package com.medicalsystem.ui;

import com.medicalsystem.repository.PatientRepository;

public class TestPatients {
    public static void main(String[] args) {
        PatientRepository repo = new PatientRepository();

        // Добавим пару пациентов
        repo.addPatient("Айдана", "Куаныш", "1998-03-12", "+77051234567");
        repo.addPatient("Дамир", "Серик", "2001-08-22", "+77087654321");

        // Выведем всех пациентов
        System.out.println("\n Список пациентов:");
        for (String p : repo.getAllPatients()) {
            System.out.println(p);
        }
    }
}
