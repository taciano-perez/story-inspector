# How to deploy to production with compute engine
- Connect to the production server and build Story Inspector (root).
- In the api module, open application.yaml and update spring.profiles.active to gcp-compengine.
- In a terminal with local admin privileges, run: `mvn clean package -Pgcp-compengine -DskipTests`
