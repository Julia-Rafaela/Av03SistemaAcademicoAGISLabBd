package br.edu.fateczl.SpringDataAluno.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import br.edu.fateczl.SpringDataAluno.repository.IDisciplinaRepository;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.util.JRLoader;

@Controller
public class RelatorioController {

    @Autowired
    private IDisciplinaRepository dRep;
    
    @Autowired
    private DataSource ds;
    
    @RequestMapping(name = "relatorio", value = "/relatorio", method = RequestMethod.GET)
    public ModelAndView indexGet(@RequestParam Map<String, String> allRequestParam, ModelMap model) {
        String cmd = allRequestParam.get("cmd");
        String codigo = allRequestParam.get("disciplina");

        String saida = "";
        String erro = "";

        try {
          
            System.out.println("cmd: " + cmd + " disciplina: " + codigo);

          
        } finally {
            model.addAttribute("saida", saida);
            model.addAttribute("erro", erro);
        }
        
        return new ModelAndView("relatorio");
    }
    
    @SuppressWarnings("rawtypes")
    @RequestMapping(name = "relatorio", value = "/relatorio", method = RequestMethod.POST)
    public ResponseEntity generateReport(@RequestParam Map<String, String> allRequestParam) throws SQLException {
        String erro = "";
        Map<String, Object> paramRelatorio = new HashMap<>();
        paramRelatorio.put("disc", allRequestParam.get("disciplina"));
        String cmd = allRequestParam.get("cmd");

        byte[] bytes = null;
        InputStreamResource resource = null;
        HttpStatus status = null;
        HttpHeaders header = new HttpHeaders();
        
        Connection connection = null;

        try {
         
            connection = DataSourceUtils.getConnection(ds);

          
            System.out.println("cmd: " + cmd + "disciplina: " + allRequestParam.get("disciplina"));

           
            File arquivo;
            if ("Relatorio Faltas".equalsIgnoreCase(cmd)) {
                System.out.println("Generating Relatorio Faltas for disciplina: " + allRequestParam.get("disciplina"));
                arquivo = ResourceUtils.getFile("classpath:reports/RelatorioFalta.jasper");
            } else {
                System.out.println("Generating Relatorio Notas for disciplina: " + allRequestParam.get("disciplina"));
                arquivo = ResourceUtils.getFile("classpath:reports/RelatorioNotas.jasper");
            }

            
            JasperReport report = (JasperReport) JRLoader.loadObjectFromFile(arquivo.getAbsolutePath());
            bytes = JasperRunManager.runReportToPdf(report, paramRelatorio, connection);

            
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            resource = new InputStreamResource(inputStream);
            header.setContentLength(bytes.length);
            header.setContentType(MediaType.APPLICATION_PDF);
            header.setCacheControl("no-cache, no-store, must-revalidate");
            header.setPragma("no-cache");
            header.setExpires(0);
            status = HttpStatus.OK;
            
        } catch (FileNotFoundException | JRException e) {
            e.printStackTrace();
            erro = e.getMessage();
            status = HttpStatus.BAD_REQUEST;
        } finally {
            if (connection != null) {
                DataSourceUtils.releaseConnection(connection, ds);
            }
        }

        if (status == HttpStatus.OK) {
            System.out.println("Report generated successfully for disciplina: " + allRequestParam.get("disciplina"));
        } else {
            System.out.println("Error generating report for disciplina: " + allRequestParam.get("disciplina") + ". Error: " + erro);
        }

        return new ResponseEntity<>(resource, header, status);
    }
}