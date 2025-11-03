# ecommerce-integration-tests

## Descrição
Repositório com testes de integração para uma aplicação Java (Maven). Contém testes em `src/test/java/com/ecommerce/EcommerceIntegrationTest.java` e scripts SQL para criação e carregamento de dados.

## Requisitos
\- Java 11+  
\- Maven 3.6+  
\- Um SGBD (ex.: PostgreSQL, MySQL ou H2) para executar os scripts SQL

## Instalação
\- Clonar o repositório:  
  `git clone <URL-do-repositório>`  
\- Entrar na pasta do projeto:  
  `cd ecommerce-integration-tests`

## Banco de dados
\- Executar `scripts/create-tables.sql` para criar as tabelas necessárias.  
\- Executar `scripts/seed-data.sql` para inserir dados de exemplo (clientes e produtos).  
\- Ajustar as configurações de conexão no `src/test/resources` ou nas variáveis de ambiente conforme o profile do projeto.

## Executar testes de integração
\- Limpar e executar todos os testes:  
  `mvn clean test`  
\- Executar apenas a classe de integração (exemplo):  
  `mvn -Dtest=EcommerceIntegrationTest test`  
\- Se o projeto usar o Failsafe para integração, executar:  
  `mvn clean verify`

## Estrutura relevante
\- `src/test/java/com/ecommerce/EcommerceIntegrationTest.java`  
\- `scripts/create-tables.sql`  
\- `scripts/seed-data.sql`  
\- `/.gitignore`

## Mensagem de commit sugerida
`feat(integration): adicionar testes de integração e scripts de banco`

## Observações
\- O arquivo `/.gitignore` já foi atualizado para ignorar `/.idea`, `/target/` e `/package.json`.
