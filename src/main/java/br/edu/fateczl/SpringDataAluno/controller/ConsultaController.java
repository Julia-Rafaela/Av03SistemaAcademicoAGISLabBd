package br.edu.fateczl.SpringDataAluno.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import br.edu.fateczl.SpringDataAluno.model.Aluno;
import br.edu.fateczl.SpringDataAluno.model.Chamada;
import br.edu.fateczl.SpringDataAluno.model.ConsultarChamada;
import br.edu.fateczl.SpringDataAluno.model.Disciplina;
import br.edu.fateczl.SpringDataAluno.repository.IAlunoRepository;
import br.edu.fateczl.SpringDataAluno.repository.IConsultarRepository;
import br.edu.fateczl.SpringDataAluno.repository.IDisciplinaRepository;

@Controller
public class ConsultaController {

    @Autowired
    private IAlunoRepository alunoRepository;
    
    @Autowired
    private IDisciplinaRepository dRep;

    @Autowired
    private IConsultarRepository consultarRepository;

    @RequestMapping(value = "/consulta", method = RequestMethod.GET)
    public ModelAndView consultaGet(ModelMap model) {
        String saida = "";
        String erro = "";
        List<ConsultarChamada> consultas = new ArrayList<>();
        List<Aluno> alunos = new ArrayList<>();

        try {
            alunos = listarAlunos();
        } catch (SQLException | ClassNotFoundException e) {
            erro = e.getMessage();
        }

        model.addAttribute("saida", saida);
        model.addAttribute("erro", erro);
        model.addAttribute("alunos", alunos);
        model.addAttribute("consultas", consultas);
        

        return new ModelAndView("consulta");
    }

    @RequestMapping(value = "/consulta", method = RequestMethod.POST)
    public ModelAndView consultaPost(@RequestParam Map<String, String> allRequestParams, ModelMap model) {
        String saida = "";
        String erro = "";
        List<ConsultarChamada> consultas = new ArrayList<>();
        List<Aluno> alunos = new ArrayList<>();
        List<Disciplina> disciplinas = new ArrayList<>();
        

        try {
            alunos = listarAlunos();
            String cmd = allRequestParams.get("botao");
            String aluno = allRequestParams.get("aluno");
            
            		
            if (cmd != null && cmd.contains("Listar")) {
                consultas = listarChamadas(aluno);
            }
        } catch (SQLException | ClassNotFoundException e) {
            erro = e.getMessage();
        }

        model.addAttribute("saida", saida);
        model.addAttribute("erro", erro);
        model.addAttribute("alunos", alunos);
        model.addAttribute("consultas", consultas);
        model.addAttribute("disciplinas", disciplinas);

        return new ModelAndView("consulta");
    }

    private List<ConsultarChamada> listarChamadas(String aluno) throws SQLException, ClassNotFoundException {
        List<ConsultarChamada> consultas = new ArrayList<>();
        List<Map<String, Object>> results = consultarRepository.fn_consultaC(aluno);

        for (Map<String, Object> result : results) {
            ConsultarChamada cc = new ConsultarChamada();

            Disciplina d = new Disciplina();
            d.setNome((String) result.get("disciplina"));
            cc.setDisciplina(d);
            
            Chamada c = new Chamada();
            c.setData((String) result.get("data"));
       
            
            Aluno a = new Aluno();
            
            a.setCpf((String) result.get("aluno"));
            a.setNome((String) result.get("nomeAluno"));
            cc.setAluno(a);

            
         
            c.setFalta((int) result.get("falta"));
            c.setCodigo((int) result.get("codigo"));
            cc.setChamada(c);

            consultas.add(cc);
        }

        return consultas;
    }

    private List<Aluno> listarAlunos() throws SQLException, ClassNotFoundException {
        return alunoRepository.findAll();
    }
    
    private Disciplina buscarDisciplina(Disciplina d) throws SQLException, ClassNotFoundException {
        Optional<Disciplina> disciplinaOptional = dRep.findById(d.getCodigo());
        return disciplinaOptional.orElse(null);
    }
}
