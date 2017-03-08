<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>       
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>Welcome Page</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="keywords" content="" />
	<meta name="description" content="" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	
	<link href="${ctx }/resource/css/font-awesome.min.css" rel="stylesheet" type="text/css">
	<link href="${ctx }/resource/css/bootstrap.min.css" rel="stylesheet" type="text/css">
	<link href="${ctx }/resource/css/bootstrap-theme.min.css" rel="stylesheet" type="text/css">
	<link href="${ctx }/resource/css/templatemo_style.css" rel="stylesheet" type="text/css">	
</head>
<body>
	<h2>this is userlogin ${message }</h2>
	<br>
	<br>

	<!-- csrt for log out-->
	<form action="j_spring_security_logout" method="post" id="logoutForm">
	  <input type="hidden" 
		name="${_csrf.parameterName}"
		value="${_csrf.token}" />
	</form>
	
	<script>
		function formSubmit() {
			document.getElementById("logoutForm").submit();
		}
	</script>

	<c:if test="${pageContext.request.userPrincipal.name != null}">
		<h2>
			Welcome : ${pageContext.request.userPrincipal.name} | <a
			 href="javascript:formSubmit()"> Logout</a>
		</h2>
	</c:if>
	
	<table>
		<tr>
			<th>姓名</th>
			<th>生日</th>
			<th>薪水</th>
		</tr>
		<c:forEach items="${users}" var="user">
			<tr>
				<td>${user.userName }</td>
				<td>${user.userBirthday }</td>
				<td>${user.userSalary }</td>
			</tr>
		</c:forEach>
	</table>
</body>
</html>