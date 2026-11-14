# Support Orders API - Mobile Incident Management Service

Solución Fullstack para la gestión de órdenes de soporte reportadas desde una aplicación móvil. Construida con Java 17, Spring Boot 3.x, PostgreSQL y React + TypeScript.

## 📑 Tabla de Contenidos
1. [Arquitectura](#1-arquitectura)
2. [Calidad (SOLID y Patrones de Diseño)](#2-calidad-solid-y-patrones-de-diseño)
3. [Restricciones y Limitaciones Deliberadas](#3-restricciones-y-limitaciones-deliberadas)
4. [SQL vs. NoSQL](#4-sql-vs-nosql)
5. [Preparación para Producción](#5-preparación-para-producción)
6. [Trabajo en Paralelo](#6-trabajo-en-paralelo)
7. [Recursos Adicionales](#7-recursos-adicionales)
8. [Instrucciones de Ejecución](#8-instrucciones-de-ejecución)
9. [Ejemplos de Consumo de la API](#9-ejemplos-de-consumo-de-la-api)

---

## 1. Arquitectura
Se utilizó una **Arquitectura en Capas (Layered Architecture)** con principios de Clean Code:
- **Capa HTTP (`controller`)**: Solo se encarga de recibir peticiones, validar DTOs con `@Valid` y devolver respuestas HTTP semánticas. Sin lógica de negocio.
- **Capa de Negocio (`service`)**: Orquesta los casos de uso, aplica reglas de negocio y maneja transacciones (`@Transactional`).
- **Capa de Persistencia (`repository`)**: Abstrae el acceso a datos con Spring Data JPA.
- **Modelos (`domain` y `dto`)**: Separación estricta entre Entidades JPA y DTOs de transferencia.

**¿Por qué es suficiente?** Para un alcance de 4 horas y un dominio acotado como este, esta estructura ofrece la mejor relación entre simplicidad, testabilidad y mantenibilidad, evitando la sobre-ingeniería de una Clean Architecture completa con puertos/adaptadores, pero manteniendo una clara separación de responsabilidades (SRP).

## 2. Calidad (SOLID y Patrones de Diseño)
- **Principio SOLID aplicado**: **Principio de Responsabilidad Única (SRP)**. El `GlobalExceptionHandler` es el único responsable de mapear excepciones a respuestas HTTP. Los controladores no contienen bloques `try-catch`, y los servicios no conocen el protocolo HTTP.
- **Patrón de Diseño**: **Patrón State**. La lógica de transición de estados está encapsulada en el método `canTransitionTo()` del Enum `OrderStatus`. 
  - *Problema que resuelve*: Evita grandes bloques `if/else` o `switch` en el servicio. Si mañana se añade un nuevo estado (ej. `PENDING_APPROVAL`), solo se modifica el Enum, cumpliendo el **Principio Abierto/Cerrado (OCP)** sin tocar la lógica del servicio.

## 3. Restricciones y Limitaciones Deliberadas
Para cumplir con el límite de 4 horas, se tomaron las siguientes decisiones conscientes:
- **Sin Autenticación/Autorización**: No se implementó Spring Security (JWT/OAuth2) para priorizar la lógica de negocio y las pruebas.
- **Sin Paginación en el Listado**: Se implementó filtrado por estado, pero no paginación (`Pageable`). En producción se añadiría fácilmente cambiando el retorno a `Page<OrderSummary>`.
- **Migraciones de BD**: Se usa `ddl-auto: update` en desarrollo para agilidad. En producción está configurado como `none`, asumiendo que se usaría Flyway/Liquibase en un entorno real.
- **Frontend Minimalista**: La UI es funcional y responsive, pero no incluye un sistema de diseño sofisticado (se priorizó la funcionalidad sobre el CSS custom).

## 4. SQL vs. NoSQL
- **Por qué SQL es apropiado**: Las órdenes de soporte tienen un esquema bien definido, requieren integridad referencial, consistencia transaccional (ACID) para evitar duplicados de `externalReference` activa, y consultas filtradas por estado. PostgreSQL es ideal para esto.
- **Cuándo revisar esta decisión (NoSQL)**: Si el sistema evolucionara para almacenar **grandes volúmenes de eventos de auditoría**, historiales de chat en tiempo real por orden, o payloads altamente variables según la categoría (ej. adjuntos de imágenes, logs de dispositivos móviles). En ese caso, MongoDB o DynamoDB ofrecerían mayor escalabilidad de escritura y flexibilidad de esquema, a cambio de sacrificar las garantías ACID estrictas y las joins relacionales.

## 5. Preparación para Producción
Antes de desplegar, se añadirían:
1. **Autenticación/Autorización**: Spring Security con JWT o OAuth2 para validar que solo usuarios autenticados creen órdenes y solo operadores cambien estados.
2. **Observabilidad**: Spring Boot Actuator + Micrometer, exportando métricas a Prometheus/Grafana y logs centralizados (ELK/Datadog).
3. **Idempotencia**: Cabecera `Idempotency-Key` en el `POST` para evitar órdenes duplicadas por reintentos de la red móvil.
4. **Auditoría**: `@EntityListeners(AuditingEntityListener.class)` para rastrear `created_by`, `created_at`, `updated_by`, `updated_at`.
5. **Rate Limiting**: Protección contra abuso en el endpoint de creación (ej. Bucket4j o a nivel de API Gateway).
6. **Despliegue**: Dockerización completa y pipeline CI/CD (GitHub Actions) hacia un entorno orquestado (Kubernetes o AWS ECS) con base de datos gestionada (AWS RDS).

## 6. Trabajo en Paralelo
- **Backend y Frontend**: Pueden trabajar en paralelo **siempre que** se acuerde primero el **Contrato de la API** (especificación OpenAPI/Swagger y estructura de DTOs). El frontend puede usar herramientas como MSW (Mock Service Worker) para simular la API.
- **DevOps/DBA**: Puede provisionar la base de datos PostgreSQL en la nube y configurar el pipeline de CI/CD mientras el equipo de desarrollo codifica.

## 7. Recursos Adicionales
Si se pudiera ampliar el equipo, solicitaría:
1. **QA Automation Engineer**: Para construir pruebas E2E (Cypress/Playwright) en paralelo al desarrollo del frontend, asegurando la calidad del flujo completo.
2. **DevOps Engineer**: Para establecer la infraestructura como código (Terraform), monitoreo y CI/CD (tarea paralelizable que no bloquea al equipo de desarrollo).
3. **UX/UI Designer**: Para proveer mockups de alta fidelidad.

---

## 8. Instrucciones de Ejecución

### Prerrequisitos
- Java 17+
- Maven 3.8+
- Docker y Docker Compose (para la base de datos local)
- Node.js 18+ y npm (para el frontend)

### Backend
1. Iniciar la base de datos PostgreSQL local:
   ```bash
   docker-compose up -d

## 9. Ejemplos de Consumo de la API

Para facilitar las pruebas, se incluye un archivo **`api-examples.http`** en la raíz del proyecto. 

- **En IntelliJ IDEA**: Abre el archivo y haz clic en el ícono "Send Request" (▶) junto a cada petición.
- **En VS Code**: Instala la extensión "REST Client", abre el archivo y haz clic en "Send Request".

Este archivo contiene ejemplos listos para ejecutar de:
- Creación exitosa y con validaciones fallidas.
- Prevención de duplicados de `externalReference` activa.
- Listado con y sin filtros.
- Transiciones de estado válidas e inválidas.
