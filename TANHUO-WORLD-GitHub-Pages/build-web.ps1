param([string]$JavaHome = "$env:USERPROFILE\.jdks\openjdk-26.0.1")

$ErrorActionPreference = 'Stop'
$classes = Join-Path $PSScriptRoot '.web-classes'
$site = Join-Path $PSScriptRoot 'site'
$sources = @(Get-ChildItem -LiteralPath (Join-Path $PSScriptRoot 'src') -Recurse -Filter '*.java' -File |
    Select-Object -ExpandProperty FullName)

if ($sources.Count -eq 0) { throw 'No Java source files were found.' }
New-Item -ItemType Directory -Path $classes -Force | Out-Null

& (Join-Path $JavaHome 'bin\javac.exe') --release 17 -encoding UTF-8 -d $classes @sources
if ($LASTEXITCODE -ne 0) { throw 'Java compilation failed.' }

& (Join-Path $JavaHome 'bin\jar.exe') --create --file (Join-Path $site 'tanhuo-world.jar') --main-class tanhuo.WebMain -C $classes .
if ($LASTEXITCODE -ne 0) { throw 'JAR creation failed.' }

Write-Host "Updated $(Join-Path $site 'tanhuo-world.jar')"
