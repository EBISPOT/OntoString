# Import base image
FROM openjdk:15-buster

RUN apt-get update
RUN apt-get install -y --no-install-recommends less

# Create log file directory and set permission
RUN groupadd -r ontostring && useradd -r --create-home -g ontostring ontostring
RUN if [ ! -d /var/log/ontotools/ ];then mkdir /var/log/ontotools/;fi
RUN chown -R ontostring:ontostring /var/log/ontotools

# Move project artifact
ADD target/ontostring-*.war /home/ontostring/
USER ontostring

# Launch application server
ENTRYPOINT exec $JAVA_HOME/bin/java $XMX $XMS -jar -Dspring.profiles.active=$ENVIRONMENT /home/ontostring/ontostring-*.war -Dhttp.proxyHost=http://hh-wwwcache.ebi.ac.uk:3128 -Dhttp.proxyPort=8080

