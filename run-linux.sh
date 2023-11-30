#!/bin/bash
#CONFIG="-Djgroups.bind_addr=172.16.2.222 -Djava.net.preferIPv4Stack=true"

if [[ "$@" =~ "-h" ]]; then
    echo "-r ---- Reconstroi o projeto novamente com o maven"
    echo "-c ---- Inicia somente o cliente"
    echo "-s ---- Inicia somente o servidor"
    echo "Podem ser usados em conjuntos, se -c ou -s nao forem passados, o cliente e o servidor sera iniciado "
    exit
fi

if [[ ! -d "./target" || "$1" == "-r" ]]; #Verifica se existe a pasta target ou se o usuario passou uma flag de rebuild para buildar o projeto
  then
    ./mvnw clean install -q
fi

if [[ "$@" =~ "-s" ]];
    then
    xterm -hold -e ./mvnw exec:java -q -Dexec.mainClass="Server.Server" &
elif [[ "$@" =~ "-c" ]];
    then
    xterm -hold -e ./mvnw exec:java -q -Dexec.mainClass="Client" &
else
    xterm -hold -e ./mvnw exec:java -q -Dexec.mainClass="Server.Server" &
    sleep 2
    xterm -hold -e ./mvnw exec:java -q -Dexec.mainClass="Client" &
fi