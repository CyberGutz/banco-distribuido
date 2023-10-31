#!/bin/bash

if [[ "$@" =~ "-h" ]]; then
    echo "-r ---- Reconstroi o projeto novamente com o maven"
    echo "-c ---- Inicia somente o cliente"
    echo "-s ---- Inicia somente o servidor"
    echo "Podem ser usados em conjuntos, se -c ou -s nao forem passados, o cliente e o servidor sera iniciado "
    exit
fi

if [[ ! -d "./target" || "$1" == "-r" ]]; #Verifica se existe a pasta target ou se o usuario passou uma flag de rebuild para buildar o projeto
  then
    mvn clean install
fi

if [[ "$@" =~ "-s" ]];
    then
    xterm -hold -e java -jar target/server.jar &
elif [[ "$@" =~ "-c" ]];
    then
    xterm -hold -e java -jar target/cliente.jar &
else
    xterm -hold -e java -jar target/server.jar &
    sleep 2
    xterm -hold -e java -jar target/cliente.jar &
fi