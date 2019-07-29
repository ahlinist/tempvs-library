# tempvs-library
[![Circle CI](https://circleci.com/gh/ahlinist/tempvs-library/tree/master.svg?&style=shield)](https://circleci.com/gh/ahlinist/tempvs-library/tree/master)

A library microservice for tempvs (see: https://github.com/ahlinist/tempvs) based on spring boot.

## Discovery
To be discovered by tempvs services this component should have the following env variables set correctly:
 * DOMAIN_NAME (domain name specific for this group of instances, defaults to "localhost")
 * EUREKA_URL (Eureka server url, defaults to "http://user:password@localhost:8084")
 * EUREKA_PASSWORD (Eureka server password, defaults to "password")

## Configuration

The following env variables need to be set:
 * PORT
 * TOKEN (security token that matches the one being set up in the host app)
 * JDBC_DATABASE_URL
 * JDBC_DATABASE_USERNAME
 * JDBC_DATABASE_PASSWORD
 * AMQP_HOST
 * AMQP_USER
 * AMQP_PASSWORD
