SpringBoot MongoDB RestAPI Docker RabbitMQ Gradle

This is a model project with sample implementation of RESTApis with MongoDB as NoSQl DB, dockerized application and RabbitMQ.

Contains an endpoint to dynamically build a Mongo query based on a RQL (Resource Query Language) query string. For 
reference and syntax rules, visit https://connect.cloudblue.com/community/api/rql/

Ex.
(item=Chicken and Waffles|item=burger)&qty>5 == and(or(eq(item, Chicken and Waffles),eq(burger))),gt(qty,5))
 == SELECT * FROM ordertable 
    WHERE (item = 'Chicken and Waffles' OR item = 'burger')
    AND qty > 5

Requirements
For building and running the application you need:

JDK 17
Gradle 7.3.2
Docker

RabbitMQ - Docker Image is sufficient
MongoDB  - Docker Image is sufficient


Building
Gradle is the main tool for build & dependency management. You will be able to run gradle commands via the gradle
wrapper in the root of this project, e.g. ./gradlew tasks

./gradlew clean - Deletes the build directory.
./gradlew build - Assembles and tests this project.
docker-compose build - Builds image of the application depending on the mongodb service and rabbitmq service.

Start application

./gradlew bootRun - Starts the application on your local environment as a normal SpringBoot app
When running the application locally, be sure to update application.properties to point to your local MongoDB server
by changing spring.data.mongodb.host=mongo to spring.data.mongodb.host=localhost


1.Chocolatey installation command for windows : Open Power Shell-->Run as Administrator.

Command :
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

After Install, Close power shell and  open Power Shell  -->Run as Administrator. 

Command:choco install rabbitmq. 
Then Go To http://localhost:15672. Use Credentials username:guest and password:guest.Check your queue started or not.
Then try to run application.


docker-compose up - Starts a container with 4 images. app image,mongo server, mongo express(UI for mongodb) and rabbitmq service.

When adding a new module, be sure to disable the Spring Boot plugin in the module's build.gradle file 
to prevent bootJar from running, which requires a main class
    --id 'org.springframework.boot' version '2.6.3' apply false

Also, add this block to retain Spring Boot's dependency management

dependencyManagement {
    imports {
        mavenBom org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES
    }
}

View it locally on Swagger here:
http://localhost:8080/swagger-ui/index.html
http://localhost:8080/v3/api-docs/
