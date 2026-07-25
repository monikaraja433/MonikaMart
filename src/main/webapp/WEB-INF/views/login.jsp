<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Sign In - MonikaMart" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="form-card">
    <h2 style="font-size: 1.6rem; font-weight: 700; color: var(--dark); margin-bottom: 8px;">Welcome Back</h2>
    <p style="color: var(--text-muted); font-size: 0.9rem; margin-bottom: 24px;">Sign in to your MonikaMart account</p>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger">
            <c:out value="${errorMessage}" />
        </div>
    </c:if>

    <c:if test="${param.loggedOut == 'true'}">
        <div class="alert alert-success">You have been logged out successfully.</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/login" method="post">
        <input type="hidden" name="redirect" value="<c:out value='${param.redirect}' />">

        <div class="form-group">
            <label class="form-label" for="email">Email Address</label>
            <input type="email" id="email" name="email" class="form-control" required placeholder="name@example.com" value="<c:out value='${email != null ? email : param.email}' />">
        </div>

        <div class="form-group">
            <label class="form-label" for="password">Password</label>
            <input type="password" id="password" name="password" class="form-control" required placeholder="Enter your password">
        </div>

        <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 10px;">Sign In</button>
    </form>

    <!-- Quick Demo Logins for Faculty & Reviewers -->
    <div style="margin-top: 28px; padding-top: 20px; border-top: 1px dashed var(--border-color);">
        <p style="font-size: 0.8rem; font-weight: 600; color: var(--text-muted); margin-bottom: 10px; text-transform: uppercase;">
            Demo Accounts (One-Click Auto-Fill):
        </p>
        <div style="display: flex; flex-direction: column; gap: 8px;">
            <button type="button" class="btn btn-secondary btn-sm" onclick="fillCredentials('buyer1@monikamart.com', 'Buyer@123')">
                🛒 Buyer: buyer1@monikamart.com (Monika)
            </button>
            <button type="button" class="btn btn-secondary btn-sm" onclick="fillCredentials('seller1@monikamart.com', 'Seller@123')">
                🏪 Seller: seller1@monikamart.com (Aditya Electronics)
            </button>
            <button type="button" class="btn btn-secondary btn-sm" onclick="fillCredentials('admin@monikamart.com', 'Admin@123')">
                ⚙️ Admin: admin@monikamart.com (System Admin)
            </button>
        </div>
    </div>

    <div style="margin-top: 24px; text-align: center; font-size: 0.9rem; color: var(--text-muted);">
        Don't have an account? <a href="${pageContext.request.contextPath}/register">Create one here</a>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
