@echo off
setlocal

set "MVNW_BASEDIR=%~dp0"
set "MVNW_CMD="

for /f "usebackq delims=" %%i in (`powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; $props=Get-Content -Raw -Path '%MVNW_BASEDIR%.mvn\wrapper\maven-wrapper.properties'; $url=[regex]::Match($props,'distributionUrl=(\S+)').Groups[1].Value; $name=[IO.Path]::GetFileName($url) -replace '-bin\.zip$',''; $userHome=$env:MAVEN_USER_HOME; if (-not $userHome) { $userHome=Join-Path $env:USERPROFILE '.m2' }; $dest=Join-Path $userHome ('wrapper\dists\' + $name); $mvn=Join-Path $dest ($name + '\bin\mvn.cmd'); if (-not (Test-Path $mvn)) { [Console]::Error.WriteLine('Downloading ' + $name + ' ...'); New-Item -ItemType Directory -Force -Path $dest | Out-Null; [Net.ServicePointManager]::SecurityProtocol=[Net.SecurityProtocolType]::Tls12; $zip=Join-Path $env:TEMP ($name + '-bin.zip'); Invoke-WebRequest -UseBasicParsing -Uri $url -OutFile $zip; Expand-Archive -Path $zip -DestinationPath $dest -Force; Remove-Item $zip }; Write-Output $mvn"`) do set "MVNW_CMD=%%i"

if not defined MVNW_CMD (
  echo Cannot download or find Maven. 1>&2
  exit /b 1
)

"%MVNW_CMD%" %*
exit /b %ERRORLEVEL%
