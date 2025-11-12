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

## 🛠️ Tecnologías Utilizadas

- Backend: Java 17, Spring Boot 3, Spring Cloud (Gateway, Eureka), Spring Security, JWT, Spring Data JPA.

- Frontend: Vue.js 3 (Composition API), Vue Router, Vuetify 3, Axios.

- Base de Datos: PostgreSQL.

## 🗃️ Esquema de Base de Datos (DBML)

Este es el esquema para cada microservicio, demostrando el patrón "Database-per-Service" (Modelo Inicial temporal).

<img width="1281" height="876" alt="BD_UtaBox" src="https://github.com/user-attachments/assets/5fedcec9-6cca-4632-a705-4097300a24a9" />

## 🚀 Guía de Instalación y Ejecución

Requisitos Previos
- Java 17 (o superior):

- Node.js 18 (o superior): (Para el frontend)

- PostgreSQL: Un servidor de base de datos PostgreSQL corriendo localmente.

1. Configuración de la Base de Datos (PostgreSQL)
Este proyecto utiliza tres bases de datos separadas, una para cada microservicio principal.

Se debe abrir una herramienta de gestión de BBDD (como pgAdmin o DBeaver).

Crear las tres bases de datos:

SQL
```
CREATE DATABASE usuarios_db;
CREATE DATABASE catalogo_db;
CREATE DATABASE reservas_db;
```
NOTA: El proyecto está configurado por defecto para usar el usuario postgres con la contraseña root.

Si sus credenciales son diferentes, deben actualizar el username y password en los 3 archivos application.properties de los servicios:

```
backend/auth-service/src/main/resources/application.properties
backend/catalogo-service/src/main/resources/application.properties
backend/reservas-service/src/main/resources/application.properties
```

Tablas: No es necesario crear las tablas manualmente. Gracias a `spring.jpa.hibernate.ddl-auto=update`, las tablas se crearán automáticamente cuando se inicie cada servicio por primera vez.

2. Ejecución del Backend (Microservicios)
   
Debes iniciarse en el siguiente orden:

Puedes ejecutar cada servicio abriendo una terminal separada para cada uno, navegando a su carpeta y ejecutando el comando de Maven (O descargar, si se usa VSCode, la extensión Spring Boot Dashboard que permite visualizar y encender de forma simple estos).

1) Eureka Server (Puerto 8761):

Bash
```
cd backend/eureka-server
./mvnw spring-boot:run
Espera a que inicie. Puedes verificar en http://localhost:8761.
```

2) Gateway Service (Puerto 8080):

Bash
```
cd backend/gateway-service
./mvnw spring-boot:run
```

3) Auth Service (Puerto 8081):

Bash
```
cd backend/auth-service
./mvnw spring-boot:run
```
4) Catalogo Service (Puerto 8082):

Bash
```
cd backend/catalogo-service
./mvnw spring-boot:run
```
5) Reservas Service (Puerto 8083):

Bash
```
cd backend/reservas-service
./mvnw spring-boot:run
```

En este punto, todos los microservicios deberían estar corriendo y registrados en Eureka.

3. Ejecución del Frontend (Vue.js)

Abrir una nueva terminal en la raíz del proyecto e instalar las siguientes dependencias de Node.js:

Bash
```
npm install
```
Inicia el servidor de desarrollo:

Bash
```
npm run dev
```

¡Listo! Abrir navegador y visitar http://localhost:5173.

## 🧑‍💼 Cuentas de Usuario

Cuenta de Cliente

a) Ir a la aplicación en http://localhost:5173.
b) Hacer clic en el botón "Registrarse".
c) Crear una nueva cuenta. Esta cuenta tendrá el rol de cliente por defecto.
d) Iniciar sesión con tus nuevas credenciales.

Cuenta de Administrador

a) Para acceder al Panel de Admin, se debe crear un administrador manualmente.

Primero, registrar un nuevo usuario siguiendo los pasos de "Cuenta de Cliente".
Conéctatarse a su base de datos usuarios_db (la que usa auth-service).
Buscar la tabla usuarios y encontrar al usuario que se acaba de crear.
Editar la fila de ese usuario y cambiar el valor de la columna rol de "cliente" a "admin".
Volver a la aplicación e iniciar sesión con ese usuario. Ahora se tendrá acceso al botón "Panel Admin".




