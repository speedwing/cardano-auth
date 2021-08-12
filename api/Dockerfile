FROM openjdk:11-jdk-slim

COPY api/target/universal/stage /opt/balcony-api

ENTRYPOINT ["bash", "-c"]

CMD ["/opt/balcony-api/bin/api"]
