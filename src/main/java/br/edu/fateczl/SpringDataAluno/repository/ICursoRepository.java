package br.edu.fateczl.SpringDataAluno.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import br.edu.fateczl.SpringDataAluno.model.Curso;

public interface ICursoRepository extends JpaRepository<Curso, Integer> {

	List<Curso> fn_cursos();

	@Procedure(name = "Curso.GerenciarCurso")
	String GerenciarCurso(@Param("acao") String acao, @Param("codigo") int codigo,
			@Param("nome") String nome, @Param("carga_horaria") int carga_horaria, @Param("sigla") String sigla, @Param("nota_enade") double nota_enade);

}