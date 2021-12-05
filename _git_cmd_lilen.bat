@echo off
::set path=C:\Programs\Git\bin\;%PATH%
@set path=C:\Programs\cygwin\bin\;%PATH%
@set HOME=C:\Users\liptakok\Gabor

if "%*" == "" goto noparam
%*
goto end

:noparam
start "%CD%" cmd

:end
