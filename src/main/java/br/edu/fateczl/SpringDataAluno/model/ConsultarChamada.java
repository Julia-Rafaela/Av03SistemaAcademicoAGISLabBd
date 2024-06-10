package br.edu.fateczl.SpringDataAluno.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedNativeQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@NamedNativeQuery(
    name = "Chamada.fn_consultaC",
    query = "SELECT * FROM fn_consultaC(?1)",
    resultClass = ConsultarChamada.class
)
public class ConsultarChamada {

    @Id
    @Column(name = "codigo")
    private int codigo;

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = Aluno.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno", nullable = false)
    private Aluno aluno;

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = Chamada.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "chamada", nullable = false)
    private Chamada chamada;

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = Disciplina.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "disciplina", nullable = false)
    private Disciplina disciplina;
}
