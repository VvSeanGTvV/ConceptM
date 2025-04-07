set stage=0
set builded=0
set copied=0
@echo off


:START
cls
title Gradlew Auto Run w/ Modified gradlew.bat
echo Gradlew Auto Run w/ Modified gradlew.bat
echo Created by VvSeanGtvV [ 2.12J ]
if %builded%==0 echo ^> :buildJar [ PROGRESS ]
if %stage%==0 goto buildJar
if %builded%==1 echo ^> :buildJar [ COMPLETE ]
if %copied%==0 echo ^> :copyJar  [ PROGRESS ]
if %stage%==1 goto copyJar
if %copied%==1 echo ^> :copyJar  [ COMPLETE ]
if %stage%==2 goto executeMindustry

:buildJar
echo > :buildJar Started
title [Gradlew]
start /wait gradlew jar
set stage=1
set builded=1
goto START

:copyJar
set modsLocation=%APPDATA%\Mindustry\mods
echo set modsLocation=%modsLocation%

cd build\libs\
copy ConceptMDesktop.jar %modsLocation%
if exist ConceptMDesktop.jar del ConceptMDesktop.jar
cd ..\..\

set stage=2
set copied=1
goto START

:executeMindustry
title Mindustry
set mindustry=C:\Users\gombo\Downloads\Mindustry-BE-Desktop-25706.jar
echo autorun to %mindustry%
set stage=3
%mindustry%
exit