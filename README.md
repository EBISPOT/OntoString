# OntoString

OntoString is a tool for curating mappings from free text to ontology terms.

## Build and run locally

1. You need a local MongoDB instance running on port `27017`
2. Add at least one `super` user in the DB in the `users` collection:
  ```
  db.users.insert({
    "name": "Super user",
    "email": "ontostring@ebi.ac.uk",
    "superUser": true,
    "roles": []
  })
  ```

3. Set `ontostring.auth.enabled` to `false` in the `dev` profile in `application.yml`  
4. Run `mvn clean package -Dspring.profiles.active=dev`
5. Then you can start the server: `java -jar -Dspring.profiles.active=dev target/ontostring-*.war`

OntoString should now be live at `http://localhost:8080/spot/ontostring`


## Docker

To build a Docker image run the `build.sh` script under `scripts`

