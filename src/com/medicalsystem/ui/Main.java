package com.medicalsystem.ui;

import com.medicalsystem.dao.PatientDAO;
import com.medicalsystem.model.Patient;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final PatientDAO dao = new PatientDAO();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n==== МЕДИЦИНСКАЯ СИСТЕМА ====");
            System.out.println("1. Добавить пациента");
            System.out.println("2. Посмотреть всех пациентов");
            System.out.println("3. Удалить пациента по ID");
            System.out.println("4. Выход");
            System.out.print("Выберите действие: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> addPatientMenu();
                case "2" -> showAllPatients();
                case "3" -> deletePatient();
                case "4" -> {
                    System.out.println(" Завершение программы...");
                    return;
                }
                default -> System.out.println(" Некорректный ввод, попробуйте снова.");
            }
        }
    }

    private static void addPatientMenu() {
        System.out.print("Имя: ");
        String firstName = scanner.nextLine();
        System.out.print("Фамилия: ");
        String lastName = scanner.nextLine();
        System.out.print("Дата рождения (гггг-мм-дд): ");
        String birthDate = scanner.nextLine();
        System.out.print("Телефон: ");
        String phone = scanner.nextLine();
        System.out.print("ИИН: ");
        String iin = scanner.nextLine();

        Patient patient = new Patient(firstName, lastName, birthDate, phone, iin);
        dao.addPatient(patient);
    }

    private static void showAllPatients() {
        List<Patient> patients = dao.getAllPatients();

        if (patients.isEmpty()) {
            System.out.println(" Список пациентов пуст.");
            return;
        }

        System.out.println("\n Список пациентов:");
        System.out.println("ID | Имя | Фамилия | Дата рождения | Телефон | ИИН");
        for (Patient p : patients) {
            System.out.printf("%d | %s | %s | %s | %s | %s%n",
                    p.getId(),
                    p.getFirstName(),
                    p.getLastName(),
                    p.getBirthDate(),
                    p.getPhone(),
                    p.getIin());
        }
    }

    private static void deletePatient() {
        System.out.print("Введите ID пациента для удаления: ");
        int id = Integer.parseInt(scanner.nextLine());
        boolean deleted = dao.deletePatientById(id);

        if (deleted)
            System.out.println(" Пациент успешно удалён.");
        else
            System.out.println(" Пациент с таким ID не найден.");
    }
}
