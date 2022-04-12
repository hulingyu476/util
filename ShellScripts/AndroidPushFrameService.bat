rem ***set IP and push path***
set IP=192.168.47.41:5555
set DES_SYSTEM=/system/framework/
set DES_SYSTEM_OAT=/system/framework/oat/arm/
set ORIGIN_PATH=C:\Users\Administrator\Desktop\service\service\

rem ***print parameter***
echo IP = %IP%
echo DES_SYSTEM = %DES_SYSTEM%
echo DES_SYSTEM_OAT = %DES_SYSTEM_OAT%

rem ***connect adb and push file***
adb connect %IP%
adb root
adb remount
adb push %ORIGIN_PATH%services.jar %DES_SYSTEM%
adb push %ORIGIN_PATH%services.core.jar %DES_SYSTEM%
adb push %ORIGIN_PATH%services.jar.prof %DES_SYSTEM%

adb push %ORIGIN_PATH%services.art  %DES_SYSTEM_OAT%
adb push %ORIGIN_PATH%services.odex %DES_SYSTEM_OAT%
adb push %ORIGIN_PATH%services.vdex %DES_SYSTEM_OAT%

rem ***reboot system***
adb reboot

pause