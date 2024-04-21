# Use the OpenJDK 17 as the base image
FROM openjdk:17

# Install MySQL server
RUN apt-get update && \
    apt-get install -y mysql-server && \
    rm -rf /var/lib/apt/lists/*

# Copy your Spring Boot application JAR file into the container
COPY target/MedicineReorderService-0.0.1-SNAPSHOT.jar app.jar

# Expose MySQL port (default port 3306)
EXPOSE 3306

# Expose your Spring Boot application port (if it's different from MySQL)
EXPOSE 8080

# Start MySQL server and your Spring Boot application
CMD service mysql start && java -jar /app.jar
