FROM amazoncorretto:21-alpine

WORKDIR /app

COPY build/libs/*.jar paytogether.jar

ARG PORT=80
ENV PORT ${PORT}
EXPOSE ${PORT}

ENTRYPOINT java -jar /app/paytogether.jar --server.port=${PORT} -Duser.timezone=Asia/Seoul