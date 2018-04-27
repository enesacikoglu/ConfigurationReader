FROM openjdk:8-jre-alpine
MAINTAINER enes.acikoglu@gmail.com
ADD target/DynamicConfigurationService-*.jar dynservice.jar
EXPOSE 9091
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/dynservice.jar"]

