package br.edu.fateczl.SpringDataAluno.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureParameter;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table (name ="curso")

@NamedNativeQuery(name = "Curso.fn_cursos", query = "SELECT * FROM fn_cursos()", resultClass = Curso.class)

@NamedStoredProcedureQuery(name = "Curso.GerenciarCurso", procedureName = "GerenciarCurso", parameters = {
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "acao", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "codigo", type = Integer.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "nome", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "carga_horaria", type = int.class),	
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "sigla", type = String.class),	
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "nota_enade", type = double.class),	
		@StoredProcedureParameter(mode = ParameterMode.OUT, name = "saida", type = String.class) })

public class Curso {
	
	@Id
	@Column(name = "codigo", nullable = false)	
	private int codigo;
	
	@Column(name = "nome", length = 100,  nullable = false)
	private String nome;
	
	@Column(name = "carga_horaria",  nullable = false)
	private int carga_horaria;
	
	@Column(name = "sigla",  nullable = false)
	private String sigla;
	
	@Column(name = "nota_enade",  nullable = false)
	private double nota_enade;
	
	@Override
    public String toString() {
        return nome;
    }
	
}
