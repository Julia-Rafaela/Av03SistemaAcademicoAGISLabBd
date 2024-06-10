package br.edu.fateczl.SpringDataAluno.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import br.edu.fateczl.SpringDataAluno.model.Disciplina;

public interface IDisciplinaRepository extends JpaRepository<Disciplina, Integer> {
    Disciplina findByNome(String nome);
    
	List<Disciplina> fn_disciplinas();

	@Procedure(name = "Disciplina.GerenciarDisciplina")
	String GerenciarDisciplina(@Param("acao") String acao, @Param("codigo") int codigo, @Param("nome") String nome,
			@Param("horas_inicio") String horas_inicio, @Param("duracao") int duracao,
			@Param("dia_semana") String dia_semana, @Param("professor") int professor);

}
