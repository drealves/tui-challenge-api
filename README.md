# tui-challenge-api
TUI | Backend Developer :: Next step - Challenge

GitHub Repository Information API

The GitHub Repository Information API is a Spring Boot-based application that provides users with detailed information about GitHub repositories. It fetches repository data, including branches and their latest commits, for a specified GitHub user, ensuring that forked repositories are filtered out for clarity.

## Features

- Fetch repository information for a specified GitHub user.
- Provide details on each repository including branch information and the last commit SHA.
- Exclude forked repositories from the fetched data.
- Reactive API built with Spring WebFlux.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

What things you need to install the software and how to install them:

- JDK 17
- Maven
- Git

### Installing

A step-by-step series of examples that tell you how to get a development environment running:

Clone the repository:

```bash
git clone https://github.com/drealves/tui-challenge-api.git
cd tui-challenge-api.git
```
Install dependencies:
```bash
mvn install
```
Run the application:
```bash
mvn spring-boot:run
```
The service should now be running on http://localhost:8080

Example

Fetching repository information for user drealves:
```bash
http://localhost:8080/api/v1/github/repositories/drealves?page=1&size=5
```
Running the Tests
```bash
mvn test
```

Built With

    Spring Boot - The framework used
    Maven - Dependency Management
    Reactor - For building reactive applications
    WebFlux - async no-bloking system


Authors

    André Freitas - drealves

Acknowledgments

    2 hours analyze
    6 hours to test
    7 hours coding
    1 hour to comment
    Dev total: 16 Hours


API doc 

    http://localhost:8080/swagger-ui/index.html

Jenkis File in Repository

Docker File in Repository

Fargate file and api-gateway file are not designed for evaluation

LoadBalancer URL: 

    load-repo-api-225205433.eu-north-1.elb.amazonaws.com

Swegger:

    http://load-repo-api-225205433.eu-north-1.elb.amazonaws.com:8080/swagger-ui/index.html

Example of request:
    
    GET http://load-repo-api-225205433.eu-north-1.elb.amazonaws.com:8080/api/v1/github/users/drealves/repositories?page=1&size=5

Details:
I didn't create the environment in ECS using cloudFormation, but I configured two EC2 instances, one with Jenkis and the other as a docker container. If the services are not up when they evaluate it is possible that they are down, but in the interview I will demonstrate the devop Build that I created. See you soon Any doubts, please let us know

    
