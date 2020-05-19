![Release](https://github.com/audig/clamapi/workflows/Release/badge.svg)
# CLAMAPI

Api to submit a file to clamav and get the information is the file is infected.

This work is inspired by https://github.com/solita/clamav-rest, but we use the spring-boot framework and log the idClient for security purpose.

It is a spring boot application with all requirements to be deployed in kubernetes :

- Liveness/Readiness Probe
- Metrics endpoint
- Gracefull shutdown


## How does it works

This spring boot application offer an api with the `/Scan` endpoint.

This endpoint must be used with 2 parameters :

- file: A multipart file
- idClient (Optionnal): A string

You can use this curl command line to test service :

```bash
curl -X POST "http://localhost:8080/Scan?idClient=12345" -H "accept: */*" -H "Content-Type: multipart/form-data" -F "file=@yourfile;type=application/json"
```


For more information you can visit the swagger-ui at `/swagger-ui.html` after running the service

## Usage
Usage in docker

```bash
docker run --env SERVICES_CLAMAV_HOST=<yourClamAvHost> --env SERVICES_CLAMAV_PORT=<yourClamAvPort> -p 8080:8080 <image_tag>
```

You can pass additional variable env to increse the scan timeout :
-  SERVICES_CLAMAV_TIMEOUT : time in ms

## Metrics
All metrics are available under `/actuator/metrics` and can be scrape by prometheus

Some custom metrics are available at :
- `/actuator/metrics/clamav_scan` : number of scan
- `/actuator/metrics/clamav_infected` : number of infected file 
- `/actuator/metrics/scan_duration` : in second total time to scan file

## Deployment with helm in kubernetes

TODO
