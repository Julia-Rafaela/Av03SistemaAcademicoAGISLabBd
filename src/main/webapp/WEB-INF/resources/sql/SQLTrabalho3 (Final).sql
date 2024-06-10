USE master
DROP DATABASE CrudAgis

CREATE DATABASE CrudAgis
GO
USE CrudAgis

--Curso
CREATE FUNCTION fn_cursos()
RETURNS TABLE
AS
RETURN
(
    SELECT 
        c.codigo AS codigo,
        c.nome AS nome,
		c.carga_horaria AS carga_horaria,
		c.sigla AS sigla,
		c.nota_enade AS nota_enade
    FROM Curso c
);
GO
CREATE PROCEDURE GerenciarCurso (
    @op VARCHAR(10),
    @codigo INT,
    @nome VARCHAR(100),
    @carga_horaria INT,
    @sigla VARCHAR(10),
    @nota_enade DECIMAL(5, 2),
    @saida VARCHAR(100) OUTPUT
)
AS
BEGIN
 IF @codigo < 0 OR @codigo > 100
    BEGIN
        SET @saida = 'O código do curso deve estar entre 0 e 100.';
        RETURN; 
    END
    IF @op = 'I' 
    BEGIN
        IF @codigo IS NOT NULL AND @nome IS NOT NULL AND @carga_horaria IS NOT NULL AND @sigla IS NOT NULL
        BEGIN
            IF NOT EXISTS (SELECT 1 FROM Curso WHERE codigo = @codigo)
            BEGIN
                INSERT INTO Curso (codigo, nome, carga_horaria, sigla, nota_enade)
                VALUES (@codigo, @nome, @carga_horaria, @sigla, @nota_enade);
                
                SET @saida = 'Curso inserido com sucesso.';
            END
            ELSE
            BEGIN
                SET @saida = 'O código do curso já existe na base de dados.';
            END
        END
        ELSE
        BEGIN
            SET @saida = 'Parâmetros incompletos para inserção.';
        END
    END
    ELSE IF @op = 'U' 
    BEGIN
        IF @codigo IS NOT NULL AND @nome IS NOT NULL AND @carga_horaria IS NOT NULL AND @sigla IS NOT NULL
        BEGIN
            IF EXISTS (SELECT 1 FROM Curso WHERE codigo = @codigo)
            BEGIN
                UPDATE Curso
                SET nome = @nome,
                    carga_horaria = @carga_horaria,
                    sigla = @sigla,
                    nota_enade = @nota_enade
                WHERE codigo = @codigo;

                SET @saida = 'Curso atualizado com sucesso.';
            END
            ELSE
            BEGIN
                SET @saida = 'O código do curso não foi encontrado na base de dados.';
            END
        END
        ELSE
        BEGIN
            SET @saida = 'Parâmetros incompletos para atualização.';
        END
    END
    ELSE IF @op = 'D'
    BEGIN
        IF @codigo IS NOT NULL
        BEGIN
            IF EXISTS (SELECT 1 FROM Curso WHERE codigo = @codigo)
            BEGIN
                DELETE FROM Curso WHERE codigo = @codigo;
                SET @saida = 'Curso excluído com sucesso.';
            END
            ELSE
            BEGIN
                SET @saida = 'O código do curso não foi encontrado na base de dados.';
            END
        END
        ELSE
        BEGIN
            SET @saida = 'Parâmetros incompletos para exclusão.';
        END
    END
    ELSE
    BEGIN
        SET @saida = 'Operação inválida.';
    END
END;
--Aluno
GO
CREATE FUNCTION fn_alunos()
RETURNS TABLE
AS
RETURN
(
    SELECT A.ra, A.cpf, A.nome, A.nome_social, A.data_nascimento, A.email_pessoal, A.email_corporativo, A.conclusao_segundo_grau,
	A.instituicao_conclusao, A.pontuacao_vestibular, A.posicao_vestibular, A.ano_ingresso, A.ano_limite_graduacao, 
	A.semestre_ingresso, A.semestre_limite_graduacao, A.curso AS codigoCurso, C.nome AS nomeCurso 
    FROM Aluno A 
    INNER JOIN Curso C ON C.codigo = A.curso
)

GO
CREATE PROCEDURE ValidarCPF (
    @CPF CHAR(11),
    @cpfValido CHAR(10) OUTPUT
)
AS
BEGIN
    DECLARE @primeiroDigito INT;
    DECLARE @segundoDigito INT;
    DECLARE @i INT;
    DECLARE @soma INT;
    DECLARE @resto INT;

    SET @cpfValido = 'Válido';

    IF @CPF NOT LIKE '%[^0-9]%'
    BEGIN
        SET @soma = 0;
        SET @i = 10;
        WHILE @i >= 2
        BEGIN
            SET @soma = @soma + (CAST(SUBSTRING(@CPF, 11 - @i, 1) AS INT) * @i);
            SET @i = @i - 1;
        END;
        SET @resto = @soma % 11;
        SET @primeiroDigito = IIF(@resto < 2, 0, 11 - @resto);

        SET @soma = 0;
        SET @i = 11;
        SET @CPF = @CPF + CAST(@primeiroDigito AS NVARCHAR(1));
        WHILE @i >= 2
        BEGIN
            SET @soma = @soma + (CAST(SUBSTRING(@CPF, 12 - @i, 1) AS INT) * @i);
            SET @i = @i - 1;
        END;
        SET @resto = @soma % 11;
        SET @segundoDigito = IIF(@resto < 2, 0, 11 - @resto);

        IF LEN(@CPF) <> 11 OR @CPF IN ('00000000000', '11111111111', '22222222222', '33333333333', '44444444444', '55555555555', '66666666666', '77777777777', '88888888888', '99999999999')
            OR SUBSTRING(@CPF, 10, 1) != CAST(@primeiroDigito AS NVARCHAR(1)) OR SUBSTRING(@CPF, 11, 1) != CAST(@segundoDigito AS NVARCHAR(1))
        BEGIN
            SET @cpfValido = 'Inválido';
        END;
    END
    ELSE
    BEGIN
        SET @cpfValido = 'Inválido';
    END;
END;

GO
CREATE PROCEDURE validarIdade (
    @data_nascimento VARCHAR(10)
)
AS
BEGIN
    DECLARE @idade INT;
    SET @idade = DATEDIFF(YEAR, CAST(@data_nascimento AS DATE), GETDATE());
    IF @idade < 16
    BEGIN
        RAISERROR('Idade deve ser igual ou superior a 16 anos', 16, 1);
    END;
END;

GO
CREATE PROCEDURE gerarRA (
    @ano_ingresso INT,
    @semestre_ingresso INT,
    @ra VARCHAR(10) OUTPUT
)
AS
BEGIN
    DECLARE @parte_numerica VARCHAR(4);
    SET @parte_numerica = RIGHT('000' + CAST(FLOOR(RAND() * 10000) AS VARCHAR(4)), 4);
    SET @ra = CONCAT(CAST(@ano_ingresso AS VARCHAR(4)), CAST(@semestre_ingresso AS VARCHAR(2)), @parte_numerica);
END;
GO

CREATE PROCEDURE GerenciarMatricula (
    @op VARCHAR(100),
    @CPF CHAR(11),
	@ano_ingresso VARCHAR(10),
    @conclusao_segundo_grau VARCHAR(10),
	@data_nascimento VARCHAR(10),
    @email_corporativo VARCHAR(100), 
	@email_pessoal VARCHAR(100), 
    @instituicao_conclusao VARCHAR(100),
	@nome VARCHAR(100),
    @nome_social VARCHAR(100),
    @pontuacao_vestibular DECIMAL(5,2),
    @posicao_vestibular INT,
    @semestre_ingresso INT,
    @semestre_limite_graduacao INT,
    @curso INT,
    @saida VARCHAR(100) OUTPUT
)
AS
BEGIN
    DECLARE @cpf_valido CHAR(10);
    DECLARE @idade INT;
    DECLARE @ano_limite_graduacao INT;
    DECLARE @ra VARCHAR(20);
    DECLARE @mensagem VARCHAR(100);
    DECLARE @curso_existe INT;

    
    SELECT @curso_existe = COUNT(*) FROM Curso WHERE codigo = @curso;
    IF @curso_existe = 0
    BEGIN
        SET @saida = 'O curso informado não existe na base de dados.';
        RETURN;
    END

    
    IF @op = 'I'
    BEGIN
        IF @CPF IS NOT NULL AND @nome IS NOT NULL AND @data_nascimento IS NOT NULL AND @ano_ingresso IS NOT NULL AND @semestre_ingresso IS NOT NULL
        BEGIN
            EXEC ValidarCPF @CPF, @cpf_valido OUTPUT;
            SET @idade = DATEDIFF(YEAR, CAST(@data_nascimento AS DATE), GETDATE());
            IF @cpf_valido = 'Válido' AND @idade >= 16
            BEGIN
                SET @ano_limite_graduacao = @ano_ingresso + 5;
                EXEC gerarRA @ano_ingresso, @semestre_ingresso, @ra OUTPUT;
                IF @ano_limite_graduacao >= YEAR(GETDATE()) OR (@ano_limite_graduacao = YEAR(GETDATE()) AND @semestre_limite_graduacao >= CASE WHEN MONTH(GETDATE()) <= 6 THEN 1 ELSE 2 END)
                BEGIN
                    
                    INSERT INTO Aluno (RA, CPF, nome, nome_social, data_nascimento, email_pessoal, email_corporativo, conclusao_segundo_grau, instituicao_conclusao, pontuacao_vestibular, posicao_vestibular, ano_ingresso, ano_limite_graduacao, semestre_ingresso, semestre_limite_graduacao, curso)
                    VALUES (@ra, @CPF, @nome, @nome_social, @data_nascimento, @email_pessoal, @email_corporativo, @conclusao_segundo_grau, @instituicao_conclusao, @pontuacao_vestibular, @posicao_vestibular, @ano_ingresso, @ano_limite_graduacao, @semestre_ingresso, @semestre_limite_graduacao, @curso);

                    DECLARE @codigo_matricula INT;
                    SELECT @codigo_matricula = ISNULL(MAX(codigo) + 1, 1) FROM Matricula;

                    WHILE EXISTS (SELECT 1 FROM Matricula WHERE codigo = @codigo_matricula)
                    BEGIN
                        SET @codigo_matricula = @codigo_matricula + 1;
                    END

                    DECLARE @disciplina INT;
                    DECLARE @codigo_matricula_atual INT; 

                    DECLARE disciplina_cursor CURSOR FOR 
                    SELECT disciplina FROM Grade WHERE curso = @curso;

                    OPEN disciplina_cursor;
                    FETCH NEXT FROM disciplina_cursor INTO @disciplina;

                    WHILE @@FETCH_STATUS = 0
                    BEGIN
                        SELECT @codigo_matricula_atual = ISNULL(MAX(codigo) + 1, @codigo_matricula) FROM Matricula;

                        INSERT INTO Matricula (codigo, aluno, disciplina, data_m, status_m)
                        VALUES (@codigo_matricula_atual, @CPF, @disciplina, GETDATE(), 'Cursando');

                        FETCH NEXT FROM disciplina_cursor INTO @disciplina;
                    END

                    CLOSE disciplina_cursor;
                    DEALLOCATE disciplina_cursor;

                    SET @saida = 'Aluno inserido com sucesso.';
                END
                ELSE
                BEGIN
                    SET @saida = 'Data limite de graduação inválida.';
                END
            END
            ELSE
            BEGIN
                SET @saida = 'CPF inválido ou idade inferior a 16 anos. Matrícula não realizada.';
            END
        END
        ELSE
        BEGIN
            SET @saida = 'Parâmetros incompletos para a operação de inserção.';
        END
    END
 
    ELSE IF @op = 'U'
    BEGIN
        IF @CPF IS NOT NULL AND @nome IS NOT NULL AND @data_nascimento IS NOT NULL AND @ano_ingresso IS NOT NULL AND @semestre_ingresso IS NOT NULL
        BEGIN
            EXEC ValidarCPF @CPF, @cpf_valido OUTPUT;
            SET @idade = DATEDIFF(YEAR, CAST(@data_nascimento AS DATE), GETDATE());
            IF @cpf_valido = 'Válido' AND @idade >= 16
            BEGIN
                SET @ano_limite_graduacao = @ano_ingresso + 5;
                IF @ano_limite_graduacao >= YEAR(GETDATE()) OR (@ano_limite_graduacao = YEAR(GETDATE()) AND @semestre_limite_graduacao >= CASE WHEN MONTH(GETDATE()) <= 6 THEN 1 ELSE 2 END)
                BEGIN
                    IF EXISTS (SELECT 1 FROM Aluno WHERE CPF = @CPF)
                    BEGIN
                        UPDATE Aluno
                        SET nome = @nome,
                            nome_social = @nome_social,
                            data_nascimento = @data_nascimento,
                            email_pessoal = @email_pessoal,
                            email_corporativo = @email_corporativo,
                            conclusao_segundo_grau = @conclusao_segundo_grau,
                            instituicao_conclusao = @instituicao_conclusao,
                            pontuacao_vestibular = @pontuacao_vestibular,
                            posicao_vestibular = @posicao_vestibular,
                            ano_ingresso = @ano_ingresso,
                            ano_limite_graduacao = @ano_limite_graduacao,
                            semestre_ingresso = @semestre_ingresso,
                            semestre_limite_graduacao = @semestre_limite_graduacao,
                            curso = @curso
                        WHERE CPF = @CPF;
                        SET @saida = 'Aluno atualizado com sucesso.';
                    END
                    ELSE
                    BEGIN
                        SET @saida = 'CPF não encontrado na base de dados.';
                    END
                END
                ELSE
                BEGIN
                    SET @saida = 'Data limite de graduação inválida.';
                END
            END
            ELSE
            BEGIN
                SET @saida = 'CPF inválido ou idade inferior a 16 anos. Atualização não realizada.';
            END
        END
        ELSE
        BEGIN
            SET @saida = 'Parâmetros incompletos para a operação de atualização.';
        END
    END

    ELSE IF @op = 'D'
    BEGIN
        IF @CPF IS NOT NULL
        BEGIN
            IF EXISTS (SELECT 1 FROM Aluno WHERE CPF = @CPF)
            BEGIN
                DELETE FROM Aluno WHERE CPF = @CPF;
                SET @saida = 'Aluno excluído com sucesso.';
            END
            ELSE
            BEGIN
                SET @saida = 'CPF não encontrado na base de dados.';
            END
        END
        ELSE
        BEGIN
            SET @saida = 'Parâmetros incompletos para a operação de exclusão.';
        END
    END
    ELSE
    BEGIN
        SET @saida = 'Operação inválida.';
    END
END;

GO

--Telefone
CREATE FUNCTION fn_telefones(@cpf CHAR(11))
RETURNS TABLE
AS
RETURN
(
    SELECT 
        t.codigo AS codigo,
        t.telefone AS telefone,
		a.nome AS aluno
    FROM telefone t
	INNER JOIN Aluno a ON t.aluno = a.cpf
    WHERE a.cpf = @cpf
);

GO
CREATE PROCEDURE GerenciarTelefone (
    @opcao VARCHAR(10),
	@codigo INT,
	@cpfAluno CHAR(11),
    @telefone VARCHAR(20),
    @saida VARCHAR(100) OUTPUT
)
AS
BEGIN

    IF @opcao = 'I'
    BEGIN
   
        IF EXISTS (SELECT 1 FROM Aluno WHERE CPF = @cpfAluno)
        BEGIN
       
            IF NOT EXISTS (SELECT 1 FROM Telefone WHERE telefone = @telefone AND aluno = @cpfAluno)
            BEGIN
               
                INSERT INTO Telefone (codigo, aluno, telefone)
                VALUES (@codigo, @cpfAluno, @telefone);
                
                SET @saida = 'Telefone inserido com sucesso.';
            END
            ELSE
            BEGIN
                SET @saida = 'O telefone já existe na base de dados para este aluno.';
            END
        END
        ELSE
        BEGIN
            SET @saida = 'O aluno com o CPF especificado não foi encontrado na base de dados.';
        END
    END
    ELSE IF @opcao = 'D'
    BEGIN
        
        DELETE FROM Telefone
        WHERE codigo = @codigo;
        
        SET @saida = 'Telefone excluído com sucesso.';
    END
    ELSE
    BEGIN
        SET @saida = 'Opção inválida.';
    END
END;
--Professor
GO
CREATE FUNCTION fn_professores()
RETURNS TABLE
AS
RETURN
(
    SELECT 
        p.codigo AS codigo,
        p.nome AS nome
    FROM Professor p
);
GO
CREATE PROCEDURE GerenciarProfessor (
    @op VARCHAR(10),
    @codigo INT,
    @nome VARCHAR(100),
    @saida VARCHAR(100) OUTPUT
)
AS
BEGIN

    IF @op = 'I' 
    BEGIN
        IF @codigo IS NOT NULL AND @nome IS NOT NULL 
        BEGIN
            IF NOT EXISTS (SELECT 1 FROM Professor WHERE codigo = @codigo)
            BEGIN
                INSERT INTO Professor (codigo, nome)
                VALUES (@codigo, @nome);
                
                SET @saida = 'Professor inserido com sucesso.';
            END
            ELSE
            BEGIN
                SET @saida = 'O código do professor já existe na base de dados.';
            END
        END
        ELSE
        BEGIN
            SET @saida = 'Parâmetros incompletos para inserção.';
        END
    END
    ELSE IF @op = 'U' 
    BEGIN
        IF @codigo IS NOT NULL AND @nome IS NOT NULL 
        BEGIN
            IF EXISTS (SELECT 1 FROM Professor WHERE codigo = @codigo)
            BEGIN
                UPDATE Professor
                SET nome = @nome
                WHERE codigo = @codigo;

                SET @saida = 'Professor atualizado com sucesso.';
            END
            ELSE
            BEGIN
                SET @saida = 'O código do professor não foi encontrado na base de dados.';
            END
        END
        ELSE
        BEGIN
            SET @saida = 'Parâmetros incompletos para atualização.';
        END
    END
    ELSE IF @op = 'D'
    BEGIN
        IF @codigo IS NOT NULL
        BEGIN
            IF EXISTS (SELECT 1 FROM Professor WHERE codigo = @codigo)
            BEGIN
                DELETE FROM Professor WHERE codigo = @codigo;
                SET @saida = 'Professor excluído com sucesso.';
            END
            ELSE
            BEGIN
                SET @saida = 'O código do Professor não foi encontrado na base de dados.';
            END
        END
        ELSE
        BEGIN
            SET @saida = 'Parâmetros incompletos para exclusão.';
        END
    END
    ELSE
    BEGIN
        SET @saida = 'Operação inválida.';
    END
END;
--Disciplina
GO
CREATE FUNCTION fn_disciplinas()
RETURNS TABLE
AS
RETURN
(
    SELECT 
        D.codigo AS codigo,
        D.nome AS nome,
        D.horas_inicio AS horas_inicio,
        D.duracao AS duracao,
        D.dia_semana AS dia_semana,
		D.professor AS codigoProfessor,
        P.nome AS nomeProfessor
    FROM Disciplina D
    LEFT JOIN Professor P ON P.codigo = D.professor
);


GO
CREATE PROCEDURE VerificarHorariosDisponiveis (
    @inicio VARCHAR(07),
    @duracao INT,
    @men VARCHAR(100) OUTPUT
)
AS
BEGIN
    DECLARE @fim TIME;

    IF @inicio NOT IN ('13:00', '16:40', '14:50') OR (@duracao != 2 AND @duracao != 4)
    BEGIN
        PRINT 'Horário de início ou duração inválidos.';
		SET @men = 'Invalido';
        RETURN;
    END;

    PRINT 'Os horários disponíveis são:';
    IF @inicio = '13:00' AND @duracao = 4
    BEGIN
        PRINT 'Iniciando às 13:00 com 4 aulas de duração (Até 16h30)';
		SET @men = 'Valido'
    END
    IF @inicio = '13:00' AND @duracao = 2
    BEGIN
        PRINT 'Iniciando às 13:00 com 2 aulas de duração (Até 14h40)';
		SET @men = 'Valido'
    END
    IF @inicio = '14:50' AND @duracao = 4
    BEGIN
        PRINT 'Iniciando às 14:50 com 4 aulas de duração (Até 19h30)';
		SET @men = 'Valido'
    END
    IF @inicio = '14:50' AND @duracao = 2
    BEGIN
        PRINT 'Iniciando às 14:50 com 2 aulas de duração (Até 16h30)';
		SET @men = 'Valido'
    END
    IF @inicio = '16:40' AND @duracao = 2
    BEGIN
        PRINT 'Iniciando às 16:40 com 2 aulas de duração (Até 18h20)';
		SET @men = 'Valido'
    END
END;

GO
CREATE PROCEDURE GerenciarDisciplina (
    @op VARCHAR(100),
    @codigo INT,
    @nome VARCHAR(100),
    @horas_inicio VARCHAR(07),
    @duracao INT,
    @dia_semana VARCHAR(30),
    @professor INT,
    @saida VARCHAR(200) OUTPUT
)
AS
BEGIN
    IF @op = 'I'
    BEGIN
        IF @dia_semana NOT IN ('Segunda-feira', 'Terça-feira', 'Quarta-feira', 'Quinta-feira', 'Sexta-feira', 'Sábado', 'Domingo')
        BEGIN
            SET @saida = 'Dia da semana inválido. Por favor, insira um dos seguintes valores: Segunda-feira, Terça-feira, Quarta-feira, Quinta-feira, Sexta-feira, Sábado, Domingo.';
            RETURN;
        END;

        DECLARE @men VARCHAR(100);

        EXEC VerificarHorariosDisponiveis @horas_inicio, @duracao, @men OUTPUT;

        IF @men = 'Valido' 
        BEGIN
            INSERT INTO Disciplina (codigo, nome, horas_inicio, duracao, dia_semana, professor)
            VALUES (@codigo, @nome, @horas_inicio, @duracao, @dia_semana, @professor);

            SET @saida = 'Disciplina inserida com sucesso.';
        END
        ELSE
        BEGIN
            SET @saida = 'Horario ou duração inválido';
        END
    END
    ELSE IF @op = 'U'
    BEGIN 
        IF @dia_semana NOT IN ('Segunda-feira', 'Terça-feira', 'Quarta-feira', 'Quinta-feira', 'Sexta-feira', 'Sábado', 'Domingo')
        BEGIN
            SET @saida = 'Dia da semana inválido. Por favor, insira um dos seguintes valores: Segunda-feira, Terça-feira, Quarta-feira, Quinta-feira, Sexta-feira, Sábado, Domingo.';
            RETURN;
        END;

        EXEC VerificarHorariosDisponiveis @horas_inicio, @duracao, @men OUTPUT;

        IF @men = 'Valido' 
        BEGIN
            IF EXISTS (SELECT 1 FROM Disciplina WHERE codigo = @codigo)
            BEGIN
                UPDATE Disciplina
                SET nome = @nome,
                    horas_inicio = @horas_inicio,
                    duracao = @duracao,
                    dia_semana = @dia_semana,
                    professor = @professor
                WHERE codigo = @codigo;

                SET @saida = 'Disciplina atualizada com sucesso.';
            END
            ELSE
            BEGIN
                SET @saida = 'Código não encontrado na base de dados.';
            END
        END
        ELSE
        BEGIN
            SET @saida = @men;
        END
    END
    ELSE IF @op = 'D'
    BEGIN
        IF @codigo IS NOT NULL
        BEGIN
            IF EXISTS (SELECT 1 FROM Disciplina WHERE codigo = @codigo)
            BEGIN
                DELETE FROM Disciplina WHERE codigo = @codigo;
                SET @saida = 'Disciplina excluída com sucesso.';
            END
            ELSE
            BEGIN
                SET @saida = 'Código não encontrado na base de dados.';
            END
        END
        ELSE
        BEGIN
            SET @saida = 'Parâmetros incompletos para a operação de exclusão.';
        END
    END
    ELSE
    BEGIN
        SET @saida = 'Operação inválida.';
    END
END;
GO
--Grade
CREATE FUNCTION fn_grade(@codigo_curso INT)
RETURNS TABLE
AS
RETURN
(
    SELECT 
        g.codigo AS codigo,
        c.nome AS curso,
        d.nome AS disciplina
    FROM Grade g
    INNER JOIN Curso c ON g.curso = c.codigo
    INNER JOIN Disciplina d ON g.disciplina = d.codigo
    WHERE c.codigo = @codigo_curso
);
GO
CREATE PROCEDURE GerenciarGrade (
    @opcao CHAR(1),
    @codigo INT,
    @curso INT,
    @disciplina INT,
    @saida VARCHAR(200) OUTPUT
)
AS
BEGIN
    IF @opcao = 'I'
    BEGIN
        IF EXISTS (SELECT 1 FROM Grade WHERE codigo = @codigo)
        BEGIN
            SET @saida = 'Erro: Já existe uma grade com o código fornecido.';
            RETURN;
        END

        INSERT INTO Grade (codigo, curso, disciplina)
        VALUES (@codigo, @curso, @disciplina);

        SET @saida = 'Grade inserida com sucesso.';
    END
    ELSE IF @opcao = 'U'
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM Grade WHERE codigo = @codigo)
        BEGIN
            SET @saida = 'Erro: Não existe uma grade com o código fornecido.';
            RETURN;
        END

        UPDATE Grade
        SET curso = @curso,
            disciplina = @disciplina
        WHERE codigo = @codigo;

        SET @saida = 'Grade atualizada com sucesso.';
    END
    ELSE IF @opcao = 'D'
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM Grade WHERE codigo = @codigo)
        BEGIN
            SET @saida = 'Erro: Não existe uma grade com o código fornecido.';
            RETURN;
        END

        DELETE FROM Grade WHERE codigo = @codigo;

        SET @saida = 'Grade excluída com sucesso.';
    END
    ELSE
    BEGIN
        SET @saida = 'Erro: Opção inválida.';
    END
END;

--Matricula
GO
CREATE PROCEDURE GerenciarMatriculaD (
    @opcao VARCHAR(10),
    @codigo INT = NULL,
    @aluno CHAR(11),
    @disciplina INT,
    @data_m VARCHAR(10),
	@status_m VARCHAR(30) =  NULL,
    @saida VARCHAR(100) OUTPUT
)
AS
BEGIN
    IF @opcao = 'I'
    BEGIN
     
        DECLARE @matriculaExistente BIT;
        SELECT @matriculaExistente = CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END
        FROM Matricula
        WHERE aluno = @aluno AND disciplina = @disciplina;

        DECLARE @todasReprovadas BIT;
        SELECT @todasReprovadas = CASE WHEN COUNT(*) = 0 THEN 1 ELSE 0 END
        FROM Matricula
        WHERE aluno = @aluno AND disciplina = @disciplina AND status_m != 'Reprovado';

        IF @matriculaExistente = 0 OR @todasReprovadas = 1
        BEGIN
           
            DECLARE @novoCodigo INT;
            SET @novoCodigo = ABS(CHECKSUM(NEWID()));

            
            INSERT INTO Matricula (codigo, aluno, disciplina, data_m, status_m)
            VALUES (@novoCodigo, @aluno, @disciplina, @data_m, 'Cursando');
        
            SET @saida = 'Matrícula inserida com sucesso.';
        END
        ELSE
        BEGIN
            SET @saida = 'O aluno já está matriculado nesta disciplina e está cursando ou foi aprovado.';
            RETURN;
        END
    END
    ELSE IF @opcao = 'U'
    BEGIN

        UPDATE Matricula
        SET aluno = @aluno,
            disciplina = @disciplina,
            data_m = @data_m
        WHERE codigo = @codigo;

        SET @saida = 'Matrícula atualizada com sucesso.';
    END
    ELSE
    BEGIN
        SET @saida = 'Opção inválida.';
        RETURN;
    END

    DECLARE @media DECIMAL(5,2);
    SELECT @media = NULL;
    IF (SELECT status_m FROM Matricula WHERE aluno = @aluno AND disciplina = @disciplina) != 'Cursando'
    BEGIN
        SELECT @media = AVG((CONVERT(DECIMAL(5,2), Nota1) + CONVERT(DECIMAL(5,2), Nota2) + ISNULL(CONVERT(DECIMAL(5,2), NotaRecuperacao), 0)) / 2.0)
        FROM Notas
        WHERE aluno = @aluno AND disciplina = @disciplina;
    END

    IF @media IS NOT NULL
    BEGIN
        UPDATE Matricula
        SET status_m = CASE 
                            WHEN @media >= 6 THEN 'Aprovado'
                            ELSE 'Reprovado'
                      END
        WHERE aluno = @aluno AND disciplina = @disciplina;
    END
    ELSE
    BEGIN
        IF (SELECT status_m FROM Matricula WHERE aluno = @aluno AND disciplina = @disciplina) = 'Cursando'
        BEGIN
            SET @saida = 'Não há notas para este aluno nesta disciplina.';
        END
    END
END;
GO
CREATE FUNCTION fn_matricula(@aluno CHAR(11))
RETURNS TABLE
AS
RETURN
(
    SELECT 
        m.codigo AS codigo,
        a.nome AS aluno,
        d.nome AS disciplina,
        m.data_m AS data_m,
        m.status_m AS status_m,
        m.nota_final AS nota_final
    FROM Matricula m
    INNER JOIN Aluno a ON m.aluno = a.CPF
    INNER JOIN Disciplina d ON m.disciplina = d.codigo
    LEFT JOIN (
        SELECT 
            Aluno,
            Disciplina,
            (CAST(nota1 AS DECIMAL(5,2)) + CAST(nota2 AS DECIMAL(5,2)) + COALESCE(CAST(notaRecuperacao AS DECIMAL(5,2)), 0)) / 2.0 AS Media
        FROM Notas
    ) n ON m.disciplina = n.Disciplina AND m.aluno = n.Aluno
    WHERE a.CPF = @aluno
);


GO
--Notas
CREATE PROCEDURE InserirNotas (
    @Opcao CHAR(1), 
    @Disciplina INT,
    @Aluno CHAR(11),
    @Nota1 FLOAT,
    @Nota2 FLOAT,
    @NotaRecuperacao FLOAT = NULL,
    @saida VARCHAR(100) OUTPUT
)
AS
BEGIN
    DECLARE @Codigo INT,
            @PesoNota1 FLOAT,
            @PesoNota2 FLOAT,
            @PesoRecuperacao FLOAT = 0.2,  
            @Media FLOAT

    IF @NotaRecuperacao IS NOT NULL AND @NotaRecuperacao != 0.0
    BEGIN
    
        SET @PesoNota1 = 0.4
        SET @PesoNota2 = 0.4
    END
    ELSE
    BEGIN
        
        SET @PesoNota1 = 0.5
        SET @PesoNota2 = 0.5
    END

   
    SET @Media = (@Nota1 * @PesoNota1) + (@Nota2 * @PesoNota2) + ISNULL(@NotaRecuperacao, 0) * @PesoRecuperacao

    IF @Opcao NOT IN ('I', 'U')
    BEGIN
        SET @saida = 'Opção inválida. Use "I" para inserir notas ou "U" para atualizar.'
        RETURN
    END

    IF @Opcao = 'U'
    BEGIN
        
        IF NOT EXISTS (SELECT 1 FROM Notas WHERE Disciplina = @Disciplina AND Aluno = @Aluno)
        BEGIN
            SET @saida = 'Não existem notas cadastradas para este aluno nesta disciplina.'
            RETURN
        END


        UPDATE Notas 
        SET Nota1 = @Nota1, 
            Nota2 = @Nota2,
            NotaRecuperacao = @NotaRecuperacao,
            Media = @Media
        WHERE Disciplina = @Disciplina AND Aluno = @Aluno

       
        UPDATE Matricula
        SET nota_final = @Media,
            status_m = CASE 
                            WHEN @Media >= 6 THEN 'Aprovado'
                            WHEN @Media >= 3 THEN 'Exame'
                            ELSE 'Reprovado'
                      END
        WHERE Disciplina = @Disciplina AND Aluno = @Aluno

        SET @saida = 'Notas atualizadas com sucesso.'
    END
    ELSE IF @Opcao = 'I'
    BEGIN
        
        IF EXISTS (SELECT 1 FROM Notas WHERE Disciplina = @Disciplina AND Aluno = @Aluno)
        BEGIN
            SET @saida = 'Já existem notas cadastradas para este aluno nesta disciplina. Utilize a opção "U" para atualizar as notas.'
            RETURN
        END


        SELECT @Codigo = ISNULL(MAX(codigo), 0) + 1 FROM Notas

  
        INSERT INTO Notas (codigo, Disciplina, Aluno, Nota1, Nota2, NotaRecuperacao, Media)
        VALUES (@Codigo, @Disciplina, @Aluno, @Nota1, @Nota2, @NotaRecuperacao, @Media)

     
        UPDATE Matricula
        SET nota_final = @Media,
            status_m = CASE 
                            WHEN @Media >= 6 THEN 'Aprovado'
                            WHEN @Media >= 3 THEN 'Exame'
                            WHEN @NotaRecuperacao IS NOT NULL AND @NotaRecuperacao != 0.0 AND @Media < 6 THEN 'Reprovado'
                            ELSE 'Reprovado'
                      END
        WHERE Disciplina = @Disciplina AND Aluno = @Aluno

        SET @saida = 'Notas cadastradas com sucesso.'
    END

    SELECT @saida AS saida;
END;
GO
CREATE FUNCTION ListarNotasEMedias (@disciplina INT)
RETURNS TABLE
AS
RETURN
(
    SELECT n.codigo,
           n.Aluno,
           n.Nota1,
           n.Nota2,
           n.NotaRecuperacao,
           n.Media
    FROM Notas n
    INNER JOIN Disciplina d ON n.Disciplina = d.codigo
    WHERE n.disciplina = @disciplina
	);

--Chamada
GO
CREATE PROCEDURE GerenciarChamada (
    @opcao VARCHAR(10),
    @id INT,
    @codigo INT,
    @codigoDisciplina INT,
    @cpfAluno VARCHAR(30),
    @falta INT,
    @data VARCHAR(10),
    @saida VARCHAR(100) OUTPUT
)
AS
BEGIN
    IF @opcao = 'I'
    BEGIN
        DECLARE @duracaoMatricula INT;

      
        SELECT @duracaoMatricula = duracao
        FROM Disciplina
        WHERE codigo = @codigoDisciplina;


        IF @falta <= @duracaoMatricula
        BEGIN
           
            SELECT @id = ISNULL(MAX(id), 0) + 1 FROM Chamada;

            
            INSERT INTO Chamada (id, codigo, disciplina, aluno, falta, data)
            VALUES (@id, @codigo, @codigoDisciplina, @cpfAluno, @falta, @data);

            SET @saida = 'Chamadas inseridas com sucesso.';
        END
        ELSE
        BEGIN
            SET @saida = 'Número de faltas excede a duração da matrícula.';
        END
    END
    ELSE IF @opcao = 'U'
    BEGIN
        
        UPDATE Chamada
        SET falta = @falta
        WHERE codigo = @codigo AND aluno = @cpfAluno;

        SET @saida = 'Falta atualizada com sucesso.';
    END
    ELSE
    BEGIN
        SET @saida = 'Opção inválida.';
    END
END;
GO
CREATE FUNCTION fn_chamadas(@codigoDisciplina INT)
RETURNS TABLE
AS
RETURN
(
    SELECT 
        d.nome AS disciplina,
		m.aluno AS cpf,
        a.nome AS aluno,
		 CAST(NULL AS INT) AS codigo,
		CAST(NULL AS DATETIME) AS data,  
        CAST(NULL AS INT) AS falta 
    FROM Matricula m
	INNER JOIN Disciplina d ON d.codigo = m.disciplina
	INNER JOIN Aluno a ON a.cpf = m.aluno
    WHERE m.disciplina = @codigoDisciplina AND m.status_m = 'Cursando'
);


GO
CREATE FUNCTION fn_chamadasAlter(@codigo int)
RETURNS TABLE
AS
RETURN
(
    SELECT 
	    c.codigo AS codigo,
	    d.nome AS disciplina,
		a.nome AS aluno,
		c.aluno AS cpf,
        c.falta AS falta,
		CAST(NULL AS INT) AS id,
		CAST(NULL AS DATETIME) AS data
    FROM Chamada c
    INNER JOIN Matricula m ON c.aluno = m.aluno AND c.disciplina = m.disciplina
	INNER JOIN Disciplina d ON d.codigo = c.disciplina
	INNER JOIN Aluno a ON a.CPF = c.aluno
    WHERE c.codigo = @codigo
);
GO
CREATE FUNCTION fn_consultaC(@aluno CHAR(11))
RETURNS TABLE
AS
RETURN
(
    SELECT 
	    c.codigo AS codigo,
	    d.nome AS disciplina,
		c.data AS data,
        c.aluno AS aluno,
		a.nome AS nomeAluno,
        c.falta AS falta
    FROM Chamada c
    INNER JOIN Matricula m ON c.aluno = m.aluno AND c.disciplina = m.disciplina
	INNER JOIN Aluno a ON a.CPF = c.aluno
	INNER JOIN Disciplina d ON c.disciplina = d.codigo
	WHERE c.aluno = @aluno

)
GO
--Dispensa
CREATE PROCEDURE iudDispensa (
    @opcao CHAR(1),
    @aluno CHAR(11),
    @disciplina INT,
    @data_s VARCHAR(10),
    @instituicao VARCHAR(100),
    @saida VARCHAR(100) OUTPUT
)
AS
BEGIN
    IF @opcao = 'I'
    BEGIN
        BEGIN TRY
         
            INSERT INTO Dispensa (aluno, disciplina, data_s, instituicao)
            VALUES (@aluno, @disciplina, @data_s, @instituicao);

        
            UPDATE Matricula
            SET status_m = 'Em análise'
            WHERE aluno = @aluno AND disciplina = @disciplina;

            SET @saida = 'Solicitação de dispensa feita com sucesso.';
        END TRY
        BEGIN CATCH
            SET @saida = 'Erro ao solicitar dispensa.';
        END CATCH;
    END
    ELSE
    BEGIN
        SET @saida = 'Opção inválida.';
    END
END;
GO
CREATE FUNCTION fn_Dispensa(@cpf_aluno CHAR(11))
RETURNS TABLE
AS
RETURN
(
    SELECT 
	    d.codigo AS codigo,
        a.nome AS aluno,
        dis.nome AS disciplina,
        d.data_s AS data_s,
        d.instituicao AS instituicao
    FROM Dispensa d
    INNER JOIN Aluno a ON d.aluno = a.CPF
    INNER JOIN Disciplina dis ON d.disciplina = dis.codigo
    WHERE d.aluno = @cpf_aluno
)
GO
--Solicitação de dispensa
CREATE PROCEDURE iudSolicitacao (
    @opcao CHAR(1),
    @saida VARCHAR(100) OUTPUT
)
AS
BEGIN
    IF @opcao = 'U'
    BEGIN
        BEGIN TRY
          
            UPDATE Matricula
            SET status_m = 'Dispensado'
            WHERE status_m = 'Em análise';
        
            UPDATE Matricula
            SET nota_final = 'D'
			WHERE status_m =  'Dispensado'
     
            SET @saida = 'Solicitação atualizada com sucesso.';
        END TRY
        BEGIN CATCH
            SET @saida = 'Erro ao atualizar a solicitação.';
        END CATCH;
    END
    ELSE
    BEGIN
        SET @saida = 'Opção inválida.';
    END
END;
GO
CREATE FUNCTION fn_Solicitacao()
RETURNS TABLE
AS
RETURN
(
    SELECT 
	    d.codigo AS codigo,
        a.nome AS aluno,
        dis.nome AS disciplina,
        d.data_s AS data_s,
        d.instituicao AS instituicao
    FROM Dispensa d
    INNER JOIN Aluno a ON d.aluno = a.CPF
    INNER JOIN Disciplina dis ON d.disciplina = dis.codigo
)
GO
--Historico do aluno
CREATE FUNCTION fn_historico (
    @CPF CHAR(11)
)
RETURNS TABLE
AS
RETURN
(
    SELECT 
        A.RA,
        A.nome AS NomeCompleto,
        C.nome AS NomeCurso,
        M.data_m AS DataPrimeiraMatricula,
        A.pontuacao_vestibular AS PontuacaoVestibular,
        A.posicao_vestibular AS PosicaoVestibular
    FROM 
        Aluno A
    INNER JOIN 
        Matricula M ON A.CPF = M.aluno
    INNER JOIN 
        Curso C ON A.curso = C.codigo
    WHERE 
        A.CPF = @CPF
)
GO
--Consulta Alunos aprovados
CREATE FUNCTION dbo.fn_Aprovados (
    @cpf CHAR(11)
)
RETURNS TABLE
AS
RETURN
(
    SELECT 
        D.codigo AS CodigoDisciplina,
        D.nome AS NomeDisciplina,
        P.nome AS NomeProfessor,
        N.media AS NotaFinal,
        COALESCE(SUM(C.falta), 0) AS QuantidadeFaltas
    FROM 
        Matricula M
    INNER JOIN Disciplina D ON M.disciplina = D.codigo
	INNER JOIN Notas N ON N.disciplina = D.codigo
	INNER JOIN Chamada C ON C.disciplina = D.codigo AND C.aluno = M.aluno
	INNER JOIN Professor P ON P.codigo = D.professor 
    WHERE 
        M.aluno = @cpf
        AND M.status_m = 'Aprovado'
    GROUP BY
        M.disciplina, D.nome, D.professor, N.media, P.nome, D.codigo
   
);
GO
--UDF do relatorio de faltas
CREATE FUNCTION dbo.GetAlunoFaltas (@codigoDisciplina INT)
RETURNS @result TABLE (
    cpfAluno VARCHAR(30),
    nomeAluno VARCHAR(100),
    codigoDisciplina INT,
    nomeDisciplina VARCHAR(100),
    faltasSemana1 INT,
    faltasSemana2 INT,
    faltasSemana3 INT,
    faltasSemana4 INT,
    faltasSemana5 INT,
    faltasSemana6 INT,
    faltasSemana7 INT,
    faltasSemana8 INT,
    totalFaltas INT,
    totalAulas INT,
    frequencia DECIMAL(5, 2),
    statusAluno VARCHAR(50)
)
AS
BEGIN
    DECLARE @cpfAluno VARCHAR(30)
    DECLARE @nomeAluno VARCHAR(100)
    DECLARE @codigoDisciplinaCursor INT
    DECLARE @nomeDisciplina VARCHAR(100)
    DECLARE @primeiraData DATE
    DECLARE @ultimaData DATE
    DECLARE @faltasSemana1 INT
    DECLARE @faltasSemana2 INT
    DECLARE @faltasSemana3 INT
    DECLARE @faltasSemana4 INT
    DECLARE @faltasSemana5 INT
    DECLARE @faltasSemana6 INT
    DECLARE @faltasSemana7 INT
    DECLARE @faltasSemana8 INT
    DECLARE @totalFaltas INT
    DECLARE @statusAluno VARCHAR(10)
    DECLARE @duracao INT
    DECLARE @TotalAulas INT
    DECLARE @semanaInicio INT
    DECLARE @semanaAtual INT


    SELECT @duracao = duracao
    FROM Disciplina
    WHERE codigo = @codigoDisciplina;


    SELECT @TotalAulas = COUNT(DISTINCT codigo) * @duracao
    FROM Chamada
    WHERE disciplina = @codigoDisciplina;


    DECLARE aluno_cursor CURSOR FOR 
    SELECT 
        a.cpf AS cpfAluno,
        a.nome AS nomeAluno,
        d.codigo AS codigoDisciplina,
        d.nome AS nomeDisciplina,
        MIN(c.data) AS primeiraData,
        MAX(c.data) AS ultimaData
    FROM 
        Chamada c
    JOIN 
        Aluno a ON c.aluno = a.cpf
    JOIN 
        Disciplina d ON c.disciplina = d.codigo
    WHERE 
        c.disciplina = @codigoDisciplina
    GROUP BY 
        a.cpf, a.nome, d.codigo, d.nome;

    OPEN aluno_cursor
    FETCH NEXT FROM aluno_cursor INTO @cpfAluno, @nomeAluno, @codigoDisciplinaCursor, @nomeDisciplina, @primeiraData, @ultimaData

    WHILE @@FETCH_STATUS = 0
    BEGIN
     
        SET @faltasSemana1 = 0
        SET @faltasSemana2 = 0
        SET @faltasSemana3 = 0
        SET @faltasSemana4 = 0
        SET @faltasSemana5 = 0
        SET @faltasSemana6 = 0
        SET @faltasSemana7 = 0
        SET @faltasSemana8 = 0
        
        SET @semanaInicio = 0
        SET @semanaAtual = 0
        WHILE @semanaAtual <= 7 AND DATEADD(WEEK, @semanaAtual, @primeiraData) <= @ultimaData
        BEGIN
            SET @semanaAtual = @semanaInicio + @semanaAtual
            SET @semanaAtual = @semanaAtual + 1

            SET @faltasSemana1 = ISNULL((SELECT SUM(c.falta) FROM Chamada c WHERE c.aluno = @cpfAluno AND c.disciplina = @codigoDisciplina AND DATEDIFF(WEEK, @primeiraData, c.data) = 0), 0)
            SET @faltasSemana2 = ISNULL((SELECT SUM(c.falta) FROM Chamada c WHERE c.aluno = @cpfAluno AND c.disciplina = @codigoDisciplina AND DATEDIFF(WEEK, @primeiraData, c.data) = 1), 0)
            SET @faltasSemana3 = ISNULL((SELECT SUM(c.falta) FROM Chamada c WHERE c.aluno = @cpfAluno AND c.disciplina = @codigoDisciplina AND DATEDIFF(WEEK, @primeiraData, c.data) = 2), 0)
            SET @faltasSemana4 = ISNULL((SELECT SUM(c.falta) FROM Chamada c WHERE c.aluno = @cpfAluno AND c.disciplina = @codigoDisciplina AND DATEDIFF(WEEK, @primeiraData, c.data) = 3), 0)
            SET @faltasSemana5 = ISNULL((SELECT SUM(c.falta) FROM Chamada c WHERE c.aluno = @cpfAluno AND c.disciplina = @codigoDisciplina AND DATEDIFF(WEEK, @primeiraData, c.data) = 4), 0)
            SET @faltasSemana6 = ISNULL((SELECT SUM(c.falta) FROM Chamada c WHERE c.aluno = @cpfAluno AND c.disciplina = @codigoDisciplina AND DATEDIFF(WEEK, @primeiraData, c.data) = 5), 0)
            SET @faltasSemana7 = ISNULL((SELECT SUM(c.falta) FROM Chamada c WHERE c.aluno = @cpfAluno AND c.disciplina = @codigoDisciplina AND DATEDIFF(WEEK, @primeiraData, c.data) = 6), 0)
            SET @faltasSemana8 = ISNULL((SELECT SUM(c.falta) FROM Chamada c WHERE c.aluno = @cpfAluno AND c.disciplina = @codigoDisciplina AND DATEDIFF(WEEK, @primeiraData, c.data) = 7), 0)
        END

      
        SET @totalFaltas = @faltasSemana1 + @faltasSemana2 + @faltasSemana3 + @faltasSemana4 + @faltasSemana5 + @faltasSemana6 + @faltasSemana7 + @faltasSemana8

       
        INSERT INTO @result (cpfAluno, nomeAluno, codigoDisciplina, nomeDisciplina, 
                             faltasSemana1, faltasSemana2, faltasSemana3, faltasSemana4,
                             faltasSemana5, faltasSemana6, faltasSemana7, faltasSemana8,
                             totalFaltas, totalAulas, frequencia, statusAluno)
        VALUES (@cpfAluno, @nomeAluno, @codigoDisciplinaCursor, @nomeDisciplina,
                @faltasSemana1, @faltasSemana2, @faltasSemana3, @faltasSemana4,
                @faltasSemana5, @faltasSemana6, @faltasSemana7, @faltasSemana8,
                @totalFaltas, @TotalAulas,
                CAST((1 - CAST(@totalFaltas AS DECIMAL(5, 2)) / CAST(@TotalAulas AS DECIMAL(5, 2))) * 100 AS DECIMAL(5, 2)),
                CASE 
                    WHEN CAST((1 - CAST(@totalFaltas AS DECIMAL(5, 2)) / CAST(@TotalAulas AS DECIMAL(5, 2))) * 100 AS DECIMAL(5, 2)) <= 75 THEN 'Reprovado por Falta'
                    ELSE 'Aprovado'
                END)

        FETCH NEXT FROM aluno_cursor INTO @cpfAluno, @nomeAluno, @codigoDisciplinaCursor, @nomeDisciplina, @primeiraData, @ultimaData
    END

    CLOSE aluno_cursor
    DEALLOCATE aluno_cursor

    RETURN
END
GO
--UDF do relatorio notas
CREATE FUNCTION fn_getAlunoNotaStatus (
    @codigoDisciplina INT
)
RETURNS TABLE
AS
RETURN
(
    SELECT 
        d.nome AS disciplina,
        n.aluno,
        a.nome AS nomeAluno,
        n.nota1,
        n.nota2,
        ISNULL(n.notaRecuperacao, 0) AS notaRecuperacao,
        CASE 
            WHEN n.notaRecuperacao IS NOT NULL AND n.notaRecuperacao != 0.0 THEN 0.4
            ELSE 0.5
        END AS pesoNota1,
        CASE 
            WHEN n.notaRecuperacao IS NOT NULL AND n.notaRecuperacao != 0.0 THEN 0.4
            ELSE 0.5
        END AS pesoNota2,
        CASE 
            WHEN n.notaRecuperacao IS NOT NULL AND n.notaRecuperacao != 0.0 THEN 0.2
            ELSE 0.0
        END AS pesoNotaRecuperacao,
        CAST(n.nota1 * CASE 
                        WHEN n.notaRecuperacao IS NOT NULL AND n.notaRecuperacao != 0.0 THEN 0.4
                        ELSE 0.5
                      END AS DECIMAL(10, 1)) AS nota1_pesada,
        CAST(n.nota2 * CASE 
                        WHEN n.notaRecuperacao IS NOT NULL AND n.notaRecuperacao != 0.0 THEN 0.4
                        ELSE 0.5
                      END AS DECIMAL(10, 1)) AS nota2_pesada,
        CAST(ISNULL(n.notaRecuperacao, 0) * CASE 
                                            WHEN n.notaRecuperacao IS NOT NULL AND n.notaRecuperacao != 0.0 THEN 0.2
                                            ELSE 0.0
                                          END AS DECIMAL(10, 1)) AS notaRecuperacao_pesada,
        CASE 
            WHEN ROUND((n.nota1 * CASE 
                                WHEN n.notaRecuperacao IS NOT NULL AND n.notaRecuperacao != 0.0 THEN 0.4
                                ELSE 0.5
                              END + 
                        n.nota2 * CASE 
                                WHEN n.notaRecuperacao IS NOT NULL AND n.notaRecuperacao != 0.0 THEN 0.4
                                ELSE 0.5
                              END + 
                        ISNULL(n.notaRecuperacao, 0) * CASE 
                                                      WHEN n.notaRecuperacao IS NOT NULL AND n.notaRecuperacao != 0.0 THEN 0.2
                                                      ELSE 0.0
                                                    END), 1) >= 6 THEN 'Aprovado'
            WHEN ROUND((n.nota1 * CASE 
                                WHEN n.notaRecuperacao IS NOT NULL AND n.notaRecuperacao != 0.0 THEN 0.4
                                ELSE 0.5
                              END + 
                        n.nota2 * CASE 
                                WHEN n.notaRecuperacao IS NOT NULL AND n.notaRecuperacao != 0.0 THEN 0.4
                                ELSE 0.5
                              END + 
                        ISNULL(n.notaRecuperacao, 0) * CASE 
                                                      WHEN n.notaRecuperacao IS NOT NULL AND n.notaRecuperacao != 0.0 THEN 0.2
                                                      ELSE 0.0
                                                    END), 1) >= 3 THEN 'Exame'
            ELSE 'Reprovado'
        END AS status
    FROM 
        Notas n
    JOIN 
        Aluno a ON n.aluno = a.cpf
    JOIN 
        Disciplina d ON n.disciplina = d.codigo
    WHERE 
        n.disciplina = @codigoDisciplina
);


--Apenas testes
DECLARE @saida VARCHAR(100);
-- Inserção de João da Silva
EXEC GerenciarMatricula 
    'I',                     -- @op
    '91345626053',           -- @CPF
    '2022',                  -- @ano_ingresso
    'Completo',              -- @conclusao_segundo_grau
    '2000-08-10',            -- @data_nascimento
    'joao.corp@example.com', -- @email_corporativo
    'joao.silva@example.com',-- @email_pessoal
    'Escola A',              -- @instituicao_conclusao
    'João da Silva',         -- @nome
    'aaaaaaa',                    -- @nome_social
    600.00,                  -- @pontuacao_vestibular
    1,                       -- @posicao_vestibular
    1,                       -- @semestre_ingresso
    10,                       -- @semestre_limite_graduacao
    1,                       -- @curso
    @saida OUTPUT;           -- @saida
SELECT @saida AS Resultado;
GO
DECLARE @saida VARCHAR(100);
-- Inserção de Maria Santos
EXEC GerenciarMatricula 
    'I',                     -- @op
    '79652540005',           -- @CPF
    '2022',                  -- @ano_ingresso
    'Completo',              -- @conclusao_segundo_grau
    '2000-04-29',            -- @data_nascimento
    'maria.corp@example.com',-- @email_corporativo
    'maria.santos@example.com',-- @email_pessoal
    'Escola B',              -- @instituicao_conclusao
    'Maria Santos',          -- @nome
    'aaaaa',                    -- @nome_social
    900.00,                  -- @pontuacao_vestibular
    1,                       -- @posicao_vestibular
    1,                       -- @semestre_ingresso
    10,                       -- @semestre_limite_graduacao
    1,                       -- @curso
    @saida OUTPUT;           -- @saida
SELECT @saida AS Resultado;
GO
DECLARE @saida VARCHAR(100);
-- Inserção de Carlos Oliveira
EXEC GerenciarMatricula 
    'I',                     -- @op
    '32742832076',           -- @CPF
    '2022',                  -- @ano_ingresso
    'Completo',              -- @conclusao_segundo_grau
    '2000-04-29',            -- @data_nascimento
    'carlos.corp@example.com',-- @email_corporativo
    'carlos.oliveiras@example.com',-- @email_pessoal
    'Escola C',              -- @instituicao_conclusao
    'Carlos Oliveira',          -- @nome
    'aaaaa',                    -- @nome_social
    900.00,                  -- @pontuacao_vestibular
    1,                       -- @posicao_vestibular
    1,                       -- @semestre_ingresso
    10,                       -- @semestre_limite_graduacao
    1,                       -- @curso
    @saida OUTPUT;           -- @saida
SELECT @saida AS Resultado;
GO
DECLARE @saida VARCHAR(100);
-- Inserção de Silvia 
EXEC GerenciarMatricula 
    'I',                     -- @op
    '43199651089',           -- @CPF
    '2022',                  -- @ano_ingresso
    'Completo',              -- @conclusao_segundo_grau
    '2000-04-29',            -- @data_nascimento
    'silvia.corp@example.com',-- @email_corporativo
    'silvia.santos@example.com',-- @email_pessoal
    'Escola D',              -- @instituicao_conclusao
    'Silvia',          -- @nome
    'aaaaa',                    -- @nome_social
    900.00,                  -- @pontuacao_vestibular
    1,                       -- @posicao_vestibular
    1,                       -- @semestre_ingresso
    10,                       -- @semestre_limite_graduacao
    1,                       -- @curso
    @saida OUTPUT;           -- @saida
SELECT @saida AS Resultado;

select * from matricula
SELECT * FROM fn_chamadas(1)
select * from matricula
DECLARE @saida VARCHAR(100);
EXEC GerenciarProfessor 'I', 1, 'Carlos Silva', @saida OUTPUT;
PRINT @saida;
GO
DECLARE @saida VARCHAR(100);
EXEC GerenciarProfessor 'I', 2, 'Ana Souza', @saida OUTPUT;
PRINT @saida;

SELECT * FROM aluno fn_chamadas(1)

DECLARE @saida VARCHAR(100);


EXEC GerenciarChamada @opcao = 'I', @codigo = 1, @codigoDisciplina = 2, @cpfAluno = '91345626053', @falta = 1, @data = '2024-06-01', @saida = @saida OUTPUT;
SELECT @saida;
 drop table matricula 
 drop table dispensa
 SELECT * FROM dispensa
  SELECT * FROM disciplina
select * from notas
SELECT * FROM dbo.GetAlunoFaltas(1);
SELECT * FROM matricula  Chamada
SELECT * FROM  notas
DROP TABLE MatriculaAluno
DROP TABLE notas
SELECT * FROM fn_getAlunoNotaStatus(1);
select * from notas 
select * from Aluno
SELECT * FROM fn_matricula('91345626053')

EXEC InserirNotas 
    @Opcao = 'I',
    @Disciplina = 1, -- Substitua pelo ID da disciplina desejada
    @Aluno = '91345626053', -- Substitua pelo CPF do aluno desejado
    @Nota1 = 7.5, -- Substitua pela primeira nota
    @Nota2 = 8.0, -- Substitua pela segunda nota
    @NotaRecuperacao = NULL, -- Se não houver nota de recuperação, deixe como NULL
    @saida = @saida OUTPUT

SELECT @saida AS Resultado

SELECT *FROM fn_getAlunoNotaStatus(2);
SELECT * FROM  matricula fn_Solicitacao()
SELECT * FROM fn_Dispensa ('91345626053')
SELECT * FROM ListarNotasEMedias (1)


