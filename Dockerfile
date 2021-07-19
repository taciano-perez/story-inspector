FROM maven:3.6.3-openjdk-11-slim as builder

WORKDIR /app/build
COPY pom.xml /app/build/pom.xml
COPY ./ /app/build/

RUN mvn clean install -DskipTests

# Use AdoptOpenJDK for base image.
FROM adoptopenjdk/openjdk11:jre-11.0.8_10-alpine

# Copy the jar to the production image from the builder stage.
COPY --from=builder /app/build/book-processor-batch/target/*.jar /app.jar

# Delete source code and build artifacts
RUN rm -fR /app/build/*

# Run the web service on container startup.
CMD ["java", "-noverify", "-jar", "/app.jar"]