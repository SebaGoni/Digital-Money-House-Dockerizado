## Desafio Profesional Backend - Digital Money House

Digital Money House una billetera virtual. El proyecto consiste en desarrollar el backend y la API que consume el frontend e integrarlo al mismo.
El lenguaje de programacion es Java con Springboot y una arquitectura de microservicios.


### Las funcionalidades desarrolladas son:

- Registro de usuario
- Envio de codigo por mail para validar registro
- Login
- Autenticacion por JWT token
- Recuperacion de contraseña.
- Actualizacion de datos (alias)
- Agregar tarjeta de credito/debito
- Eliminar tarjeta
- Envio/transferencia de dinero en cuenta a otros usuarios
- Recepcion/transferencia de dinero de otros usuarios
- Agregar dinero a partir de una tarjeta de credito/debito
- Consultar ultimos movimientos/actividad
- Ver detalle de movimiento
- Imprimir y/o descargar comprobante de transferencia en pdf
- Datos de usuario
- Alias y CVU
- Cantidad de dinero disponible
- Tarjetas registradas


> [!NOTE]
> ### Microservicios

- eureka-server: servicio de registro y descubrimiento de servicios.
- config-server: servicio de configuraciones de los microservicios.
- gateway: punto de entrada único para clientes externos que desean acceder a los diferentes servicios.
- users-service: Registro, logueo, actualizacion e informacion de usuario.
- accounts-service: servicios relacionados a toda la operacion de la billetera virtual.
- Base de Datos: MySQL


> [!NOTE]
> # Pasos para ejecutar el frontend
> 1. Clonar repositorio del frontend -> git clone https://github.com/SebaGoni/frontend-DMH.git
> 2. ejecutar el comando -> npm install
> 3. ejecutar el comando -> npm start


> [!NOTE]
> # Pasos para ejecutar el backend
> 1. Clonar repositorio del backend -> git clone https://github.com/SebaGoni/Digital-Money-House-Dockerizado.git
> 2. Abrir el proyecto con IntelliJ o Eclipse
> 3. Ejecutar mvn package en cada microservicio
> 4. Ejecutar docker-compose up (si algun microservicio se cae volver a correrlo desde docker desktop una vez que este todo creado)

> [!NOTE]
> # Video presentacion
[![Video presentacion del proyecto](https://res.cloudinary.com/dbguimlv8/image/upload/v1729521527/presentacion_syqrl3.jpg)](https://youtu.be/ynSnZCV2b4E)

> [!NOTE]
> # Documentacion (SWAGGER)
> 
> 1. API de USERS
> http://localhost:8082/swagger-ui/index.html#/

> 2. API de ACCOUNTS
> http://localhost:8085/swagger-ui/index.html#/

> [!NOTE]
> # Documentacion (POSTMAN)
https://www.postman.com/winter-crater-900915/digital-money-house/folder/fxr13k7/dmh?action=share&creator=24678576&ctx=documentation

> [!NOTE]
> # Estructura del proyecto
> ![Estructura del proyecto](https://res.cloudinary.com/dbguimlv8/image/upload/v1729350702/whn9bhfuyrqlheohwe20.jpg)

> [!NOTE]
> # Testing Kick Off
https://docs.google.com/document/d/e/2PACX-1vRPGp2omCvcFaL75DRpgmVCA95vu5G2Z4m48kdGxicE_0nNGBhaZHJsv9jOpIjFhdV_BQgvsGRPeq6h/pub

> [!NOTE]
> # Testing Manual
https://docs.google.com/spreadsheets/d/e/2PACX-1vQvakE-DpJwgJxJVJXQ2IVh9RE_bm5vWAqPP2DmGk_Em059av1O5lwgbTbpjbor9ZFZ6cubM9Ns0MEa/pubhtml

> [!NOTE]
> # Testing Automatizado
https://github.com/SebaGoni/automated-tests.git](https://github.com/SebaGoni/automated-testing-DMH.git

> [!NOTE]
> # Testing Exploratorio
https://docs.google.com/spreadsheets/d/e/2PACX-1vQWNB4z3GBbumKKREOA1969XGaINrNy_aAEHgExu4O4v6nI-lUSvXdN6wD8OlCUmIBy5iG_5qOlf7eq/pubhtml

> [!NOTE]
> # QA Sign off
https://docs.google.com/document/d/e/2PACX-1vTwCcR2815HXulG9w2fkxkHLhPj9M7whPjRyHhFC97uoeRhGBAm2AtpeVykODk--kb5Rt7fudZg2m5h/pub

> [!NOTE]
> # Documentacion - Informe del Proyecto
https://docs.google.com/document/d/e/2PACX-1vRxJDa8K7NRzEEuKpS2LjARWBEo_kArqmjoQ0KE5VkEj9BGU_nb21gRc3z3mM_p8AEA50eNtV-cJHyp/pub

## MUCHAS GRACIAS! :smile:








