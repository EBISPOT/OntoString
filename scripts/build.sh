#!/bin/bash

cd ..
docker build --force-rm=true -t ontotools-curation-service:latest .
docker tag ontotools-curation-service:latest ebispot/ontotools-curation-service:latest-sandbox
docker push ebispot/ontotools-curation-service:latest-sandbox
