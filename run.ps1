start rmiregistry

javac API.java 
javac Client.java  
javac Server.java

#Inicia server em outro terminal
Start-Process -FilePath "cmd.exe" -ArgumentList "/k java Server"