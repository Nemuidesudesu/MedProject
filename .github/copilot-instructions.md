<!-- Copilot instructions for working with the Java_Over medical system project -->
# Project overview
- Small Java-based medical system with both CLI and JavaFX GUI frontends.
- Storage: embedded SQLite (`jdbc:sqlite:database.db`) accessed via `com.medicalsystem.util.DBConnection`.
- Two data-access styles coexist: `dao` (returns model objects) and `repository` (returns formatted strings).

# Big picture (what to know fast)
- Entry points:
  - Console UI: `src/com/medicalsystem/ui/Main.java` (text menu, uses `PatientDAO`).
  - GUI: `src/com/medicalsystem/ui/MainFX.java` (JavaFX, uses `PatientDAO`).
  - DB init + quick test: `src/com/medicalsystem/ui/TestDB.java` and `src/com/medicalsystem/ui/TestPatients.java` (compile/run examples in `README.md`).
- DB initialization: `DBConnection.initializeDatabase()` creates `patients` and `appointments` tables. `TestDB` calls this at startup.

# Key files & responsibilities
- `src/com/medicalsystem/util/DBConnection.java`: central DB URL (`jdbc:sqlite:database.db`), driver registration, and schema creation SQL.
- `src/com/medicalsystem/dao/PatientDAO.java`: primary CRUD for `Patient` model (used by CLI and GUI). Uses try-with-resources and `DBConnection.connect()`.
- `src/com/medicalsystem/repository/PatientRepository.java`: a simpler layer returning formatted `String` records (used by tests/examples).
- `src/com/medicalsystem/ui/MainFX.java`: JavaFX UI; binds `TableView` to `Patient` model and calls DAO methods.

# Build & run (concrete commands discovered in `README.md`)
- Compile (example used by repo):
```powershell
javac -cp "lib/sqlite-jdbc-3.45.3.0.jar;src" -d out src/com/medicalsystem/util/*.java src/com/medicalsystem/ui/*.java
```
- Run test DB or CLI examples (as in README):
```powershell
java -cp ".;lib/sqlite-jdbc-3.45.3.0.jar;lib/slf4j-api-2.0.13.jar;lib/slf4j-simple-2.0.13.jar;out" com.medicalsystem.ui.TestDB
```
- Run CLI main:
```powershell
java -cp "out;lib/*" com.medicalsystem.ui.Main
```
- Run JavaFX GUI (requires JavaFX SDK in `lib/javafx-sdk-25.0.1`):
```powershell
java --module-path "lib/javafx-sdk-25.0.1/lib" --add-modules javafx.controls,javafx.fxml -cp "out;lib/*" com.medicalsystem.ui.MainFX
```
(Adjust paths if your JavaFX SDK jars are elsewhere.)

# Patterns & conventions specific to this repo
- DB access always via `DBConnection.connect()` (single URL constant). Follow try-with-resources for PreparedStatement/ResultSet.
- Two styles coexist: prefer `PatientDAO` when you need `Patient` objects; `PatientRepository` is for string-based quick listing.
- Logging/feedback: code prints user-facing messages (Russian) and emojis (`✅`, `❌`). Keep consistency when adding messages.
- Error handling: methods catch SQLException and print to `System.err`/`System.out` instead of throwing. When changing this, update callers accordingly.

# Integration points & external dependencies
- SQLite JDBC: `lib/sqlite-jdbc-3.45.3.0.jar` — required at compile & runtime classpath.
- SLF4J: `lib/slf4j-api-2.0.13.jar` and `lib/slf4j-simple-2.0.13.jar` used in runtime classpath (README includes them).
- JavaFX SDK: present under `lib/javafx-sdk-25.0.1` — GUI requires `--module-path` and `--add-modules` when running.

# Notable issues and quick checks for contributors
- `MainFX` calls `dao.updatePatient(selected);` but `PatientDAO` in this repo does not define `updatePatient(...)`. Before running GUI flows that edit patients, implement `updatePatient` in `PatientDAO` (same connection pattern).
- Schema edits: `DBConnection.initializeDatabase()` contains the DDL; `create_tables.sql` also exists at repo root if you prefer separate SQL file.

# How Copilot / AI agents should help here (practical guidance)
- When adding DB-facing methods, follow the `try (Connection conn = DBConnection.connect())` pattern and return meaningful booleans/objects as other methods do.
- For GUI changes, update both `MainFX` and CLI `Main` where behavior overlaps (they both rely on `PatientDAO`).
- Keep console messages in Russian to match existing UI language and maintain emoji-style status markers.
- When suggesting run/compile commands, prefer the concrete examples in `README.md` and include `lib/*` or explicit jars shown there.

# If something is missing or unclear
- Look at `src/com/medicalsystem/ui/TestDB.java` for how DB is initialized and seeded.
- If you want, I can implement the missing `updatePatient` in `PatientDAO` or adjust `MainFX` to use a repository method instead.

---
If any section should be expanded (examples, missing methods, or CI/run scripts), tell me which part to improve.