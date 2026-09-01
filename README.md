Sistema de Migración Batch - Banco XYZSolución integral de procesamiento por lotes desarrollada con Spring Boot 3.4
y asegurando la integridad de los datos financieros.
Arquitectura y Jobs ImplementadosEl sistema ejecuta tres procesos independientes diseñados para la gestión operativa y de auditoría:
transaccionJobProcesa el archivo diario transacciones.
csv, aplica validaciones de montos y tipos, filtra anomalías mediante una política de tolerancia a fallos (faultTolerant y SkipListener),
y persiste el resumen consolidado.interesJobLee las cuentas desde intereses.csv, calcula y aplica las tasas de interés según el tipo de producto
(ahorro, préstamo, hipoteca) actualizando el saldo final directamente en la base de datos.cuentaAnualJobConsolida la información histórica anual por cliente, 
agrupando movimientos, depósitos, retiros y saldos para los reportes de auditoría regulatoria.Stack TecnológicoLenguaje: Java 21  Framework: Spring Boot 3.4.3, 
Spring Batch, Spring Data JPA  Base de Datos: MySQL (con pool HikariCP) / H2 para pruebas locales  Gestión de Dependencias: 
Maven Wrapper (mvnw)  Estructura del ProyectoPlaintextsrc/

├── main/
│   ├── java/com/duoc/migracion/
│   │   ├── config/          # Configuración de Jobs, Steps y Executors
│   │   ├── controller/      # Endpoints REST para ejecución manual de jobs
│   │   ├── dto/             # Objetos de transferencia (DTOs) para lectura CSV
│   │   ├── exception/       # Manejo de excepciones de negocio y validación
│   │   ├── listener/        # Listeners de pasos y control de omisiones (SkipListener)
│   │   ├── model/           # Entidades JPA (Transaccion, Interes, CuentaAnual)
│   │   ├── processor/       # Lógica de transformación y reglas de negocio
│   │   ├── reader/          # Lectores optimizados (FlatFileItemReader)
│   │   ├── repository/      # Repositorios Spring Data JPA
│   │   └── writer/          # Escritores y persistencia (RepositoryItemWriter)
│   └── resources/
│       ├── application.properties
│       └── data/            # Archivos fuente (transacciones.csv, intereses.csv, cuentas_anuales.csv)
└── test/java/com/duoc/migracion/
└── MigracionBatchApplicationTests.java
Configuración de Base de DatosLa aplicación está configurada para conectar a MySQL en entornos productivos o de integración, 
manteniendo H2 en memoria por defecto para pruebas rápidas.Variables soportadas en application.properties:Propertiesspring.
datasource.url=jdbc:mysql://localhost:3306/bankdb?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.batch.jdbc.initialize-schema=always
Tolerancia a Fallos y ConcurrenciaChunk-oriented processing: Configurado con bloques eficientes para optimizar transacciones de base de datos.Fault Tolerance: 
Uso de políticas faultTolerant() y skipLimit combinadas con un SkipListener personalizado para aislar registros malformados (montos negativos o ceros) sin detener la ejecución global.
Paralelismo: Soporte opcional de ejecución concurrente mediante ThreadPoolTaskExecutor.
Endpoints de Ejecución RESTLos procesos pueden ser disparados mediante peticiones 
HTTP GET:PlaintextGET /api/batch/run/transaccion
GET /api/batch/run/interes
GET /api/batch/run/cuenta-anual
Instrucciones de Compilación y EjecuciónClonar el repositorio y situarse en la raíz del proyecto.
Ejecutar pruebas unitarias y compilación limpia:Bash./mvnw clean test
Ejecutar la aplicación:Bash./mvnw spring-boot:run