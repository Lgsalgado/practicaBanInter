# Módulo de Carga Masiva de Campañas (Fullstack)

**Autor:** Gabriel Salgado

Este proyecto implementa una solución completa (Backend + Frontend) para la gestión y carga masiva de campañas bancarias mediante archivos CSV. Ha sido diseñado siguiendo principios de **Arquitectura Hexagonal**, **Clean Code** y estrategias avanzadas de **Testing**.

## 📋 Descripción del Problema
Debido a una falla en el proceso automático de gestión de campañas, se requería un módulo de soporte para cargar nuevamente la información de forma controlada, validando la estructura, persistiendo los datos y mostrando resultados ordenados con cálculos acumulativos.

## 🚀 Tecnologías Utilizadas

### Backend
*   **Lenguaje**: Java 21
*   **Framework**: Spring Boot 3.4.1 (Jakarta EE)
*   **Base de Datos**: PostgreSQL 16
*   **Procesamiento CSV**: OpenCSV 5.10
*   **Documentación API**: OpenAPI 3.0 (Swagger UI)

### Frontend
*   **Framework**: Angular 16+
*   **Lenguaje**: TypeScript
*   **Estilos**: Bootstrap 5
*   **Cliente HTTP**: RxJS & HttpClient

### Infraestructura
*   **Contenedorización**: Docker & Docker Compose
*   **Servidor Web**: Nginx (Reverse Proxy para Frontend)

## 🏗️ Arquitectura

### Backend (Hexagonal)
El backend sigue una **Arquitectura Hexagonal (Puertos y Adaptadores)** para desacoplar la lógica de negocio de la infraestructura.
*   **Domain**: Modelos puros (`Campaign`) e interfaces (`Ports`).
*   **Application**: Casos de uso (`CampaignService`) y lógica de negocio.
*   **Infrastructure**: Controladores REST, Adaptadores JPA y Configuración.

### Frontend (Component-Based)
El frontend sigue una arquitectura basada en componentes y servicios:
*   **Components**: `CampaignUploadComponent` maneja la lógica de presentación y validación de archivos.
*   **Services**: `CampaignService` encapsula la comunicación HTTP con el backend.
*   **Models**: Interfaces DTO (`CampaignResponseDto`) alineadas con el contrato del API.

## ✅ Características Implementadas
1.  **Carga Masiva**: Procesamiento de archivos CSV desde una interfaz web amigable.
2.  **Validaciones**:
    *   **Frontend**: Validación de extensión de archivo (.csv) antes del envío.
    *   **Backend**: Validaciones estrictas de tipos de datos, longitud y formato.
3.  **Visualización de Resultados**:
    *   Tabla ordenada por presupuesto.
    *   Indicador destacado del **Presupuesto Total Acumulado**.
    *   Manejo de errores visual (alertas rojas) con mensajes descriptivos del backend.
4.  **Idempotencia**: Actualización de registros existentes.
5.  **Dockerización Completa**: Orquestación de Frontend, Backend y Base de Datos.

## 🧪 Estrategia de Testing

### Backend
*   **Unitarios**: JUnit 5 + Mockito (100% cobertura en lógica de negocio).
*   **Integración**: `@SpringBootTest` (End-to-End) y `@WebMvcTest`.
*   **Calidad**: JaCoCo (>95% cobertura) y Pitest (Mutation Testing).

### Frontend
*   **Unitarios (Karma + Jasmine)**:
    *   Validación de componentes y servicios.
    *   Mocks de `HttpClient` para probar manejo de errores y respuestas exitosas.
    *   Pruebas de lógica de selección de archivos.

## 🛠️ Ejecución del Proyecto (Docker)

La forma más sencilla de levantar toda la aplicación es utilizando Docker Compose.

### Prerrequisitos
*   Docker Desktop instalado y corriendo.

### Pasos
1.  Ubícate en la raíz del proyecto (donde está el `docker-compose.yml`).
2.  Ejecuta el siguiente comando:

```bash
docker-compose up --build
```

Esto levantará 3 contenedores:
*   **Base de Datos**: Puerto `5432`
*   **Backend**: Puerto `8080`
*   **Frontend**: Puerto `4200`

### Acceso a la Aplicación
*   👉 **Frontend (Web)**: [http://localhost:4200](http://localhost:4200)
*   👉 **Swagger UI (Backend)**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## 💻 Ejecución Manual (Desarrollo)

Si prefieres ejecutar los servicios individualmente:

### Backend
```bash
cd backend/company-backend
./gradlew bootRun
```

### Frontend
```bash
cd frontend
npm install
npm start
```
*Nota: Asegúrate de tener la base de datos corriendo o configurar una H2 en memoria.*

## 📊 Ejecución de Pruebas

### Backend
```bash
./gradlew test
./gradlew jacocoTestReport
./gradlew pitest
```

### Frontend
```bash
cd frontend
ng test
```

## 📂 Estructura de Archivos Entregados
*   `backend/`: Código fuente Java/Spring Boot.
*   `frontend/`: Código fuente Angular.
*   `Scripts/`: DDL de base de datos.
*   `campaigns_test.csv`: Archivo de prueba.
*   `docker-compose.yml`: Orquestación de servicios.
