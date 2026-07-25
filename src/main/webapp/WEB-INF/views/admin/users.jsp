<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="User Registry - MonikaMart Admin" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 12px;">
    <div>
        <h1 style="font-size: 1.8rem; font-weight: 700; color: var(--dark);">User Registry</h1>
        <p style="color: var(--text-muted); font-size: 0.9rem;">View all registered buyers, sellers, and system administrators</p>
    </div>
    <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-secondary btn-sm">&larr; Back to Admin Hub</a>
</div>

<div class="table-responsive">
    <table class="table">
        <thead>
            <tr>
                <th>ID</th>
                <th>Full Name</th>
                <th>Email Address</th>
                <th>Assigned Role</th>
                <th>Phone Number</th>
                <th>Registered Date</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="u" items="${users}">
                <tr>
                    <td>#${u.id}</td>
                    <td><strong><c:out value="${u.name}" /></strong></td>
                    <td><c:out value="${u.email}" /></td>
                    <td>
                        <c:choose>
                            <c:when test="${u.role == 'ADMIN'}"><span class="badge badge-danger">ADMIN</span></c:when>
                            <c:when test="${u.role == 'SELLER'}"><span class="badge badge-warning">SELLER</span></c:when>
                            <c:otherwise><span class="badge badge-primary">BUYER</span></c:otherwise>
                        </c:choose>
                    </td>
                    <td><c:out value="${u.phone != null ? u.phone : '—'}" /></td>
                    <td><fmt:formatDate value="${u.createdAt}" pattern="dd MMM yyyy, hh:mm a" /></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
