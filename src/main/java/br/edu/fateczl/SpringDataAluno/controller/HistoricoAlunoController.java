package br.edu.fateczl.SpringDataAluno.controller;

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
import br.edu.fateczl.SpringDataAluno.model.Curso;
import br.edu.fateczl.SpringDataAluno.model.Historico;
import br.edu.fateczl.SpringDataAluno.model.Matricula;
import br.edu.fateczl.SpringDataAluno.repository.IAlunoRepository;
import br.edu.fateczl.SpringDataAluno.repository.ICursoRepository;
import br.edu.fateczl.SpringDataAluno.repository.IHistoricoRepository;
import br.edu.fateczl.SpringDataAluno.repository.IMatriculaRepository;

@Controller
public class HistoricoAlunoController {

    @Autowired
    private IAlunoRepository aRep;

    @Autowired
    private ICursoRepository cRep;

    @Autowired
    private IHistoricoRepository hRep;

    @Autowired
    private IMatriculaRepository mRep;

    @RequestMapping(name = "historicoAluno", value = "/historicoAluno", method = RequestMethod.GET)
    public ModelAndView indexGet(@RequestParam Map<String, String> allRequestParam, ModelMap model) {
        String cpf = allRequestParam.get("cpf");
        return handleRequest(cpf, model);
    }

    @RequestMapping(name = "historicoAluno", value = "/historicoAluno", method = RequestMethod.POST)
    public ModelAndView indexPost(@RequestParam Map<String, String> allRequestParam, ModelMap model) {
        String cpf = allRequestParam.get("aluno");
        return handleRequest(cpf, model);
    }

    private ModelAndView handleRequest(String cpf, ModelMap model) {
        List<Historico> historicos = new ArrayList<>();
        List<Aluno> alunos = new ArrayList<>();
        List<Curso> cursos = new ArrayList<>();
        List<Matricula> matriculas = new ArrayList<>();
        String erro = "";

        try {
            historicos = ListarHistorico(cpf);
            alunos = aRep.findAll();
            cursos = cRep.findAll();
            matriculas = mRep.findAll();
        } catch (Exception e) {
            erro = e.getMessage();
        }

        model.addAttribute("historicos", historicos);
        model.addAttribute("alunos", alunos);
        model.addAttribute("cursos", cursos);
        model.addAttribute("matriculas", matriculas);
        model.addAttribute("erro", erro);

        return new ModelAndView("historicoAluno");
    }

    private List<Historico> ListarHistorico(String cpf) {
        List<Historico> historicos = new ArrayList<>();

        List<Map<String, Object>> results = hRep.fn_historico(cpf);

        for (Map<String, Object> result : results) {
            Historico h = new Historico();
            h.setCodigo(0);

            Aluno a = new Aluno();
            a.setRa((Long) result.get("RA"));
            a.setNome((String) result.get("NomeCompleto"));

            Curso c = new Curso();
            c.setNome((String) result.get("NomeCurso"));

            Matricula m = new Matricula();
            m.setData_m((String) result.get("DataPrimeiraMatricula"));

            a.setPontuacao_vestibular((Double) result.get("PontuacaoVestibular"));
            a.setPosicao_vestibular((int) result.get("PosicaoVestibular"));

            h.setAluno(a);
            h.setCurso(c);
            h.setMatricula(m);

            historicos.add(h);
        }

        return historicos;
    }
}
