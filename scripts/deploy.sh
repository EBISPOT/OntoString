#!/bin/bash

kubectl delete deploy ontotools-curation-service -n ontotools
kubectl apply -f config/ontotools-curation-service-deployment.yaml
