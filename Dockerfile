FROM alpine/git:1.0.7 as clone
WORKDIR /app
RUN git clone https://github.com/FultonBrowne/Pegasus.git


FROM openjdk:12-jdk-alpine as build
WORKDIR /app
COPY --from=clone /app/Pegasus /app
RUN ./gradlew build shadowJar

FROM openjdk:11.0-jre
WORKDIR /app
COPY --from=build /app/build/libs /app
EXPOSE 8000
ENTRYPOINT ["sh", "-c"]
CMD ["java -jar Pegasus-1.0-SNAPSHOT-all.jar"]