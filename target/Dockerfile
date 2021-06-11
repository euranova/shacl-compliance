FROM openjdk:17

WORKDIR application
COPY lib lib
COPY shacl-compl-1.0-SNAPSHOT.jar shacl-compl-1.0-SNAPSHOT.jar

ENTRYPOINT ["/usr/bin/java", "-jar", "shacl-compl-1.0-SNAPSHOT.jar"]

#Run the executable
#CMD ["java", "Hello"]