package br.edu.fateczl.SpringDataAluno.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.fateczl.SpringDataAluno.model.MatriculaAluno;

public interface IMatriculaAlunoRepository extends JpaRepository<MatriculaAluno, Integer> {
	    @Query(nativeQuery = true, value = "SELECT * FROM dbo.fn_Aprovados(:cpf)")
	    List<Map<String, Object>> fn_Aprovados(@Param("cpf") String cpf);
}
