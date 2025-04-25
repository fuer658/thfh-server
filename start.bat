@echo off
echo Starting Full Stack Application...

:: Start Backend
echo Starting Backend...
start cmd /k "cd thfh-server-new && mvn spring-boot:run"

:: Wait for backend to start
timeout /t 10

:: Start Frontend
echo Starting Frontend...
start cmd /k "cd thfh-admin-new && npm install && npm run serve"

echo Full Stack Application is starting...
echo Backend will be available at http://localhost:8085
echo Frontend will be available at http://localhost:8080 