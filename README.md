Atividade 3 Laboratório de Banco de Dados
Prof. Leandro Colevati FATEC-ZL
No sistema acadêmico AGIS, se permite fazer o encerramento de semestre.
A partir da relação de matrícula, criar uma tela que permita inserir as notas parciais das 
disciplinas. Para verificar o funcionamento da funcionalidade, utilizar as seguintes disciplinas
(Os códigos podem ser ajustados conforme projeto já em desenvolvimento):
Código Turno Disciplina Avaliações
4203-010 T Arquitetura e Organização de Computadores P1 (Peso 0,3)
P2 (Peso 0,5)
T (Peso 0,2)
4203-020 N Arquitetura e Organização de Computadores
4208-010 T Laboratório de Hardware
4226-004 T Banco de Dados
4213-003 T Sistemas Operacionais I P1 (Peso 0,35)
P2 (Peso 0,35)
T (Peso 0,3)
4213-013 N Sistemas Operacionais I
4216-013 N Estruturas de Dados
4233-005 T Laboratório de Banco de Dados P1, P2 e P3
(Peso 0,333333...)
5005-220 T Metodologia de Pesquisa Científico Tecnológica
Artigo Resumido
(Peso 0,2)
Monografia
(Peso 0,8)
Não estão contemplados, neste momento, avaliações substitutivas ou recuperações.
Na tela de consulta de notas, deve-se apresentar as notas parciais e as médias calculadas 
de acordo com os pesos.
Deve-se apresentar, nesta tela, também, um status:
• Aprovado  Média >= 6,0
• Exame  Média >= 3,0 e Média <= 6,0
• Reprovado  Média < 3,0
Com base nos lançamentos das faltas, deve-se exibir, em uma tela, uma tabela com a lista 
dos alunos matriculados nas disciplinas e, por semana, a quantidade de faltas. A 
penúltima coluna deve ser o total de faltas do aluno. A última coluna deve ser o status do 
aluno (Reprovado para frequência inferior a 75% do total de aulas do semestre e Aprovado 
para frequência superior a 75% do total de aulas do semestre). A tela deve ser montada a 
partir da saída de uma UDF com cursor.
Deve-se gerar uma tela relatório que permita gerar um PDF de saída semelhante à tela de 
notas e um PDF de saída semelhante à tela de consulta de frequências.
Para a avaliação deve se fazer, utilizando Java Web (Spring Web, Spring Data, JSP e JSTL), 
com SQL Server:
Um protótipo do AGIS:
• A atualização da modelagem do sistema pedido, com as diagramações 
pertinentes;
• Adequar o sistema para implementar os módulos das UDFs
• Aplicar os gatilhos em situações que se apresentem como necessárias
Serão avaliados, além da solução do código:
A qualidade do desenvolvimento e as boas práticas
A qualidade da usabilidade do sistema pelo usuário
A qualidade da modelagem do sistema
Atividade 3 Laboratório de Banco de Dados
Prof. Leandro Colevati FATEC-ZL
O projeto deve ser carregado no Github e o link encaminhado na tarefa correspondente no 
prazo estipulado.
O projeto será apresentado, rodando e questionamentos sobre o código, ao professor na 
aula determinada. A apresentação sem o projeto entregue antecipadamente na tarefa 
incorre em desconto da avaliação.
As modelagens devem estar em uma pasta chamada doc na pasta WEB-INF do projeto Java 
Web.
Códigos Java ou SQL com semelhanças geram grandes descontos
