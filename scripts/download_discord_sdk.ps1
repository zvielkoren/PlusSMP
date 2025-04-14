$ErrorActionPreference = "Stop"

# Create directories if they don't exist
if (-not (Test-Path "scripts")) {
    New-Item -ItemType Directory -Path "scripts"
}
if (-not (Test-Path "libs")) {
    New-Item -ItemType Directory -Path "libs"
}

# Download Discord Game SDK
$sdkUrl = "https://dl-game-sdk.discordapp.net/2.5.6/discord_game_sdk.zip"
$zipPath = "scripts/discord_game_sdk.zip"
$extractPath = "scripts/discord_sdk"

# Download the SDK
Write-Host "Downloading Discord Game SDK..."
Invoke-WebRequest -Uri $sdkUrl -OutFile $zipPath

# Extract the SDK
Write-Host "Extracting Discord Game SDK..."
Expand-Archive -Path $zipPath -DestinationPath $extractPath -Force

# Copy the JAR and DLL to the appropriate locations
$jarPath = Join-Path $extractPath "lib/x86_64/discord_game_sdk.jar"
$dllPath = Join-Path $extractPath "lib/x86_64/discord_game_sdk.dll"

Copy-Item -Path $jarPath -Destination "libs/" -Force
Copy-Item -Path $dllPath -Destination "." -Force

# Clean up
Remove-Item -Path $zipPath -Force
Remove-Item -Path $extractPath -Recurse -Force

Write-Host "Discord Game SDK setup complete!" 