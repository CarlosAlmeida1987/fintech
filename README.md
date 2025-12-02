# Fintech

1. Crie uma API RESTful para realizar lançamentos bancários de débito e crédito nas contas dos clientes. 

2\. Seus endpoints devem permitir as seguintes operações:

\- Realizar um lançamento de débito e crédito em uma conta específica.

\- Deve permitir mais de um lançamento na mesma requisição.

\- Obter o saldo atual de uma conta específica.

3\. Certifique-se de que a API seja thread-safe para lidar com requisições

concorrentes.

4\. Evite condições de corrida e garanta a consistência dos dados

compartilhados entre as threads.

5\. Documente a API Swagger (Springdoc), especificando os endpoints, métodos HTTP suportados,

parâmetros esperados e formatos de resposta. /swagger-ui.html - /v3/api-docs

6\. Escreva um conjunto de testes que julgar necessário utilizar Testes com JUnit 5 e Mockito.

7\. Tecnologias/frameworks: Java, Spring,Hibernate incluir banco (H2).

8\. Tratamento de erro com mensagem de retorno na API



\- Acesse a documentação:

&nbsp; - Swagger UI: http://localhost:8080/swagger-ui.html

&nbsp; - OpenAPI JSON: http://localhost:8080/v3/api-docs

Como usar os endpoints:



\- POST /api/contas/lancamentos

&nbsp; - Corpo da requisição (JSON):

&nbsp;   - numeroConta: "0001"

&nbsp;   - postagem: lista de itens com tipo ("DEBITO" ou "CREDITO") e quantia (> 0)

&nbsp; - Exemplo:

&nbsp;   {

&nbsp;   "numeroConta": "0001",

&nbsp;   "postagem": \[

&nbsp;   {"tipo":"DEBITO","quantia":100.00},

&nbsp;   {"tipo":"CREDITO","quantia":50.00}

&nbsp;   ]

&nbsp;   }

\- GET /api/contas/0001/balance

&nbsp; - Retorna:

&nbsp;   { "numeroConta": "0001", "balance": 950.00 }

Erros e mensagens:



\- 404: Conta não encontrada

\- 422: Erro de negócio (ex.: saldo insuficiente)

\- 400: Validação de parâmetros (mensagens amigáveis)

\- 500: Erro interno



curl -X 'POST' \\

&nbsp; 'http://localhost:8080/api/contas/lancamentos' \\

&nbsp; -H 'accept: application/json' \\

&nbsp; -H 'Content-Type: application/json' \\

&nbsp; -d '{

&nbsp; "numeroConta": "0001",

&nbsp; "postagens": \[

&nbsp;   {

&nbsp;     "tipo": "DEBITO",

&nbsp;     "quantia": 0.01

&nbsp;   }

&nbsp; ]

}'



curl -X 'GET' \\

&nbsp; 'http://localhost:8080/api/contas/0001/balance' \\

&nbsp; -H 'accept: application/json'



