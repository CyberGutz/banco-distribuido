if($args -contains "-h"){
    echo "-r ---- Reconstroi o projeto novamente com o maven"
    echo "-c ---- Inicia somente o cliente"
    echo "-s ---- Inicia somente o servidor"
    echo "Podem ser usados em conjuntos, se -c ou -s nao forem passados, o cliente e o servidor sera iniciado "
    return
}

if (-not (Test-Path -Path "target" -PathType Container) -or $args -contains "-r"){
    ./mvnw clean install -q
}

if ($args -contains "-c"){
    Start-Process -FilePath "cmd.exe" -ArgumentList '/k mvnw exec:java -q -D"exec.mainClass"="Client"'
} elseif ($args -contains "-s"){
    Start-Process -FilePath "cmd.exe" -ArgumentList '/k mvnw exec:java -q -D"exec.mainClass"="Server.Server"'
} else {
    Start-Process -FilePath "cmd.exe" -ArgumentList '/k mvnw exec:java -q -D"exec.mainClass"="Server.Server"'
    Start-Sleep 2
    Start-Process -FilePath "cmd.exe" -ArgumentList '/k mvnw exec:java -q -D"exec.mainClass"="Client"'
}