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
@Entity
@Table(name = "Dispensa")
@NamedNativeQuery(name = "Dispensa.fn_Dispensa", query = "SELECT * FROM fn_Dispensa(:cpf_aluno)", resultClass = Dispensa.class)
@NamedNativeQuery(name = "Dispensa.fn_Solicitacao", query = "SELECT * FROM fn_Solicitacao()", resultClass = Dispensa.class)
@NamedStoredProcedureQuery(
    name = "Dispensa.iudDispensa",
    procedureName = "iudDispensa",
    parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "acao", type = String.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "aluno", type = String.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "disciplina", type = int.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "data_s", type = String.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "instituicao", type = String.class),
        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "saida", type = String.class)
    }
)

@NamedStoredProcedureQuery(
	    name = "Dispensa.Solicitacao",
	    procedureName = "iudSolicitacao",
	    parameters = {
	        @StoredProcedureParameter(mode = ParameterMode.IN, name = "acao", type = String.class),
	        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "saida", type = String.class)
	    }
	)
public class Dispensa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo")
    private int codigo;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno", nullable = false)
    private Aluno aluno;
    
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "disciplina", nullable = false)
    private Disciplina disciplina;

    @Column(name = "data_s", nullable = false)
    private String data_s;

    @Column(name = "instituicao", nullable = false)
    private String instituicao;
}