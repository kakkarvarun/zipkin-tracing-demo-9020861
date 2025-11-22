# Zipkin + Micrometer Tracing Demo (Currency ↔ Exchange)

## What this shows
- Two Spring Boot 3 services (`currency-service` WebFlux and `exchange-service` MVC) registered in Eureka.
- Distributed tracing with **Micrometer Tracing (Brave)** exporting to **Zipkin**.
- Propagation across service boundary and correlation via `traceId` in logs.

## How to run (local)
1. Start Zipkin:
   ```bash
   docker run -d -p 9411:9411 openzipkin/zipkin
2. Start Eureka:
     mvn -q -DskipTests -f microservices-demo/eureka-service/pom.xml spring-boot:run
3. Start Exchange:
    mvn -q -DskipTests -f microservices-demo/exchange-service/pom.xml spring-boot:run
4. Start Currency:
    mvn -q -DskipTests -f microservices-demo/currency-service/pom.xml spring-boot:run

Endpoints

Exchange: GET http://localhost:8000/exchange/rate/from/{from}/to/{to}

Currency: GET http://localhost:8100/currency/convert/from/{from}/to/{to}/amount/{amount}

Generate sample traffic

   1..12 | % {
  Invoke-RestMethod "http://localhost:8100/currency/convert/from/USD/to/INR/amount/10"
  Invoke-RestMethod "http://localhost:8100/currency/convert/from/CAD/to/USD/amount/5"
  Start-Sleep -Milliseconds 300
}


Observe traces

Zipkin UI: http://localhost:9411/zipkin/

Filter by serviceName=currency-service, open a trace to see spans across both services.

Dependencies tab shows currency-service → exchange-service.

Logging correlation

In application.yml:

logging:
  pattern:
    level: "%5p [${spring.application.name:},traceId=%X{traceId:-},spanId=%X{spanId:-}]"

Notes & issues I hit

Port already in use (8100/9411): freed using netstat + taskkill or removed old Docker Zipkin container.

404 on convert route: fixed to /currency/convert/from/{from}/to/{to}/amount/{amount} based on controller mappings.

500 error earlier: Eureka wasn’t up; after starting Eureka first, currency registered and calls succeeded.

Tech

Spring Boot 3.x, Micrometer Tracing (Brave), Zipkin exporter

Spring Cloud Netflix Eureka (service discovery)
