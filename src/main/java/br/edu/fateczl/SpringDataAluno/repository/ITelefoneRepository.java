package br.edu.fateczl.SpringDataAluno.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import br.edu.fateczl.SpringDataAluno.model.Telefone;

public interface ITelefoneRepository extends JpaRepository<Telefone, Integer> {

	List<Telefone> fn_telefones(int aluno);

	@Procedure(name = "Telefone.GerenciarTelefone")
	String GerenciarTelefone(@Param("acao") String acao, @Param("codigo") int codigo, @Param("aluno") String aluno,
			@Param("telefone") String telefone);

}
