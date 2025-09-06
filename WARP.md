# WARP.md

Este archivo proporciona guía a WARP (warp.dev) cuando trabaja con código en este repositorio.

## Arquitectura del Proyecto

Este es un proyecto de autenticación completo que consta de dos componentes principales:

### Backend (Autenticacion-Backend)
- Spring Boot 3.5.4 con Java 17
- Autenticación basada en JWT
- Seguridad Spring
- Persistencia con JPA y PostgreSQL
- API REST

### Frontend (Autenticacion-Frontend)
- Angular 17
- SSR (Server-Side Rendering) habilitado
- RxJS para manejo de estados y operaciones asíncronas

## Comandos Comunes

### Backend

```bash
# En el directorio Autenticacion-Backend:

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

### Frontend

```bash
# En el directorio Autenticacion-Frontend/security-front:

# Instalar dependencias
npm install

# Ejecutar en modo desarrollo
npm start

# Construir el proyecto
npm run build

# Ejecutar pruebas
npm test

# Ejecutar con SSR
npm run serve:ssr:security-front
```

## Configuración del Entorno

### Base de Datos
El proyecto utiliza PostgreSQL como base de datos. La configuración se encuentra en:
`Autenticacion-Backend/src/main/resources/application.properties`

### Dependencias Principales

Backend:
- Spring Boot 3.5.4
- Spring Security
- Spring Data JPA
- JWT (jjwt) 0.11.5
- PostgreSQL
- Lombok

Frontend:
- Angular 19
- Express.js (para SSR)
- RxJS 7.8

## Estructura del Proyecto

```
Autenticacion_Angular_spring/
├── Autenticacion-Backend/    # Servidor Spring Boot
│   ├── src/main/java/       # Código fuente Java
│   ├── src/main/resources/  # Configuraciones
│   └── src/test/           # Pruebas
└── Autenticacion-Frontend/   # Cliente Angular
    └── security-front/      # Aplicación Angular
        ├── src/            # Código fuente TypeScript
        └── dist/           # Archivos compilados
```
