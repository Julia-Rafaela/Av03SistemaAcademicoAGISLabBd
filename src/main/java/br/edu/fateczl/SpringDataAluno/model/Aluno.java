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
@Table(name = "aluno")

@NamedNativeQuery(name = "Aluno.fn_alunos", query = "SELECT * FROM fn_alunos()", resultClass = Aluno.class)

@NamedStoredProcedureQuery(name = "Aluno.GerenciarMatricula", procedureName = "GerenciarMatricula", parameters = {
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "acao", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "cpf", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "ano_ingresso", type = int.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "conclusao_segundo_grau", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "data_nascimento", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "email_corporativo", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "email_pessoal", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "instituicao_conclusao", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "nome", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "nome_social", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "pontuacao_vestibular", type = double.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "posicao_vestibular", type = int.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "semestre_ingresso", type = int.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "semestre_limite_graduacao", type = int.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "curso", type = Integer.class),
		@StoredProcedureParameter(mode = ParameterMode.OUT, name = "saida", type = String.class) })

public class Aluno {


	@Id
	@Column(name = "cpf", nullable = false)	
	private String cpf;
	
	@Column(name = "ra", nullable = false)
	private long ra;

	@Column(name = "nome", nullable = false)
	private String nome;
	
	@Column(name = "nome_social", nullable = false)
	private String nome_social;
	
	@Column(name = "data_nascimento", nullable = false)
	private String data_nascimento;
	
	@Column(name = "email_pessoal", nullable = false)
	private String email_pessoal;
	
	@Column(name = "email_corporativo", nullable = false)
	private String email_corporativo;
	
	@Column(name = "conclusao_segundo_grau", nullable = false)
	private String conclusao_segundo_grau;
	
	@Column(name = "instituicao_conclusao", nullable = false)
	private String instituicao_conclusao;

	@Column(name = "pontuacao_vestibular", nullable = false)
	private double pontuacao_vestibular;
	
	@Column(name = "posicao_vestibular", nullable = false)
	private int posicao_vestibular;
	
	@Column(name = "ano_ingresso", nullable = false)
	private int ano_ingresso;
	
	@Column(name = "ano_limite_graduacao", nullable = false)
	private int ano_limite_graduacao;
	
	
	@Column(name = "semestre_ingresso", nullable = false)
	private int semestre_ingresso;
	
	@Column(name = "semestre_limite_graduacao", nullable = false)
	private int semestre_limite_graduacao;
	
	@ManyToOne(cascade = CascadeType.ALL, targetEntity = Curso.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "curso", nullable = false)
	private Curso curso;

	
	@Override
    public String toString() {
        return nome;
    }

}
