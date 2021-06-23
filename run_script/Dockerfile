FROM openjdk:17

WORKDIR @application.workdir@
COPY @application.dependencies@ @application.dependencies@
COPY @project.build.finalName@.jar.original @project.build.finalName@.jar.original

ENTRYPOINT ["/usr/bin/java", "-cp", "@project.build.finalName@.jar.original", "SAVETests"]

#Run the executable
#CMD ["java", "Hello"]