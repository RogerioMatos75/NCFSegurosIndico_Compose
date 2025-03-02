# NCF Seguros Indico - Aplicativo de Indicações

## Sobre o Projeto

O NCF Seguros Indico é um aplicativo Android desenvolvido para facilitar e incentivar a indicação de novos clientes para seguros de automóveis. O aplicativo permite que segurados indiquem amigos e familiares interessados em contratar seguros, recebendo descontos acumulativos na renovação de suas próprias apólices.

## Funcionalidades

### Para Segurados
- **Cadastro e Login**: Autenticação segura com Firebase Auth
- **Perfil de Usuário**: Gerenciamento de informações pessoais
- **Indicação de Amigos**: Formulário simples para indicar potenciais clientes
- **Acompanhamento de Indicações**: Visualização do status de todas as indicações feitas
- **Desconto Acumulativo**: Visualização do desconto acumulado para renovação do seguro

### Para Administradores
- **Painel Administrativo**: Visualização de todas as indicações recebidas
- **Gerenciamento de Status**: Atualização do status das indicações (pendente, contatado, convertido, rejeitado)
- **Notificações**: Sistema de notificações para novas indicações

## Regras de Negócio

- Cada indicação gera 1% de desconto para o segurado
- Se a indicação se converter em cliente, o segurado ganha mais 1% de desconto
- O desconto máximo acumulado é de 10%
- Os descontos são aplicados na renovação da apólice do segurado

## Tecnologias Utilizadas

- **Kotlin**: Linguagem de programação principal
- **Jetpack Compose**: Framework moderno para UI declarativa
- **Firebase Auth**: Autenticação de usuários
- **Firebase Firestore**: Banco de dados NoSQL para armazenamento de dados
- **Firebase Cloud Messaging**: Sistema de notificações push
- **Hilt**: Injeção de dependência
- **MVVM**: Arquitetura Model-View-ViewModel
- **Coroutines e Flow**: Programação assíncrona

## Estrutura do Projeto

- **model**: Classes de dados
- **repository**: Camada de acesso a dados
- **viewmodel**: Lógica de negócios e gerenciamento de estado
- **ui**: Componentes de interface do usuário
  - **screens**: Telas do aplicativo
  - **components**: Componentes reutilizáveis
  - **navigation**: Navegação entre telas
- **service**: Serviços em background
- **worker**: Workers para tarefas periódicas

## Configuração do Projeto

1. Clone o repositório
2. Abra o projeto no Android Studio
3. Configure o Firebase:
   - Crie um projeto no Firebase Console
   - Adicione um aplicativo Android com o pacote `com.ncf.seguros.indico`
   - Baixe o arquivo `google-services.json` e coloque-o na pasta `app/`
4. Execute o aplicativo em um emulador ou dispositivo físico

## Contribuição

Para contribuir com o projeto, siga estas etapas:

1. Faça um fork do repositório
2. Crie uma branch para sua feature (`git checkout -b feature/nova-funcionalidade`)
3. Faça commit das suas alterações (`git commit -m 'Adiciona nova funcionalidade'`)
4. Faça push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

## Licença

Este projeto está licenciado sob a licença MIT - veja o arquivo LICENSE para mais detalhes.

## Contato

NCF Seguros - [contato@ncfseguros.com.br](mailto:contato@ncfseguros.com.br) 