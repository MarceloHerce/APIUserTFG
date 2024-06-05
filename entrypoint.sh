#!/bin/sh

# Crear el archivo application.properties con los valores de las variables de entorno
cat <<EOF > /app/src/main/resources/application.properties
spring.application.name=ApiUsers
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
azureBlobStorageConnectionString=${AZURE_BLOB_STORAGE_CONNECTION_STRING}
azureBlobStorageContainerName=${AZURE_BLOB_STORAGE_CONTAINER_NAME}
spring.cloud.azure.storage.blob.account-name=${AZURE_STORAGE_ACCOUNT_NAME}
spring.cloud.azure.storage.name=${AZURE_STORAGE_ACCOUNT_NAME}
spring.cloud.azure.storage.connection.string=${AZURE_BLOB_STORAGE_CONNECTION_STRING}
spring.cloud.azure.storage.blob.endpoint=${AZURE_BLOB_STORAGE_ENDPOINT}
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB
EOF

# Ejecutar la aplicación
exec java -jar app.jar
