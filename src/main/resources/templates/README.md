# {{componentPrefix}}

This is the {{componentPrefix}} Project.

## Building
`./gradlew clean build`

That will compile, test and package the project.

## Running
`./gradlew bootRun`

That will execute the project.

To check out the server from the browser:

http://localhost:8081/demo

{{#hasFlyway}}
## Flyway

In order to use flyway you need to setup the database and then
add the properties to the application.properties file

For local our example will be using H2 with Compatibility mode
```
spring.datasource.url=jdbc:h2:../../LOCAL-DB/demo;MODE=MYSQL
```

If the project is going to use a different database then the `databases/db.gradle`
file to assign the `spring.datasource.url` to the correct jdbc url.

To setup the test and local databases use:
```
SPRING_DATASOURCE_USERNAME={{artifactId_underscored}}_user ./gradlew testClean testMigrate flywayClean flywayMigrate
```

### Flyway and Cloud Foundry
Once the application pushed to cloud foundry gradle
can get the database user, password and URL from the `cf env` for the application

The assumption is that the database service is named `{{artifactId}}-database`

```
cf create-service <database service name> <database service plan> {{artifactId}}-database
```

if that is not the service name then the `databases/db.gradle` file
and update:
```
def sqlCredentials = vcapServices?.getAt("{{artifactId}}-database")?.getAt(0)?.getAt("credentials")
```

Setting the CF_MIGRATE variable to `true` indicates that flyway should migrate
the database in the cloud foundary environment.
```
CF_MIGRATE=true ./gradlew flywayMigrate
```

{{/hasFlyway}}
