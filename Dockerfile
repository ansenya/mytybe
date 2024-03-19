FROM openjdk:21-jdk


WORKDIR /app

COPY build/libs/*.jar app.jar

EXPOSE 1984

CMD ["java", "-Xmx2G", "-jar", "app.jar"]