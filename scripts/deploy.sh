#!/bin/bash

kubectl delete deploy ontostring -n ontotools
kubectl apply -f config/ontostring-deployment.yaml
