@ECHO OFF
COLOR 70
title "B-zipa 1.0" - �妸Zipalign�u�� 


ECHO.
ECHO �Ч�n�B�z��APK�ɩ�bInput��Ƨ��̡A��X��APK�ɦs���Output��Ƨ��C
ECHO.
ECHO �������`�N!!! APK�ɦW���ФŦs�b�Ů�A��ĳ�Ω��u�u_�v���N�Ů桭����
ECHO.

pause

FOR %%A in (.\Input\*.apk) do (
ECHO Zipaligning - %%~nA.apk
.\Tools\zipalign -cv 4 %%A 
.\Tools\zipalign -fv 4 %%A .\Output\%%~nA.apk
)

ECHO.
ECHO �w���\�����B�z...
ECHO.
ECHO �Ʊ�z���w...:)
ECHO Ryuinferno @ XDA 2013, �c��By TN-Man
ECHO.

pause

