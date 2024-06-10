package br.edu.fateczl.SpringDataAluno.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import br.edu.fateczl.SpringDataAluno.model.Notas;

public interface INotasRepository extends JpaRepository<Notas, Integer> {

	@Query("SELECT n FROM Notas n WHERE n.disciplina.codigo = :disciplina")
	List<Notas> ListarNotasEMedias(@Param("disciplina") int disciplina);

	@Procedure(name = "Notas.InserirNotas")
	String InserirNotas(@Param("acao") String acao, @Param("disciplina") int disciplina, @Param("aluno") String aluno,
			@Param("nota1") double nota1, @Param("nota2") double nota2,
			@Param("notaRecuperacao") double notaRecuperacao);

}
