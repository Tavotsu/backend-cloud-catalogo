# Changelog

## [v0.1.0]
### Añadido
- Inicialización del microservicio `backend-cloud-catalogo` con Spring Boot (Web, Security, OAuth2 Resource Server).
- Configuración de `SecurityConfig` y `application.yml` para validación estricta de JWT de Azure AD (Issuer y Audience).
- Integración con base de datos PostgreSQL usando Spring Data JPA (Entidad `Producto` y `ProductoRepository`).
- `CatalogoController` para servir los datos desde la BD validando acceso público y privado.
- `Dockerfile` para ejecutar el proyecto optimizado en EC2.
- `README.md` con instrucciones para levantar la BD y la app usando `docker run` en EC2.
