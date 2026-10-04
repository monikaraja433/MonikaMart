@echo off
title MonikaMart E-Commerce Platform
echo ==================================================================
echo  Starting MonikaMart on Apache Tomcat (Port 8080)...
echo ==================================================================
cd /d "%~dp0"
call mvn compile exec:java
pause
