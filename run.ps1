if (-not (Test-Path -Path "target" -PathType Container) -or $args[0]){
    mvn clean install
}

#Inicia server em outro terminal
Start-Process -FilePath "cmd.exe" -ArgumentList "/k java -jar target/server.jar"
#A mimir alguns segundos pro server iniciar a tempo do client abrir
sleep 2
#Inicia cliente em outro terminal
Start-Process -FilePath "cmd.exe" -ArgumentList "/k java -jar target/cliente.jar"