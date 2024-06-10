package br.edu.fateczl.SpringDataAluno.controller;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import br.edu.fateczl.SpringDataAluno.model.Matricula;
import br.edu.fateczl.SpringDataAluno.model.Notas;
import br.edu.fateczl.SpringDataAluno.repository.IAlunoRepository;
import br.edu.fateczl.SpringDataAluno.repository.IDisciplinaRepository;
import br.edu.fateczl.SpringDataAluno.repository.IMatriculaRepository;
import br.edu.fateczl.SpringDataAluno.repository.INotasRepository;
@Controller
public class MatriculaController {
	
    @Autowired
    private IMatriculaRepository mRep;
	
    @Autowired
    private IDisciplinaRepository dRep;

    @Autowired
    private IAlunoRepository aRep;

    
    @Autowired
    private INotasRepository nRep;
    
    @RequestMapping(value = "/matricula", method = RequestMethod.GET)
    public ModelAndView matriculaGet(@RequestParam Map<String, String> allRequestParam, ModelMap model) {
        String cmd = allRequestParam.get("cmd");
        String alunoId = allRequestParam.get("aluno");

        Matricula matricula = new Matricula();
        String saida = "";
        String erro = "";
        List<Matricula> matriculas = new ArrayList<>();
        List<Aluno> alunos = new ArrayList<>();
        List<Disciplina> disciplinas = new ArrayList<>();
        List<Notas> notas = new ArrayList<>();

        try {
            if (cmd != null && cmd.contains("alterar")) {
                String codigo = allRequestParam.get("codigo");
                if (codigo != null && !codigo.isEmpty()) {
                    int codigoInt = Integer.parseInt(codigo);
                    matricula = buscarMatricula(codigoInt);
                }
            }

            matriculas = listarMatriculas(alunoId);
            alunos = listarAlunos();
            disciplinas = listarDisciplina();
            notas = listarNotas();
        } catch (NumberFormatException e) {
            erro = "Erro ao converter dados numéricos.";
        } catch (Exception e) {
            erro = e.getMessage();
        } finally {
            model.addAttribute("saida", saida);
            model.addAttribute("erro", erro);
            model.addAttribute("matricula", matricula);
            model.addAttribute("matriculas", matriculas);
            model.addAttribute("alunos", alunos);
            model.addAttribute("disciplinas", disciplinas);
            model.addAttribute("notas", notas);
        }

        return new ModelAndView("matricula");
    }

   

	@RequestMapping(value = "/matricula", method = RequestMethod.POST)
    public ModelAndView matriculaPost(@RequestParam Map<String, String> allRequestParam, ModelMap model) {
        String cmd = allRequestParam.get("botao");
        String alunoId = allRequestParam.get("aluno");
        String disciplinaId = allRequestParam.get("disciplina");
        String data = allRequestParam.get("data_m");
        String codigo = allRequestParam.get("codigo");

        String saida = "";
        String erro = "";
        Matricula matricula = new Matricula();

        List<Matricula> matriculas = new ArrayList<>();
        List<Aluno> alunos = new ArrayList<>();
        List<Disciplina> disciplinas = new ArrayList<>();
        List<Notas> notas = new ArrayList<>();

        try {
            alunos = listarAlunos();
            disciplinas = listarDisciplina(); 
            notas = listarNotas();

            if (alunoId != null && !alunoId.isEmpty() && disciplinaId != null && !disciplinaId.isEmpty()) {
                if (cmd != null && (cmd.contains("Cadastrar") || cmd.contains("Alterar"))) {
                    matricula.setAluno(new Aluno());
                    matricula.getAluno().setCpf(alunoId);
                    matricula.setDisciplina(new Disciplina());

                    matricula.getDisciplina().setCodigo(Integer.parseInt(disciplinaId));

                    matricula.setData_m(data);

                    if (cmd.contains("Cadastrar")) {
                        saida = cadastrarMatricula(matricula);
                    } else if (cmd.contains("Alterar")) {
                        if (codigo != null && !codigo.isEmpty()) {
                            matricula.setCodigo(Integer.parseInt(codigo));
                            saida = alterarMatricula(matricula);
                           
                        } else {
                            throw new IllegalArgumentException("Código não fornecido.");
                        }
                    }
                }
            } else {
                throw new IllegalArgumentException("Aluno ou disciplina não especificados.");
            }

            if (cmd != null && cmd.contains("Listar")) {
                matriculas = listarMatriculas(alunoId);
                disciplinas = listarDisciplinas(alunoId); 
            }
        } catch (Exception e) {
            erro = e.getMessage();
        } finally {
            model.addAttribute("saida", saida);
            model.addAttribute("erro", erro);
            model.addAttribute("matricula", matricula);
            model.addAttribute("matriculas", matriculas);
            model.addAttribute("alunos", alunos);
            model.addAttribute("disciplinas", disciplinas);
            model.addAttribute("notas", notas);
        }

        return new ModelAndView("matricula");
    }

    private List<Matricula> listarMatriculas(String alunoId) {
    	 if (alunoId != null && !alunoId.isEmpty()) {
    	        return mRep.findByAlunoCpf(alunoId);
    	    }
    	    return new ArrayList<>();
    }

    private Matricula buscarMatricula(int codigo) {
        Optional<Matricula> matriculaOptional = mRep.findById(codigo);
        return matriculaOptional.orElse(null);
    }

    private String cadastrarMatricula(Matricula m) {
        return mRep.GerenciarMatriculaD("I", m.getCodigo(), m.getAluno().getCpf(), m.getDisciplina().getCodigo(), null, m.getData_m());
    }

    private String alterarMatricula(Matricula m) {
        return mRep.GerenciarMatriculaD("U", m.getCodigo(), m.getAluno().getCpf(), m.getDisciplina().getCodigo(), null, m.getData_m());
    }

    @Transactional(readOnly = true)
    private List<Disciplina> listarDisciplinas(String alunoId) {
    	 List<Disciplina> disciplinas = new ArrayList<>();
    	    if (alunoId != null && !alunoId.isEmpty()) {
    	        List<Matricula> matriculas = mRep.findByAlunoCpf(alunoId);
    	        for (Matricula matricula : matriculas) {
    	            disciplinas.add(matricula.getDisciplina());
    	        }
    	    }
    	    return disciplinas;
    }

    @Transactional(readOnly = true)
    private List<Aluno> listarAlunos() {
        return aRep.findAll();
    }

    @Transactional(readOnly = true)
    private List<Notas> listarNotas() {
        return nRep.findAll();
    }
    @Transactional(readOnly = true)
    private List<Disciplina> listarDisciplina() {
    	 return dRep.findAll();
	
	}
}