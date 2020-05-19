FROM openjdk:14-jdk-alpine as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x mvnw && ./mvnw install --ntp
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM openjdk:14-jdk-alpine
RUN addgroup -S clamapi && adduser -S clamapi -G clamapi
RUN mkdir -p /app/clamapi
RUN chown clamapi:clamapi /app/clamapi
USER clamapi
WORKDIR /app/clamapi

VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/clamapi/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/clamapi/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app/clamapi

ENTRYPOINT ["java","-cp",".:lib/*","fr.cnieg.clamav.clamapi.ClamapiApplication"]
