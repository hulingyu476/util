@echo off
::echo [version] > uvcversion.txt

for /F "tokens=3,4 delims= " %%i in ('%~dp0\dfu-util.exe -l') do (
set pidvid=%%i
set swver=%%j
)

if "%swver%"=="to" (goto notfind) else (goto find)

:find
::echo find

::echo vid:pid=%pidvid:~1,9%

set vid=%pidvid:~1,4%
set pid=%pidvid:~6,4%

::echo vid=%vid% >> uvcversion.txt
::echo pid=%pid% >> uvcversion.txt

for /F "tokens=1 delims=ver^=" %%i in ("%swver%") do (set swver=%%i)
for /F "tokens=1 delims=," %%i in ("%swver%") do (set swver=%%i)

echo JX1701U:%swver%> uvcversion.txt

exit

:notfind
::echo notfind

echo. > uvcversion.txt

exit

