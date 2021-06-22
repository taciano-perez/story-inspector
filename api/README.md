# How to deploy to production with compute engine
- Connect to the production server and build Story Inspector (root).
- In the api module, open application.yaml and update spring.profiles.active to gcp-compengine.
- If necessary, copy si-keystore.p12 into `api/main/resources/`.
- In a terminal with local admin privileges, run: `mvn clean package -Pgcp-compengine -DskipTests`

# How to deploy to production with app engine
- Go to application.yaml and update spring.profiles.active to gcp-appengine.
- Update app.yaml and ensure SPRING_PROFILES_ACTIVE includes `gcp-appengine`.
- In a terminal with local admin privileges, run: `mvn package appengine:deploy -P gcp-appengine -DskipTests -Dgcp-appengine`