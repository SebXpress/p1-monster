# Comercializadora de Teléfonos Celulares + Core BanQuito (RESTful Java)

ESPE · Arquitectura de Software · NRC 36841 · Grupo 1 · PAO202651
Tutor: Ing. Eduardo Mauricio Campaña Ortega

Integrantes: Benalcázar Fariango Moisés Sebastián · Buestán Guaillas Wilmer Omar · Masapanta Guilcatoma Jefferson Santiago

## Qué hay aquí

Dos aplicaciones web que se comunican con servicios web REST:

| Proyecto | Qué hace |
|---|---|
| `WS_BANQUITO_CORE_RESTFUL_JAVA` | Core BanQuito, módulo de crédito. Solo expone servicios: sujeto de crédito, monto máximo, otorgar crédito y tabla de amortización |
| `WS_COMERCIALIZADORA_RESTFUL_JAVA` | Catálogo de celulares y facturación. En efectivo tiene 42 % de descuento; con crédito directo consulta al Core |
| `CLICON_…` / `CLIESC_…` / `CLIWEB_…` / `CLIMOV_…` | Los 4 clientes de la comercializadora: consola, escritorio Swing, web JSP y Android |

## Carpetas

- `01. UML`: ECUD y diagramas de PowerDesigner. Están el de casos de uso del módulo de crédito (`.oom`) y el de arquitectura (`.eam`).
- `02. MER`: modelos físicos de PowerDesigner (`.pdm`) del Core y de la comercializadora.
- `03. BDD`: scripts SQL. Hay que ejecutarlos en orden: 01 → 02 → 03 → 04.
- `04. CODIGO`: servidores y clientes.
- `05. DOCUMENTACION`: diccionario de datos y manual técnico.
- `06. EVIDENCIAS`: capturas de las pruebas.

## Puesta en marcha rápida

1. MariaDB en el puerto 3307 con `root/rootroot`. Ejecutar los 4 scripts de `03. BDD`.
2. Payara 7: hacer Run de `WS_BANQUITO_CORE_RESTFUL_JAVA` y después de `WS_COMERCIALIZADORA_RESTFUL_JAVA`.
   - La URL del Core se cambia en `db.properties`, en la clave `banquito.url`.
3. Clientes:
   - Web: Run de `CLIWEB_…`.
   - Consola y escritorio: `ejecutar_*.bat`.
   - Móvil: abrir en Android Studio. En el emulador usa `10.0.2.2`.

URL de prueba del Core: http://localhost:8080/WS_BANQUITO_CORE_RESTFUL_JAVA/

## Fórmulas

- Monto máximo = ((PromDepósitos − PromRetiros) × 30 %) × 6, con los movimientos de los últimos 3 meses.
- Cuota = Valor / ((1 − (1 + i)^−n) / i), donde i = 16,5 % / 12 y n va de 3 a 18. El ejemplo de $450 a 6 meses da una cuota de $78,65.
