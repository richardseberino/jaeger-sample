# build de SrpingBootAdmin
cd eclipse-workspace/SpringBootAdmin
./mvnw package -Dmaven.test.skip=true
cd target
cp *.jar ../springAdmin/app.jar
cd ..

# build de conta corrente
cd eclipse-workspace/conta-corrente
./mvnw package -Dmaven.test.skip=true
cd target
cp *.jar ../conta-corrente/app.jar
cd ..

# build de saldoKafka
cd eclipse-workspace/saldoKafka
./mvnw package -Dmaven.test.skip=true
cd target
cp *.jar ../saldoKafka/app.jar
cd ..

# build de senha
cd eclipse-workspace/senha
./mvnw package -Dmaven.test.skip=true
cd target
cp *.jar ../senha/app.jar
cd ..

# build de senhaKafka
cd eclipse-workspace/senhaKafka
./mvnw package -Dmaven.test.skip=true
cd target
scp *.jar ../senhaKafka/app.jar
cd ..

# build de transferencia
cd /Users/richardmarques/eclipse-workspace/transferencia
./mvnw package -Dmaven.test.skip=true
cd target
cp *.jar ../transferencia/app.jar
cd ..
