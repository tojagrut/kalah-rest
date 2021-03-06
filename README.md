# kalah-rest

This is a spring boot based application to expose Rest APIs for Kalah.
It provides interfaces to initialize, retrieve and play the kalah board.

Detailed API documentation can be found using below url once application is started: 
http://localhost:8081/swagger-ui.html

It is also hosted on heroku. https://shrouded-lake-38768.herokuapp.com/swagger-ui.html

![API Documentation](docs/swagger-ui-operations.png)

This project is built using JDK 1.8 and uses:
- spring-boot
- spring-hateoas (for HAL link generation)
- projectlombok
- springfox-swagger2 (for api documentation)
- springfox-swagger-ui (for api documentation ui)

Run the jar file from target folder after build or use the one in dist folder.
You can access the endpoints using http://localhost:8081/kalah
Heroku endpoint https://shrouded-lake-38768.herokuapp.com/kalah

Cheers!

