BankChallenge - Banco Digital em Java

🚀 Um sistema de banco digital desenvolvido com Java e PostgreSQL

📌 Descrição

O BankChallenge é um sistema de banco digital desenvolvido em Java utilizando POO (Programação Orientada a Objetos), com persistência de dados no PostgreSQL.

O sistema permite a criação de contas bancárias, realização de depósitos, saques e transferências, além da exibição de extratos. Todos os dados são armazenados em um banco de dados relacional, garantindo integridade e segurança das transações.

🛠 Tecnologias Utilizadas
Java 17
PostgreSQL
pgAdmin 4
JDBC (Java Database Connectivity)
Maven
Git e GitHub

📌 Funcionalidades Implementadas

✅ Criar conta bancária (corrente, poupança e salário)
✅ Depositar valores na conta
✅ Sacar valores da conta
✅ Transferir valores entre contas da instituição
✅ Exibir saldo da conta
✅ Consultar extrato bancário
✅ Validação de CPF e senha
✅ Persistência de dados com PostgreSQL

⚛️ Conceitos de POO Aplicados

🔹 Abstração
Os objetos do sistema representam entidades reais, como User, Account e Transaction, focando apenas nos atributos e métodos essenciais.

🔹 Encapsulamento
Os atributos das classes são privados e acessados via métodos getters e setters, garantindo segurança e integridade dos dados.

🔹 Herança
A estrutura de contas utiliza herança para compartilhar comportamentos comuns, permitindo especializações para diferentes tipos de contas.

🔹 Polimorfismo
Métodos genéricos para operações como deposit() e withdraw() são sobrescritos conforme a necessidade de cada tipo de conta.

💾 Modelagem do Banco de Dados
O sistema utiliza um banco de dados relacional no PostgreSQL, com o seguinte esquema ER:

🔹 Entidades principais:

Usuários (users) → Representam os clientes do banco

Contas (accounts) → Contas bancárias vinculadas a um usuário

Transações (transactions) → Registro de operações financeiras

⚙️ Como Executar o Projeto

🔹 Pré-requisitos
Antes de rodar a aplicação, certifique-se de ter instalado:

Java 17

PostgreSQL

pgAdmin 4

Maven


🔹 Passos para execução

1️⃣ Clone o repositório

git clone https://github.com/matheusfredo/bankchallenge.git

2️⃣ Configure o banco de dados

Crie um banco de dados no PostgreSQL chamado bank_system

Importe o esquema SQL disponível em /database/schema.sql


DB_HOST=localhost
DB_PORT=5432
DB_NAME=bank_system
DB_USER=seu_usuario
DB_PASSWORD=sua_senha
4️⃣ Compile e execute o programa

mvn clean install
java -jar target/bankchallenge.jar

📜 Exemplo de Uso
Após executar o programa, o menu interativo permite realizar operações:

========= Main Menu =========
1. Login
2. Account Opening
0. Exit

=============================
Choose an option:

Após login, o usuário pode realizar transações:

========= Bank Menu ========
1. Deposit
2. Withdraw
3. Check Balance
4. Transfer
5. Bank Statement
0. Exit
=============================
   Choose an option:

✍️ Autor
Matheus Fredo Alves
💼 Desenvolvedor Java e Front-end
📩 Contato: matheusfredo16@outlook.com
📌 LinkedIn: /matheusfredo