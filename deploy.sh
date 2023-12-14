rm -r deploy
mkdir deploy
cp ./target/cliente.jar ./deploy
cp ./target/server.jar ./deploy
cp ./protocolos.xml ./deploy
cp ./run.sh ./deploy
cp ./log4j.xml ./deploy
cp ./users.json ./deploy
cp ./transferencias.json ./deploy
cp ./versao.txt ./deploy