docker build --tag starter/postgres:1.0 .; \
docker run -d \
-p 5432:5432 \
--name spring-security-starter-postgres \
starter/postgres:1.0
