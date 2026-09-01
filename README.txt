SISTEMA DE PROCESAMIENTO POR LOTES - BANCO XYZ (Versión Texto Plano - README.txt)
Plaintext
================================================================================
PROYECTO: Sistema de Migración Batch - Banco XYZ
VERSIÓN: 1.0 (Java 21 / Spring Boot 3.4.3 / Spring Batch)
================================================================================

1. DESCRIPCIÓN GENERAL
Este sistema automatiza la modernización de los procesos batch legacy del Banco XYZ,
implementando tres jobs independientes para la transaccionalidad diaria, el cálculo
de intereses mensuales y la consolidación de estados de cuenta anuales.

2. ESTRUCTURA DE ARCHIVOS
src/
├── main/
│   ├── java/com/duoc/migracion/
│   │   ├── config/       # Configuración de Jobs y Executors
│   │   ├── controller/   # Endpoints REST de disparo
│   │   ├── dto/          # Modelos de mapeo CSV
│   │   ├── listener/     # SkipListeners y control de errores
│   │   ├── model/        # Entidades JPA
│   │   ├── processor/    # Validaciones y reglas de negocio (ItemProcessor)
│   │   ├── reader/       # Lectores planos CSV
│   │   ├── repository/   # Repositorios JPA
│   │   └── writer/       # Escritores de base de datos
│   └── resources/
│       ├── application.properties
│       └── data/         # CSVs de origen (transacciones, intereses, cuentas)
└── pom.xml

3. REQUISITOS DEL SISTEMA
- JDK 21 instalado y configurado en el PATH.
- Maven Wrapper (incluido en el repositorio).
- Base de datos relacional (MySQL o H2 embebido para pruebas).

4. INSTRUCCIONES DE EJECUCIÓN
- Compilación y pruebas limpias:
  ./mvnw clean test

- Arranque de la aplicación Spring Boot:
  ./mvnw spring-boot:run

- Ejecución de Jobs vía REST endpoints:
  - GET http://localhost:8080/api/batch/run/transaccion
  - GET http://localhost:8080/api/batch/run/interes
  - GET http://localhost:8080/api/batch/run/cuenta-anual