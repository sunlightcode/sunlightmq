
<%
	response.setCharacterEncoding("utf-8");
	boolean jsonP = false;  
	String cb = request.getParameter("callback");
  
	if (cb != null) {  
	    jsonP = true;  
	    response.setContentType("text/javascript");
	}  
	if (jsonP) {  
	    out.write(cb + "("); 
	}  
	out.print(request.getAttribute("responseObj").toString());  
	if (jsonP) {  
	    out.write(");");  
	}  
%>