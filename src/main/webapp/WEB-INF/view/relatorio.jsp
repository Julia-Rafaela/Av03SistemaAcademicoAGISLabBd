<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<link rel="stylesheet" type="text/css"
	href='<c:url value = "./resources/css/relatorio.css" />'>
<title>Relatório de Frequência</title>
</head>
<body class="tela_relatorio">
    <div class="menu">
        <jsp:include page="menuS.jsp" />
    </div>
    <br />
    <div align="center" class="container">
        <form action="relatorio" method="post" target="_blank">
            <p class="cadastrar">Relatorios</p>

            <table>
                <tr>
                    <td class="aluno" colspan="3">
                        <p class="title">Código da Disciplina:</p>
                        <input class="cadastro" type="text" id="disciplina" name="disciplina" placeholder="Digite o código da disciplina" value='<c:out value="${disciplina.codigo}" />'>
                      </td>   
                </tr>
                <tr class="botoes1">
                    <td><input type="submit" id="cmd" name="cmd" value="Relatorio Faltas"></td>
                    <td><input type="submit" id="cmd" name="cmd" value="Relatorio Notas"></td>
                </tr>
                  
            </table>
        </form>
    </div>
    <br />
    <div align="center">
        <c:if test="${not empty erro}">
            <h2>
                <b><c:out value="${erro}" /></b>
            </h2>
        </c:if>
    </div>
     <div align="center">
        <c:if test="${not empty saida }">
            <h3>
                <b><c:out value="${saida }" /></b>
            </h3>
        </c:if>
    </div>
    <br />
</body>
</html>
