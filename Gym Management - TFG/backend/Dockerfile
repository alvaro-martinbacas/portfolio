
FROM openjdk:24-jdk-slim

RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*


WORKDIR /app


COPY pom.xml .
COPY src ./src


RUN mvn clean package -DskipTests


EXPOSE 8080


CMD ["java", "-jar", "target/gestion-gimnasios-0.0.1-SNAPSHOT.jar"]