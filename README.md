# Microservicio Catálogo

Este microservicio se encarga de exponer los productos de la tienda digital.

## Despliegue en EC2 con Docker Run

Para desplegar este microservicio junto con su base de datos usando `docker run`, sigue estos pasos en tu instancia EC2:

1. **Crear una red de Docker** para que los contenedores se comuniquen:
   ```bash
   docker network create catalogo-net
   ```

2. **Levantar la base de datos PostgreSQL**:
   ```bash
   docker run -d \
     --name catalogo_db \
     --network catalogo-net \
     -e POSTGRES_USER=admin \
     -e POSTGRES_PASSWORD=password123 \
     -e POSTGRES_DB=catalogo_db \
     -v catalogo_db_data:/var/lib/postgresql/data \
     postgres:15-alpine
   ```

3. **Construir la imagen del microservicio**:
   ```bash
   docker build -t catalogo_app .
   ```

4. **Levantar el microservicio**:
   ```bash
   docker run -d \
     --name catalogo_app \
     --network catalogo-net \
     -p 8081:8081 \
     -e DB_HOST=catalogo_db \
     -e DB_PORT=5432 \
     -e DB_NAME=catalogo_db \
     -e DB_USER=admin \
     -e DB_PASS=password123 \
     catalogo_app
   ```
