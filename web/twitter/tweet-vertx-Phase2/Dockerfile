# Use Maven 3.8.4 with JDK 17 for the build stage
FROM maven:3.8.3-openjdk-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and src directory (your Maven project) into the container
COPY pom.xml .
COPY src ./src

# Build the project inside the container
RUN mvn clean package -Dmaven.test.skip=true

# Use the official JDK 17 slim image for the final stage
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built jar file from the build stage into the final stage
COPY --from=build /app/target/tweet-0.1.0.jar .

# Specify the entrypoint to run your application
ENTRYPOINT ["java", "-jar", "tweet-0.1.0.jar"]