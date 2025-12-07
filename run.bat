@echo off
cd /d c:\Users\kaira\Desktop\MedProjectNew\Java_Over
echo Compiling...
javac -cp "lib/sqlite-jdbc-3.45.3.0.jar" src/com/medicalsystem/util/DBConnection.java src/com/medicalsystem/model/*.java src/com/medicalsystem/dao/*.java src/com/medicalsystem/ui/MainWeb.java
echo.
echo Starting server...
java -cp "lib/sqlite-jdbc-3.45.3.0.jar;lib/slf4j-api-2.0.13.jar;lib/slf4j-simple-2.0.13.jar;src" com.medicalsystem.ui.MainWeb
