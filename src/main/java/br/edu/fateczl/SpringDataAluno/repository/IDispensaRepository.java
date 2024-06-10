package br.edu.fateczl.SpringDataAluno.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import br.edu.fateczl.SpringDataAluno.model.Dispensa;

public interface IDispensaRepository extends JpaRepository<Dispensa, Integer>{
	
	@Query(nativeQuery = true, value = "SELECT * FROM fn_Dispensa(:cpf_aluno)")
    List<Map<String, Object>> fn_Dispensa(@Param("cpf_aluno") String aluno);


	@Procedure(name = "Dispensa.iudDispensa")
	String iudDispensa(@Param("acao") String acao,
			@Param("aluno") String aluno,@Param("disciplina") int disciplina, @Param("data_s") String data_s, @Param("instituicao") String instituicao);

}
	