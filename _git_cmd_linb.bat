@echo off
set path=C:\Programok\git\bin;%PATH%
set HOME=D:\Gabor
::set HOME=C:\Dokumentumok\Gabor\cygwin

if "%*" == "" goto noparam
%*
goto end

:noparam
start "%CD%" cmd

:end
