docker build --tag starter/security-mysql:1.0 .
docker run -d \
-p 3399:3306 \
--name spring-security-starter-mysql \
starter/security-mysql:1.0
