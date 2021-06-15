FROM openjdk:17

WORKDIR ${application.workdir}
COPY ${application.dependencies} ${application.dependencies}
COPY ${project.build.finalName}.jar ${project.build.finalName}.jar

ENTRYPOINT ["/usr/bin/java", "-jar", "${project.build.finalName}.jar"]

#Run the executable
#CMD ["java", "Hello"]