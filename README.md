# Application Features
The main functionality of this API is to take a given url of public Github repository, navigate to this url, 
and returns in JSON format the file count, the total number of lines and the total number of
bytes grouped by file extension.

When you do a get request from the browser you will have to follow the localhost with a ?url=someActualURL The url is 
the parameter you are passing to the Web-Service for processing.
Hence an example of a full request to the API-endpoint will be :

> http://localhost:8080/?url=https://github.com/pamelafracalossi/WebScraper

You can also access the application through [Heroku](https://blueberry-cupcake-41686.herokuapp.com/?url=https://github.com/pamelafracalossi/WebScraper)

###Getting the Project

*Required*

- Maven 3.3+
- JDK 8+

###Running the Project

To run the project, first navigate into the source directory cd apigithub and execute the following command:

- mvn spring-boot:run:
that's all you need to get it started.

The application starts the server instance on port 8080.

http://localhost:8080

Open the link in your browser and start using it.
