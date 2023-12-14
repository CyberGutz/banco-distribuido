#!/bin/bash
#CONFIG="-Djgroups.bind_addr=172.16.2.222 -Djava.net.preferIPv4Stack=true"

#IPV4="-Djgroups.bind_addr=172.16.2.222 -Djava.net.preferIPv4Stack=true"
# necessário forçar IPv4 para funcionar na rede cabeada dos laboratórios do IFMG  
IPV4='-Djava.net.preferIPv4Stack="true"'

# necessário no L.A.R. caso existam muitas interfaces virtuais de rede com nomes iniciados com br-, docker0, etc
## sudo apt install net-tools 
get_IP_of_interface(){
    if [[ -n $1 ]]; then 
        ifconfig | grep "$1" -A5 | grep 'inet ' | sed 's/ *inet //; s/\/.*//; s/ .*//; s/[ \t]*//'
    else 
        ifconfig | grep -E "(eno1|eth0|en0)" -A5 | grep 'inet ' | sed 's/ *inet //; s/\/.*//; s/ .*//; s/[ \t]*//'
        #ifconfig | grep 'inet ' | grep '172.22.70.' | sed 's/.*inet //; s/\/27 .*//'
    fi
}
MEU_IP="$(get_IP_of_interface eno1)"
IPV4="$IPV4 -Djgroups.bind_addr=\"$MEU_IP\""
echo $IPV4 

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
    xterm -hold -e './mvnw exec:java -q -Dexec.mainClass="Server.Server" '"$IPV4"' 2> /dev/null' &
elif [[ "$@" =~ "-c" ]];
    then
    xterm -hold -e ./mvnw exec:java -q -Dexec.mainClass="Client" -Djava.net.preferIPv4Stack="true" &
else
    #xterm -hold -e '(./mvnw exec:java -q -Dexec.mainClass="Server.Server" -Djava.net.preferIPv4Stack="true" 2&>1 | grep -v ADVERT) &'
    xterm -hold -e './mvnw exec:java -q -Dexec.mainClass="Server.Server" '"$IPV4"' 2> /dev/null' &
    #xterm -hold -e ./mvnw exec:java -q -Dexec.mainClass="Server.Server" -Djava.net.preferIPv4Stack="true" &
    sleep 2
    xterm -hold -e ./mvnw exec:java -q -Dexec.mainClass="Client" $IPV4 &
fi