# springboot_sample

## JVM image build
ex: rest-backend
```
# on dev container
./mvnw clean
./mvnw package

# on your host pc
docker build -f src/main/docker/Dockerfile.jvm -t springboot/rest-backend-jvm .
```

## Running locally via Docker Compose  
After build each images, run below command at project root directory.
| Description | Image Tag       | Docker Compose Run Command                                               | Docker Compose Run Command with Monitoring                                                                       |
|-------------|-----------------|--------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------|
| JVM Java 17 | `java17-latest` | `docker-compose -f deploy/docker-compose/java17.yml up --remove-orphans` | `docker-compose -f deploy/docker-compose/java17.yml -f deploy/docker-compose/monitoring.yml up --remove-orphans` |

## Monitoring
- [Prometheus](https://prometheus.io/)
    - Polls metrics from all the services within the system.
- [Jaeger](https://www.jaegertracing.io)
    - Jaeger is an open-source distributed tracing system.
- [GrayLog](https://graylog.org/)
    - Log management platform for collecting, indexing, and analyzing both structured and unstructured data from almost any source.

| Service | URL |
| :----: | :------------: |
| Prometheus | http://localhost:9090 |
| Jaeger | http://localhost:16686 |
| GrayLog | http://localhost:9000 |
