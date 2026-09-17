@echo off
chcp 65001 >nul
setlocal

echo ============================================
echo   租租侠 - 数据库初始化
echo ============================================
echo.

set "ROOT=%~dp0.."
set "MYSQL_BIN=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"

if not exist "%MYSQL_BIN%" (
    echo [错误] 未找到 mysql.exe：%MYSQL_BIN%
    echo 请修改本脚本顶部的 MYSQL_BIN 变量，指向你的 MySQL 安装目录。
    pause
    exit /b 1
)

set /p MYSQL_USER=请输入 MySQL 用户名（默认 root）:
if "%MYSQL_USER%"=="" set "MYSQL_USER=root"

set /p MYSQL_PWD=请输入 MySQL 密码:

echo.
echo [1/3] 检查 MySQL 服务状态...
sc query MySQL80 | find "RUNNING" >nul
if errorlevel 1 (
    echo       服务未运行，尝试启动（需要管理员权限）...
    net start MySQL80
    if errorlevel 1 (
        echo.
        echo [警告] 无法启动 MySQL80 服务。
        echo        请用管理员身份运行本脚本，或手动到"服务"中启动 MySQL80。
        pause
        exit /b 1
    )
) else (
    echo       服务运行中
)

echo.
echo [2/3] 建库建表（会 DROP 已有 zuzuxia 库！）...
set /p CONFIRM=确认继续？输入 y 回车: 
if /i not "%CONFIRM%"=="y" (
    echo 已取消
    pause
    exit /b 0
)

"%MYSQL_BIN%" -u%MYSQL_USER% -p%MYSQL_PWD% < "%ROOT%\docs\db\schema.sql"
if errorlevel 1 (
    echo [错误] 建表失败，请检查用户名密码
    pause
    exit /b 1
)
echo       建表完成

echo.
echo [3/3] 导入演示数据...
"%MYSQL_BIN%" -u%MYSQL_USER% -p%MYSQL_PWD% zuzuxia < "%ROOT%\docs\db\data.sql"
if errorlevel 1 (
    echo [错误] 导入数据失败
    pause
    exit /b 1
)

echo.
echo ============================================
echo   初始化完成
echo.
echo   演示账号：
echo     管理员     admin / admin123
echo     普通用户   wujiahao / 123456
echo                hejinquan / 123456
echo                wanghaoming / 123456
echo                zhangmingliang / 123456
echo                liangxingyu / 123456
echo.
echo   若后端启动时报数据库连接失败，
echo   请把密码填到 backend\src\main\resources\application-local.yml
echo ============================================
pause
