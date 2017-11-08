# Crypto Simple Moving Average Service
Crypto Quote Microservice

## Dependencies

- Java SE Development Kit 8 (Oracle JDK or Azul OpenJDK)
- Clojure 1.8
- Leiningen 2.6
- Docker 17

## Running service through REPL

Download all library dependencies from Maven and/or Clojars, and start REPL:

    $ lein deps
    $ lein repl

Run service:

    user=> (go)

Make sure you set the required environment variables (check Dockerfile.template for the list).

    # DynamoDB Local
    export CRYPTO_DYNAMODB_URL="http://localhost:9000"
    export ZIBBY_DYNAMODB_ENV="LOCAL"

    # Fraud Detective microservice
    export CRYPTO_HOST="0.0.0.0"
    export CRYPTO_PORT="9091"

    # Logging 
    export CRYPTO_LOG_LEVEL="INFO"


## Running tests

To run integration and unit test:

    $ lein test

## Compiling executable

Download all library dependencies from Maven and/or Clojars:

    $ lein deps

Create java executable packaged with all dependencies:

    $ lein uberjar

This will create `server.jar` in `target` folder.

To run locally with a cryptocurrency pair:

    $ java -jar target/server.jar "USDT_BTC"

Make sure you set the required environment variables (check Dockerfile.template for the list).

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



