<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Register Account - MonikaMart" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="form-card">
    <h2 style="font-size: 1.6rem; font-weight: 700; color: var(--dark); margin-bottom: 8px;">Create an Account</h2>
    <p style="color: var(--text-muted); font-size: 0.9rem; margin-bottom: 24px;">Join MonikaMart as a Buyer or Seller</p>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger">
            <c:out value="${errorMessage}" />
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/register" method="post">
        <div class="form-group">
            <label class="form-label" for="name">Full Name *</label>
            <input type="text" id="name" name="name" class="form-control" required placeholder="e.g. Monika Subramanian" value="<c:out value='${form.name}' />">
            <c:if test="${not empty fieldErrors.name}"><div class="field-error"><c:out value="${fieldErrors.name}" /></div></c:if>
        </div>

        <div class="form-group">
            <label class="form-label" for="email">Email Address *</label>
            <input type="email" id="email" name="email" class="form-control" required placeholder="name@example.com" value="<c:out value='${form.email}' />">
            <c:if test="${not empty fieldErrors.email}"><div class="field-error"><c:out value="${fieldErrors.email}" /></div></c:if>
        </div>

        <div class="form-group">
            <label class="form-label" for="role">Account Role *</label>
            <select id="role" name="role" class="form-control" required>
                <option value="BUYER" ${form.role == 'BUYER' ? 'selected' : ''}>Buyer (Purchase products & leave reviews)</option>
                <option value="SELLER" ${form.role == 'SELLER' ? 'selected' : ''}>Seller (List & manage products, fulfill orders)</option>
            </select>
            <c:if test="${not empty fieldErrors.role}"><div class="field-error"><c:out value="${fieldErrors.role}" /></div></c:if>
        </div>

        <div class="form-group">
            <label class="form-label" for="password">Password * (min 8 chars, 1 uppercase, 1 lowercase, 1 number)</label>
            <input type="password" id="password" name="password" class="form-control" required placeholder="Create a secure password">
            <c:if test="${not empty fieldErrors.password}"><div class="field-error"><c:out value="${fieldErrors.password}" /></div></c:if>
        </div>

        <div class="form-group">
            <label class="form-label" for="confirmPassword">Confirm Password *</label>
            <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" required placeholder="Confirm your password">
            <c:if test="${not empty fieldErrors.confirmPassword}"><div class="field-error"><c:out value="${fieldErrors.confirmPassword}" /></div></c:if>
        </div>

        <div class="form-group">
            <label class="form-label" for="phone">Phone Number</label>
            <input type="tel" id="phone" name="phone" class="form-control" placeholder="+91 9876543210" value="<c:out value='${form.phone}' />">
        </div>

        <div class="form-group">
            <label class="form-label" for="address">Default Shipping / Business Address</label>
            <textarea id="address" name="address" class="form-control" rows="2" placeholder="Street, City, State, PIN"><c:out value='${form.address}' /></textarea>
        </div>

        <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 10px;">Create Account</button>
    </form>

    <div style="margin-top: 24px; text-align: center; font-size: 0.9rem; color: var(--text-muted);">
        Already have an account? <a href="${pageContext.request.contextPath}/login">Sign in here</a>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
