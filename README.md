# Java_Over — Medical system (small demo)

Quick commands (Windows PowerShell)

```powershell
# Build all Java sources (requires JDK + sqlite JDBC jar in lib/)
javac --module-path "lib/javafx-sdk-25.0.1/lib" --add-modules javafx.controls,javafx.fxml -cp "lib/*;src" -d out src\com\medicalsystem\**\*.java

# Run DB test (creates tables and seeds example patients)
java -cp ".;lib/sqlite-jdbc-3.45.3.0.jar;lib/slf4j-api-2.0.13.jar;lib/slf4j-simple-2.0.13.jar;out" com.medicalsystem.ui.TestDB

# Run CLI
chcp 65001
java -cp "out;lib/*" com.medicalsystem.ui.Main

# Run JavaFX GUI (Windows PowerShell). Ensure JavaFX SDK is at lib/javafx-sdk-25.0.1
java --module-path "lib/javafx-sdk-25.0.1/lib" --add-modules javafx.controls,javafx.fxml -Dfile.encoding=UTF-8 -cp "out;lib/*" com.medicalsystem.ui.MainFX

```

Notes:
- The GUI includes a "Пациенты" tab with add/edit/delete/search features implemented in `src/com/medicalsystem/ui/PatientsView.java`.
- The DB schema used by the app is defined in `create_tables.sql` and also created at runtime by `DBConnection.initializeDatabase()`.
- If you modify schema, either update `DBConnection.initializeDatabase()` or run a migration (deleting `database.db` will recreate tables on next run).

TODOs:
- Implement appointments UI and doctor records (`src/com/medicalsystem/service` intended location).
- Add unit tests and input validation for dates.
Тест DB
javac -cp "lib/sqlite-jdbc-3.45.3.0.jar;src" -d out src/com/medicalsystem/util/*.java src/com/medicalsystem/ui/*.java

java -cp ".;lib/sqlite-jdbc-3.45.3.0.jar;lib/slf4j-api-2.0.13.jar;lib/slf4j-simple-2.0.13.jar;out" com.medicalsystem.ui.TestDB

Тест Пациентов их данных
javac -cp "lib/sqlite-jdbc-3.45.3.0.jar;lib/slf4j-api-2.0.13.jar;lib/slf4j-simple-2.0.13.jar;src" -d out src/com/medicalsystem/util/*.java src/com/medicalsystem/repository/*.java src/com/medicalsystem/ui/*.java

java -cp ".;lib/sqlite-jdbc-3.45.3.0.jar;lib/slf4j-api-2.0.13.jar;lib/slf4j-simple-2.0.13.jar;out" com.medicalsystem.ui.TestPatients


chcp 65001
java -cp "out;lib/*" com.medicalsystem.ui.Main