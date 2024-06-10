package br.edu.fateczl.SpringDataAluno.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import br.edu.fateczl.SpringDataAluno.model.Chamada;

public interface IChamadaRepository extends JpaRepository<Chamada, Integer> {
	
    @Query(nativeQuery = true, value = "SELECT * FROM fn_chamadas(:codigoDisciplina)")
    List<Map<String, Object>> fn_chamadas(@Param("codigoDisciplina") int codigoDisciplina);

    
   @Query(value = "SELECT * FROM fn_chamadasAlter(:codigo)", nativeQuery = true)
    List<Map<String, Object>> fn_matriculaAlter(@Param("codigo") int codigo);

    @Procedure(name = "Chamada.GerenciarChamada")
    String gerenciarChamada(
        @Param("acao") String acao, 
        @Param("id") int id,
        @Param("codigo") int codigo,
        @Param("disciplina") int disciplina, 
        @Param("aluno") String aluno, 
        @Param("falta") int falta, 
        @Param("data") String data
    );
}
