@echo off
::set path=C:\Programs\Git\bin\;%PATH%
@set path=C:\Programs\Git\bin\;%PATH%
@set HOME=D:\Gabor

if "%*" == "" goto noparam
%*
goto end

:noparam
start "%CD%" cmd

:end
