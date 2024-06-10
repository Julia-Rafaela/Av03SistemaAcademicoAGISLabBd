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

import br.edu.fateczl.SpringDataAluno.model.Curso;
import br.edu.fateczl.SpringDataAluno.model.Disciplina;
import br.edu.fateczl.SpringDataAluno.model.Grade;
import br.edu.fateczl.SpringDataAluno.repository.ICursoRepository;
import br.edu.fateczl.SpringDataAluno.repository.IDisciplinaRepository;
import br.edu.fateczl.SpringDataAluno.repository.IGradeRepository;

@Controller
public class GradeController {

    @Autowired
    private IGradeRepository gRep;

    @Autowired
    private ICursoRepository cRep;
    
    @Autowired
    private IDisciplinaRepository dRep;

    @RequestMapping(name = "grade", value = "/grade", method = RequestMethod.GET)
    public ModelAndView GradeGet(@RequestParam Map<String, String> allRequestParam, ModelMap model) {
        Grade g = new Grade();
        String saida = "";
        String erro = "";
        List<Grade> grades = new ArrayList<>();
        List<Curso> cursos = new ArrayList<>();
        List<Disciplina> disciplinas = new ArrayList<>();
        
        try {
            String cmd = allRequestParam.get("cmd");
            String codigo = allRequestParam.get("codigo");
            String curso = allRequestParam.get("curso");
            
            if (cmd != null && !cmd.contains("Listar")) {
                g.setCodigo(Integer.parseInt(codigo));
            }
            
            if (cmd != null) {
                if (cmd.contains("alterar")) {
                    g = buscarGrade(g);
                } else if (cmd.contains("excluir")) {
                    g = buscarGrade(g);
                    saida = excluirGrade(g);
                }
            }
            
            if (curso != null && !curso.isEmpty()) {
                grades = listarGrades(Integer.parseInt(curso));
            }
            cursos = listarCursos();
            disciplinas = listarDisciplinas();
            
        } catch (SQLException | ClassNotFoundException e) {
            erro = e.getMessage();
        } finally {
            model.addAttribute("saida", saida);
            model.addAttribute("erro", erro);
            model.addAttribute("grade", g);
            model.addAttribute("grades", grades);
            model.addAttribute("cursos", cursos);
            model.addAttribute("disciplinas", disciplinas);
        }
        
        return new ModelAndView("grade");
    }

    @RequestMapping(name = "grade", value = "/grade", method = RequestMethod.POST)
    public ModelAndView indexPost(@RequestParam Map<String, String> allRequestParam, ModelMap model) {
        String cmd = allRequestParam.get("botao");
        String codigo = allRequestParam.get("codigo");
        String curso = allRequestParam.get("curso");
        String disciplina = allRequestParam.get("disciplina");

        String saida = "";
        String erro = "";
        Grade g = new Grade();

        List<Grade> grades = new ArrayList<>();
        List<Curso> cursos = new ArrayList<>();
        List<Disciplina> disciplinas = new ArrayList<>();

        try {
            if (cmd != null && !cmd.contains("Listar")) {
                g.setCodigo(Integer.parseInt(codigo));
            }

            cursos = listarCursos();
            disciplinas = listarDisciplinas();

            if (cmd.contains("Cadastrar") || cmd.contains("Alterar")) {
                g.setCodigo(Integer.parseInt(codigo));

                Curso c = new Curso();
                c.setCodigo((Integer.parseInt(curso)));
                c = buscarCurso(c);
                g.setCurso(c);
                
                Disciplina d = new Disciplina();
                d.setCodigo((Integer.parseInt(disciplina)));
                d = buscarDisciplina(d);
                g.setDisciplina(d);
            }
            if (cmd.contains("Cadastrar")) {
                saida = cadastrarGrade(g);
                g = null;
            }
            if (cmd.contains("Alterar")) {
                saida = alterarGrade(g);
                g = null;
            }
            if (cmd.contains("Excluir")) {
                saida = excluirGrade(g);
                g = null;
            }
            if (cmd != null && cmd.contains("Listar")) {
                grades = listarGrades(Integer.parseInt(curso));
            }
        } catch (SQLException | ClassNotFoundException e) {
            erro = e.getMessage();
        } finally {
            model.addAttribute("saida", saida);
            model.addAttribute("erro", erro);
            model.addAttribute("grade", g);
            model.addAttribute("grades", grades);
            model.addAttribute("cursos", cursos);
            model.addAttribute("disciplinas", disciplinas);
        }

        return new ModelAndView("grade");
    }

    private String cadastrarGrade(Grade g) throws SQLException, ClassNotFoundException {
        String saida = gRep.GerenciarGrade("I", g.getCodigo(), g.getCurso().getCodigo(), g.getDisciplina().getCodigo());
        return saida;
    }
    
    private String alterarGrade(Grade g) throws SQLException, ClassNotFoundException {
        String saida = gRep.GerenciarGrade("U", g.getCodigo(), g.getCurso().getCodigo(), g.getDisciplina().getCodigo());
        return saida;
    }

    private String excluirGrade(Grade g) throws SQLException, ClassNotFoundException {
        Optional<Grade> gradeOptional = gRep.findById(g.getCodigo());
        if (gradeOptional.isPresent()) {
            Grade grade = gradeOptional.get();
            grade.setCurso(null);  // Desvincula o curso da grade
            grade.setDisciplina(null);  // Desvincula a disciplina da grade
            gRep.delete(grade);  // Exclui a grade
            return "Grade Excluída";
        } else {
            throw new IllegalArgumentException("Grade não encontrada para exclusão.");
        }
    }

    private List<Grade> listarGrades(int curso) throws SQLException, ClassNotFoundException {
        return gRep.fn_grade(curso);
    }
    
    private Grade buscarGrade(Grade g) throws SQLException, ClassNotFoundException {
        Optional<Grade> gradeOptional = gRep.findById(g.getCodigo());
        return gradeOptional.orElseGet(Grade::new);
    }

    private Curso buscarCurso(Curso c) throws SQLException, ClassNotFoundException {
        Optional<Curso> cursoOptional = cRep.findById(c.getCodigo());
        return cursoOptional.orElseGet(Curso::new);
    }

    private List<Curso> listarCursos() throws SQLException, ClassNotFoundException {
        return cRep.findAll();
    }
    
    private Disciplina buscarDisciplina(Disciplina d) throws SQLException, ClassNotFoundException {
        Optional<Disciplina> disciplinaOptional = dRep.findById(d.getCodigo());
        return disciplinaOptional.orElseGet(Disciplina::new);
    }

    private List<Disciplina> listarDisciplinas() throws SQLException, ClassNotFoundException {
        return dRep.findAll();
    }
}
