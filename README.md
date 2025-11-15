Тест DB
javac -cp "lib/sqlite-jdbc-3.45.3.0.jar;src" -d out src/com/medicalsystem/util/*.java src/com/medicalsystem/ui/*.java

java -cp ".;lib/sqlite-jdbc-3.45.3.0.jar;lib/slf4j-api-2.0.13.jar;lib/slf4j-simple-2.0.13.jar;out" com.medicalsystem.ui.TestDB

Тест Пациентов их данных
javac -cp "lib/sqlite-jdbc-3.45.3.0.jar;lib/slf4j-api-2.0.13.jar;lib/slf4j-simple-2.0.13.jar;src" -d out src/com/medicalsystem/util/*.java src/com/medicalsystem/repository/*.java src/com/medicalsystem/ui/*.java

java -cp ".;lib/sqlite-jdbc-3.45.3.0.jar;lib/slf4j-api-2.0.13.jar;lib/slf4j-simple-2.0.13.jar;out" com.medicalsystem.ui.TestPatients


chcp 65001
java -cp "out;lib/*" com.medicalsystem.ui.Main