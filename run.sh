#!/bin/bash
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


if [[ "$@" =~ "-s" ]];
    then
    xterm -hold -e "java -jar server.jar $IPV4 2>/dev/null" &
elif [[ "$@" =~ "-c" ]];
    then
    xterm -hold -e "java -jar cliente.jar $IPV4" &
else
    xterm -hold -e "java -jar server.jar $IPV4 2>/dev/null" &
    sleep 2
    xterm -hold -e "java -jar cliente.jar $IPV4" &
fi
