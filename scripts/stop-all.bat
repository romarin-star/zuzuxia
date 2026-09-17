@echo off
chcp 65001 >nul
setlocal

echo ============================================
echo   租租侠 - 停止服务
echo ============================================
echo.

echo [1/3] 关闭后端与前端的命令行窗口...
taskkill /FI "WINDOWTITLE eq zuzuxia-backend*" /T /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq zuzuxia-frontend*" /T /F >nul 2>&1

echo [2/3] 释放 8080 端口上的 Java 进程...
for /f "tokens=5" %%p in ('netstat -ano ^| findstr ":8080 " ^| findstr "LISTENING"') do (
    echo       结束进程 PID=%%p
    taskkill /PID %%p /F >nul 2>&1
)

echo [3/3] 释放 5173 端口上的 Node 进程...
for /f "tokens=5" %%p in ('netstat -ano ^| findstr ":5173 " ^| findstr "LISTENING"') do (
    echo       结束进程 PID=%%p
    taskkill /PID %%p /F >nul 2>&1
)

echo.
echo 完成。MySQL 服务保持运行（如需停止：net stop MySQL80，需管理员权限）
echo.
pause
