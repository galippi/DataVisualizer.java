@echo off
cls

@call config.bat

@set PATH=%CYGWIN%\bin\;%PATH%

:: updating version info
%CYGWIN%\bin\bash.exe -i -c "./version.sh"

:: updating source list
del /f /Q javafiles
%CYGWIN%\bin\bash.exe -i -c "find src -iname '*.java' ! -name DataVisualizerLayoutFile_test.java ! -name PythonStructUnitTest.java ! -name ScaleTest.java >javafiles"
mkdir bin
del /f /q /s *.class
::"C:\Program Files (x86)\Java\jdk1.7.0_11\bin\javac" -Werror -d bin -cp patzh-to-jar;path-to-jar2 @javafiles
"%JAVA%\bin\javac" -Werror -d bin -cp lib/json-20180813.jar @javafiles
if %ERRORLEVEL%==0 goto link_step
echo ERROR!
goto end_pause

:link_step
cd bin
del /f /Q classfiles
%CYGWIN%\bin\bash.exe -i -c "find . -iname '*.class' >classfiles"

del /f /Q %JAR_FILE%
"%JAVA%\bin\jar" cmf ../manifest.mf %JAR_FILE% @classfiles
if %ERRORLEVEL%==0 goto copy_step
echo ERROR!
cd ..
goto end_pause

:copy_step
::del /f /q PATH\%JAR_FILE%
::copy %JAR_FILE% PATH
cd ..
goto end

:end_pause
::pause

:end
