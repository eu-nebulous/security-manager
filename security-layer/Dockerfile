# Build stage
FROM docker.io/library/maven:3.9.2-eclipse-temurin-17 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests

# Package stage
FROM registry.access.redhat.com/ubi8/openjdk-17:1.18

# Set environment variables
ENV LANGUAGE='en_US:en'
ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"

# Copy built artifact from the build stage
COPY --from=build /home/app/target/quarkus-app/lib/ /deployments/lib/
COPY --from=build /home/app/target/quarkus-app/*.jar /deployments/
COPY --from=build /home/app/target/quarkus-app/app/ /deployments/app/
COPY --from=build /home/app/target/quarkus-app/quarkus/ /deployments/quarkus/

# Expose port and define user
EXPOSE 8080
USER 185

# Set the entrypoint
ENTRYPOINT [ "/opt/jboss/container/java/run/run-java.sh" ]
