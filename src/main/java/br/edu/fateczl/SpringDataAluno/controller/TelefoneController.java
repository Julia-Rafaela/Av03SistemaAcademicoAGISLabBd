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
import br.edu.fateczl.SpringDataAluno.model.Telefone;
import br.edu.fateczl.SpringDataAluno.repository.IAlunoRepository;
import br.edu.fateczl.SpringDataAluno.repository.ITelefoneRepository;


@Controller
public class TelefoneController {

	 @Autowired
	    private IAlunoRepository aRep;

	    @Autowired
	    private ITelefoneRepository tRep;
	    
	@RequestMapping(name = "telefone", value = "/telefone", method = RequestMethod.GET)
	public ModelAndView indexGet(@RequestParam Map<String, String> allRequestParam, ModelMap model) {
		
		Telefone t = new Telefone();
		String saida = "";
		String erro = "";
		List<Telefone> telefones = new ArrayList<>();
		List<Aluno> alunos = new ArrayList<>();
		
		try {
			
			String cmd = allRequestParam.get("cmd");
			String codigo = allRequestParam.get("codigo");
			String aluno = allRequestParam.get("aluno");
			
			if (cmd != null) {
				if (codigo != null && !codigo.isEmpty()) {
					t.setCodigo(Integer.parseInt(codigo));
					saida = excluirTelefone(t);
					t = null;
				} else {
					saida = "Erro: código de telefone não especificado.";
				}
			}

			telefones = listarTelefones(aluno);
			alunos = listarAlunos();

		} catch (SQLException | ClassNotFoundException e) {
			erro = e.getMessage();
		} finally {

			model.addAttribute("saida", saida);
			model.addAttribute("erro", erro);
			model.addAttribute("telefone", t);
			model.addAttribute("telefones", telefones);
			model.addAttribute("alunos", alunos);

		}

		return new ModelAndView("telefone");
	}

	@RequestMapping(name = "telefone", value = "/telefone", method = RequestMethod.POST)
	public ModelAndView indexPost(@RequestParam Map<String, String> allRequestParam, ModelMap model) {

		// passar todos os parametros
		String cmd = allRequestParam.get("botao");
		String codigo = allRequestParam.get("codigo");
		String telefone = allRequestParam.get("telefone");
		String aluno = allRequestParam.get("aluno");

		// Saida
		String saida = "";
		String erro = "";
		Telefone t = new Telefone();

		List<Telefone> telefones = new ArrayList<>();
		List<Aluno> alunos = new ArrayList<>();

		if (!cmd.contains("Listar")) {
			t.setCodigo(Integer.parseInt(codigo));
		}

		try {

			alunos = listarAlunos();

			if (cmd.contains("Cadastrar") || cmd.contains("Alterar")) {
				t.setTelefone(telefone);

				Aluno a = new Aluno();
				a.setCpf((aluno));
				a = buscarAluno(a);
				t.setAluno(a);
			}
			if (cmd.contains("Cadastrar")) {
				saida = cadastrarTelefone(t);
				t = null;
			}
			if (cmd.contains("Excluir")) {
				saida = excluirTelefone(t);
				t = null;
			}
			if (cmd.contains("Listar")) {
				telefones = listarTelefones(aluno);
			}
		} catch (SQLException | ClassNotFoundException e) {
			erro = e.getMessage();

		} finally {

			model.addAttribute("saida", saida);
			model.addAttribute("erro", erro);
			model.addAttribute("telefone", t);
			model.addAttribute("telefones", telefones);
			model.addAttribute("alunos", alunos);

		}

		return new ModelAndView("telefone");

	}

	private String cadastrarTelefone(Telefone t) throws SQLException, ClassNotFoundException {
		String saida = tRep.GerenciarTelefone("I", t.getCodigo(), t.getAluno().getCpf(), t.getTelefone());
		return saida;
    }

	private String excluirTelefone(Telefone t) throws SQLException, ClassNotFoundException {
        tRep.delete(t);
        return "Telefone Excluido!";
    }

	private List<Telefone> listarTelefones(String cpf) throws SQLException, ClassNotFoundException {
		 return tRep.findAll();
	}

	private Aluno buscarAluno(Aluno a) throws SQLException, ClassNotFoundException {
		Optional<Aluno> alunoOptional = aRep.findById(a.getCpf());
		if (alunoOptional.isPresent()) {
			return alunoOptional.get();
		} else {
			return null;
		}

	}

	private List<Aluno> listarAlunos() throws SQLException, ClassNotFoundException {
		 return aRep.findAll();
	}

}
