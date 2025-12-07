package com.medicalsystem.ui;

import com.medicalsystem.dao.AppointmentDAO;
import com.medicalsystem.dao.DoctorDAO;
import com.medicalsystem.dao.PatientDAO;
import com.medicalsystem.model.Appointment;
import com.medicalsystem.model.Doctor;
import com.medicalsystem.model.Patient;
import com.medicalsystem.util.DBConnection;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class MainWeb {
    public static void main(String[] args) throws Exception {
        DBConnection.initializeDatabase();

        int port = 8081;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // статические файлы
        server.createContext("/", new StaticHandler());
        server.createContext("/app.js", new StaticHandler());
        server.createContext("/styles.css", new StaticHandler());

        // API (REST‑интерфейс)
        server.createContext("/api/patients", new PatientsHandler());
        server.createContext("/api/doctors", new DoctorsHandler());
        server.createContext("/api/appointments", new AppointmentsHandler());

        // Страницы создания/редактирования (можно открыть в новом окне)
        server.createContext("/create/patient", new CreatePatientHandler());
        server.createContext("/edit/patient", new EditPatientHandler());
        server.createContext("/create/doctor", new CreateDoctorHandler());
        server.createContext("/edit/doctor", new EditDoctorHandler());
        server.createContext("/create/appointment", new CreateAppointmentHandler());
        server.createContext("/edit/appointment", new EditAppointmentHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("Веб-интерфейс запущен на http://localhost:" + port + " — откройте страницу в браузере.");
    }

    static class StaticHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";
            Path file = Paths.get("web" + path).normalize();
            if (!Files.exists(file) || Files.isDirectory(file)) {
                sendResponse(exchange, 404, "Not found", "text/plain");
                return;
            }
            String contentType = guessContentType(file.toString());
            byte[] bytes = Files.readAllBytes(file);
            exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    // Пациенты
    static class PatientsHandler implements HttpHandler {
        PatientDAO dao = new PatientDAO();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            try {
                if (method.equalsIgnoreCase("GET")) {
                    List<Patient> list = dao.getAllPatients();
                    String json = listToJsonPatients(list);
                    sendResponse(exchange, 200, json, "application/json");
                    return;
                }

                if (method.equalsIgnoreCase("POST")) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
                    Map<String, String> params = parseForm(body);
                    Patient p = new Patient(params.getOrDefault("firstName", ""), params.getOrDefault("lastName", ""), params.getOrDefault("birthDate", ""), params.getOrDefault("phone", ""), params.getOrDefault("iin", ""));
                    dao.addPatient(p);
                    sendResponse(exchange, 201, "{\"ok\":true}", "application/json");
                    return;
                }

                if (method.equalsIgnoreCase("PUT")) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
                    Map<String, String> params = parseForm(body);
                    int id = Integer.parseInt(params.getOrDefault("id", "0"));
                    Patient p = new Patient(id, params.getOrDefault("firstName", ""), params.getOrDefault("lastName", ""), params.getOrDefault("birthDate", ""), params.getOrDefault("phone", ""), params.getOrDefault("iin", ""));
                    boolean ok = dao.updatePatient(p);
                    sendResponse(exchange, ok ? 200 : 404, "{\"ok\":" + ok + "}", "application/json");
                    return;
                }

                if (method.equalsIgnoreCase("DELETE")) {
                    String query = exchange.getRequestURI().getQuery();
                    Map<String, String> q = parseQuery(query);
                    int id = Integer.parseInt(q.getOrDefault("id", "0"));
                    boolean ok = dao.deletePatientById(id);
                    if (ok) {
                        sendResponse(exchange, 200, "{\"ok\":true}", "application/json");
                    } else {
                        sendResponse(exchange, 409, "{\"ok\":false,\"error\":\"Невозможно удалить пациента. У него есть записи о приемах\"}", "application/json");
                    }
                    return;
                }
            } catch (IOException | NumberFormatException e) {
                sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}", "application/json");
                return;
            }

            sendResponse(exchange, 405, "Method not allowed", "text/plain");
        }
    }

    // Врачи
    static class DoctorsHandler implements HttpHandler {
        DoctorDAO dao = new DoctorDAO();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            try {
                if (method.equalsIgnoreCase("GET")) {
                    List<Doctor> list = dao.getAllDoctors();
                    String json = listToJsonDoctors(list);
                    sendResponse(exchange, 200, json, "application/json");
                    return;
                }

                if (method.equalsIgnoreCase("POST")) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
                    Map<String, String> params = parseForm(body);
                    Doctor d = new Doctor(params.getOrDefault("firstName", ""), params.getOrDefault("lastName", ""), params.getOrDefault("specialization", ""), params.getOrDefault("phone", ""), params.getOrDefault("email", ""), params.getOrDefault("iin", ""));
                    dao.addDoctor(d);
                    sendResponse(exchange, 201, "{\"ok\":true}", "application/json");
                    return;
                }

                if (method.equalsIgnoreCase("DELETE")) {
                    String query = exchange.getRequestURI().getQuery();
                    Map<String, String> q = parseQuery(query);
                    int id = Integer.parseInt(q.getOrDefault("id", "0"));
                    boolean ok = dao.deleteDoctorById(id);
                    if (ok) {
                        sendResponse(exchange, 200, "{\"ok\":true}", "application/json");
                    } else {
                        sendResponse(exchange, 409, "{\"ok\":false,\"error\":\"Невозможно удалить врача. У него есть записи о приемах\"}", "application/json");
                    }
                    return;
                }
            } catch (IOException | NumberFormatException e) {
                sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}", "application/json");
                return;
            }

            sendResponse(exchange, 405, "Method not allowed", "text/plain");
        }
    }

    // Записи (приёмы)
    static class AppointmentsHandler implements HttpHandler {
        AppointmentDAO dao = new AppointmentDAO();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            try {
                if (method.equalsIgnoreCase("GET")) {
                    List<Appointment> list = dao.getAllAppointments();
                    // сортировать по возрастанию id (чтобы записи шли 1,2,3...)
                    list.sort(Comparator.comparingInt(Appointment::getId));
                    String json = listToJsonAppointments(list);
                    sendResponse(exchange, 200, json, "application/json");
                    return;
                }

                if (method.equalsIgnoreCase("POST")) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
                    Map<String, String> params = parseForm(body);
                    int patientId = Integer.parseInt(params.getOrDefault("patientId", "0"));
                    int doctorId = Integer.parseInt(params.getOrDefault("doctorId", "0"));
                    int typeId = Integer.parseInt(params.getOrDefault("typeId", "0"));
                    String dateTime = params.getOrDefault("dateTime", "");
                    String diagnosis = params.getOrDefault("diagnosis", "");
                    Appointment a = new Appointment(patientId, doctorId, dateTime, diagnosis);
                    a.setTypeId(typeId);
                    dao.addAppointment(a);
                    sendResponse(exchange, 201, "{\"ok\":true}", "application/json");
                    return;
                }

                if (method.equalsIgnoreCase("PUT")) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
                    Map<String, String> params = parseForm(body);
                    int id = Integer.parseInt(params.getOrDefault("id", "0"));
                    int patientId = Integer.parseInt(params.getOrDefault("patientId", "0"));
                    int doctorId = Integer.parseInt(params.getOrDefault("doctorId", "0"));
                    int typeId = Integer.parseInt(params.getOrDefault("typeId", "0"));
                    String dateTime = params.getOrDefault("dateTime", "");
                    String diagnosis = params.getOrDefault("diagnosis", "");
                    Appointment a = new Appointment(id, patientId, doctorId, dateTime, diagnosis);
                    a.setTypeId(typeId);
                    boolean ok = dao.updateAppointment(a);
                    sendResponse(exchange, ok ? 200 : 404, "{\"ok\":" + ok + "}", "application/json");
                    return;
                }

                if (method.equalsIgnoreCase("DELETE")) {
                    String query = exchange.getRequestURI().getQuery();
                    Map<String, String> q = parseQuery(query);
                    int id = Integer.parseInt(q.getOrDefault("id", "0"));
                    boolean ok = dao.deleteAppointmentById(id);
                    sendResponse(exchange, ok ? 200 : 404, "{\"ok\":" + ok + "}", "application/json");
                    return;
                }
            } catch (IOException | NumberFormatException e) {
                sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}", "application/json");
                return;
            }

            sendResponse(exchange, 405, "Method not allowed", "text/plain");
        }
    }

    // Вспомогательные методы
    static void sendResponse(HttpExchange exchange, int status, String body, String contentType) throws IOException {
        byte[] bytes = body.getBytes("UTF-8");
        Headers h = exchange.getResponseHeaders();
        h.set("Content-Type", contentType + "; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    static String listToJsonPatients(List<Patient> list) {
        return list.stream().map(p -> {
            return "{" +
                    "\"id\":" + p.getId() + "," +
                    "\"firstName\":\"" + escapeJson(p.getFirstName()) + "\"," +
                    "\"lastName\":\"" + escapeJson(p.getLastName()) + "\"," +
                    "\"birthDate\":\"" + escapeJson(p.getBirthDate()) + "\"," +
                    "\"phone\":\"" + escapeJson(p.getPhone()) + "\"," +
                    "\"iin\":\"" + escapeJson(p.getIin()) + "\"}";
        }).collect(Collectors.joining(",", "[", "]"));
    }

    static String listToJsonDoctors(List<Doctor> list) {
        return list.stream().map(d -> {
            return "{" +
                    "\"id\":" + d.getId() + "," +
                    "\"firstName\":\"" + escapeJson(d.getFirstName()) + "\"," +
                    "\"lastName\":\"" + escapeJson(d.getLastName()) + "\"," +
                    "\"specialization\":\"" + escapeJson(d.getSpecialization()) + "\"," +
                    "\"phone\":\"" + escapeJson(d.getPhone()) + "\"," +
                    "\"email\":\"" + escapeJson(d.getEmail()) + "\"," +
                    "\"iin\":\"" + escapeJson(d.getIin()) + "\"}";
        }).collect(Collectors.joining(",", "[", "]"));
    }

    static String listToJsonAppointments(List<Appointment> list) {
        return list.stream().map(a -> {
            return "{" +
                    "\"id\":" + a.getId() + "," +
                    "\"patientId\":" + a.getPatientId() + "," +
                    "\"doctorId\":" + a.getDoctorId() + "," +
                    "\"dateTime\":\"" + escapeJson(a.getDateTime()) + "\"," +
                    "\"diagnosis\":\"" + escapeJson(a.getDiagnosis()) + "\"," +
                    "\"typeId\":" + a.getTypeId() + "," +
                    "\"typeName\":\"" + escapeJson(a.getTypeName()) + "\"," +
                    "\"isOverdue\":" + a.isOverdue() + "}";
        }).collect(Collectors.joining(",", "[", "]"));
    }

    static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    static Map<String, String> parseForm(String body) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        if (body == null || body.isEmpty()) return map;
        String[] parts = body.split("&");
        for (String p : parts) {
            String[] kv = p.split("=", 2);
            String key = URLDecoder.decode(kv[0], "UTF-8");
            String val = kv.length > 1 ? URLDecoder.decode(kv[1], "UTF-8") : "";
            map.put(key, val);
        }
        return map;
    }

    static Map<String, String> parseQuery(String query) throws UnsupportedEncodingException {
        if (query == null) return Collections.emptyMap();
        return parseForm(query);
    }

    // --- Страницы создания и редактирования ---
    static class CreatePatientHandler implements HttpHandler {
        PatientDAO dao = new PatientDAO();
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                String html =
                        "<!doctype html><html><head><meta charset='utf-8'><title>Create Patient</title><style>body{font-family:Arial,sans-serif;padding:20px}form{max-width:400px;margin:0 auto}label{display:block;margin-top:12px;margin-bottom:4px;font-weight:bold}input,button{display:block;width:100%;margin-bottom:12px;padding:8px;box-sizing:border-box}button{background:#2980b9;color:white;border:none;cursor:pointer}button:hover{background:#1f6f9a}</style></head><body>" +
                                "<h2 style='text-align:center'>Создать пациента</h2>" +
                                "<form method='post' action='/create/patient'>" +
                                "<label>Имя:</label><input name='firstName'><br>" +
                                "<label>Фамилия:</label><input name='lastName'><br>" +
                                "<label>Дата рождения:</label><input type='date' name='birthDate'><br>" +
                                "<label>Телефон:</label><input name='phone'><br>" +
                                "<label>ИИН:</label><input name='iin' placeholder='Индивидуальный номер'><br>" +
                                "<button type='submit'>Создать</button>" +
                                "</form>" +
                                "<script>document.querySelector('form').addEventListener('submit', evt=>{evt.preventDefault();fetch('/api/patients',{method:'POST',headers:{'Content-Type':'application/x-www-form-urlencoded'},body:new URLSearchParams(new FormData(evt.target))}).then(()=>{try{ if(typeof BroadcastChannel!=='undefined'){ new BroadcastChannel('medsys').postMessage('patients'); } else { localStorage.setItem('medsys-refresh','patients:' + Date.now()); } }catch(e){ localStorage.setItem('medsys-refresh','patients:' + Date.now()); } window.close();});});</script>" +
                                "</body></html>";
                sendResponse(exchange, 200, html, "text/html");
                return;
            }
            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
                Map<String, String> params = parseForm(body);
                Patient p = new Patient(params.getOrDefault("firstName", ""), params.getOrDefault("lastName", ""), params.getOrDefault("birthDate", ""), params.getOrDefault("phone", ""), params.getOrDefault("iin", ""));
                dao.addPatient(p);
                sendResponse(exchange, 303, "", "text/plain");
                return;
            }
            sendResponse(exchange, 405, "Method not allowed", "text/plain");
        }
    }

    static class EditPatientHandler implements HttpHandler {
        PatientDAO dao = new PatientDAO();
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getQuery();
            int id = 0;
            if (query != null && !query.isEmpty()) {
                try {
                    id = Integer.parseInt(parseQuery(query).getOrDefault("id", "0"));
                } catch (NumberFormatException e) {
                    sendResponse(exchange, 400, "Invalid id", "text/plain");
                    return;
                }
            }
            if (id == 0) {
                sendResponse(exchange, 400, "Missing or invalid id parameter", "text/plain");
                return;
            }
            if (method.equalsIgnoreCase("GET")) {
                Patient p = dao.getPatientById(id);
                if (p == null) { sendResponse(exchange, 404, "Patient not found", "text/plain"); return; }
                String html =
                        "<!doctype html><html><head><meta charset='utf-8'><title>Edit Patient</title><style>body{font-family:Arial,sans-serif;padding:20px}form{max-width:400px;margin:0 auto}label{display:block;margin-top:12px;margin-bottom:4px;font-weight:bold}input,button{display:block;width:100%;margin-bottom:12px;padding:8px;box-sizing:border-box}button{background:#2980b9;color:white;border:none;cursor:pointer}button:hover{background:#1f6f9a}</style></head><body>" +
                                "<h2 style='text-align:center'>Редактировать пациента</h2>" +
                        "<form id='f' method='post'>" +
                                "<label>Имя:</label><input name='firstName' value='"+escapeHtml(p.getFirstName())+"'><br>" +
                                "<label>Фамилия:</label><input name='lastName' value='"+escapeHtml(p.getLastName())+"'><br>" +
                                "<label>Дата рождения:</label><input type='date' name='birthDate' value='"+escapeHtml(p.getBirthDate())+"'><br>" +
                                "<label>Телефон:</label><input name='phone' value='"+escapeHtml(p.getPhone())+"'><br>" +
                                "<label>ИИН:</label><input name='iin' value='"+escapeHtml(p.getIin())+"'><br>" +
                                "<button type='submit'>Сохранить</button>" +
                                "</form>" +
                        "<script>document.getElementById('f').addEventListener('submit', evt=>{evt.preventDefault();const fd=new FormData(evt.target);fd.append('id','"+id+"');fetch('/edit/patient?id="+id+"',{method:'POST',headers:{'Content-Type':'application/x-www-form-urlencoded'},body:new URLSearchParams(fd)}).then(()=>{try{ if(typeof BroadcastChannel!=='undefined'){ new BroadcastChannel('medsys').postMessage('patients'); } else { localStorage.setItem('medsys-refresh','patients:' + Date.now()); } }catch(e){ localStorage.setItem('medsys-refresh','patients:' + Date.now()); } window.close();});});</script>" +
                                "</body></html>";
                sendResponse(exchange, 200, html, "text/html");
                return;
            }
            if (method.equalsIgnoreCase("POST")) {
                String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
                Map<String, String> params = parseForm(body);
                Patient p = new Patient(id, params.getOrDefault("firstName", ""), params.getOrDefault("lastName", ""), params.getOrDefault("birthDate", ""), params.getOrDefault("phone", ""), params.getOrDefault("iin", ""));
                dao.updatePatient(p);
                sendResponse(exchange, 303, "", "text/plain");
                return;
            }
            sendResponse(exchange, 405, "Method not allowed", "text/plain");
        }
    }

    // --- Доктора ---
    static class CreateDoctorHandler implements HttpHandler {
        DoctorDAO dao = new DoctorDAO();
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                String html =
                        "<!doctype html><html><head><meta charset='utf-8'><title>Create Doctor</title><style>body{font-family:Arial,sans-serif;padding:20px}form{max-width:400px;margin:0 auto}label{display:block;margin-top:12px;margin-bottom:4px;font-weight:bold}input,button{display:block;width:100%;margin-bottom:12px;padding:8px;box-sizing:border-box}button{background:#2980b9;color:white;border:none;cursor:pointer}button:hover{background:#1f6f9a}</style></head><body>" +
                                "<h2 style='text-align:center'>Создать врача</h2>" +
                                "<form id='f'>" +
                                "<label>Имя:</label><input name='firstName'><br>" +
                                "<label>Фамилия:</label><input name='lastName'><br>" +
                                "<label>Специальность:</label><input name='specialization'><br>" +
                                "<label>Телефон:</label><input name='phone'><br>" +
                                "<label>Email:</label><input name='email'><br>" +
                                "<label>ИИН:</label><input name='iin' placeholder='Индивидуальный номер'><br>" +
                                "<button type='submit'>Создать</button>" +
                                "</form>" +
                               "<script>document.getElementById('f').addEventListener('submit',evt=>{evt.preventDefault();fetch('/api/doctors',{method:'POST',headers:{'Content-Type':'application/x-www-form-urlencoded'},body:new URLSearchParams(new FormData(evt.target))}).then(()=>{try{ if(typeof BroadcastChannel!=='undefined'){ new BroadcastChannel('medsys').postMessage('doctors'); } else { localStorage.setItem('medsys-refresh','doctors:' + Date.now()); } }catch(e){ localStorage.setItem('medsys-refresh','doctors:' + Date.now()); } window.close();});});</script>" +
                                "</body></html>";
                sendResponse(exchange, 200, html, "text/html"); return;
            }
            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
                Map<String, String> params = parseForm(body);
                Doctor d = new Doctor(params.getOrDefault("firstName", ""), params.getOrDefault("lastName", ""), params.getOrDefault("specialization", ""), params.getOrDefault("phone", ""), params.getOrDefault("email", ""), params.getOrDefault("iin", ""));
                dao.addDoctor(d);
                sendResponse(exchange, 303, "", "text/plain");
                return;
            }
            sendResponse(exchange, 405, "Method not allowed", "text/plain");
        }
    }

    static class EditDoctorHandler implements HttpHandler {
        DoctorDAO dao = new DoctorDAO();
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            int id = 0;
            if (query != null && !query.isEmpty()) {
                try {
                    id = Integer.parseInt(parseQuery(query).getOrDefault("id", "0"));
                } catch (NumberFormatException e) {
                    sendResponse(exchange, 400, "Invalid id", "text/plain");
                    return;
                }
            }
            if (id == 0) {
                sendResponse(exchange, 400, "Missing or invalid id parameter", "text/plain");
                return;
            }
            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                Doctor d = dao.getDoctorById(id);
                if (d == null) { sendResponse(exchange, 404, "Doctor not found", "text/plain"); return; }
                String html =
                    "<!doctype html><html><head><meta charset='utf-8'><title>Edit Doctor</title><style>body{font-family:Arial,sans-serif;padding:20px}form{max-width:400px;margin:0 auto}label{display:block;margin-top:12px;margin-bottom:4px;font-weight:bold}input,button{display:block;width:100%;margin-bottom:12px;padding:8px;box-sizing:border-box}button{background:#2980b9;color:white;border:none;cursor:pointer}button:hover{background:#1f6f9a}</style></head><body>" +
                        "<h2 style='text-align:center'>Редактировать врача</h2>" +
                        "<form id='f' method='post'>" +
                        "<label>Имя:</label><input name='firstName' value='"+escapeHtml(d.getFirstName())+"'><br>" +
                        "<label>Фамилия:</label><input name='lastName' value='"+escapeHtml(d.getLastName())+"'><br>" +
                        "<label>Специальность:</label><input name='specialization' value='"+escapeHtml(d.getSpecialization())+"'><br>" +
                        "<label>Телефон:</label><input name='phone' value='"+escapeHtml(d.getPhone())+"'><br>" +
                        "<label>Email:</label><input name='email' value='"+escapeHtml(d.getEmail())+"'><br>" +
                        "<label>ИИН:</label><input name='iin' value='"+escapeHtml(d.getIin())+"'><br>" +
                        "<button type='submit'>Сохранить</button>" +
                        "</form>" +
                        "<script>document.getElementById('f').addEventListener('submit',evt=>{evt.preventDefault();const fd=new FormData(evt.target);fd.append('id','"+id+"');fetch('/edit/doctor?id="+id+"',{method:'POST',headers:{'Content-Type':'application/x-www-form-urlencoded'},body:new URLSearchParams(fd)}).then(()=>{try{ if(typeof BroadcastChannel!=='undefined'){ new BroadcastChannel('medsys').postMessage('doctors'); } else { localStorage.setItem('medsys-refresh','doctors:' + Date.now()); } }catch(e){ localStorage.setItem('medsys-refresh','doctors:' + Date.now()); } window.close();});});</script>" +
                        "</body></html>";
                sendResponse(exchange, 200, html, "text/html"); return;
            }
            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
                Map<String, String> params = parseForm(body);
                Doctor d = new Doctor(id, params.getOrDefault("firstName", ""), params.getOrDefault("lastName", ""), params.getOrDefault("specialization", ""), params.getOrDefault("phone", ""), params.getOrDefault("email", ""), params.getOrDefault("iin", ""));
                dao.updateDoctor(d);
                sendResponse(exchange, 303, "", "text/plain"); return;
            }
            sendResponse(exchange, 405, "Method not allowed", "text/plain");
        }
    }

    // --- Приёмы (appointments) ---
    static class CreateAppointmentHandler implements HttpHandler {
        AppointmentDAO dao = new AppointmentDAO();
        PatientDAO pdao = new PatientDAO();
        DoctorDAO ddao = new DoctorDAO();
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                List<Patient> patients = pdao.getAllPatients();
                List<Doctor> doctors = ddao.getAllDoctors();
                StringBuilder sbPatients = new StringBuilder();
                for (Patient p: patients) {
                    sbPatients.append("<option value='").append(p.getId()).append("'>")
                              .append(escapeHtml(p.getFirstName() + " " + p.getLastName()))
                              .append("</option>");
                }
                StringBuilder sbDoctors = new StringBuilder();
                for (Doctor d: doctors) {
                    sbDoctors.append("<option value='").append(d.getId()).append("'>")
                             .append(escapeHtml(d.getFirstName() + " " + d.getLastName() + " (" + d.getSpecialization() + ")"))
                             .append("</option>");
                }
                String html =
                    "<!doctype html><html><head><meta charset='utf-8'><title>Create Appointment</title><style>body{font-family:Arial,sans-serif;padding:20px}form{max-width:400px;margin:0 auto}label{display:block;margin-top:12px;margin-bottom:4px;font-weight:bold}input,select,button{display:block;width:100%;margin-bottom:12px;padding:8px;box-sizing:border-box;font-size:14px}button{background:#2980b9;color:white;border:none;cursor:pointer}button:hover{background:#1f6f9a}</style></head><body>" +
                        "<h2 style='text-align:center'>Создать приём</h2>" +
                        "<form id='f'>" +
                        "<label>Пациент:</label><select name='patientId'>"+sbPatients.toString()+"</select>" +
                        "<label>Врач:</label><select name='doctorId'>"+sbDoctors.toString()+"</select>" +
                        "<label>Дата/время:</label><input type='datetime-local' name='dateTime'>" +
                        "<label>Тип приёма:</label><select name='typeId'><option value='1'>Платная</option><option value='2'>По страховке</option><option value='3'>Экстренная</option><option value='4'>По месту закрепления</option></select>" +
                        "<label>Диагноз:</label><input name='diagnosis'>" +
                        "<button type='submit'>Создать</button>" +
                        "</form>" +
                        "<script>document.getElementById('f').addEventListener('submit',evt=>{evt.preventDefault();const fd=new FormData(evt.target);const dt=fd.get('dateTime');if(dt){fd.set('dateTime',dt.replace('T',' '));}fetch('/api/appointments',{method:'POST',headers:{'Content-Type':'application/x-www-form-urlencoded'},body:new URLSearchParams(fd)}).then(()=>{try{ if(typeof BroadcastChannel!=='undefined'){ new BroadcastChannel('medsys').postMessage('appointments'); } else { localStorage.setItem('medsys-refresh','appointments:' + Date.now()); } }catch(e){ localStorage.setItem('medsys-refresh','appointments:' + Date.now()); } window.close();});});</script>" +
                        "</body></html>";
                sendResponse(exchange, 200, html, "text/html"); return;
            }
            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
                Map<String, String> params = parseForm(body);
                int pid = Integer.parseInt(params.getOrDefault("patientId", "0"));
                int did = Integer.parseInt(params.getOrDefault("doctorId", "0"));
                int typeId = Integer.parseInt(params.getOrDefault("typeId", "0"));
                Appointment a = new Appointment(pid, did, params.getOrDefault("dateTime", ""), params.getOrDefault("diagnosis", ""));
                a.setTypeId(typeId);
                dao.addAppointment(a);
                sendResponse(exchange, 303, "", "text/plain"); return;
            }
            sendResponse(exchange, 405, "Method not allowed", "text/plain");
        }
    }

    static class EditAppointmentHandler implements HttpHandler {
        AppointmentDAO dao = new AppointmentDAO();
        PatientDAO pdao = new PatientDAO();
        DoctorDAO ddao = new DoctorDAO();
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            int id = 0;
            if (query != null && !query.isEmpty()) {
                try {
                    id = Integer.parseInt(parseQuery(query).getOrDefault("id", "0"));
                } catch (NumberFormatException e) {
                    sendResponse(exchange, 400, "Invalid id", "text/plain");
                    return;
                }
            }
            if (id == 0) {
                sendResponse(exchange, 400, "Missing or invalid id parameter", "text/plain");
                return;
            }
            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                Appointment a = dao.getAppointmentById(id);
                if (a == null) { sendResponse(exchange, 404, "Appointment not found", "text/plain"); return; }
                List<Patient> patients = pdao.getAllPatients();
                List<Doctor> doctors = ddao.getAllDoctors();
                StringBuilder sbPatients = new StringBuilder();
                for (Patient p: patients) {
                    sbPatients.append("<option value='").append(p.getId()).append("' ")
                              .append(p.getId() == a.getPatientId() ? "selected" : "")
                              .append(">").append(escapeHtml(p.getFirstName() + " " + p.getLastName()))
                              .append("</option>");
                }
                StringBuilder sbDoctors = new StringBuilder();
                for (Doctor d: doctors) {
                    sbDoctors.append("<option value='").append(d.getId()).append("' ")
                             .append(d.getId() == a.getDoctorId() ? "selected" : "")
                             .append(">").append(escapeHtml(d.getFirstName() + " " + d.getLastName() + " (" + d.getSpecialization() + ")"))
                             .append("</option>");
                }
                // Convert datetime string (YYYY-MM-DD HH:MM) to datetime-local format (YYYY-MM-DDTHH:MM)
                String dtValue = escapeHtml(a.getDateTime()).replace(" ", "T");
                String html =
                        "<!doctype html><html><head><meta charset='utf-8'><title>Edit Appointment</title><style>body{font-family:Arial,sans-serif;padding:20px}form{max-width:400px;margin:0 auto}label{display:block;margin-top:12px;margin-bottom:4px;font-weight:bold}input,select,button{display:block;width:100%;margin-bottom:12px;padding:8px;box-sizing:border-box;font-size:14px}button{background:#2980b9;color:white;border:none;cursor:pointer}button:hover{background:#1f6f9a}</style></head><body>" +
                                "<h2 style='text-align:center'>Редактировать приём</h2>" +
                                "<form id='f' method='post'>" +
                                "<label>Пациент:</label><select name='patientId'>"+sbPatients.toString()+"</select>" +
                                "<label>Врач:</label><select name='doctorId'>"+sbDoctors.toString()+"</select>" +
                                "<label>Дата/время:</label><input type='datetime-local' name='dateTime' value='"+dtValue+"'>" +
                                "<label>Тип приёма:</label><select name='typeId'><option value='1' " + (a.getTypeId() == 1 ? "selected" : "") + ">Платная</option><option value='2' " + (a.getTypeId() == 2 ? "selected" : "") + ">По страховке</option><option value='3' " + (a.getTypeId() == 3 ? "selected" : "") + ">Экстренная</option><option value='4' " + (a.getTypeId() == 4 ? "selected" : "") + ">По месту закрепления</option></select>" +
                                "<label>Диагноз:</label><input name='diagnosis' value='"+escapeHtml(a.getDiagnosis())+"'>" +
                                "<button type='submit'>Сохранить</button>" +
                                "</form>" +
                        "<script>document.getElementById('f').addEventListener('submit',evt=>{evt.preventDefault();const fd=new FormData(evt.target);const dt=fd.get('dateTime');if(dt){fd.set('dateTime',dt.replace('T',' '));}fd.append('id','"+id+"');fetch('/edit/appointment?id="+id+"',{method:'POST',headers:{'Content-Type':'application/x-www-form-urlencoded'},body:new URLSearchParams(fd)}).then(()=>{try{ if(typeof BroadcastChannel!=='undefined'){ new BroadcastChannel('medsys').postMessage('appointments'); } else { localStorage.setItem('medsys-refresh','appointments:' + Date.now()); } }catch(e){ localStorage.setItem('medsys-refresh','appointments:' + Date.now()); } window.close();});});</script>" +
                                "</body></html>";
                sendResponse(exchange, 200, html, "text/html"); return;
            }
            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
                Map<String, String> params = parseForm(body);
                int pid = Integer.parseInt(params.getOrDefault("patientId", "0"));
                int did = Integer.parseInt(params.getOrDefault("doctorId", "0"));
                int typeId = Integer.parseInt(params.getOrDefault("typeId", "0"));
                Appointment a = new Appointment(id, pid, did, params.getOrDefault("dateTime", ""), params.getOrDefault("diagnosis", ""));
                a.setTypeId(typeId);
                dao.updateAppointment(a);
                sendResponse(exchange, 303, "", "text/plain"); return;
            }
            sendResponse(exchange, 405, "Method not allowed", "text/plain");
        }
    }

    static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("'", "&#39;").replace("\"", "&quot;");
    }

    static String guessContentType(String name) {
        if (name.endsWith(".html")) return "text/html";
        if (name.endsWith(".js")) return "application/javascript";
        if (name.endsWith(".css")) return "text/css";
        if (name.endsWith(".json")) return "application/json";
        return "application/octet-stream";
    }
}
