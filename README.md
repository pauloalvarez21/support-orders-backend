# Support Orders API - Backend

API REST construida con Java 17 y Spring Boot 4.x para la gestión de órdenes de soporte reportadas desde una aplicación móvil

## 🛠️ Stack Tecnológico
- **Lenguaje:** Java 17
- **Framework:** Spring Boot 4.x
- **Persistencia:** Spring Data JPA con PostgreSQL (H2 para tests)
- **Documentación:** SpringDoc OpenAPI (Swagger UI)
- **Build Tool:** Maven

---

## 1. Arquitectura
**¿Cómo separaste responsabilidades y por qué esa estructura es suficiente?**
Se utilizó una **Arquitectura en Capas (Layered Architecture)** con principios de Clean Code:
- **Capa HTTP (`controller`)**: Solo recibe peticiones, valida DTOs con `@Valid` y devuelve respuestas HTTP semánticas.
- **Capa de Negocio (`service`)**: Orquesta los casos de uso, aplica reglas de negocio y maneja transacciones (`@Transactional`).
- **Capa de Persistencia (`repository`)**: Abstrae el acceso a datos con Spring Data JPA.
- **Modelos (`domain` y `dto`)**: Separación estricta entre Entidades JPA y DTOs de transferencia.

**¿Por qué es suficiente?** Para un alcance de 4 horas y un dominio acotado, esta estructura ofrece la mejor relación entre simplicidad, testabilidad y mantenibilidad, evitando la sobre-ingeniería de una Clean Architecture completa con puertos/adaptadores, pero manteniendo una clara separación de responsabilidades (SRP).

## 2. Calidad (SOLID y Patrones de Diseño)
- **Principio SOLID aplicado**: **Principio de Responsabilidad Única (SRP)**. El `GlobalExceptionHandler` es el único responsable de mapear excepciones a respuestas HTTP. Los controladores no contienen bloques `try-catch`, y los servicios no conocen el protocolo HTTP.
- **Patrón de Diseño**: **Patrón State**. La lógica de transición de estados está encapsulada en el método `canTransitionTo()` del Enum `OrderStatus`. 
  - *Problema que resuelve*: Evita grandes bloques `if/else` o `switch` en el servicio. Si mañana se añade un nuevo estado (ej. `PENDING_APPROVAL`), solo se modifica el Enum, cumpliendo el **Principio Abierto/Cerrado (OCP)** sin tocar la lógica del servicio.

## 3. Restricciones y Limitaciones Deliberadas
**¿Qué no implementaste deliberadamente para cumplir el límite de cuatro horas?**
- **Autenticación/Autorización**: No se implementó Spring Security (JWT/OAuth2) para priorizar la lógica de negocio central y las pruebas.
- **Paginación en el Listado**: Se implementó filtrado por estado, pero no paginación (`Pageable`). En producción se añadiría fácilmente cambiando el retorno a `Page<OrderSummary>`.
- **Migraciones de BD**: Se usa `ddl-auto: update` en desarrollo para agilidad. En producción está configurado como `none`, asumiendo que se usaría Flyway/Liquibase en un entorno real.
- **Rate Limiting y Auditoría**: Omitidos en esta fase inicial para enfocarse en el flujo core de negocio.

## 4. SQL vs. NoSQL
**¿Por qué SQL es apropiado aquí y cuándo revisarías esa decisión?**
- **Por qué SQL es apropiado**: Las órdenes de soporte tienen un esquema bien definido, requieren integridad referencial, consistencia transaccional (ACID) para evitar duplicados de `externalReference` activa, y consultas filtradas por estado. PostgreSQL es ideal para esto.
- **Cuándo revisar esta decisión (NoSQL)**: Si el sistema evolucionara para almacenar **grandes volúmenes de eventos de auditoría**, historiales de chat en tiempo real por orden, o payloads altamente variables según la categoría (ej. adjuntos de imágenes, logs de dispositivos móviles). En ese caso, MongoDB o DynamoDB ofrecerían mayor escalabilidad de escritura y flexibilidad de esquema, a cambio de sacrificar las garantías ACID estrictas y las joins relacionales.

## 5. Preparación para Producción
**¿Qué añadirías antes de desplegar el servicio?**
1. **Autenticación/Autorización**: Spring Security con JWT o OAuth2 para validar que solo usuarios autenticados creen órdenes y solo operadores cambien estados.
2. **Observabilidad**: Spring Boot Actuator + Micrometer, exportando métricas a Prometheus/Grafana y logs centralizados (ELK/Datadog).
3. **Idempotencia**: Cabecera `Idempotency-Key` en el `POST` para evitar órdenes duplicadas por reintentos de la red móvil.
4. **Auditoría**: `@EntityListeners(AuditingEntityListener.class)` para rastrear `created_by`, `created_at`, `updated_by`, `updated_at`.
5. **Rate Limiting**: Protección contra abuso en el endpoint de creación (ej. Bucket4j o a nivel de API Gateway).
6. **Despliegue**: Dockerización completa y pipeline CI/CD (GitHub Actions) hacia un entorno orquestado (Kubernetes o AWS ECS) con base de datos gestionada (AWS RDS).

## 6. Trabajo en Paralelo
**¿Qué actividades podrían desarrollar en paralelo y qué contratos deben acordarse primero?**
- **Backend y Frontend**: Pueden trabajar en paralelo **siempre que** se acuerde primero el **Contrato de la API** (especificación OpenAPI/Swagger y estructura de DTOs). El frontend puede usar herramientas como MSW (Mock Service Worker) para simular la API mientras el backend se desarrolla.
- **DevOps/DBA**: Puede provisionar la base de datos PostgreSQL en la nube y configurar el pipeline de CI/CD de forma independiente, sin bloquear al equipo de desarrollo.

## 7. Recursos Adicionales
**Si pudieras incorporar más recursos, ¿qué perfiles pedirías y con qué beneficio?**
1. **QA Automation Engineer (Tarea paralelizable)**: Para construir pruebas E2E (Cypress/Playwright) en paralelo al desarrollo del frontend, asegurando la calidad del flujo completo sin bloquear a los desarrolladores.
2. **DevOps Engineer (Tarea paralelizable)**: Para establecer la infraestructura como código (Terraform), monitoreo y CI/CD. Es una tarea 100% paralelizable que no requiere coordinación constante con el equipo de desarrollo.
3. **UX/UI Designer (Requiere coordinación)**: Para proveer mockups de alta fidelidad. Requiere coordinación inicial fuerte para definir el sistema de diseño, pero luego permite al frontend trabajar de forma desacoplada.

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