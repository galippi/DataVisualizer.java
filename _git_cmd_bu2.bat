@echo off
::@set path=C:\KBApps\DevEnv\cygwin\Git\bin\;%PATH%
@set path=C:\KBApps\DevEnv\Git.2.29.2\cmd;%PATH%
@set HOME=C:\Users\Liptak

if "%*" == "" goto noparam
%*
goto end

:noparam
start "%CD%" cmd

:end
