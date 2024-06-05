# Usar una imagen base oficial de Java 17
FROM openjdk:17-slim

# Establecer el directorio de trabajo en el contenedor
WORKDIR /app

# Copiar el archivo jar del proyecto Spring Boot en el directorio de trabajo del contenedor
COPY target/ApiUsers-1.0-SNAPSHOT.jar app.jar

# Exponer el puerto en el que se ejecutará la aplicación Spring Boot
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "app.jar"]
