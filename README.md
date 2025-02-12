[中文](README_zh.md) | [English](README.md)
# Short URL Service Demo

This project is a proof-of-concept demo for a short URL generation system. It includes a RESTful service for generating short URLs and unit tests to verify the implementation.

## Features

- REST API for generating short URLs
- In-memory storage using `HashMap`
- Unit tests to ensure correctness
- Configuration for domain prefix in `application.properties`
- Future plan to integrate H2SQL for data storage

## Project Structure

- `ShortUrlController.java`: REST controller for handling short URL requests
- `ShortUrlService.java`: Service layer for generating and managing short URLs
- `ShortUrl.java`: Data structure for storing short URL information
- `ShortUrlVO.java`: Data structure for request payload
- `DemoApplicationTest.java`: Unit tests for the short URL service
- `build.gradle.kts`: Gradle build configuration

## Configuration

Configure the domain prefix in `application.properties`:

```properties
shorturl.domain.prefix=http://yourdomain.com
```

## Usage

1. Clone the repository.
2. Configure the domain prefix in `application.properties`.
3. Run the application.
4. Use the REST API to generate and retrieve short URLs.

## REST API Endpoints

- `POST /sol/url`: Create a short URL
- `GET /sol/{shortUrl}`: Retrieve the original URL
- `GET /sol/redirect/{shortUrl}`: Redirect to the original URL

## Unit Tests

Unit tests are provided to verify the implementation. The tests use `originUrl.json` and `result.csv` files, which can be generated using the `DemoApplicationTest` class.

## Future Plans

- Integrate H2SQL for data storage

## Task List

- [x] Set up short URL generation service
- [x] Implement REST API
- [x] Add unit tests
- [x] Configure domain prefix in `application.properties`
- [ ] Integrate H2SQL for data storage

## Running the Application

To run the application, use the following command:

```bash
./gradlew bootRun
```

## Running Tests

To run the unit tests, use the following command:

```bash
./gradlew test
```

## License

This project is licensed under the MIT License.

