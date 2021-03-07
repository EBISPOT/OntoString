# Onto-tools Curation App

## Requirements to build and run locally

* Local MongoDB instance running on port `27017`
* Some test credentials and data added to the DB (if authentication is turned on) - see https://github.com/EBISPOT/ontotools-curator/wiki/Test-data
* Alternatively:
  * Add at least one `super` user in the DB in the `users` collection:
  ```
  {
    "name": "Super user",
    "email": "ontotools-curator@ebi.ac.uk",
    "superUser": true
  }
  ```
  * Set `ontotools-curation.auth.enabled` to `false` in the `dev` profile in `application.yml`

### Build package

* Run `mvn clean install -Dspring.profiles.active=dev`
* Alternatively, introduce in a new profile in `application.yml` and use it as active when building
* At least one active profile has to be specified when building the package

### Build docker container
* Run the `build.sh` script under `scripts`

### Run locally
* `java -jar -Dspring.profiles.active=dev target/ontotools-curation-service-*.jar`