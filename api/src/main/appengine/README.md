# How to deploy to production with app engine

- Go to application.yaml and update spring.profiles.active to gcp-appengine.
- Update app.yaml and ensure SPRING_PROFILES_ACTIVE includes `gcp-appengine`.
- In a terminal with local admin privileges, run: `mvn package appengine:deploy -P gcp-appengine -DskipTests -Dgcp-appengine`

# How to deploy to production with compute engine
- Go to application.yaml and update spring.profiles.active to gcp-compengine.
- Update app.yaml and ensure SPRING_PROFILES_ACTIVE includes `gcp-compengine`.
- In a terminal with local admin privileges, run: `mvn clean package -Pgcp-compengine -DskipTests`

