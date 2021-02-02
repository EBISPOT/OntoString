#!/bin/bash

kubectl delete deploy ontotools-curation-service -n gwas
kubectl apply -f config/ontotools-curation-service-deployment.yaml
