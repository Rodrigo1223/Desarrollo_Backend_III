# Sistema de Migración Batch - Banco XYZ

Solución integral de procesamiento por lotes desarrollada con Spring Boot y Spring Batch, diseñada para modernizar los sistemas legacy del Banco XYZ asegurando la integridad, resiliencia y consistencia de los datos financieros.

---

## Arquitectura y Jobs Implementados

El sistema ejecuta tres procesos independientes diseñados para la gestión operativa y de auditoría:

1. **`transaccionJob`**: Procesa el archivo diario `transacciones.csv`, aplica validaciones de montos y tipos, filtra anomalías mediante una política de tolerancia a fallos (`faultTolerant` y `SkipListener`), y persiste el resumen consolidado.
2. **`interesJob`**: Lee las cuentas desde `intereses.csv`, calcula y aplica las tasas de interés según el tipo de producto actualizando el saldo final directamente en la base de datos relacional.
3. **`cuentaAnualJob`**: Consolida la información histórica anual por cliente, agrupando movimientos, depósitos, retiros y saldos para los reportes de auditoría regulatoria.

---

## Stack Tecnológico

* **Lenguaje:** Java 17 o superior
* **Framework:** Spring Boot, Spring Batch, Spring Data JPA
* **Base de Datos:** MySQL (con pool de conexiones HikariCP)
* **Gestión de Dependencias:** Maven Wrapper (`mvnw`)

---

## Estructura del Proyecto

```text
src/
├── main/
│   ├── java/com/duoc/migracion/
│   │   ├── config/       # Configuración de Jobs, Steps y Executors
│   │   ├── controller/   # Endpoints REST para ejecución manual de jobs
│   │   ├── dto/          # Objetos de transferencia (DTOs) para lectura CSV
│   │   ├── exception/    # Manejo de excepciones de negocio y validación
│   │   ├── listener/     # Listeners de pasos y control de omisiones (SkipListener)
│   │   ├── model/        # Entidades JPA (Transaccion, Interes, CuentaAnual)
│   │   ├── processor/    # Lógica de transformación y reglas de negocio
│   │   ├── reader/       # Lectores optimizados (FlatFileItemReader)
│   │   ├── repository/   # Repositorios Spring Data JPA
│   │   └── writer/       # Escritores y persistencia (RepositoryItemWriter)
│   └── resources/
│       ├── application.properties
│       └── data/         # Archivos fuente (transacciones.csv, intereses.csv, cuentas_anuales.csv)
└── test/
    └── java/com/duoc/migracion/
        └── MigracionBatchApplicationTests.java