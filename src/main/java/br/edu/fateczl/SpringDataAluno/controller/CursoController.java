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
import br.edu.fateczl.SpringDataAluno.repository.ICursoRepository;



@Controller
public class CursoController {

	@Autowired
	private ICursoRepository cRep;
	
	
	@RequestMapping(name = "curso", value = "/curso", method = RequestMethod.GET)
	public ModelAndView indexGet(@RequestParam Map<String, String> allRequestParam, ModelMap model) {

			String saida = "";
			String erro = "";
			
			List<Curso> cursos = new ArrayList<>();
			
			Curso cs = new Curso();

			try {
				String cmd = allRequestParam.get("cmd");
				String codigo = allRequestParam.get("codigo");
			
			  if (cmd != null) {
				if (cmd.contains("alterar")) {
					cs.setCodigo(Integer.parseInt(codigo));
					cs = buscarCurso(cs);
				} else if (cmd.contains("excluir")) {
					cs.setCodigo(Integer.parseInt(codigo));
					 saida = excluirCurso(cs);
				}

			  }
				cursos = listarCursos();
			  
			  
			} catch (SQLException | ClassNotFoundException e) {
				erro = e.getMessage();
			} finally {

				model.addAttribute("saida", saida);
				model.addAttribute("erro", erro);
				model.addAttribute("curso", cs);
				model.addAttribute("cursos", cursos);
		
			}
		
		return new ModelAndView("curso");
	}

	@RequestMapping(name = "curso", value = "/curso", method = RequestMethod.POST)
	public ModelAndView indexPost(@RequestParam Map<String, String> allRequestParam, ModelMap model) {

		//passar todos os parametros
		String cmd = allRequestParam.get("botao");
		String codigo = allRequestParam.get("codigo");
        String nome = allRequestParam.get("nome");
        String carga_horaria = allRequestParam.get("carga_horaria");
        String sigla = allRequestParam.get("sigla");
        String nota_enade = allRequestParam.get("nota_enade");

		// Saida
		String saida = "";
		String erro = "";
		Curso cs = new Curso();
		List<Curso> cursos= new ArrayList<>();

		if (!cmd.contains("Listar")) {
			cs.setCodigo(Integer.parseInt(codigo));
		}
		if (cmd.contains("Cadastrar") || cmd.contains("Alterar")) {
			//todos os parametros menos o codigo
			cs.setNome(nome);
			cs.setCarga_horaria(Integer.parseInt(carga_horaria));
			cs.setSigla(sigla);
			cs.setNota_enade(Float.parseFloat(nota_enade));
		}

		try {
			if (cmd.contains("Cadastrar")) {
				saida = cadastrarCurso(cs);
				cs = null;
			}
			if (cmd.contains("Alterar")) {
				saida = alterarCurso(cs);
				cs = null;
			}
			if (cmd.contains("Excluir")) {
				saida = excluirCurso(cs);
				cs = null;
			}
			if (cmd.contains("Buscar")) {
				cs.setCodigo(Integer.parseInt(codigo));
				cs = buscarCurso(cs);
				if (cs == null) {
					saida = "Nenhum Produto encontrado com o código especificado.";
				}
			}
			if (cmd.contains("Listar")) {
				cursos = listarCursos();
			}

		} catch (SQLException | ClassNotFoundException e) {
			erro = e.getMessage();
		} finally {
			model.addAttribute("saida", saida);
			model.addAttribute("erro", erro);
			model.addAttribute("curso", cs);
			model.addAttribute("cursos", cursos);
		}

		return new ModelAndView("curso");
	}

	
	private String cadastrarCurso(Curso cs) throws SQLException, ClassNotFoundException {
		String saida = cRep.GerenciarCurso("I", cs.getCodigo(), cs.getNome(), cs.getCarga_horaria(), cs.getSigla(), cs.getNota_enade());
		return saida;

	}

	private String alterarCurso(Curso cs) throws SQLException, ClassNotFoundException {
		String saida = cRep.GerenciarCurso("U", cs.getCodigo(), cs.getNome(), cs.getCarga_horaria(), cs.getSigla(), cs.getNota_enade());
		return saida;

	}

	private String excluirCurso(Curso cs) throws SQLException, ClassNotFoundException {
		String saida = cRep.GerenciarCurso("D", cs.getCodigo(), cs.getNome(), cs.getCarga_horaria(), cs.getSigla(), cs.getNota_enade());
	    return saida;
	}

	private Curso buscarCurso(Curso cs) throws SQLException, ClassNotFoundException {
		Optional<Curso> cursoOptional = cRep.findById(cs.getCodigo());
		if (cursoOptional.isPresent()) {
			return cursoOptional.get();
		} else {
			return null;
		}
	}

	//lista pelo sql
	private List<Curso> listarCursos() throws SQLException, ClassNotFoundException {
		List<Curso> cursos = cRep.findAll();
		return cursos;
	}

}
