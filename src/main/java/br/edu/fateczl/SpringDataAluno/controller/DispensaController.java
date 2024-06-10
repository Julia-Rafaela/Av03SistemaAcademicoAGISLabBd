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
import br.edu.fateczl.SpringDataAluno.model.Disciplina;
import br.edu.fateczl.SpringDataAluno.model.Dispensa;
import br.edu.fateczl.SpringDataAluno.repository.IAlunoRepository;
import br.edu.fateczl.SpringDataAluno.repository.IDisciplinaRepository;
import br.edu.fateczl.SpringDataAluno.repository.IDispensaRepository;


@Controller
public class DispensaController {
    @Autowired
    private IAlunoRepository aRep;

    @Autowired
    private IDisciplinaRepository dRep;

    @Autowired
    private IDispensaRepository dsRep;

    @RequestMapping(name = "solicitarDispensa", value = "/solicitarDispensa", method = RequestMethod.GET)
    public ModelAndView solicitarDispensaGet(ModelMap model) {
        List<Aluno> alunos = new ArrayList<>();
        List<Disciplina> disciplinas = new ArrayList<>();

        try {
            alunos = listarAlunos();
            disciplinas = listarDisciplinas();
        } catch (SQLException | ClassNotFoundException e) {
            model.addAttribute("erro", e.getMessage());
        }

        model.addAttribute("alunos", alunos);
        model.addAttribute("disciplinas", disciplinas);

        return new ModelAndView("solicitarDispensa", model);
    }

    @RequestMapping(name = "solicitarDispensa", value = "/solicitarDispensa", method = RequestMethod.POST)
    public ModelAndView solicitarDispensaPost(@RequestParam Map<String, String> allRequestParam, ModelMap model) {

        String cmd = allRequestParam.get("botao");
        String data = allRequestParam.get("data_s");
        String alunoCpf = allRequestParam.get("cpf");
        String disciplinaCodigo = allRequestParam.get("disciplina");
        String instituicao = allRequestParam.get("instituicao");

        String saida = "";
        String erro = "";

        List<Dispensa> dispensas = new ArrayList<>();
        List<Aluno> alunos = new ArrayList<>();
        List<Disciplina> disciplinas = new ArrayList<>();

        try {
            alunos = listarAlunos();
            disciplinas = listarDisciplinas();

            if ("Solicitar".equals(cmd)) {
                if (data == null || alunoCpf == null || disciplinaCodigo == null || instituicao == null) {
                    throw new IllegalArgumentException("Todos os campos são obrigatórios.");
                }

                if (!disciplinaCodigo.matches("\\d+")) {
                    throw new IllegalArgumentException("Código de disciplina inválido.");
                }

                Dispensa ds = new Dispensa();
                ds.setData_s(data);
                ds.setInstituicao(instituicao);

                Aluno aluno = buscarAluno(alunoCpf);
                ds.setAluno(aluno);

                int codigoDisciplina = Integer.parseInt(disciplinaCodigo);
                if (codigoDisciplina <= 0) {
                    throw new IllegalArgumentException("Código de disciplina inválido.");
                }

                Disciplina disciplina = buscarDisciplina(codigoDisciplina);
                ds.setDisciplina(disciplina);

                saida = cadastrarDispensa(ds);

            } else if ("Listar".equals(cmd)) {
                dispensas = listarDispensa(alunoCpf);
            }

        } catch (SQLException | ClassNotFoundException | IllegalArgumentException e) {
            e.printStackTrace();
            erro = e.getMessage();
        } finally {
            model.addAttribute("saida", saida);
            model.addAttribute("erro", erro);
            model.addAttribute("dispensas", dispensas);
            model.addAttribute("alunos", alunos);
            model.addAttribute("disciplinas", disciplinas);
        }

        return new ModelAndView("solicitarDispensa", model);
    }

    private String cadastrarDispensa(Dispensa ds) throws SQLException, ClassNotFoundException {
        return dsRep.iudDispensa("I", ds.getAluno().getCpf(), ds.getDisciplina().getCodigo(),
                                 ds.getData_s(), ds.getInstituicao());
    }

    private List<Dispensa> listarDispensa(String aluno) throws SQLException, ClassNotFoundException {
    	List<Dispensa> dispensas = new ArrayList<>();
        
        List<Map<String, Object>> results = dsRep.fn_Dispensa(aluno);
        
        for (Map<String, Object> result : results) {
            Dispensa ds = new Dispensa();
            ds.setCodigo ((int) result.get("codigo"));
            
            Disciplina d = new Disciplina();
            d.setNome((String) result.get("disciplina"));
            ds.setDisciplina(d);

            Aluno a = new Aluno();
        
            a.setNome((String) result.get("aluno"));
            ds.setAluno(a);
            
            ds.setData_s((String) result.get("data_s"));
            ds.setInstituicao((String) result.get("instituicao"));
            dispensas.add(ds);
        }
        
        return dispensas;
    }

    private Aluno buscarAluno(String cpf) throws SQLException, ClassNotFoundException {
        return aRep.findById(cpf)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));
    }

    private Disciplina buscarDisciplina(int codigo) throws SQLException, ClassNotFoundException {
        return dRep.findById(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Disciplina não encontrada"));
    }

    private List<Aluno> listarAlunos() throws SQLException, ClassNotFoundException {
        return aRep.findAll();
    }

    private List<Disciplina> listarDisciplinas() throws SQLException, ClassNotFoundException {
        return dRep.findAll();
    }
}