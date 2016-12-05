@ECHO OFF
COLOR 70
title "B-zipa 1.0" - 批次Zipalign工具 


ECHO.
ECHO 請把要處理的APK檔放在Input資料夾裡，輸出的APK檔存放至Output資料夾。
ECHO.
ECHO ＃＃＃注意!!! APK檔名中請勿存在空格，建議用底線「_」取代空格＃＃＃
ECHO.

pause

FOR %%A in (.\Input\*.apk) do (
ECHO Zipaligning - %%~nA.apk
.\Tools\zipalign -cv 4 %%A 
.\Tools\zipalign -fv 4 %%A .\Output\%%~nA.apk
)

ECHO.
ECHO 已成功完成處理...
ECHO.
ECHO 希望您喜歡...:)
ECHO Ryuinferno @ XDA 2013, 繁體By TN-Man
ECHO.

pause

