# 🎤 Uta-Box: Sistema de Reservas de Karaoke Box

**Uta-Box** (del japonés *uta*, "canción") es una plataforma web completa para la gestión y reserva de salas de karaoke privadas estilo japonés. El proyecto implementa una **arquitectura de microservicios** con Spring Boot y Spring Cloud para manejar un modelo de negocio híbrido complejo:

1.  **Alquiler de Salas (Stock de Tiempo):** Gestión de un calendario en tiempo real para activos únicos (las salas).
2.  **Venta de Consumibles (Stock de Inventario):** Gestión de inventario de comida y bebidas

Este proyecto fue desarrollado como una solución a los requisitos de un sistema de e-commerce avanzado, centrándose en la correcta gestión de roles, seguridad y lógica de negocio desacoplada.

---

## 🏗️ Arquitectura de Microservicios

El sistema está dividido en servicios independientes, cada uno con su propia base de datos (siguiendo el patrón **Database-per-Service**) para asegurar un bajo acoplamiento y alta cohesión.

* **1. API Gateway (Spring Cloud Gateway):**
    * Punto de entrada único para todo el tráfico del frontend.
    * Maneja el enrutamiento a los servicios internos.
    * Implementa el **Middleware de Autorización**, validando los JWT en cada petición protegida

* **2. Service Discovery (Spring Cloud Eureka):**
    * Un "directorio telefónico" que permite a los servicios encontrarse entre sí dinámicamente en la red.

* **3. Servicio de Usuarios (Auth-Service):**
    * **Base de Datos:** `usuarios_db`
    * Responsable del registro, login (generación de JWT) y gestión de perfiles de usuario (`admin`, `cliente`).

* **4. Servicio de Catálogo (Catalog-Service):**
    * **Base de Datos:** `catalogo_db`
    * Responsable de definir *qué* se vende. Gestiona dos conceptos:
        * `productos`: Los *tipos* de cosas (ej. "Sala Idol", "Bebida Pepsi").
        * `activos`: Las salas físicas *individuales* (ej. "Sala 'K-pop'").

* **5. Servicio de Reservas (Booking-Service):**
    * **Base de Datos:** `reservas_db`
    * El cerebro del negocio. Es responsable de:
        * Gestionar la agenda (tabla `reservas`) para evitar *overbooking*.
        * Gestionar los pedidos de comida (tabla `pedidos_consumibles`) y coordinar la lógica de descuento de `stock` .

---

## 🗃️ Esquema de Base de Datos (DBML)

Este es el esquema para cada microservicio, demostrando el patrón "Database-per-Service".



