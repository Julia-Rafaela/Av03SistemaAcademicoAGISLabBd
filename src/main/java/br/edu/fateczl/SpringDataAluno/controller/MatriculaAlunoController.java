package br.edu.fateczl.SpringDataAluno.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import br.edu.fateczl.SpringDataAluno.model.Aluno;
import br.edu.fateczl.SpringDataAluno.model.Chamada;
import br.edu.fateczl.SpringDataAluno.model.Disciplina;
import br.edu.fateczl.SpringDataAluno.model.MatriculaAluno;
import br.edu.fateczl.SpringDataAluno.model.Notas;
import br.edu.fateczl.SpringDataAluno.model.Professor;
import br.edu.fateczl.SpringDataAluno.repository.IAlunoRepository;
import br.edu.fateczl.SpringDataAluno.repository.IChamadaRepository;
import br.edu.fateczl.SpringDataAluno.repository.IDisciplinaRepository;
import br.edu.fateczl.SpringDataAluno.repository.IMatriculaAlunoRepository;
import br.edu.fateczl.SpringDataAluno.repository.INotasRepository;
import br.edu.fateczl.SpringDataAluno.repository.IProfessorRepository;

@Controller
public class MatriculaAlunoController {

    @Autowired
    private IProfessorRepository pRep;

    @Autowired
    private IMatriculaAlunoRepository maRep;

    @Autowired
    private IChamadaRepository cRep;

    @Autowired
    private INotasRepository nRep;

    @Autowired
    private IDisciplinaRepository dRep;

    @Autowired
    private IAlunoRepository aRep;

    @RequestMapping(name = "matriculaAluno", value = "/matriculaAluno", method = RequestMethod.GET)
    public ModelAndView indexGet(@RequestParam Map<String, String> allRequestParam, ModelMap model) {
        String cpf = allRequestParam.get("cpf");
        String disciplina = allRequestParam.get("CodigoDisciplina");
        return handleRequest(cpf, model, disciplina);
    }

    @RequestMapping(name = "matriculaAluno", value = "/matriculaAluno", method = RequestMethod.POST)
    public ModelAndView indexPost(@RequestParam Map<String, String> allRequestParam, ModelMap model) {
        String cpf = allRequestParam.get("aluno");
        String disciplina = allRequestParam.get("CodigoDisciplina");
        return handleRequest(cpf, model, disciplina);
    }

    private ModelAndView handleRequest(String cpf, ModelMap model, String disciplina) {
        List<MatriculaAluno> historicosM = new ArrayList<>();
        List<Disciplina> disciplinas = new ArrayList<>();
        List<Professor> professores = new ArrayList<>();
        List<Chamada> chamadas = new ArrayList<>();
        List<Notas> notass = new ArrayList<>();
        List<Aluno> alunos = new ArrayList<>();
        String erro = "";

        try {
            historicosM = listarMatriculaAluno(cpf);
            disciplinas = dRep.findAll();
            professores = pRep.findAll();
            if (disciplina != null && !disciplina.isEmpty()) {
                int disciplinaId = tryParseInt(disciplina);
                if (disciplinaId != -1) {
                    chamadas = listarChamadas(disciplinaId);
                }
            }
            alunos = aRep.findAll();
        } catch (SQLException | ClassNotFoundException e) {
            erro = e.getMessage();
        }

        model.addAttribute("historicosM", historicosM);
        model.addAttribute("disciplinas", disciplinas);
        model.addAttribute("notass", notass);
        model.addAttribute("chamadas", chamadas);
        model.addAttribute("professores", professores);
        model.addAttribute("alunos", alunos);
        model.addAttribute("erro", erro);

        return new ModelAndView("matriculaAluno");
    }

    private int tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private List<Chamada> listarChamadas(int disciplina) throws SQLException, ClassNotFoundException {
        List<Chamada> chamadas = new ArrayList<>();

        List<Map<String, Object>> results = cRep.fn_chamadas(disciplina);

        for (Map<String, Object> result : results) {
            Chamada chamada = new Chamada();

            Disciplina d = new Disciplina();
            d.setNome((String) result.get("disciplina"));
            chamada.setDisciplina(d);

            Aluno a = new Aluno();
            a.setCpf((String) result.get("cpf"));
            a.setNome((String) result.get("aluno"));
            chamada.setAluno(a);

            chamada.setCodigo(0); 
            chamada.setData(""); 
            chamada.setFalta(0); 

            chamadas.add(chamada);
        }

        return chamadas;
    }

    private List<MatriculaAluno> listarMatriculaAluno(String cpf) throws SQLException, ClassNotFoundException {
        List<MatriculaAluno> matriculas = new ArrayList<>();

        List<Map<String, Object>> results = maRep.fn_Aprovados(cpf);

        for (Map<String, Object> result : results) {
            MatriculaAluno ma = new MatriculaAluno();
            ma.setCodigo(0);

            Disciplina d = new Disciplina();
            d.setCodigo((int) result.get("CodigoDisciplina"));
            d.setNome((String) result.get("NomeDisciplina"));
            ma.setDisciplina(d);

            Professor p = new Professor();
            p.setNome((String) result.get("NomeProfessor"));
            ma.setProfessor(p);

            Notas n = new Notas();
            n.setMedia((double) result.get("NotaFinal"));
            ma.setNotas(n);

            Chamada c = new Chamada();
            c.setFalta((int) result.get("QuantidadeFaltas"));
            ma.setChamada(c);

            matriculas.add(ma);
        }

        return matriculas;
    }
}