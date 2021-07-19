# How to deploy to production with GCP Compute Engine
- Connect to the production server and build Story Inspector (root).
- In the api module, open application.yaml and update spring.profiles.active to gcp-compengine.
- If necessary, copy si-keystore.p12 into `api/main/resources/`.
- In a terminal with local admin privileges, run: `mvn clean package -Pgcp-compengine -DskipTests`

# How to deploy to production with GCP App Engine
- Go to application.yaml and update spring.profiles.active to gcp-appengine.
- Update app.yaml and ensure SPRING_PROFILES_ACTIVE includes `gcp-appengine`.
- In a terminal with local admin privileges, run: `mvn package appengine:deploy -P gcp-appengine -DskipTests -Dgcp-appengine`

# How to deploy to production with GCP Cloud Run
- Ensure the application has been properly created on GCP Cloud Run
- Build the container from the project's root directory `docker build -t gcr.io/story-inspector/gcp-cloudrun-api:latest .`
- Push the container to GCR registry `push gcr.io/story-inspector/gcp-cloudrun-api:latest`
- Start the service via GCP web console 