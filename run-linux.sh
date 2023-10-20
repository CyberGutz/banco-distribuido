#!/bin/bash
if [[ ! -d "./target" || "$1" == "-r" ]]; #Verifica se existe a pasta target ou se o usuario passou uma flag de rebuild para buildar o projeto
then
mvn clean install
fi

xterm -hold -e java -jar target/server.jar &
sleep 2
xterm -hold -e java -jar target/cliente.jar &