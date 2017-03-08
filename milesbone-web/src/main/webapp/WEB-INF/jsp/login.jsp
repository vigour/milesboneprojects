<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>       
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>Login One</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="keywords" content="" />
	<meta name="description" content="" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	
	<link href="${ctx }/resource/css/font-awesome.min.css" rel="stylesheet" type="text/css">
	<link href="${ctx }/resource/css/bootstrap.min.css" rel="stylesheet" type="text/css">
	<link href="${ctx }/resource/css/bootstrap-theme.min.css" rel="stylesheet" type="text/css">
	<link href="${ctx }/resource/css/templatemo_style.css" rel="stylesheet" type="text/css">	
</head>


<body class="templatemo-bg-gray">
	<div class="container">
		<div class="col-md-12">
			<h1 class="margin-bottom-15">&nbsp;</h1>
			<form class="form-horizontal templatemo-container templatemo-login-form-1 margin-bottom-30" role="form" action="${ctx}/j_spring_security_check" method="post" >				
		        <div class="form-group">
		          <div class="col-xs-12">		            
		            <div class="control-wrapper">
		            	<label for="username" class="control-label fa-label"><i class="fa fa-user fa-medium"></i></label>
		            	<input type="text" class="form-control" id="username" name="username" placeholder="用户名">
		            </div>		            	            
		          </div>              
		        </div>
		        <div class="form-group">
		          <div class="col-md-12">
		          	<div class="control-wrapper">
		            	<label for="password" class="control-label fa-label"><i class="fa fa-lock fa-medium"></i></label>
		            	<input type="password" class="form-control" id="password" name="password" placeholder="密码">
		            </div>
		          </div>
		        </div>
		        <div class="form-group">
		          <div class="col-md-12">
	             	<div class="checkbox control-wrapper">
	                	<label>
	                  		<input type="checkbox" name="remember-me"> 记住我
                		</label>
	              	</div>
		          </div>
		        </div>
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				
				<div class="form-group">
		          <div class="col-md-12">
	             	<div class="control-wrapper">
	                	<label>
							<c:if test="${param.error != null}">
			                    <div class="alert alert-danger">
			                       	用户名或者密码错误!
			                    </div>
			                </c:if>
			                <c:if test="${param.logout != null}">
			                    <div class="alert alert-success">
			                      	 退出登录成功!
			                    </div>
			                </c:if>
	                  		
                		</label>
	              	</div>
		          </div>
		        </div>
		        <div class="form-group">
		          <div class="col-md-12">
		          	<div class="control-wrapper">
		          		<input type="submit" value="登录" class="btn btn-info">
		          		<a href="forgot-password.html" class="text-right pull-right">忘记密码?</a>
		          	</div>
		          </div>
		        </div>
		        <hr>
		      </form>
		</div>
	</div>
</body>
</html>
