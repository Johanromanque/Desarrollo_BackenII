# 🛒 MiniMarket Plus - Backend API (Semana 8)

Sistema backend desarrollado en capas utilizando **Spring Boot 3.4.1** para la gestión automatizada y securizada de operaciones comerciales de MiniMarket Plus. Esta versión incorpora un pipeline de pruebas unitarias y de integración robustas con **JUnit 5**, **Mockito** y **JaCoCo**, alcanzando un **92% de cobertura global** y un **99% en la capa de controladores**.

---

## 🛠️ Requisitos del Entorno

Antes de ejecutar la aplicación o la suite de pruebas, asegúrese de contar con:
* **Java Development Kit (JDK):** Versión 17 o superior.
* **Apache Maven:** Versión 3.8 o superior configurada en las variables de entorno.
* **IDE Recomendado:** Visual Studio Code o IntelliJ IDEA con las extensiones de Java activas.

---

## 🚀 Pasos de Ejecución en Local

### 1. Clonar el repositorio y acceder a la raíz del proyecto:
```bash
git clone <enlace-de-tu-repositorio-github>
cd minimarket


2. Compilar el proyecto y ejecutar las pruebas lógicas:
Ejecuta la suite completa de 105 tests automatizados concurrentes y genera el reporte dinámico de JaCoCo en la ruta target/site/jacoco/index.html:

Bash
mvn clean test


3. Levantar la aplicación en el entorno de desarrollo:
El backend se encuentra configurado para iniciar automáticamente bajo el perfil desacoplado dev, utilizando una persistencia en memoria RAM (H2 Database) sobre el puerto alternativo 8081 para evitar conflictos de red locales:

Bash
mvn spring-boot:run


📖 Documentación Navegable de la API (Swagger)
El proyecto cuenta con especificaciones OpenAPI 3 autogeneradas para exponer e interactuar de forma profesional con los endpoints del sistema sin depender de colecciones externas o llamadas manuales a ciegas:

Swagger UI (Interfaz Visual Interactiva): http://localhost:8081/swagger-ui/index.html

OpenAPI Docs (JSON Base): http://localhost:8081/v3/api-docs



🔏 Matriz de Endpoints Principales
🔓 Endpoints Públicos (Acceso para todos)
Autenticación Base:
POST http://localhost:8081/public/
Propósito: Manejo de flujos de acceso e inicio de sesión base del ecosistema.

Documentación Interactiva:
GET http://localhost:8081/swagger-ui/
Propósito: Interfaz gráfica de la API para la revisión del equipo evaluador.

🔐 Endpoints Protegidos (Requieren Autenticación)
Módulo de Productos:
GET http://localhost:8081/api/productos
Permisos: Usuarios autenticados con rol USER o ADMIN.
Propósito: Consulta completa del catálogo de artículos lógicos.

POST http://localhost:8081/api/productos
Permisos: Restringido estrictamente a usuarios con rol ADMIN.
Propósito: Creación y mutación de stock en el catálogo perimetral.

Módulo de Inventario:
POST http://localhost:8081/api/inventario
Permisos: Restringido estrictamente a usuarios con rol ADMIN.
Propósito: Registro de movimientos controlados de entrada y salida física en bodega.

Módulo de Ventas:
POST http://localhost:8081/api/ventas
Permisos: Restringido estrictamente a usuarios con rol CAJERO.
Propósito: Consolidación y facturación de carritos activos de compra con decremento síncrono de stock.




🔑 Credenciales Estatales de Prueba
Para facilitar los flujos de pruebas operacionales en la interfaz interactiva de Swagger UI, se encuentran preconfiguradas las siguientes identidades lógicas dentro de la persistencia efímera del sistema:

Perfil Administrador:

Usuario: admin1

Contraseña: admin123

Privilegios: Gestión total de catálogo de productos, alteración de maestros y movimientos físicos de inventario.

Perfil Cajero:

Usuario: cajero1

Contraseña: cajero123

Privilegios: Confirmación de compras, generación de boletas y procesamiento síncrono de decremento de stock.

Perfil Cliente Común:

Usuario: cliente1

Contraseña: cliente123

Privilegios: Consultas de lectura restringidas (Lanzamiento preventivo de error 403 Forbidden ante intentos de mutación).