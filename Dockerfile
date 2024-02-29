FROM openjdk:19-jdk-alpine

RUN apk update && apk add --no-cache ffmpeg

WORKDIR /app

RUN mkdir -p imgs vids scripts
COPY scripts/encode.sh scripts/encode.sh
COPY imgs/def.png imgs/def.png
COPY imgs/def_th.png imgs/def_th.png

RUN chmod +x scripts/encode.sh

COPY build/libs/*.jar app.jar

EXPOSE 1986

CMD ["java", "-Xmx2G", "-jar", "app.jar"]
