package br.edu.fateczl.SpringDataAluno.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Relatorio {
    
    @Id
    @Column(name = "codigo", nullable = false)
    private int codigo;

    @ManyToOne
    @JoinColumn(name = "disciplina", nullable = false)
    private Disciplina disciplina;

}
