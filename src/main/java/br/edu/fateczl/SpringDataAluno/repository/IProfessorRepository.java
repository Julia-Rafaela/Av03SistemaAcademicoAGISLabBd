package br.edu.fateczl.SpringDataAluno.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import br.edu.fateczl.SpringDataAluno.model.Professor;

public interface IProfessorRepository extends JpaRepository<Professor, Integer> {
	@Procedure(name = "Professor.GerenciarProfessor")
	String iudProfessor(
			@Param("op") String acao,
		    @Param("codigo") int codigo,
		    @Param("nome") String nome

		);

}
