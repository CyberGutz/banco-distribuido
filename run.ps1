start rmiregistry
sleep 2

set CLASSPATH=lib/json.jar

javac src/API.java 
javac src/Client.java  
javac src/Server.java

#Inicia server em outro terminal
Start-Process -FilePath "cmd.exe" -ArgumentList "/k java src/Server"
#A mimir alguns segundos pro server iniciar a tempo do client abrir
sleep 2
#Inicia client em outro terminal
Start-Process -FilePath "cmd.exe" -ArgumentList "/k java src/Client"