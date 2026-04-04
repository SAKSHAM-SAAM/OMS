# OMS Backend — E-Commerce Order Management System

A production-grade microservices backend built with Spring Boot, Kafka, and PostgreSQL.

---

## Architecture

```
Client → API Gateway (8080) → [Kafka Bus] → Microservices → PostgreSQL DBs
```

| Service              | Port | DB                |
|----------------------|------|-------------------|
| API Gateway          | 8080 | —                 |
| Config Server        | 8888 | —                 |
| Order Service        | 8081 | orders_db (5432)  |
| Inventory Service    | 8082 | inventory_db (5433)|
| Fulfillment Service  | 8083 | fulfillment_db (5434)|
| Payment Service      | 8084 | payments_db (5435) |
| Notification Service | 8085 | notifications_db (5436)|
| IoT Gateway          | 8086 | iot_db (5437)     |

---

## Phase 1 — Getting Started

### 1. Start Infrastructure

```bash
cd docker/
docker compose up -d
```

Wait ~30 seconds for all services to be healthy:

```bash
docker compose ps   # all should show "healthy"
```

### 2. Verify Services

| Tool       | URL                          | Credentials              |
|------------|------------------------------|--------------------------|
| Kafka UI   | http://localhost:8090        | none                     |
| pgAdmin    | http://localhost:5050        | admin@oms.dev / admin123 |
| Redis      | localhost:6379               | password: oms_redis_pass |

### 3. Start Config Server

```bash
cd infrastructure/config-server
mvn spring-boot:run
# Runs on http://localhost:8888
```

Verify config is served:
```bash
curl -u config-admin:config-secret-2024 http://localhost:8888/order-service/default
```

### 4. Start API Gateway

```bash
cd infrastructure/api-gateway
mvn spring-boot:run
# Runs on http://localhost:8080
```

Verify gateway health:
```bash
curl http://localhost:8080/actuator/health
```

---

## Port Reference

| Port  | Service            |
|-------|--------------------|
| 8080  | API Gateway        |
| 8888  | Config Server      |
| 8090  | Kafka UI           |
| 5050  | pgAdmin            |
| 9092  | Kafka (external)   |
| 6379  | Redis              |
| 5432  | orders_db          |
| 5433  | inventory_db       |
| 5434  | fulfillment_db     |
| 5435  | payments_db        |
| 5436  | notifications_db   |
| 5437  | iot_db             |

---

## Project Phases

- [x] Phase 1 — Infrastructure (Docker + Config Server + API Gateway)
- [x] Phase 2 — Order Service (State Machine + Kafka Producer)
- [-] Phase 3 — Inventory Service (IN PROGRESS)
- [ ] Phase 4 — Fulfillment Service
- [ ] Phase 5 — Payment Service + Saga
- [ ] Phase 6 — Notification Service
- [ ] Phase 7 — IoT Gateway
- [ ] Phase 8 — Hardening (Tracing, Tests, Swagger)
