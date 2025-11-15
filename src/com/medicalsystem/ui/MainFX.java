package com.medicalsystem.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

// Main JavaFX application: top-level TabPane with Patients, Appointments, About
public class MainFX extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Медицинская система");

        TabPane tabs = new TabPane();

        // Patients tab — uses PatientsView (separate component)
        Tab patientsTab = new Tab("Пациенты");
        patientsTab.setContent(new PatientsView());
        patientsTab.setClosable(false);

        // Appointments tab — placeholder for now
        Tab apptTab = new Tab("Приёмы");
        StackPane apptPane = new StackPane(new Label("Раздел приёмов — TODO"));
        apptTab.setContent(apptPane);
        apptTab.setClosable(false);

        // About tab
        Tab aboutTab = new Tab("О программе");
        StackPane aboutPane = new StackPane(new Label("Медицинская система — JavaFX demo"));
        aboutTab.setContent(aboutPane);
        aboutTab.setClosable(false);

        tabs.getTabs().addAll(patientsTab, apptTab, aboutTab);

        Scene scene = new Scene(tabs, 1000, 600);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Ensure UTF-8 if needed via VM arg: -Dfile.encoding=UTF-8
        launch(args);
    }
}
