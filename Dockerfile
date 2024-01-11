# Step 1: Build stage using Maven
FROM maven:3.8.4-openjdk-21 as build

# Copy the source code
COPY . /app

# Set the working directory
WORKDIR /app

# Compile and package the application
RUN mvn clean package

# Step 2: Create the runtime image
FROM openjdk:21-slim

# Copy the JAR from the build stage to the runtime container
COPY --from=build /app/target/*.jar app.jar

# Expose the port your app runs on
EXPOSE 8080

# Command to execute the application
CMD ["java", "-jar", "app.jar"]
