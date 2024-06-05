# Usar una imagen base oficial de Java 17
FROM openjdk:17-slim

# Establecer el directorio de trabajo en el contenedor
WORKDIR /app

# Argumentos de construcción
ARG DATABASE_URL
ARG DATABASE_USERNAME
ARG DATABASE_PASSWORD
ARG AZURE_BLOB_STORAGE_CONNECTION_STRING
ARG AZURE_BLOB_STORAGE_CONTAINER_NAME
ARG AZURE_STORAGE_ACCOUNT_NAME
ARG AZURE_BLOB_STORAGE_ENDPOINT

# Establecer las variables de entorno
ENV DATABASE_URL $DATABASE_URL
ENV DATABASE_USERNAME $DATABASE_USERNAME
ENV DATABASE_PASSWORD $DATABASE_PASSWORD
ENV AZURE_BLOB_STORAGE_CONNECTION_STRING $AZURE_BLOB_STORAGE_CONNECTION_STRING
ENV AZURE_BLOB_STORAGE_CONTAINER_NAME $AZURE_BLOB_STORAGE_CONTAINER_NAME
ENV AZURE_STORAGE_ACCOUNT_NAME $AZURE_STORAGE_ACCOUNT_NAME
ENV AZURE_BLOB_STORAGE_ENDPOINT $AZURE_BLOB_STORAGE_ENDPOINT

# Copiar el archivo jar del proyecto Spring Boot en el directorio de trabajo del contenedor
COPY target/ApiUsers-1.0-SNAPSHOT.jar app.jar

# Copiar el archivo de propiedades
COPY src/main/resources/application.properties /app/src/main/resources/application.properties

# Exponer el puerto en el que se ejecutará la aplicación Spring Boot
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "app.jar"]
