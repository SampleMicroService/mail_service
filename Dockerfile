FROM openjdk:17

EXPOSE 8083

ADD target/mail_service-0.0.1-SNAPSHOT.jar /mail_service-0.0.1-SNAPSHOT.jar

ENTRYPOINT [ "java","-jar","/mail_service-0.0.1-SNAPSHOT.jar" ]
