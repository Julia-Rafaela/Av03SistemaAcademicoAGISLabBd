package br.edu.fateczl.SpringDataAluno.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.fateczl.SpringDataAluno.model.ConsultarChamada;

public interface IConsultarRepository extends JpaRepository<ConsultarChamada, Integer> {
    
    @Query(nativeQuery = true, value = "SELECT * FROM fn_consultaC(:aluno)")
    List<Map<String, Object>> fn_consultaC(@Param("aluno") String aluno);
}

