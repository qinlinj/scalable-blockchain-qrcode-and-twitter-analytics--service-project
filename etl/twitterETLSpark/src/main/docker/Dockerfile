# Ubuntu Linux as the base image
FROM maven:3.8.4-jdk-8-slim

EXPOSE 8080

ADD . /twitterETLSpark

RUN cd twitterETLSpark && mvn clean package

CMD ["spark-submit", "--class", "edu.cmu.ETLScala", "target/project_spark.jar", "wasb://datasets@clouddeveloper.blob.core.windows.net/twitter-dataset/part-r-00000.gz"]

# Use Bash as the container's entry point.
ENTRYPOINT ["/bin/bash", "-c"]