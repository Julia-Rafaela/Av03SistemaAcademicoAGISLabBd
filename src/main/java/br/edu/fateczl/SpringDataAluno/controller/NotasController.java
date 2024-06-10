package br.edu.fateczl.SpringDataAluno.controller;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import br.edu.fateczl.SpringDataAluno.model.Aluno;
import br.edu.fateczl.SpringDataAluno.model.Disciplina;
import br.edu.fateczl.SpringDataAluno.model.Notas;
import br.edu.fateczl.SpringDataAluno.repository.IAlunoRepository;
import br.edu.fateczl.SpringDataAluno.repository.IDisciplinaRepository;
import br.edu.fateczl.SpringDataAluno.repository.INotasRepository;

@Controller
public class NotasController {

    @Autowired
    private INotasRepository nRep;

    @Autowired
    private IDisciplinaRepository dRep;
    
    @Autowired
    private IAlunoRepository aRep;

    @RequestMapping(value = "/notas", method = RequestMethod.GET)
    public ModelAndView indexGet(@RequestParam Map<String, String> allRequestParam, ModelMap model) {
        String saida = "";
        String erro = "";
        List<Notas> notas = new ArrayList<>();
        List<Aluno> alunos = new ArrayList<>();
        List<Disciplina> disciplinas = new ArrayList<>();

        try {
            String cmd = allRequestParam.get("cmd");
            String disciplinaId = allRequestParam.get("disciplina");

            alunos = listarAlunos();
            disciplinas = listarDisciplinas();

            if (disciplinaId != null && !disciplinaId.isEmpty()) {
                notas = listarNotas(Integer.parseInt(disciplinaId));
            }

        } catch (NumberFormatException e) {
            erro = "Erro ao converter dados numéricos.";
        } catch (SQLException | ClassNotFoundException e) {
            erro = e.getMessage();
        } finally {
            model.addAttribute("saida", saida);
            model.addAttribute("erro", erro);
            model.addAttribute("notas", notas);
            model.addAttribute("alunos", alunos);
            model.addAttribute("disciplinas", disciplinas);
        }

        return new ModelAndView("notas");
    }

    @RequestMapping(value = "/notas", method = RequestMethod.POST)
    @Transactional
    public ModelAndView indexPost(@RequestParam Map<String, String> allRequestParam, ModelMap model) {
        String cmd = allRequestParam.get("botao");
        String alunoCpf = allRequestParam.get("aluno");
        String disciplinaId = allRequestParam.get("disciplina");
        String nota1 = allRequestParam.get("nota1");
        String nota2 = allRequestParam.get("nota2");
        String notaRecuperacao = allRequestParam.get("notaRecuperacao");
        String codigo = allRequestParam.get("codigo");

        String saida = "";
        String erro = "";

        List<Aluno> alunos = new ArrayList<>();
        List<Disciplina> disciplinas = new ArrayList<>();

        try {
            alunos = listarAlunos();
            disciplinas = listarDisciplinas();

            if (cmd != null && cmd.equals("Listar")) {
                List<Notas> notas = new ArrayList<>();
                if (disciplinaId != null && !disciplinaId.isEmpty()) {
                    notas = listarNotas(Integer.parseInt(disciplinaId));
                }
                model.addAttribute("notas", notas);
            } else {
                if (alunoCpf != null && !alunoCpf.isEmpty() && disciplinaId != null && !disciplinaId.isEmpty() && nota1 != null
                        && !nota1.isEmpty() && nota2 != null && !nota2.isEmpty()) {
                    Notas n = new Notas();
                    n.setAluno(new Aluno());
                    n.getAluno().setCpf(alunoCpf);
                    n.setDisciplina(new Disciplina());
                    n.getDisciplina().setCodigo(Integer.parseInt(disciplinaId));

                    if (codigo != null && !codigo.isEmpty()) {
                        n.setCodigo(Integer.parseInt(codigo));
                    }

                    if (nota1 != null && !nota1.isEmpty()) {
                        n.setNota1(Double.parseDouble(nota1));
                    }
                    if (nota2 != null && !nota2.isEmpty()) {
                        n.setNota2(Double.parseDouble(nota2));
                    }
                    if (notaRecuperacao != null && !notaRecuperacao.isEmpty()) {
                        n.setNotaRecuperacao(Double.parseDouble(notaRecuperacao));
                    }

                    if (cmd != null && (cmd.contains("Cadastrar") || cmd.contains("Alterar"))) {
                        if (cmd.contains("Cadastrar")) {
                            saida = cadastrarNota(n); 
                        } else if (cmd.contains("Alterar")) {
                            saida = alterarNota(n); 
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            erro = "Erro ao converter dados numéricos.";
        } catch (SQLException | ClassNotFoundException e) {
            erro = e.getMessage();
        } catch (IllegalArgumentException e) {
            erro = e.getMessage();
        } finally {
            model.addAttribute("saida", saida); 
            model.addAttribute("erro", erro);
            model.addAttribute("alunos", alunos);
            model.addAttribute("disciplinas", disciplinas);
        }

        return new ModelAndView("notas");
    }

    @Transactional(readOnly = true)
    private String alterarNota(Notas n) throws SQLException, ClassNotFoundException {
        return nRep.InserirNotas("U", n.getDisciplina().getCodigo(), n.getAluno().getCpf(), n.getNota1(), n.getNota2(), n.getNotaRecuperacao());
    }

    @Transactional(readOnly = true)
    private String cadastrarNota(Notas n) throws SQLException, ClassNotFoundException {
        return nRep.InserirNotas("I", n.getDisciplina().getCodigo(), n.getAluno().getCpf(), n.getNota1(), n.getNota2(), n.getNotaRecuperacao());
    }

    @Transactional(readOnly = true)
    private List<Disciplina> listarDisciplinas() throws SQLException, ClassNotFoundException {
        return dRep.findAll();
    }

    @Transactional(readOnly = true)
    private List<Aluno> listarAlunos() throws SQLException, ClassNotFoundException {
        return aRep.findAll();
    }

    @Transactional(readOnly = true)
    private List<Notas> listarNotas(int disciplinaId) throws SQLException, ClassNotFoundException {
        return nRep.ListarNotasEMedias(disciplinaId);
    }
}
