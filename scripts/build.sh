#!/bin/bash

cd ..
docker build --force-rm=true -t ontostring:latest .
docker tag ontostring:latest ebispot/ontostring:latest-sandbox
docker push ebispot/ontostring:latest-sandbox
