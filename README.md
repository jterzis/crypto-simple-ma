# Crypto Simple Moving Average Service
Crypto Quote Microservice

## Project Dependencies

- Java SE Development Kit 8 (Oracle JDK or Azul OpenJDK)
- Maven
- Clojure 1.8
- Leiningen 2.6
- Docker 17

## Requirements to run on command line

- Java 8
- Maven

## Running service through REPL

Download all library dependencies from Maven and/or Clojars, and start REPL:

    $ lein deps
    $ lein repl

Run service:

    user=> (go)

(OPTIONAL for local builds) Make sure you set the required environment variables. 

    # DynamoDB Local
    export CRYPTO_DYNAMODB_URL="http://localhost:9000"
    export CRYPTO_DYNAMODB_ENV="LOCAL"

    # Crypto Simple MA microservice
    export CRYPTO_HOST="0.0.0.0"
    export CRYPTO_PORT="9091"

    # Logging 
    export CRYPTO_LOG_LEVEL="INFO"

## Running tests

To run integration and unit test:

    $ lein test

## (OPTIONAL) Compiling executable

Download all library dependencies from Maven and/or Clojars:

    $ lein deps

Create java executable packaged with all dependencies:

    $ lein uberjar

This will create `server.jar` in `target` folder.

## To run locally with a cryptocurrency pair:

Save the web-socket-client to your local maven repo

    $ mvn install:install-file -Dfile=./jar/websocket-client-1.1.jar -DgroupId=org.johnterzis.websocket -DartifactId=websocket-client -Dversion=1.1 -Dpackaging=jar -DgeneratePom=true

Unpack DynamoDBLocal_lib.tar.gz and in directory where DynamoDBLocal.jar is located run the following command (note port)
    
    $ tar -zxvf ./DynamoDBLocal_lib.tar.gz

    $ java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -port 9000 -sharedDb

Start the service with your desired crypto ccy pair entered in underscore delimited form as below

    $ java -jar target/server.jar "USDT_BTC"


## Listing DynamoDB tables

First compile executable (read section above). Make sure you have set environment vars `CRYPTO_DYNAMODB_URL`
(for example `http://localhost:9000`) and `CRYPTO_DYNAMODB_ENV` (`LOCAL`, `STAGE` or `PROD`). To create tables:

To list all tables:

    $ java -jar target/server.jar dynamo-list

## Health Checks and API testing / validation

Navigate to `http://localhost:5050/index.html` after starting server.jar.

## Building and running Docker image

Copy `Dockerfile.template` to `Dockerfile` and edit ENV variables for running locally, on stage or production.
To build docker image:

    $ docker build --tag crypto-quotron-local:1.0 .

To run docker image:

    $ docker run --publish 9091:9091 crypto-quotron-local:1.0

IMPORTANT: Do not push Dockerfiles that contain endpoints, usernames and passwords to the Github repository.



