# URL Shortener

This is a simple URL shortener application built with Spring Boot.

## Features

*   Shorten long URLs.
*   Retrieve original long URLs from short URLs.
*   Uses PostgreSQL for storing URL mappings.

## Setup

1.  **Prerequisites:**
    *   Java 21
    *   PostgreSQL

2.  **Database Setup:**
    *   Create a PostgreSQL database. You can use the following command:
        ```sh
        createdb -U your_username your_database_name
        ```
    *   Update the `src/main/resources/application.properties` file with your database credentials:
        ```properties
        spring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name
        spring.datasource.username=your_username
        spring.datasource.password=your_password
        spring.jpa.hibernate.ddl-auto=update
        ```

## Running the Server

You can run the application using the Gradle wrapper:

```sh
./gradlew bootRun
```

The server will start on port 8080.

## API Usage

### Shorten a URL

Send a POST request to `/shorten` with the long URL in the request body.

**Sample Request:**

```sh
curl -X POST -H "Content-Type: text/plain" -d "https://www.google.com" http://localhost:8080/shorten
```

**Sample Response:**

```
http://sh.rt/aB1cD2
```

### Resolve a Short URL

Make a GET request to the short URL to retrieve the original long URL.

**Sample Request:**

```sh
curl http://localhost:8080/aB1cD2
```

**Sample Response:**

```
https://www.google.com
```
