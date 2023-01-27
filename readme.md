# poc-gcp-pub-sub

App console simples para explorar o pub-sub da google. Inspirado no tutorial oficial:
https://cloud.google.com/pubsub/docs/publish-receive-messages-client-library?hl=pt-br

## Pré requisitos

### 1 - gerar credenciais no GCP
- https://cloud.google.com/pubsub/docs/spring?hl=pt-br
- cria um projeto poc, cria a conta de serviço e baixa as credenciais em JSON.

### 2 - salvar as credenciais
- \src\main\resources\pub-sub-credential.json

---------------------------------------------------------
## rodar pelo console

```bash
# producer
./gradlew bootRun --args='producer'

# consumer 1
./gradlew bootRun --args='consumer1'

# consumer 2
./gradlew bootRun --args='consumer2'
```


---
## Como esse projeto foi criado?

### 1 - instalar o JDK 11.
- https://adoptium.net/temurin/releases/?version=11

### 2 - criar o projeto pelo spring initializr
- https://start.spring.io/
- java 11
- springboot 2.7.7