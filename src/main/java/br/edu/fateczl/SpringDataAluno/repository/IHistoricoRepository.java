package br.edu.fateczl.SpringDataAluno.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.fateczl.SpringDataAluno.model.Historico;

public interface IHistoricoRepository extends JpaRepository<Historico, Integer> {
    @Query(nativeQuery = true, value = "SELECT * FROM fn_historico(:cpf)")
    List<Map<String, Object>> fn_historico(@Param("cpf") String cpf);

}
