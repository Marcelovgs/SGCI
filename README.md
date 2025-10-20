SGCI - Sistema de Gerenciamento de Chamados Industriais
<p align="center"> <img src="https://img.shields.io/badge/Java-17-blue?logo=java" alt="Java 17"> <img src="https://img.shields.io/badge/Spring_Boot-3.x-green?logo=spring" alt="Spring Boot"> <img src="https://img.shields.io/badge/React-19-blue?logo=react" alt="React 19"> <img src="https://img.shields.io/badge/PostgreSQL-15-blue?logo=postgresql" alt="PostgreSQL"> <img src="https://img.shields.io/badge/Status-Concluído-brightgreen" alt="Status Concluído"> </p>

📖 Sobre o Projeto
O SGCI é uma aplicação full-stack que simula um sistema de gerenciamento de chamados de manutenção para ambientes industriais. A plataforma permite que diferentes tipos de usuários (Solicitantes, Técnicos e Admins) interajam com o sistema para abrir, gerenciar e resolver ordens de serviço, com foco em equipamentos como câmeras de segurança.

Este projeto foi construído do zero, com uma arquitetura que separa o backend (API REST) do frontend (Single-Page Application), seguindo as melhores práticas de segurança, organização de código e experiência do usuário.

✨ Funcionalidades Principais
🔐 Sistema de Autenticação e Permissão Multi-Role com JWT:

Três níveis de acesso distintos: SOLICITANTE, TECNICO e ADMIN.

Autenticação segura baseada em tokens JWT.

Rotas de frontend e endpoints de backend protegidos de acordo com o papel do usuário.

🎫 Ciclo de Vida Completo de Chamados:

Criação: Usuários SOLICITANTE podem abrir novos chamados.

Gerenciamento: Usuários TECNICO podem visualizar a fila, se atribuir a um chamado, adicionar atualizações ao histórico e marcar o chamado como concluído.

Preservação de Histórico: O sistema desvincula os chamados de equipamentos excluídos, mantendo a integridade do histórico para auditoria.

⚙️ Painel de Administração (ADMIN):

CRUD completo de Equipamentos: O admin pode criar, listar, editar e excluir equipamentos do sistema através da interface.

Lógica de Negócio Avançada: O sistema impede a exclusão de um equipamento se ele possuir chamados ativos.

💻 Interface Dinâmica e Reativa com React:

Dashboards Condicionais: A tela principal se adapta, mostrando informações e ações diferentes com base no papel do usuário logado.

Navegação Protegida: Utilização de React Router para criar rotas privadas.

Atualização em Tempo Real: A interface é atualizada instantaneamente após ações do usuário, sem a necessidade de recarregar a página.

🛠️ Tecnologias Utilizadas
Backend (Java)
Java 17

Spring Boot 3

Spring Web

Spring Data JPA

Spring Security

JWT (JSON Web Tokens) para autenticação

Maven para gerenciamento de dependências

Hibernate como implementação do JPA

Lombok

Frontend (React)
React 19

Vite como ferramenta de build

Tailwind CSS para estilização

React Router DOM para roteamento

Axios para comunicação com a API

npm / Yarn para gerenciamento de pacotes

Banco de Dados
PostgreSQL

Ferramentas de Desenvolvimento
IntelliJ IDEA

Git & GitHub

Postman / Insomnia

pgAdmin 4

🚀 Como Executar o Projeto
Pré-requisitos:

Java 17 (ou superior)

Node.js (versão 18 ou superior)

Maven

PostgreSQL

Backend
Abra a pasta raiz do projeto (sgci) no IntelliJ IDEA.

Crie um banco de dados no PostgreSQL chamado sgci_db.

Configure suas credenciais do PostgreSQL no arquivo src/main/resources/application.properties.

Execute a classe principal SgciApplication.java. O servidor iniciará na porta 8080.

Frontend
Abra um terminal e navegue até a pasta do frontend: cd frontend-sgci.

Instale as dependências: npm install (ou yarn).

Inicie o servidor de desenvolvimento: npm run dev (ou yarn dev).

Abra o navegador e acesse http://localhost:5173 (ou a porta indicada no terminal).

Autor: Marcelo Varela
