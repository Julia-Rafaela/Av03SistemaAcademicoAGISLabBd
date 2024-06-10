package br.edu.fateczl.SpringDataAluno.model;

import jakarta.persistence.CascadeType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "disciplina")

@NamedNativeQuery(name = "Disciplina.fn_disciplinas", query = "SELECT * FROM fn_disciplinas()", resultClass = Disciplina.class)

@NamedStoredProcedureQuery(name = "Disciplina.GerenciarDisciplina", procedureName = "GerenciarDisciplina", parameters = {
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "acao", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "codigo", type = int.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "nome", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "horas_inicio", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "duracao", type = int.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "dia_semana", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "professor", type = Integer.class),
		@StoredProcedureParameter(mode = ParameterMode.OUT, name = "saida", type = String.class) })

public class Disciplina {


	@Id
	@Column(name = "codigo", nullable = false)	
	private int codigo;
	
	@Column(name = "nome", nullable = false)
	private String nome;

	@Column(name = "horas_inicio", nullable = false)
	private String horas_inicio;
	
	@Column(name = "duracao", nullable = false)
	private int duracao;
	
	@Column(name = "dia_semana", nullable = false)
	private String dia_semana;
	
	
	@ManyToOne(cascade = CascadeType.ALL, targetEntity = Professor.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "professor", nullable = false)
	private Professor professor;

	
	@Override
    public String toString() {
        return nome;
    }

}


