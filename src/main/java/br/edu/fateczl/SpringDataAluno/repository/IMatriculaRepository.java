package br.edu.fateczl.SpringDataAluno.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import br.edu.fateczl.SpringDataAluno.model.Matricula;

public interface IMatriculaRepository extends JpaRepository<Matricula, Integer> {
	List<Matricula> fn_matricula(String alunoId);

	List<Matricula> findByAlunoCpf(String cpf);

	@Procedure(name = "Matricula.GerenciarMatriculaD")
	String GerenciarMatriculaD(@Param("acao") String acao, @Param("codigo") int codigo, @Param("aluno") String aluno,
			@Param("disciplina") int disciplina, @Param("status_m") String status_m, @Param("data_m") String data_m);
}
