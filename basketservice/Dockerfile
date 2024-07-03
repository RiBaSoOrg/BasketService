# Verwende ein offizielles OpenJDK-Image als Basis
FROM openjdk:17-jdk-slim

# Erstelle ein Verzeichnis für die Anwendung
VOLUME /tmp

# Kopiere die JAR-Datei in das Image
ARG JAR_FILE=target/basketservice-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# Exponiere den Port, auf dem die Anwendung läuft
EXPOSE 8080

# Starte die Anwendung
ENTRYPOINT ["java", "-jar", "/app.jar"]
