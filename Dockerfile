# syntax=docker/dockerfile:1

FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY queless-webapp/pom.xml ./queless-webapp/pom.xml
RUN mvn -f ./queless-webapp/pom.xml dependency:go-offline

COPY queless-webapp/src ./queless-webapp/src
RUN mvn -f ./queless-webapp/pom.xml clean package -DskipTests

FROM tomcat:10.1-jdk21-temurin
WORKDIR /usr/local/tomcat

COPY --from=build /workspace/queless-webapp/target/queless.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
