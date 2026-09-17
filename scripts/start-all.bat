@echo off
chcp 65001 >nul
setlocal

echo ============================================
echo   租租侠 - 一键启动
echo ============================================
echo.

set "ROOT=%~dp0.."

echo [1/4] 检查 MySQL 服务...
sc query MySQL80 | find "RUNNING" >nul
if errorlevel 1 (
    echo       未运行，尝试启动...
    net start MySQL80 >nul 2>&1
    if errorlevel 1 (
        echo       [警告] 启动失败，请用管理员身份重试或手动启动 MySQL80
    ) else (
        echo       已启动
    )
) else (
    echo       运行中
)

echo.
echo [2/4] 检查端口占用...
call :checkport 8080 "后端"
call :checkport 5173 "前端"

echo.
echo [3/4] 启动后端（Spring Boot, :8080）...
start "zuzuxia-backend" cmd /k "cd /d %ROOT%\backend && mvnw.cmd spring-boot:run"

echo       等待后端就绪...
set /a WAIT=0
:waitloop
timeout /t 2 /nobreak >nul
set /a WAIT+=2
curl -s -o nul --max-time 3 http://localhost:8080/api/ping
if not errorlevel 1 goto backend_ready
if %WAIT% GEQ 90 (
    echo       [警告] 等待超时，请查看后端窗口的报错
    goto start_front
)
goto waitloop

:backend_ready
echo       后端已就绪（耗时约 %WAIT% 秒）

:start_front
echo.
echo [4/4] 启动前端（Vite, :5173）...
start "zuzuxia-frontend" cmd /k "cd /d %ROOT%\frontend && npm run dev"

echo.
echo ============================================
echo   启动完成
echo.
echo   前端地址：http://localhost:5173
echo   后端地址：http://localhost:8080/api/ping
echo   接口文档：http://localhost:8080/doc.html
echo.
echo   关闭服务：运行 scripts\stop-all.bat
echo ============================================
echo.
echo 等待前端编译（约 5 秒）...
timeout /t 5 /nobreak >nul
start http://localhost:5173
exit /b 0

:checkport
netstat -ano | findstr ":%1 " | findstr "LISTENING" >nul
if not errorlevel 1 (
    echo       [警告] 端口 %1 已被占用（%2 可能已在运行）
) else (
    echo       端口 %1 空闲
)
exit /b 0
