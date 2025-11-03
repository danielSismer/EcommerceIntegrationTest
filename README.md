# JDBC E-commerce System

Sistema completo de e-commerce para praticar JDBC com testes de integração.

## Estrutura do Projeto

\`\`\`
src/
├── main/java/com/ecommerce/
│   ├── config/
│   │   └── DatabaseConfig.java          # Configuração de conexão
│   ├── model/
│   │   ├── Product.java                 # Modelo de Produto
│   │   ├── Customer.java                # Modelo de Cliente
│   │   ├── Order.java                   # Modelo de Pedido
│   │   └── OrderItem.java               # Modelo de Item do Pedido
│   ├── repository/
│   │   ├── ProductRepository.java       # Interface do Repository
│   │   ├── CustomerRepository.java
│   │   ├── OrderRepository.java
│   │   ├── OrderItemRepository.java
│   │   └── impl/
│   │       ├── ProductRepositoryImpl.java    # Implementação JDBC
│   │       ├── CustomerRepositoryImpl.java
│   │       ├── OrderRepositoryImpl.java
│   │       └── OrderItemRepositoryImpl.java
│   └── service/
│       ├── ProductService.java          # Interface do Service
│       ├── CustomerService.java
│       ├── OrderService.java
│       └── impl/
│           ├── ProductServiceImpl.java  # Implementação com lógica de negócio
│           ├── CustomerServiceImpl.java
│           └── OrderServiceImpl.java
├── test/java/com/ecommerce/
│   └── EcommerceIntegrationTest.java    # Testes de integração
└── scripts/
    ├── create-tables.sql                # Script de criação de tabelas
    └── seed-data.sql                    # Script de dados iniciais
\`\`\`

## Funcionalidades

### Products (Produtos)
- ✅ CRUD completo
- ✅ Busca por categoria
- ✅ Busca por faixa de preço
- ✅ Gerenciamento de estoque

### Customers (Clientes)
- ✅ CRUD completo
- ✅ Busca por email
- ✅ Validação de email único

### Orders (Pedidos)
- ✅ Criação de pedidos com múltiplos itens
- ✅ Cálculo automático de totais
- ✅ Atualização de status
- ✅ Cancelamento com restauração de estoque
- ✅ Busca por cliente
- ✅ Busca por status

## Como Executar

### 1. Compilar o projeto
\`\`\`bash
mvn clean compile
\`\`\`

### 2. Executar os testes
\`\`\`bash
mvn test
\`\`\`

### 3. Executar a classe de teste de integração
\`\`\`bash
mvn exec:java -Dexec.mainClass="com.ecommerce.EcommerceIntegrationTest"
\`\`\`

## Testes de Integração

A classe `EcommerceIntegrationTest` executa os seguintes testes:

1. **Product Operations**: CRUD de produtos, busca por categoria e preço
2. **Customer Operations**: CRUD de clientes, busca por email
3. **Order Creation**: Criação de pedidos com múltiplos itens
4. **Order Status Update**: Atualização de status do pedido
5. **Order Cancellation**: Cancelamento com restauração de estoque
6. **Stock Management**: Gerenciamento de estoque
7. **Complex Queries**: Consultas complexas com joins
8. **Error Handling**: Validação de erros e exceções

## Tecnologias

- **Java 11**
- **JDBC** (puro, sem ORM)
- **H2 Database** (banco em memória)
- **Maven** (gerenciamento de dependências)

## Conceitos JDBC Praticados

- ✅ Connection management
- ✅ PreparedStatement (prevenção de SQL injection)
- ✅ ResultSet manipulation
- ✅ Transaction management
- ✅ Auto-generated keys
- ✅ Batch operations
- ✅ Exception handling
- ✅ Resource management (try-with-resources)

## Próximos Passos para Praticar

1. Adicionar transações explícitas nos services
2. Implementar paginação nas consultas
3. Adicionar mais validações de negócio
4. Criar relatórios complexos com JOINs
5. Implementar soft delete
6. Adicionar auditoria (created_by, updated_by)
