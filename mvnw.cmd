@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM
@REM Required ENV Vars:
@REM JAVA_HOME - location of a JDK home dir
@REM
@REM Optional ENV Vars
@REM MAVEN_BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
@REM MAVEN_BATCH_PAUSE - set to 'on' to wait at the end of the script
@REM MAVEN_OPTS - parameters to passed to the Java VM when running Maven
@REM     e.g. to debug Maven itself, use
@REM set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
@REM MAVEN_SKIP_RC - flag to disable loading of mavenrc files
@REM ----------------------------------------------------------------------------

@ECHO OFF
@setlocal
set ERROR_CODE=0

@REM set %HOME% to equivalent of $HOME
if "%HOME%" == "" (set "HOME=%HOMEDRIVE%%HOMEPATH%")

@REM Execute a user defined script before this one
if not "%MAVEN_SKIP_RC%" == "" goto skipRcPre
@REM Personal execute-pre run script
if exist "%USERPROFILE%\.mavenrc_pre.bat" call "%USERPROFILE%\.mavenrc_pre.bat"
@REM System-wide execute-pre run script
if exist "%ALLUSERSPROFILE%\.mavenrc_pre.bat" call "%ALLUSERSPROFILE%\.mavenrc_pre.bat"
:skipRcPre

@REM set %MAVEN_PROJECTBASEDIR%
set "MAVEN_PROJECTBASEDIR=%~dp0"
if "%MAVEN_PROJECTBASEDIR:~-1%"=="\" set "MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%"

@REM Extension to allow automatically downloading the maven-wrapper.jar
@REM This allows using the wrapper in projects that don't have it checked in
set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

if exist %WRAPPER_JAR% goto runWrapper

set WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"

if not exist "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper" mkdir "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper"

powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object Net.WebClient).DownloadFile('%WRAPPER_URL%', '%WRAPPER_JAR%')}"

:runWrapper

if not "%JAVACMD%"=="" goto checkJavaCmd
if not "%JAVA_HOME%"=="" goto checkJavaHome

set "JAVACMD=java"
goto runWrapperLauncher

:checkJavaHome
set "JAVACMD=%JAVA_HOME%\bin\java.exe"
if exist "%JAVACMD%" goto runWrapperLauncher

echo.
echo Error: JAVA_HOME is set to an invalid directory.
echo JAVA_HOME = "%JAVA_HOME%"
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.
echo.
goto error

:checkJavaCmd
if exist "%JAVACMD%" goto runWrapperLauncher

echo.
echo Error: JAVACMD is set to an invalid file.
echo JAVACMD = "%JAVACMD%"
echo Please set the JAVACMD variable in your environment to match the
echo location of your Java installation.
echo.
goto error

:runWrapperLauncher
"%JAVACMD%" %MAVEN_OPTS% %MAVEN_DEBUG_OPTS% -classpath %WRAPPER_JAR% "-Dmaven.home=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper" "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" %WRAPPER_LAUNCHER% %*
if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%
if not "%MAVEN_SKIP_RC%" == "" goto skipRcPost
if exist "%USERPROFILE%\.mavenrc_post.bat" call "%USERPROFILE%\.mavenrc_post.bat"
if exist "%ALLUSERSPROFILE%\.mavenrc_post.bat" call "%ALLUSERSPROFILE%\.mavenrc_post.bat"
:skipRcPost
cmd /C exit /B %ERROR_CODE%
