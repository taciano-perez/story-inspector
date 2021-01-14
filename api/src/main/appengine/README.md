# How to deploy to production

- Go to application.yaml and update spring.profiles.active to production.
- Update app.yaml setting SPRING_PROFILES_ACTIVE to include production.
- Run: `mvn package appengine:deploy -Dapp.deploy.projectId=storyinspector -P production -DskipTests -Dgcp`