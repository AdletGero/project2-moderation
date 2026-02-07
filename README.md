# project2-moderation
# Project2_kaspi

## Требования

- Docker + Docker Compose
- (Опционально) Node.js 18+ для генератора нагрузки Kafka

## Быстрый старт (через Docker Compose)

1. Перейдите в каталог с инфраструктурой:
   ```bash
   cd infra
   ```
2. Запустите все сервисы:
   ```bash
   docker compose up --build
   ```

После запуска будут доступны сервисы:

- service-1: http://localhost:8081
- service-2: http://localhost:8082
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000 (логин/пароль: admin/admin)
- Kafka (external): localhost:19092
- Redis: localhost:6379
- Postgres: localhost:5435 (db/user/password: moderation)

## Остановка

```bash
docker compose down
```

## Генератор нагрузки Kafka (опционально)

Генератор отправляет сообщения в `topic-1` на Kafka (порт 19092).

```bash
cd kafka-load-js
npm install
node load.js
