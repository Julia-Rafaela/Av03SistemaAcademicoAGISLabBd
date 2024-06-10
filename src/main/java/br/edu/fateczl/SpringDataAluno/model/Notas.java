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

@Entity
@Table(name = "notas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NamedNativeQuery(name = "Notas.ListarNotasEMedias", query = "SELECT * FROM ListarNotasEMedias(?1)", resultClass = Notas.class)

@NamedStoredProcedureQuery(name = "Notas.InserirNotas", procedureName = "InserirNotas", parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "acao", type = String.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "disciplina", type = Integer.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "aluno", type = String.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "nota1", type = double.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "nota2", type = double.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "notaRecuperacao", type = double.class),
        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "saida", type = String.class)
})
public class Notas {

	@Id
	@Column(name = "codigo", nullable = false)	
	private int codigo;

    @Column(name = "nota1")
    private double nota1;

    @Column(name = "nota2")
    private double nota2;

    @Column(name = "notaRecuperacao")
    private double notaRecuperacao;

    @Column(name = "media", nullable = false)
    private double media;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "disciplina", nullable = false)
    private Disciplina disciplina;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno", nullable = false)
    private Aluno aluno;

}
