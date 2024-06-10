package br.edu.fateczl.SpringDataAluno.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "chamada")
@NamedNativeQuery(name = "Chamada.fn_chamadas", query = "SELECT * FROM fn_chamadas(?1)", resultClass = Chamada.class)
@NamedNativeQuery(name = "Chamada.fn_chamadasAlter", query = "SELECT * FROM fn_chamadasAlter(?1)", resultClass = Chamada.class)
@NamedStoredProcedureQuery(name = "Chamada.GerenciarChamada", procedureName = "GerenciarChamada", parameters = {
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "acao", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "id", type = int.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "codigo", type = int.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "disciplina", type = Integer.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "aluno", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "falta", type = int.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "data", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.OUT, name = "saida", type = String.class) })

public class Chamada {

	@Id
	@Column(name = "id")
	private int id;

	@Column(name = "codigo")
	private int codigo;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "aluno", referencedColumnName = "cpf", insertable = false, updatable = false)
	private Aluno aluno;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "disciplina", referencedColumnName = "codigo", insertable = false, updatable = false)
	private Disciplina disciplina;

	@Column(name = "falta")
	private int falta;

	@Column(name = "data", nullable = false)
	private String data;
}
