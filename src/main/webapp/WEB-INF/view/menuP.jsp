<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<link rel="stylesheet" type="text/css"
	href='<c:url value = "./resources/css/menu.css" />'>
<title></title>
</head>
<body>
	<nav id="menu">
		<ul>
			<div class="logo">
				<li><img alt="Logo" src="./resources/imagens/logo_trabalho.webp"></li>
			</div>
			<li><a href="index">Home</a>
			<li><a href="chamada">Chamada</a></li>
			<li><a href="notas">Lançar Notas</a></li>
			

		</ul>
	</nav>
</body>
</html>