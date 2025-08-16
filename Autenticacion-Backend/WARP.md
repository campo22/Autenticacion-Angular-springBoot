# WARP.md

Este archivo proporciona guía a WARP (warp.dev) cuando trabaja con código en este repositorio.

## Arquitectura del Proyecto

Este es un proyecto de autenticación construido con Spring Boot 3.5.4 que implementa:

- Autenticación basada en JWT
- Seguridad Spring
- Persistencia con JPA y PostgreSQL
- API REST

## Comandos Comunes

### Construcción y Ejecución

```bash
# Compilar el proyecto
./mvnw clean install

# Ejecutar la aplicación
./mvnw spring-boot:run

# Ejecutar pruebas
./mvnw test

# Ejecutar una prueba específica
./mvnw test -Dtest=NombreDeLaPrueba

# Construir sin ejecutar pruebas
./mvnw clean package -DskipTests
```

### Base de Datos

El proyecto utiliza PostgreSQL como base de datos. Asegúrate de tener PostgreSQL ejecutándose localmente o configura las credenciales de la base de datos en `application.properties`.

## Estructura del Proyecto

El proyecto sigue la estructura estándar de Spring Boot:

- `src/main/java/` - Código fuente Java
- `src/main/resources/` - Archivos de configuración
- `src/test/` - Pruebas unitarias y de integración

### Dependencias Principales

- Spring Boot 3.5.4
- Spring Security
- Spring Data JPA
- JWT (jjwt) 0.11.5
- PostgreSQL
- Lombok

### Configuración de Java

El proyecto utiliza Java 17 como versión base.
