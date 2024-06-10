package br.edu.fateczl.SpringDataAluno.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.OneToMany;
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
@Table(name = "matricula")

@NamedNativeQuery(name = "Matricula.fn_matricula", query = "SELECT * FROM fn_matricula(:aluno)", resultClass = Matricula.class)

@NamedStoredProcedureQuery(name = "Matricula.GerenciarMatriculaD", procedureName = "GerenciarMatriculaD", parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "acao", type = String.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "codigo", type = Integer.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "aluno", type = String.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "disciplina", type = Integer.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "data_m", type = String.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "status_m", type = String.class),
        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "saida", type = String.class) })

public class Matricula {

    @Id
    @Column(name = "codigo", nullable = false)
    private int codigo;

    @ManyToOne
    @JoinColumn(name = "aluno", nullable = false)
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "disciplina", nullable = false)
    private Disciplina disciplina;

    @Column(name = "status_m")
    private String status_m;

    @Column(name = "data_m", nullable = false)
    private String data_m;

    @Column(name = "nota_final")
    private String nota_final;
}
