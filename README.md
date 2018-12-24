[![Build Status](https://travis-ci.org/systelab/r2d2.svg?branch=master)](https://travis-ci.org/systelab/r2d2)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/7ce4e563c45b4d09a975d61bed7d5d50)](https://www.codacy.com/app/systelab/r2d2?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=systelab/r2d2&amp;utm_campaign=Badge_Grade)
[![Known Vulnerabilities](https://snyk.io/test/github/systelab/r2d2/badge.svg?targetFile=pom.xml)](https://snyk.io/test/github/systelab/r2d2?targetFile=pom.xml)

#  Sample Slack Robot

This is a simple Slack boot based on JBot. 

## Getting Started

To get you started you can simply clone the `r2d2` repository and install the dependencies:

### Prerequisites

You need [git][git] to clone the `r2d2` repository.

You will need [Java™ SE Development Kit 8][jdk-download] and [Maven][maven].

### Clone `r2d2`

Clone the `r2d2` repository using git:

```bash
git clone https://github.com/systelab/r2d2.git
cd r2d2
```

### Install Dependencies

In order to install the dependencies and generate the Uber jar you must run:

```bash
mvn clean install
```

### Run

To launch the server, simply run with java -jar the generated jar file.

```bash
cd target
java -jar r2d2-1.0.jar
```

Or with the maven plugin:

```bash
mvn spring-boot:run
```


Head to http://localhost:8080 in order to see the Eureka Daskboard


## Docker

### Build docker image

There is an Automated Build Task in Docker Cloud in order to build the Docker Image. 
This task, triggers a new build with every git push to your source code repository to create a 'latest' image.
There is another build rule to trigger a new tag and create a 'version-x.y.z' image

You can always manually create the image with the following command:

```bash
docker build -t systelab/r2d2 . 
```

### Run the container

```bash
docker run -p 8761:8761 systelab/r2d2
```

The app will be available at http://localhost:8080


[git]: https://git-scm.com/
[sboot]: https://projects.spring.io/spring-boot/
[maven]: https://maven.apache.org/download.cgi
[jdk-download]: http://www.oracle.com/technetwork/java/javase/downloads
[JEE]: http://www.oracle.com/technetwork/java/javaee/tech/index.html
[eureka]: https://github.com/Netflix/eureka
