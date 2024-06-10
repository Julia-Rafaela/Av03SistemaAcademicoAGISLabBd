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
@Table(name = "telefone")

@NamedNativeQuery(name = "Telefone.fn_telefones", query = "SELECT * FROM fn_telefones(?1)", resultClass = Telefone.class)

@NamedStoredProcedureQuery(name = "Telefone.GerenciarTelefone", procedureName = "GerenciarTelefone", parameters = {
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "acao", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "codigo", type = int.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "aluno", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "telefone", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.OUT, name = "saida", type = String.class) })

public class Telefone {

	@Id
	@Column(name = "codigo", nullable = false)	
	private int codigo;
	
	@Column(name = "telefone", nullable = false)
	private String telefone ;
	
	@ManyToOne(cascade = CascadeType.ALL, targetEntity = Aluno.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "aluno", nullable = false)
	private Aluno aluno;

	
	@Override
    public String toString() {
        return telefone;
    }

}
