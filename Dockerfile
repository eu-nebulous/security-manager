# Downloader stage for run-java.sh script
FROM docker.io/curlimages/curl:8.5.0 AS downloader
ARG RUN_JAVA_VERSION=1.3.5
RUN curl https://repo1.maven.org/maven2/io/fabric8/run-java-sh/${RUN_JAVA_VERSION}/run-java-sh-${RUN_JAVA_VERSION}-sh.sh -o /tmp/run-java.sh

# Build stage
FROM docker.io/library/maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /home/app
COPY src ./src
COPY pom.xml ./
RUN mvn clean package -DskipTests -Dquarkus.container-image.build=false

# Package stage
FROM docker.io/eclipse-temurin:20-jre-alpine

# Set up environment variables
ENV LANGUAGE='en_US:en'
ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"
ENV USER_ID=185

# Create deployments directory and set permissions
RUN mkdir /deployments \
    && chown ${USER_ID} /deployments \
    && chmod "g+rwX" /deployments \
    && chown 1001:root /deployments

# Copy run-java.sh script from downloader stage
COPY --from=downloader /tmp/run-java.sh /deployments/run-java.sh
RUN chown ${USER_ID} /deployments/run-java.sh && chmod 540 /deployments/run-java.sh

# Copy built artifact from the build stage
COPY --from=build /home/app/target/quarkus-app/lib/ /deployments/lib/
COPY --from=build /home/app/target/quarkus-app/*.jar /deployments/
COPY --from=build /home/app/target/quarkus-app/app/ /deployments/app/
COPY --from=build /home/app/target/quarkus-app/quarkus/ /deployments/quarkus/

# Expose necessary ports
EXPOSE 8080

# Set user
USER ${USER_ID}

# Set entrypoint
ENTRYPOINT [ "/deployments/run-java.sh" ]
