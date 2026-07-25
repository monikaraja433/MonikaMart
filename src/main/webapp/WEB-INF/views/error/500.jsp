<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Server Error - MonikaMart" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="text-align: center; padding: 60px 20px;">
    <h1 style="font-size: 4rem; color: var(--danger); margin-bottom: 12px;">500</h1>
    <h2 style="font-size: 1.5rem; color: var(--dark); margin-bottom: 16px;">Internal Server Error</h2>
    <p style="color: var(--text-muted); max-width: 500px; margin: 0 auto 28px auto;">
        We encountered an unexpected error processing your request. Our engineering team has logged this incident. Please try again shortly.
    </p>
    <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">Return to Catalog</a>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
