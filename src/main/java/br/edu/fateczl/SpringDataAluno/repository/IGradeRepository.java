package br.edu.fateczl.SpringDataAluno.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import br.edu.fateczl.SpringDataAluno.model.Grade;


public interface IGradeRepository extends JpaRepository<Grade, Integer> {

	 @Query("SELECT g FROM Grade g WHERE g.curso.codigo = :cursoCodigo")
	    List<Grade> fn_grade(@Param("cursoCodigo") int cursoCodigo);

	@Procedure(name = "Grade.GerenciarGrade")
	String GerenciarGrade(@Param("acao") String acao, @Param("codigo") int codigo,
			@Param("curso") int curso, @Param("disciplina") int disciplina);



}