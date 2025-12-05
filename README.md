# E-Commerce Platform (Spring Boot API)

Türkçe dokümantasyon aşağıdadır — lütfen aşağı kaydırın.

---

## Table of Contents

- [Overview](#overview)  
- [Architecture & Tech Stack](#architecture--tech-stack)  
- [Domain Modules](#domain-modules)  
- [API Highlights](#api-highlights)  
- [Security](#security)  
- [Local Development](#local-development)  
- [Environment Variables](#environment-variables)  
- [Database & Migrations](#database--migrations)  
- [Run & Test](#run--test)  
- [Example Requests](#example-requests)  
- [Project Structure](#project-structure)  
- [Contributing](#contributing)  
- [License](#license)  

- [E-Ticaret Platformu (Spring Boot API) — Türkçe](#e-ticaret-platformu-spring-boot-api--türkçe)  
  - [Genel Bakış](#genel-bakış)  
  - [Mimari ve Teknolojiler](#mimari-ve-teknolojiler)  
  - [Domain Modülleri (TR)](#domain-modülleri-tr)  
  - [API Özet](#api-özet)  
  - [Güvenlik (TR)](#güvenlik-tr)  
  - [Yerel Geliştirme](#yerel-geliştirme)  
  - [Ortam Değişkenleri (TR)](#ortam-değişkenleri-tr)  
  - [Veritabanı ve Migrasyon (TR)](#veritabanı-ve-migrasyon-tr)  
  - [Çalıştırma ve Test (TR)](#çalıştırma-ve-test-tr)  
  - [Örnek İstekler (TR)](#örnek-istekler-tr)  
  - [Proje Yapısı (TR)](#proje-yapısı-tr)  
  - [Katkı (TR)](#katkı-tr)  
  - [Lisans (TR)](#lisans-tr)  

---

## Overview

Production-grade e-commerce backend built with Spring Boot + JPA/Hibernate + PostgreSQL.  
It implements a full shopping flow with robust cart, inventory reservation/consumption, orders, payments (capture), cancellations, and JWT-based authentication.  
The code uses MapStruct for clean DTO mapping and exposes OpenAPI/Swagger for testing.

Key points:

- Inventory-first checkout with pessimistic locking and reservation records.  
- Cart snapshots and Order snapshots for durability against catalog changes.  
- Stateless access tokens; refresh tokens stored and revocable (logout / logout all).  
- Clean separation of DTOs, Mappers, Services, Controllers, Repositories.  

---

## Architecture & Tech Stack

Runtime:

- Java 17+  
- Spring Boot  

Persistence:

- PostgreSQL  
- JPA/Hibernate  

Security:

- JWT (access + refresh tokens)  
- ROLE_USER / ROLE_ADMIN  

Other:

- Mapping: MapStruct  
- Validation: Jakarta Validation  
- Docs: OpenAPI 3 / Swagger UI  
- Build: Maven  

---

## Domain Modules

### Catalog

- Products  
- Variants (price, SKU, attributes JSON)  
- Product images (with sort order for thumbnails)  

### Cart

- Cart and CartItem with snapshot fields:  
  - unitPriceCents  
  - skuSnapshot  
  - productTitleSnapshot  
  - attributesJsonSnapshot  
- Snapshots are taken at add-time to freeze values.  
- Shipping calculation and totals:  
  - itemsTotal  
  - shipping  
  - discount  
  - tax  
  - grandTotal  

### Inventory

- Stock (on-hand, reserved)  
- StockReservation  
- StockMovement (INBOUND / OUTBOUND / RESERVATION / RELEASE)  
- Pessimistic locks on stock rows during reservation/consumption  
- Admin endpoints to adjust or set on-hand stock  

### Orders & Payments

- Order  
- OrderItem  
- Payment  
- Refund  
- Shipment  
- OrderCancellation  

Address handling:

- Address snapshots (OrderAddressSnapshot) persisted as JSONB (shipping/billing).  

Checkout flow:

1. Create order (build items, compute totals, snapshot addresses)  
2. Reserve stock for each variant (locks + StockReservation)  
3. Payment capture:  
   - Consume reservations (decrement on-hand)  
   - Set order status PAID  
4. Cancel (pre-capture):  
   - Release reservations  
   - Set order status CANCELED  

### Security & Users

- User  
- Role  
- RefreshToken  
- (Optional) UserDetail  

Features:

- JWT access tokens are stateless.  
- Refresh tokens are persisted in the database and can be revoked.  

---

## API Highlights

Auth:

- POST /api/auth/register  
- POST /api/auth/login  
- POST /api/auth/refresh  
- POST /api/auth/logout (optional: logoutAll)  

Catalog (public GET):

- GET /api/catalog/**  

Cart (AUTH required):

- /api/cart/**  

Inventory (PUBLIC + ADMIN):

Public:

- GET /api/inventory/variants/{variantId}/stock  
- GET /api/inventory/movements  

Checkout:

- POST /api/inventory/reservations (reserve)  
- POST /api/inventory/reservations/{id}/release  
- POST /api/inventory/reservations/{id}/consume  

Admin:

- POST /api/admin/inventory/adjust  
- POST /api/admin/inventory/set  

Orders (AUTH):

- POST /api/orders (create)  
- POST /api/orders/{id}/capture  
- POST /api/orders/{id}/cancel  
- GET  /api/orders (paginated list)  
- GET  /api/orders/{id} (detail)  

Swagger / OpenAPI:

- Swagger UI: GET /swagger-ui.html  
- OpenAPI JSON: GET /v3/api-docs  

---

## Security

CORS (for local development):

- http://localhost:5173  
- http://localhost:3000  

Session:

- Stateless sessions  
- Bearer authentication required except explicitly public routes  

Public GET routes:

- /api/catalog/**  
- /api/content/**  
- /api/marketing/**  

Swagger:

- /v3/api-docs/**  
- /swagger-ui/**  

Method security:

- Admin routes protected with @PreAuthorize("hasRole('ADMIN')")  

---

## Local Development

### Prerequisites

- Java 17+  
- Maven  
- PostgreSQL running locally  

### Database

- Create a PostgreSQL database (for example: ecommerce).  
- Configure connection using environment variables or application.yml.  

### Build and run

- Command: mvn clean spring-boot:run  
- API URL: http://localhost:8080  

---

## Environment Variables

Typical variables (you can adjust the names/values as needed):

Database:

- SPRING_DATASOURCE_URL = jdbc:postgresql://localhost:5432/ecommerce  
- SPRING_DATASOURCE_USERNAME = postgres  
- SPRING_DATASOURCE_PASSWORD = postgres  

JPA:

- SPRING_JPA_HIBERNATE_DDL_AUTO = update (or validate)  
- SPRING_JPA_SHOW_SQL = false  

JWT:

- JWT_SECRET = change-me  
- JWT_ACCESS_TTL_MINUTES = 60  
- JWT_REFRESH_TTL_DAYS = 30  

Swagger:

- SPRINGDOC_SWAGGER_UI_PATH = /swagger-ui.html  

---

## Database & Migrations

- Entities use UUID primary keys.  
- @SQLDelete + @Where implement soft delete for some aggregates.  
- For local development, ddl-auto = update is convenient.  
- For production, Flyway or Liquibase is recommended for schema migrations.  

---

## Run & Test

- Swagger UI: open http://localhost:8080/swagger-ui.html  
- Simple health check: call any public GET, for example: /api/catalog/**  

---

## Example Requests

Register:

curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"P@ssw0rd!","firstName":"John","lastName":"Doe"}'

Login (get tokens):

curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"P@ssw0rd!"}'

Add item to cart:

curl -X POST http://localhost:8080/api/cart/items \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"variantId":"<uuid>","quantity":1}'

Create order:

curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "items":[{"variantId":"<uuid>","quantity":1}],
    "shippingAddress":{
      "fullName":"John Doe",
      "line1":"Street 1",
      "line2":null,
      "city":"Istanbul",
      "state":"TR-34",
      "postalCode":"34000",
      "countryCode":"TR"
    }
  }'

Capture payment:

curl -X POST http://localhost:8080/api/orders/<ORDER_ID>/capture \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"provider":"iyzico","providerRef":"TXN-123","amountCents":3755,"payloadJson":"{}"}'

---

## Project Structure

src/  
  main/  
    java/com/commerce/e_commerce/  
      config/               Security, Swagger  
      controller/           REST controllers  
      domain/               Entities (cart, order, inventory, catalog, security)  
      dto/                  DTOs  
      exceptions/           ApiException etc.  
      mapper/               MapStruct mappers  
      repository/           Spring Data repos  
      security/             JWT, filters, providers  
      service/              Business services  
    resources/  
      application.yml  

---

## Contributing

PRs are welcome! Please:

- Include tests where possible.  
- Follow the existing code style:  
  - DTO-first design  
  - MapStruct for mappings  
  - Thin controllers  
  - Business logic in services  

---

## License

MIT (or your preferred license)

---

## E-Ticaret Platformu (Spring Boot API) — Türkçe

### İçindekiler

- [Genel Bakış](#genel-bakış)  
- [Mimari ve Teknolojiler](#mimari-ve-teknolojiler)  
- [Domain Modülleri (TR)](#domain-modülleri-tr)  
- [API Özet](#api-özet)  
- [Güvenlik (TR)](#güvenlik-tr)  
- [Yerel Geliştirme](#yerel-geliştirme)  
- [Ortam Değişkenleri (TR)](#ortam-değişkenleri-tr)  
- [Veritabanı ve Migrasyon (TR)](#veritabanı-ve-migrasyon-tr)  
- [Çalıştırma ve Test (TR)](#çalıştırma-ve-test-tr)  
- [Örnek İstekler (TR)](#örnek-istekler-tr)  
- [Proje Yapısı (TR)](#proje-yapısı-tr)  
- [Katkı (TR)](#katkı-tr)  
- [Lisans (TR)](#lisans-tr)  

---

### Genel Bakış

Spring Boot + JPA/Hibernate + PostgreSQL ile geliştirilen üretim seviyesinde bir e-ticaret arka ucu.  
Sepet, stok rezervasyonu/tüketimi, sipariş, ödeme (capture), iptal ve JWT tabanlı kimlik doğrulama süreçlerinin tamamını kapsar.  
MapStruct ile temiz DTO eşlemeleri ve Swagger/OpenAPI ile rahat test imkânı sağlar.

Öne çıkanlar:

- Stok öncelikli checkout: pessimistic lock ve reservation kayıtları.  
- Sepet ve Sipariş kalemlerinde snapshot yaklaşımı.  
- Stateless access token + revoke edilebilir refresh token.  
- Katmanlı mimari: DTO, Mapper, Service, Controller, Repository.  

---

### Mimari ve Teknolojiler

Runtime:

- Java 17+  
- Spring Boot  

Veri:

- PostgreSQL  
- JPA/Hibernate  

Güvenlik:

- JWT (access + refresh)  
- ROLE_USER / ROLE_ADMIN  

Diğer:

- Mapping: MapStruct  
- Validasyon: Jakarta Validation  
- Dokümantasyon: OpenAPI 3 / Swagger UI  
- Build: Maven  

---

### Domain Modülleri (TR)

#### Katalog

- Ürünler  
- Varyantlar (fiyat, SKU, attributes JSON)  
- Ürün görselleri (thumbnail için sort order)  

#### Sepet

- Cart ve CartItem üzerinde snapshot alanları:  
  - unitPriceCents  
  - skuSnapshot  
  - productTitleSnapshot  
  - attributesJsonSnapshot  
- Kargo ve tüm tutar hesapları:  
  - itemsTotal  
  - shipping  
  - discount  
  - tax  
  - grandTotal  

#### Envanter

- Stock (eldeki, rezerve)  
- StockReservation  
- StockMovement (INBOUND / OUTBOUND / RESERVATION / RELEASE)  
- Rezervasyonda pessimistic lock ile stok satırı kilitleme.  
- Admin uçları:  
  - adjust (arttır/azalt)  
  - setOnHand (doğrudan değer atama)  

#### Sipariş & Ödeme

- Order  
- OrderItem  
- Payment  
- Refund  
- Shipment  
- OrderCancellation  

Adres yönetimi:

- Adres snapshot (shipping/billing) OrderAddressSnapshot olarak JSONB.  

Akış:

1. Sipariş oluştur (kalemleri hesapla, adres snapshot’ları yaz)  
2. Stok rezervasyon (kilit + StockReservation)  
3. Ödeme al (capture) → rezervasyonu tüket (eldeki stoktan düş) + durum PAID  
4. İptal (capture öncesi) → rezervasyonu serbest bırak (release) + durum CANCELED  

#### Güvenlik & Kullanıcılar

- User  
- Role  
- RefreshToken  
- Opsiyonel UserDetail  

Özellikler:

- JWT access token stateless.  
- Refresh token’lar DB’de tutulur ve revoke edilebilir.  

---

### API Özet

Auth:

- /api/auth/register  
- /api/auth/login  
- /api/auth/refresh  
- /api/auth/logout (opsiyonel: logoutAll)  

Catalog (GET, public):

- /api/catalog/**  

Cart (AUTH):

- /api/cart/**  

Inventory (PUBLIC+ADMIN):

Public:

- /api/inventory/variants/{variantId}/stock  
- /api/inventory/movements  

Checkout:

- /api/inventory/reservations (reserve)  
- /api/inventory/reservations/{id}/release  
- /api/inventory/reservations/{id}/consume  

Admin:

- /api/admin/inventory/adjust  
- /api/admin/inventory/set  

Orders (AUTH):

- POST /api/orders (create)  
- POST /api/orders/{id}/capture  
- POST /api/orders/{id}/cancel  
- GET  /api/orders (sayfalı liste)  
- GET  /api/orders/{id} (detay)  

Swagger / OpenAPI:

- Swagger UI: GET /swagger-ui.html  
- OpenAPI JSON: GET /v3/api-docs  

---

### Güvenlik (TR)

CORS (geliştirme ortamı için):

- http://localhost:5173  
- http://localhost:3000  

Session:

- Stateless oturum  
- Bearer zorunlu, public GET’ler ve Swagger hariç  

Public GET’ler:

- /api/catalog/**  
- /api/content/**  
- /api/marketing/**  

Swagger:

- /v3/api-docs/**  
- /swagger-ui/**  

Admin uçları:

- @PreAuthorize("hasRole('ADMIN')") ile korunur.  

---

### Yerel Geliştirme

Gereksinimler:

- Java 17+  
- Maven  
- PostgreSQL  

Veritabanı:

- PostgreSQL üzerinde bir veritabanı oluşturun (örneğin: ecommerce).  
- Bağlantı ayarlarını ortam değişkenleri veya application.yml üzerinden yapılandırın.  

Çalıştırma:

- Komut: mvn clean spring-boot:run  
- API: http://localhost:8080  

---

### Ortam Değişkenleri (TR)

Database:

- SPRING_DATASOURCE_URL = jdbc:postgresql://localhost:5432/ecommerce  
- SPRING_DATASOURCE_USERNAME = postgres  
- SPRING_DATASOURCE_PASSWORD = postgres  

JPA:

- SPRING_JPA_HIBERNATE_DDL_AUTO = update  
- SPRING_JPA_SHOW_SQL = false  

JWT:

- JWT_SECRET = degistir-beni  
- JWT_ACCESS_TTL_MINUTES = 60  
- JWT_REFRESH_TTL_DAYS = 30  

Swagger:

- SPRINGDOC_SWAGGER_UI_PATH = /swagger-ui.html  

---

### Veritabanı ve Migrasyon (TR)

- UUID primary key kullanımı.  
- Bazı varlıklarda soft delete için @SQLDelete + @Where.  
- Lokal geliştirme için ddl-auto = update yeterli.  
- Prod ortamında Flyway veya Liquibase ile migrasyon yönetimi önerilir.  

---

### Çalıştırma ve Test (TR)

- Swagger: http://localhost:8080/swagger-ui.html  
- Hızlı test için basit bir public GET çağrısı: /api/catalog/**  

---

### Örnek İstekler (TR)

Kayıt ol:

curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"P@ssw0rd!","firstName":"John","lastName":"Doe"}'

Giriş (token al):

curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"P@ssw0rd!"}'

Sepete ekle:

curl -X POST http://localhost:8080/api/cart/items \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"variantId":"<uuid>","quantity":1}'

Sipariş oluştur:

curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "items":[{"variantId":"<uuid>","quantity":1}],
    "shippingAddress":{
      "fullName":"John Doe",
      "line1":"Street 1",
      "line2":null,
      "city":"Istanbul",
      "state":"TR-34",
      "postalCode":"34000",
      "countryCode":"TR"
    }
  }'

Ödeme onayı (capture):

curl -X POST http://localhost:8080/api/orders/<ORDER_ID>/capture \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"provider":"iyzico","providerRef":"TXN-123","amountCents":3755,"payloadJson":"{}"}'

---

### Proje Yapısı (TR)

src/  
  main/  
    java/com/commerce/e_commerce/  
      config/              Security, Swagger  
      controller/          REST controller'lar  
      domain/             Varlıklar (cart, order, inventory, catalog, security)  
      dto/                DTO'lar  
      exceptions/         ApiException vb.  
      mapper/             MapStruct mappers  
      repository/         Spring Data repos  
      security/           JWT, filters, providers  
      service/            İş servisleri  
    resources/  
      application.yml  

---

### Katkı (TR)

Katkılar memnuniyetle!  
Lütfen mümkün olduğunca test ekleyin ve mevcut stil ile uyumlu geliştirme yapın:

- DTO odaklı tasarım  
- MapStruct kullanımı  
- Controller ince (thin controller)  
- İş mantığı serviste  

---

### Lisans (TR)

MIT (veya tercih ettiğiniz lisans)
