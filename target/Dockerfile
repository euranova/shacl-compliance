FROM openjdk:17

WORKDIR application
COPY lib lib
COPY shacl-compl-1.0-SNAPSHOT.jar.original shacl-compl-1.0-SNAPSHOT.jar.original

ENTRYPOINT ["/usr/bin/java", "-cp", "shacl-compl-1.0-SNAPSHOT.jar.original", "SAVETests"]

#Run the executable
#CMD ["java", "Hello"]