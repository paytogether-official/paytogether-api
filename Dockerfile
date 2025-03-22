FROM amazoncorretto:21-alpine

COPY build/libs/*.jar paytogether.jar

ARG PORT=8080
ENV PORT ${PORT}
EXPOSE ${PORT}

ENTRYPOINT java -jar paytogether.jar --server.port=${PORT} -Duser.timezone=Asia/Seoul