# Система управления взаимоотношениями с клиентами (CRM) - Backend API

Это бэкенд-приложение, разработанное на **Spring Boot 3** с использованием **Java 21** и **PostgreSQL**, предназначенное для управления информацией о продавцах и их транзакциях.

## Функциональность

Приложение предоставляет API для выполнения следующих ключевых операций:

### Управление продавцами (Sellers)
*   **CRUD операции:** Создание, чтение, обновление и удаление информации о продавцах.
*   **Пагинация:** Получение списка всех продавцов с поддержкой пагинации (`page`, `perPage`).
*   **Детализация:** Получение подробной информации о конкретном продавце, включая список его транзакций.

### Управление транзакциями (Transactions)
*   **CRUD операции:** Создание, чтение, обновление и удаление транзакций.
*   **Связь с Seller:** Транзакции всегда привязаны к конкретному продавцу.
*   **Пагинация:** Получение списка всех транзакций и транзакций конкретного продавца с пагинацией.

### Аналитика (Analytics)
*   **Самый продуктивный продавец:** Определение лучшего продавца по объему продаж за заданный период (`DAY`, `MONTH`, `QUARTER`, `YEAR`).
*   **Продавцы ниже лимита:** Получение списка продавцов, чья общая сумма транзакций за указанный период меньше определенной суммы.
*   **Лучший период:** Определение наиболее продуктивного временного окна (окна в $N$ дней) для конкретного продавца по количеству транзакций.

---

## Зависимости и Сборка

### Технологический стек
*   **Backend:** Spring Boot 3.2.x, Java 21, Spring Data JPA, Lombok, Jakarta Validation.
*   **Документация:** SpringDoc OpenAPI 2.6 (Swagger UI).
*   **База данных:** PostgreSQL 15.
*   **Сборка/Оркестрация:** Gradle, Docker, Docker Compose.

### Основные Зависимости
*   `org.springframework.boot:spring-boot-starter-web` - Для создания RESTful веб-приложения.
*   `org.springframework.boot:spring-boot-starter-data-jpa` - Для работы с базой данных (ORM).
*   `org.springframework.boot:spring-boot-starter-validation` - Для валидации данных входящих DTO/сущностей.
*   `org.postgresql:postgresql` - JDBC-драйвер для базы данных PostgreSQL.
*   `org.projectlombok:lombok` - Для сокращения шаблонного кода (генерация геттеров, сеттеров и т.д.).
*   `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0` - Генерация OpenAPI-спецификации и Swagger UI.
*   `org.springframework.boot:spring-boot-devtools` - Для ускорения разработки (автоматический перезапуск).

### Структура БД

Проект использует две основные таблицы:

#### `sellers`
| Колонка | Тип | Nullable | Описание |
| :--- | :--- | :--- | :--- |
| `id` | `bigint` | `NOT NULL` | **PRIMARY KEY**. Уникальный ID продавца. |
| `name` | `varchar(255)` | `NOT NULL` | Имя продавца. |
| `contact_info` | `varchar(255)` | - | Контактная информация. |
| `registration_date` | `timestamp` | `NOT NULL` | Дата и время регистрации. |
| `is_deleted` | `boolean` | `NOT NULL` | Флаг мягкого удаления. `false` по умолчанию. |

#### `transactions`
| Колонка | Тип | Nullable | Описание |
| :--- | :--- | :--- | :--- |
| `id` | `bigint` | `NOT NULL` | **PRIMARY KEY**. Уникальный ID транзакции. |
| `seller_id` | `bigint` | `NOT NULL` | **FOREIGN KEY** на `sellers(id)`. |
| `amount` | `numeric(38,2)` | `NOT NULL` | Сумма транзакции. |
| `payment_type` | `varchar(255)` | `NOT NULL` | Тип оплаты (`CASH`, `CARD`, `TRANSFER`). |
| `transaction_date` | `timestamp` | `NOT NULL` | Дата и время транзакции. |

---

## Инструкции по запуску с помощью Docker Compose

Для запуска используйте предоставленный `docker-compose.yml`. Проект поддерживает два режима работы.

### Режим 1: Локальная разработка (БД в Docker, приложение локально)

Запускает только PostgreSQL и Flyway (применяет миграции):
```bash
docker compose up -d
```
Затем запустите Spring Boot приложение локально:
```bash
./gradlew :service:bootRun
```
Spring Boot автоматически подключится к запущенной БД.

### Режим 2: Полный Docker-деплой (всё в контейнерах)

Запускает БД, Flyway и само приложение (профиль `deploy`):
```bash
docker compose --profile deploy up -d --build
```

### Доступы
| Сервис | Порт | URL (при локальном запуске) |
| :--- | :--- | :--- |
| **Spring API** | `8080` | `http://localhost:8080/api` |
| **Swagger UI** | `8080` | `http://localhost:8080/swagger-ui.html` |
| **OpenAPI JSON** | `8080` | `http://localhost:8080/v3/api-docs` |
| **PostgreSQL DB** | `5432` | `localhost:5432` |
| **DB Name / User / Pass** | - | `crm_db` / `postgres` / `1234` |

---

## Как пользоваться API?

Основная точка входа для всех запросов: `http://localhost:8080/api`

API структурирован по основным ресурсам: **Продавцы** (`/sellers`), **Транзакции** (`/transactions`) и **Аналитика** (`/analytics`).

### 1. Ресурс: Продавцы (`/sellers`)

Управление информацией о продавцах (CRUD-операции, детализация, пагинация).

| Метод | Путь | Описание | Параметры |
| :--- | :--- | :--- | :--- |
| **POST** | `/sellers` | **Создание нового продавца** | **Схема тела запроса:** <br> - `name` (string) Имя продавца <br> - `contactInfo` (string) Контактная информация |
| **GET** | `/sellers` | **Получение списка продавцов**. | **Параметры запроса:** <br> - `page` (int, default: 0) Номер страницы <br> - `perPage` (int, default: 10) Количество элементов
| **GET** | `/sellers/{id}` | **Получение деталей продавца** по ID. | **Параметры пути:** <br> - `{id}` (bigint) ID продавца |
| **PUT** | `/sellers/{id}` | **Обновление информации о продавце** по ID. | **Параметры пути:** <br> - `{id}` (bigint) ID продавца <br><br> **Схема тела запроса:** <br> - `name` (string) Имя продавца <br> - `contactInfo` (string) Контактная информация |
| **DELETE** | `/sellers/{id}` | **Мягкое удаление продавца** по ID. Продавец помечается как удалённый и исчезает из всех запросов, но его транзакции сохраняются в базе данных. | **Параметры пути:** <br> - `{id}` (bigint) ID продавца |

---

### 2. Ресурс: Транзакции (`/transactions`)

Управление транзакциями и привязка их к продавцам.

| Метод | Путь | Описание | Параметры |
| :--- | :--- | :--- | :--- |
| **POST** | `/transactions/seller/{sellerId}` | **Создание новой транзакции** для продавца. | **Параметры пути:** <br> - `{sellerId}` (bigint) ID продавца <br><br> **Схема тела запроса:** <br> - `amount` (bigDecimal) Сумма транзакции <br> - `paymentType` (string) Тип оплаты (`'CASH'`, `'CARD'` или `'TRANSFER'`) |
| **GET** | `/transactions/seller/{sellerId}` | **Получение списка транзакций** продавца. | **Параметры пути:** <br> - `{sellerId}` (bigint) ID продавца <br><br> **Параметры запроса:** <br> - `page` (int, default: 0) Номер страницы <br> - `perPage` (int, default: 10) Количество элементов |
| **GET** | `/transactions` | **Получение списка всех транзакций**. | **Параметры запроса:** <br> - `page` (int, default: 0) Номер страницы <br> - `perPage` (int, default: 10) Количество элементов |
| **PUT** | `/transactions/{id}` | **Обновление информации о транзакции**. | **Параметры пути:** <br> - `{id}` (bigint) ID транзакции <br><br> **Схема тела запроса:** <br> - `amount` (bigDecimal) Сумма транзакции <br> - `paymentType` (string) Тип оплаты (`'CASH'`, `'CARD'` или `'TRANSFER'`) |
| **DELETE** | `/transactions/{id}` | **Удаление транзакции** по ID. | **Параметры пути:** <br> - `{id}` (bigint) ID транзакции |

---

### 3. Ресурс: Аналитика (`/analytics`)

Предоставление агрегированных данных и отчетов.

| Метод | Путь | Описание | Параметры |
| :--- | :--- | :--- | :--- |
| **GET** | `/analytics/most-productive-seller/{periodType}` | **Самый продуктивный продавец** за период. Если за период нет транзакций — возвращается `HTTP 204 No Content`. | **Параметры пути:** <br> - `{periodType}` (`DAY`, `MONTH`, `QUARTER`, `YEAR`) — регистронезависимо <br> |
| **GET** | `/analytics/sellers-below-amount` | **Продавцы ниже лимита** по сумме транзакций. | **Параметры запроса:** <br> - `startDate` (timestamp) Начало промежутка <br> - `endDate` (timestamp) Конец промежутка <br> - `maxTotalAmount` (numeric) Сумма транзакций <br> |
| **GET** | `/analytics/most-productive-period/{sellerId}` | **Лучший период** для продавца по кол-ву транзакций. | **Параметры пути:** <br> - `{sellerId}` (bigint) ID продавца <br><br> **Параметры запроса:** <br> - `days` (int) Период в днях <br> |

---

## Примеры использования API

Ниже приведены примеры запросов и ответов от API.

### 1. Создание нового продавца

**Запрос:**

`POST /api/sellers`
```json
{
    "name": "Иван Петров",
    "contactInfo": "ivan.p@example.com"
}
```

**Ответ (HTTP 201 Created), содержимое JSON:**

```json
{
    "id": 1,
    "name": "Иван Петров",
    "contactInfo": "ivan.p@example.com",
    "registrationDate": "2025-10-11T07:25:23.913967031",
    "transactionsCount": 0
}
```


### 2. Создание новой транзакции

**Запрос:**

`POST /api/transactions/seller/1`
```json
{
    "amount" : 1000,
    "paymentType": "CARD"
}
```

**Ответ (HTTP 201 Created), содержимое JSON:**

```json
{
    "id": 1,
    "amount": 1000,
    "transactionDate": "2025-10-11T07:25:44.733768495",
    "sellerId": 1,
    "paymentType": "CARD"
}
```

### 3. Получение информации о продавце и его транзакциях

**Запрос:**

`GET /api/sellers/1`

**Ответ (HTTP 200 OK), содержимое JSON:**

```json
{
    "id": 1,
    "name": "Иван Петров",
    "contactInfo": "ivan.p@example.com",
    "registrationDate": "2025-10-11T07:25:23.913967",
    "transactionsCount": 1,
    "transactions": [
        {
            "id": 1,
            "amount": 1000.00,
            "transactionDate": "2025-10-11T07:25:44.733768"
        }
    ]
}
```

### 4. Получение списка продавцов с суммой транзакций ниже лимита (`10 000`)

**Запрос:**

`GET /api/analytics/sellers-below-amount?startDate=2025-01-01T00:00:00&endDate=2025-12-31T23:59:59&maxTotalAmount=10000`

**Ответ (HTTP 200 OK), содержимое JSON:**
```json
[
    {
        "id": 1,
        "name": "Иван Петров",
        "contactInfo": "ivan.p@example.com",
        "registrationDate": "2025-10-11T07:25:23.913967",
        "transactionsCount": 1
    }
]
```

### 5. Получение списка продавцов с суммой транзакций ниже лимита (`1`)

**Запрос:**

`GET /api/analytics/sellers-below-amount?startDate=2025-01-01T00:00:00&endDate=2025-12-31T23:59:59&maxTotalAmount=1`

**Ответ (HTTP 200 OK), содержимое JSON:**
```json
[]
```

### 6. Получение самого продуктивного продавца за месяц

**Запрос:**

`GET /api/analytics/most-productive-seller/MONTH`

**Ответ (HTTP 200 OK), содержимое JSON:**
```json
{
    "id": 1,
    "name": "Иван Петров",
    "contactInfo": "ivan.p@example.com",
    "registrationDate": "2025-10-11T07:25:23.913967",
    "transactionsCount": 1,
    "transactions": [
        {
            "id": 1,
            "amount": 1000.00,
            "transactionDate": "2025-10-11T07:25:44.733768"
        }
    ]
}
```

### 7. Получение лучшего периода продавца

**Запрос:**

`GET /api/analytics/most-productive-period/1?days=7`

**Ответ (HTTP 200 OK), содержимое JSON:**
```json
{
    "startDate": "2025-10-07",
    "endDate": "2025-10-14",
    "transactionCount": 2
}
```

> `endDate` — эксклюзивная правая граница: период охватывает дни `[startDate, endDate)`, то есть ровно `days` календарных дней.
> Если у продавца нет транзакций — возвращается `{ "startDate": null, "endDate": null, "transactionCount": 0 }`.

---

## Формат дат

Все параметры с датой и временем передаются в формате ISO 8601: `yyyy-MM-dd'T'HH:mm:ss`.

**Пример:** `startDate=2025-01-01T00:00:00`

---

## Примеры ошибок

**Ошибка валидации (HTTP 400):**
```json
{
    "timestamp": "2025-10-11T07:30:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Ошибка валидации данных. Подробности: name: Имя продавца не может быть пустым",
    "path": "/api/sellers"
}
```

**Ресурс не найден (HTTP 404):**
```json
{
    "timestamp": "2025-10-11T07:30:00",
    "status": 404,
    "error": "Not Found",
    "message": "Продавец с ID 999 не найден",
    "path": "/api/sellers/999"
}
```