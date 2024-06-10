package br.edu.fateczl.SpringDataAluno.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureParameter;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "professor")
@NamedStoredProcedureQuery(name = "Professor.GerenciarProfessor", procedureName = "GerenciarProfessor", parameters = {
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "op", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "codigo", type = Integer.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "nome", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.OUT, name = "saida", type = String.class) })
public class Professor {
	@Id
	@Column(name = "codigo", nullable = false)
    private int codigo;
	
	@Column(name = "nome", length = 255, nullable = false)
	private String nome;

	@Override
	public String toString() {
		return nome;
	}
}
