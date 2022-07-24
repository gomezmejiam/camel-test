# camel-test
POC basis usage of apache camel
using mongo, rest api exposure, rest api client, rabbit mq producers.

docker pull rabbitmq:3-management
docker run --rm -it -p 15672:15672 -p 5672:5672 rabbitmq:3-management

docker pull mongo:4.0.4
docker run -d -p 27017:27017 --name test-mongo mongo:latest



