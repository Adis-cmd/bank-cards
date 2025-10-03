# 💳 Bank REST Application

Защищенная система управления банковскими картами с JWT-аутентификацией и ролевым доступом.

---

## 📝 Описание

RESTful сервис для управления банковскими картами, переводами и пользователями с полной поддержкой безопасности.

**Основные возможности:**
- 🔐 JWT аутентификация (роли: ADMIN, USER)
- 💰 Управление картами и балансом
- 💸 Переводы между картами
- 📊 История транзакций с пагинацией
- 🔒 Шифрование номеров карт
- 🎭 Маскирование данных (**** **** **** 1234)

---

## 🛠 Технологии

Java 21 • Spring Boot 3 • Spring Security • PostgreSQL 16 • Liquibase • JWT • Docker • Swagger/OpenAPI

---

## 🚀 Быстрый старт

### Требования
- Docker 20.10+
- Docker Compose 2.0+

### Запуск

```bash
# 1. Клонировать репозиторий
git clone https://github.com/Adis-cmd/bank-cards.git
cd bank_rest

# 2. Запустить через Docker Compose
docker-compose up --build

# 3. Открыть Swagger UI
# http://localhost:9999/swagger-ui/index.html
```

**Приложение запущено!** 🎉

---

## 📍 Доступ к сервисам

| Сервис | URL |
|--------|-----|
| **REST API** | http://localhost:9999 |
| **Swagger UI** | http://localhost:9999/swagger-ui/index.html |
| **PostgreSQL** | localhost:5432 |

**Данные для подключения к БД:**
- Database: `bankdb`
- User: `bank-rest`
- Password: `bank-rest-password`

---

## 🔌 API

Приложение предоставляет следующие группы эндпоинтов:

- **`/api/auth`** - Аутентификация (регистрация, вход)
- **`/api/user`** - Операции пользователя (создание карт, просмотр своих карт)
- **`/api/cards`** - Работа с картами (просмотр, баланс)
- **`/api/transaction`** - Транзакции (переводы, пополнение, история)
- **`/api/admin`** - Административные операции (управление пользователями и картами)

📖 **Полная документация API:** [Swagger UI](http://localhost:9999/swagger-ui/index.html)

---

## 💡 Пример использования

### 1. Регистрация пользователя
```bash
curl -X POST http://localhost:9999/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "name": "John",
    "surname": "Doe"
  }'
```

### 2. Вход и получение JWT токена
```bash
curl -X POST http://localhost:9999/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

**Ответ:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 3. Просмотр своих карт (с токеном)
```bash
curl -X GET http://localhost:9999/api/user \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 4. Создание новой карты
```bash
curl -X POST http://localhost:9999/api/user/create \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 🔧 Управление

```bash
# Остановить контейнеры
docker-compose down

# Остановить с удалением данных
docker-compose down -v

# Просмотр логов
docker-compose logs -f app

# Перезапуск
docker-compose restart
```

---


### Роли пользователей

| Роль | Возможности |
|------|-------------|
| **USER** | Создание и просмотр своих карт, переводы между своими картами, запрос блокировки |
| **ADMIN** | Управление всеми пользователями и картами, блокировка/разблокировка карт, просмотр всех транзакций |

### Защита данных
- Номера карт хранятся в БД в зашифрованном виде (AES-256)
- При отображении номер маскируется: `**** **** **** 1234`
- JWT токены для аутентификации
- Валидация всех входящих данных

---

## ⚠️ Устранение неполадок

### Порты заняты
Измените порты в `docker-compose.yml`:
```yaml
ports:
  - "8080:9999"  # Вместо 9999:9999
```

### Проблемы с БД
```bash
# Пересоздать контейнеры
docker-compose down -v
docker-compose up --build
```

---

## 📁 Структура

```
bank_rest/
├── src/main/java/com/example/bankcards/
│   ├── config/              # Конфигурация
│   ├── controller/          # REST контроллеры
│   ├── service/             # Бизнес-логика
│   ├── repository/          # JPA репозитории
│   ├── model/               # Сущности
│   ├── dto/                 # DTO
│   ├── security/            # JWT
│   └── util/                # Утилиты
├── src/main/resources/
│   ├── application.yml
│   └── db/changelog/        # Liquibase миграции
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

---

<div align="center">

**Создано с ❤️ для управления банковскими картами**

</div>