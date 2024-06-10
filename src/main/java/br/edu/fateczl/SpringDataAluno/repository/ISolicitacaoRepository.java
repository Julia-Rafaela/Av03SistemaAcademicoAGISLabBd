package br.edu.fateczl.SpringDataAluno.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import br.edu.fateczl.SpringDataAluno.model.Dispensa;

public interface ISolicitacaoRepository extends JpaRepository<Dispensa, Integer>{
	@Query(nativeQuery = true, value = "SELECT * FROM fn_Solicitacao()")
    List<Map<String, Object>> fn_Solicitacao();


	@Procedure(name = "Dispensa.iudSolicitacao")
	String iudSolicitacao(@Param("acao") String acao);

}

