# How to deploy to production

- Go to application.yaml and update spring.profiles.active to gcp.
- Update app.yaml setting SPRING_PROFILES_ACTIVE to include gcp.
- In a terminal with local admin privileges, run: `mvn package appengine:deploy -Dapp.deploy.projectId=storyinspector -P gcp -DskipTests -Dgcp`
- In a terminal with local admin privileges, run: `mvn package appengine:deploy -P gcp -DskipTests -Dgcp`