FROM eclipse-temurin:21

COPY ./project /project

RUN cd /project && \
    ./gradlew clean build