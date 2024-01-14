# Step 1: Build stage using Maven
FROM maven:3.8.4-openjdk-17 as build

# Copy the source code
COPY . /tui-challenge-api

# Set the working directory
WORKDIR /tui-challenge-api

# Compile and package the application
RUN mvn clean package

# Step 2: Create the runtime image
FROM openjdk:17-slim

# Copy the JAR from the build stage to the runtime container
COPY --from=build /tui-challenge-api/target/*.jar app.jar

# Expose the port your app runs on
EXPOSE 8080

# Command to execute the application
CMD ["java", "-jar", "app.jar"]
