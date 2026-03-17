$ErrorActionPreference = "Stop"

Write-Host "Renaming directory..."
if (Test-Path "app\src\main\java\com\ios7\wallify") {
    Rename-Item -Path "app\src\main\java\com\ios7\wallify" -NewName "vibeify"
}

Write-Host "Updating app/build.gradle..."
$buildGradle = Get-Content "app\build.gradle" -Raw
$buildGradle = $buildGradle -creplace 'com\.ios7\.wallify', 'com.ios7.vibeify'
$buildGradle = $buildGradle -creplace 'wallify', 'vibeify'
$buildGradle | Set-Content "app\build.gradle" -NoNewline

Write-Host "Updating source files in app/src/main..."
Get-ChildItem -Path "app\src\main" -File -Recurse | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    $modified = $false

    if ($content -match 'com\.ios7\.wallify') {
        $content = $content -creplace 'com\.ios7\.wallify', 'com.ios7.vibeify'
        $modified = $true
    }
    if ($content -match 'Wallify') {
        $content = $content -creplace 'Wallify', 'Vibeify'
        $modified = $true
    }
    if ($content -match 'wallify') {
        $content = $content -creplace 'wallify', 'vibeify'
        $modified = $true
    }

    if ($modified) {
        $content | Set-Content $_.FullName -NoNewline
        Write-Host "Updated $($_.FullName)"
    }
}

Write-Host "Updating JSON files in root..."
Get-ChildItem -Path "." -Filter "*.json" -File | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    $modified = $false

    if ($content -match 'Wallify') {
        $content = $content -creplace 'Wallify', 'Vibeify'
        $modified = $true
    }
    if ($content -match 'wallify') {
        $content = $content -creplace 'wallify', 'vibeify'
        $modified = $true
    }
    
    if ($modified) {
        $content | Set-Content $_.FullName -NoNewline
        Write-Host "Updated $($_.FullName)"
    }
}

Write-Host "Done!"
