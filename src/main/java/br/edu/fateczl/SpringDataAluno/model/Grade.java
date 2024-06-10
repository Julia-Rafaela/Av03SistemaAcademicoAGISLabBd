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
@Table(name = "grade")

@NamedNativeQuery(name = "Grade.fn_grade", query = "SELECT * FROM fn_grade(?1)", resultClass = Grade.class)

@NamedStoredProcedureQuery(name = "Grade.GerenciarGrade", procedureName = "GerenciarGrade", parameters = {
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "acao", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "codigo", type = int.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "curso", type = Integer.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "disciplina", type = Integer.class),
		@StoredProcedureParameter(mode = ParameterMode.OUT, name = "saida", type = String.class) })

public class Grade {


	@Id
	@Column(name = "codigo", nullable = false)	
	private int codigo;
	
	@ManyToOne(cascade = CascadeType.ALL, targetEntity = Curso.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "curso", nullable = false)
	private Curso curso;
	
	@ManyToOne(cascade = CascadeType.ALL, targetEntity = Disciplina.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "disciplina", nullable = false)
	private Disciplina disciplina;


}

