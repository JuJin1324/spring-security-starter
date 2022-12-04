FROM mysql:8.0.31
LABEL maintainer="jujin1324@daum.net"
LABEL dockerhub="https://hub.docker.com/_/mysql"

EXPOSE 3306

ADD spring-security-starter.cnf /etc/mysql/conf.d/

ENV MYSQL_ROOT_PASSWORD root
ENV MYSQL_DATABASE spring_security
ENV MYSQL_USER spring
ENV MYSQL_PASSWORD security
