@echo off
call mvn clean package -DskipTests
docker-compose up -d --build