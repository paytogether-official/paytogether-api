FROM amazoncorretto:21-alpine

WORKDIR /app

COPY build/libs/*.jar paytogether.jar

ARG PORT=8080
ENV PORT ${PORT}
EXPOSE ${PORT}

ENTRYPOINT exec java -jar /app/paytogether.jar --server.port=${PORT} -Duser.timezone=Asia/Seoul