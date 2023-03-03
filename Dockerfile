# Import base image
FROM openjdk:15-buster

RUN apt-get update
RUN apt-get install -y --no-install-recommends less maven

# Create log file directory and set permission
RUN groupadd -r ontostring && useradd -r --create-home -g ontostring ontostring
RUN if [ ! -d /var/log/ontotools/ ];then mkdir /var/log/ontotools/;fi
RUN chown -R ontostring:ontostring /var/log/ontotools

RUN mvn clean package spring-boot:repackage -DskipTests=true

# Move project artifact
COPY target/ontostring-*.war /home/ontostring/
USER ontostring

# Launch application server
ENTRYPOINT exec $JAVA_HOME/bin/java $XMX $XMS -jar -Dspring.profiles.active=$ENVIRONMENT -Dhttp.proxyHost=hh-wwwcache.ebi.ac.uk -Dhttp.proxyPort=3128 -Dhttps.proxyHost=hh-wwwcache.ebi.ac.uk -Dhttps.proxyPort=3128 -Dhttp.proxySet=true -Dhttps.proxySet=true /home/ontostring/ontostring-*.war 

