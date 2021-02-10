# Import base image
FROM openjdk:15-buster

RUN apt-get update
RUN apt-get install -y --no-install-recommends less

# Create log file directory and set permission
RUN groupadd -r ontotools-curation-service && useradd -r --create-home -g ontotools-curation-service ontotools-curation-service
RUN if [ ! -d /var/log/ontotools/ ];then mkdir /var/log/ontotools/;fi
RUN chown -R ontotools-curation-service:ontotools-curation-service /var/log/ontotools

# Move project artifact
ADD target/ontotools-curation-service-*.jar /home/ontotools-curation-service/
USER ontotools-curation-service

# Launch application server
ENTRYPOINT exec $JAVA_HOME/bin/java $XMX $XMS -jar -Dspring.profiles.active=$ENVIRONMENT /home/ontotools-curation-service/ontotools-curation-service-*.jar
