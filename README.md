# Sistema De Assembléias

Desafio para o processo de contratação da IBM.

## Detalhes

- A resolução do problema foi baseada em uma estrutura de clean arch, demostrada com mais detalhe posteriormente.
- A solução está disponível em heroku: https://ibm-cooperativism.herokuapp.com/
- A documentação das Api's esta disponível em: https://matheussn.s3.sa-east-1.amazonaws.com/ibm-cooperativism/index.html
- Caso os testes sejam utilizados no ambiente do Heroku, temos que considerar um tempo a mais na resposta nas primeiras requisições, visto que o mesmo "desliga" a aplicação após certo período.

## Heroku

- As URL's da Api no ambiente do heroku estão listadas abaixo
    - POST https://ibm-cooperativism.herokuapp.com/v1/agenda
    - POST https://ibm-cooperativism.herokuapp.com/v1/agenda/{agenda-id}/session
    - POST https://ibm-cooperativism.herokuapp.com/v1/agenda/{agenda-id}/vote
    - GET https://ibm-cooperativism.herokuapp.com/v1/agenda/{agenda-id}/result

## Arquitetura
A estrutura de pastas foi baseada no Clean Arch, com o foco no domínio. Sendo assim, ao criar um novo domínio, uma nova estrutura de pastas é criada, com seu modelo (ou entidade), controladores (ou handlers), serviços (use-cases) e seu repositório (ou dao).

Neste projeto a estrutura de pastas segue esta linha:
```
cooperativism/
└── agenda
    ├── Agenda.kt
    ├── controller
    │   ├── AgendaController.kt
    │   ├── request
    │   │   └── CreateAgendaRequest.kt
    │   └── response
    │       └── CreateAgendaResponse.kt
    ├── repository
    │   └── AgendaRepository.kt
    └── service
        ├── AgendaService.kt
        ├── converters
        │   └── AgendaConverters.kt
        └── impl
            └── AgendaServiceImpl.kt

```

## Desenvolvimento

### Pré-requisitos

Este projeto usa Kotlin na versão 1.5.10, com Java na versão 11.

### Dependências
Rodar Banco localmente:
```shell
docker-compose up -d
```

Instalando dependencias:
```shell
mvn clean install -DskipTests
```

### Iniciar Aplicação
```shell
mvn spring-boot:run
```

### Rodar testes

```shell
mvn test
```