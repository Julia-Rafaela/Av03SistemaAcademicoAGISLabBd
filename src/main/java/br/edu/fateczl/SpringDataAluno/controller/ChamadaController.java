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
import br.edu.fateczl.SpringDataAluno.model.Disciplina;
import br.edu.fateczl.SpringDataAluno.repository.IAlunoRepository;
import br.edu.fateczl.SpringDataAluno.repository.IChamadaRepository;
import br.edu.fateczl.SpringDataAluno.repository.IDisciplinaRepository;

@Controller
public class ChamadaController {

	@Autowired
	private IChamadaRepository cRep;

	@Autowired
	private IDisciplinaRepository dRep;

	@Autowired
	private IAlunoRepository aRep;

	@RequestMapping(value = "/chamada", method = RequestMethod.GET)
	public ModelAndView chamadaGet(@RequestParam Map<String, String> allRequestParam, ModelMap model) {
		String saida = "";
		String erro = "";
		List<Chamada> chamadas = new ArrayList<>();
		List<Aluno> alunos = new ArrayList<>();
		List<Disciplina> disciplinas = new ArrayList<>();

		try {
			String cmd = allRequestParam.get("cmd");
			String codigo = allRequestParam.get("codigo");

			if (cmd != null && cmd.contains("alterar") && codigo != null) {
				chamadas = buscarChamada(Integer.parseInt(codigo));
			}

			alunos = listarAlunos();
			disciplinas = listarDisciplinas();

		} catch (SQLException | ClassNotFoundException e) {
			erro = e.getMessage();
		} finally {
			model.addAttribute("saida", saida);
			model.addAttribute("erro", erro);
			model.addAttribute("chamadas", chamadas);
			model.addAttribute("alunos", alunos);
			model.addAttribute("disciplinas", disciplinas);
		}

		return new ModelAndView("chamada");
	}

	@RequestMapping(value = "/chamada", method = RequestMethod.POST)
	public ModelAndView chamadaPost(@RequestParam Map<String, String> allRequestParam, ModelMap model) {
		String saida = "";
		String erro = "";

		List<Chamada> chamadas = new ArrayList<>();
		List<Disciplina> disciplinas = new ArrayList<>();
		List<Aluno> alunos = new ArrayList<>();

		try {
			String cmd = allRequestParam.get("botao");
			String data = allRequestParam.get("data");
			String codigo = allRequestParam.get("codigo");
			String disciplina = allRequestParam.get("disciplina");

			disciplinas = listarDisciplinas();
			alunos = listarAlunos();

			if ("Cadastrar".equals(cmd)) {
				for (Map.Entry<String, String> entry : allRequestParam.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					if (key.startsWith("falta")) {
						String chamadaIndex = key.substring(5);
						Chamada novaChamada = new Chamada();
						novaChamada.setData(data);
						novaChamada.setCodigo(Integer.parseInt(codigo));

						String alunoKey = "aluno" + chamadaIndex;
						String alunoValue = allRequestParam.get(alunoKey);
						if (alunoValue != null && !alunoValue.isEmpty()) {
							Aluno a = new Aluno();
							a.setCpf(alunoValue);
							novaChamada.setAluno(a);

							Disciplina d = new Disciplina();
							d.setCodigo(Integer.parseInt(disciplina));
							d = buscarDisciplina(d);
							novaChamada.setDisciplina(d);

							novaChamada.setFalta(Integer.parseInt(value));

							try {
								String resultadoCadastro = cadastrarChamada(novaChamada);
								saida += resultadoCadastro + "<br>";
							} catch (SQLException | ClassNotFoundException e) {
								saida += "Erro ao cadastrar chamada: " + e.getMessage() + "<br>";
							}
						}
					}
				}
			} else if ("Alterar".equals(cmd) && codigo != null) {
				for (Map.Entry<String, String> entry : allRequestParam.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					if (key.startsWith("falta")) {
						String chamadaIndex = key.substring(5);
						Chamada novaChamada = new Chamada();
						novaChamada.setData(data);
						novaChamada.setCodigo(Integer.parseInt(codigo));

						String alunoKey = "aluno" + chamadaIndex;
						String alunoValue = allRequestParam.get(alunoKey);
						if (alunoValue != null && !alunoValue.isEmpty()) {
							Aluno a = new Aluno();
							a.setCpf(alunoValue);
							novaChamada.setAluno(a);

							Disciplina d = new Disciplina();
							d.setCodigo(Integer.parseInt(disciplina));
							d = buscarDisciplina(d);
							novaChamada.setDisciplina(d);

							novaChamada.setFalta(Integer.parseInt(value));

							try {
								String resultadoAlteracao = alterarChamada(novaChamada);
								saida += resultadoAlteracao + "<br>";
							} catch (SQLException | ClassNotFoundException e) {
								saida += "Erro ao alterar chamada: " + e.getMessage() + "<br>";
							}
						}
					}
				}
			} else if ("Realizar Chamada".equals(cmd)) {
				chamadas = listarChamadas(Integer.parseInt(disciplina));
			} else if ("Buscar".equals(cmd) && codigo != null) {
				chamadas = buscarChamada(Integer.parseInt(codigo));
			}

		} catch (SQLException | ClassNotFoundException e) {
			erro = e.getMessage();
		} finally {
			model.addAttribute("saida", saida);
			model.addAttribute("erro", erro);
			model.addAttribute("chamadas", chamadas);
			model.addAttribute("alunos", alunos);
			model.addAttribute("disciplinas", disciplinas);
		}

		return new ModelAndView("chamada");
	}

	private String cadastrarChamada(Chamada cm) throws SQLException, ClassNotFoundException {
		return cRep.gerenciarChamada("I", cm.getId(), cm.getCodigo(), cm.getDisciplina().getCodigo(),
				cm.getAluno().getCpf(), cm.getFalta(), cm.getData());
	}

	private String alterarChamada(Chamada cm) throws SQLException, ClassNotFoundException {
		if (cm == null) {
			return "Chamada não encontrada.";
		}
		return cRep.gerenciarChamada("U", cm.getId(), cm.getCodigo(), cm.getDisciplina().getCodigo(),
				cm.getAluno().getCpf(), cm.getFalta(), cm.getData());
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

			// Atribuindo valores padrão se null não é permitido
			chamada.setCodigo(0); // valor padrão, ajuste conforme necessário
			chamada.setData(""); // valor padrão para data, ajuste conforme necessário
			chamada.setFalta(0); // valor padrão para falta, ajuste conforme necessário

			chamadas.add(chamada);
		}

		return chamadas;
	}

	private List<Chamada> buscarChamada(int codigo) throws SQLException, ClassNotFoundException {

		List<Chamada> chamadas = new ArrayList<>();

		List<Map<String, Object>> results = cRep.fn_matriculaAlter(codigo);

		for (Map<String, Object> result : results) {
			Chamada chamada = new Chamada();

			Disciplina d = new Disciplina();
			d.setNome((String) result.get("disciplina"));
			chamada.setDisciplina(d);

			Aluno a = new Aluno();
			a.setCpf((String) result.get("cpf"));
			a.setNome((String) result.get("aluno"));
			chamada.setAluno(a);

			chamada.setCodigo(codigo);
			chamada.setFalta((int) result.get("falta"));

			chamada.setData("");
			chamada.setId(0);

			chamadas.add(chamada);
		}

		return chamadas;
	}

	private Aluno buscarAluno(String aluno) throws SQLException, ClassNotFoundException {
		Optional<Aluno> alunoOptional = aRep.findById(aluno);
		return alunoOptional.orElse(null);
	}

	private List<Aluno> listarAlunos() throws SQLException, ClassNotFoundException {
		return aRep.findAll();
	}

	private Disciplina buscarDisciplina(Disciplina d) throws SQLException, ClassNotFoundException {
		Optional<Disciplina> disciplinaOptional = dRep.findById(d.getCodigo());
		return disciplinaOptional.orElse(null);
	}

	private List<Disciplina> listarDisciplinas() throws SQLException, ClassNotFoundException {
		return dRep.findAll();
	}
}
