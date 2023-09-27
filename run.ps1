start rmiregistry
sleep 2

javac API.java 
javac Client.java  
javac Server.java

#Inicia server em outro terminal
Start-Process -FilePath "cmd.exe" -ArgumentList "/k java Server"
#A mimir alguns segundos pro server iniciar a tempo do client abrir
sleep 2
#Inicia client em outro terminal
Start-Process -FilePath "cmd.exe" -ArgumentList "/k java Client"