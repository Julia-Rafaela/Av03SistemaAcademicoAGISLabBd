package br.edu.fateczl.SpringDataAluno.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import br.edu.fateczl.SpringDataAluno.model.Aluno;

public interface IAlunoRepository extends JpaRepository<Aluno, String> {
	 
	List<Aluno> fn_alunos();

	@Procedure(name = "Aluno.GerenciarMatricula")
	String GerenciarMatricula(@Param("acao") String acao, @Param("cpf") String cpf,
			@Param("ano_ingresso") int ano_ingresso, @Param("conclusao_segundo_grau") String conclusao_segundo_grau,@Param("data_nascimento") String data_nascimento,
			@Param("email_corporativo") String email_corporativo, @Param("email_pessoal") String email_pessoal,
			@Param("instituicao_conclusao") String instituicao_conclusao,@Param("nome") String nome, @Param("nome_social") String nome_social, 
			@Param("pontuacao_vestibular") double pontuacao_vestibular, @Param("posicao_vestibular") int posicao_vestibular,
			@Param("semestre_ingresso") int semestre_ingresso, @Param("semestre_limite_graduacao") int semestre_limite_graduacao,
			@Param("curso") int curso);



}