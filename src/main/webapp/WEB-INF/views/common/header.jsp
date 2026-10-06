<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${pageTitle != null ? pageTitle : 'MonikaMart - Anna University R2025 Capstone'}" /></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/chatbot.css">
</head>
<body>
    <header class="navbar">
        <div class="container nav-container">
            <a href="${pageContext.request.contextPath}/products" class="brand">
                <span>🛒 MonikaMart</span>
                <span class="brand-badge">AU R2025</span>
            </a>

            <!-- Search Form -->
            <form action="${pageContext.request.contextPath}/products" method="get" class="search-bar">
                <input type="text" name="keyword" placeholder="Search products, brands, or categories..." value="<c:out value='${param.keyword}' />">
                <button type="submit">Search</button>
            </form>

            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/products" class="nav-link">Catalog</a></li>

                <c:choose>
                    <c:when test="${not empty sessionScope.user}">
                        <!-- Logged In Links -->
                        <c:if test="${sessionScope.user.role == 'BUYER' || sessionScope.user.role == 'ADMIN'}">
                            <li>
                                <a href="${pageContext.request.contextPath}/wishlist" class="nav-link">
                                    ❤️ Wishlist
                                </a>
                            </li>
                            <li>
                                <a href="${pageContext.request.contextPath}/cart" class="nav-link">
                                    🛍️ Cart
                                </a>
                            </li>
                            <li>
                                <a href="${pageContext.request.contextPath}/orders" class="nav-link">
                                    📦 My Orders
                                </a>
                            </li>
                        </c:if>

                        <c:if test="${sessionScope.user.role == 'SELLER' || sessionScope.user.role == 'ADMIN'}">
                            <li>
                                <a href="${pageContext.request.contextPath}/seller/dashboard" class="nav-link">
                                    🏪 Seller Hub
                                </a>
                            </li>
                        </c:if>

                        <c:if test="${sessionScope.user.role == 'ADMIN'}">
                            <li>
                                <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-link">
                                    ⚙️ Admin
                                </a>
                            </li>
                        </c:if>

                        <li>
                            <span class="badge badge-primary">
                                <c:out value="${sessionScope.user.role}" />: <c:out value="${sessionScope.user.name}" />
                            </span>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary btn-sm">Logout</a>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <!-- Guest Links -->
                        <li><a href="${pageContext.request.contextPath}/login" class="nav-link">Login</a></li>
                        <li><a href="${pageContext.request.contextPath}/register" class="btn btn-primary btn-sm">Register</a></li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </header>

    <main class="main-content container">
        <c:if test="${not empty sessionScope.flashSuccess}">
            <div class="alert alert-success" style="margin-bottom: 24px;">
                <c:out value="${sessionScope.flashSuccess}" />
            </div>
            <c:remove var="flashSuccess" scope="session" />
        </c:if>
        <c:if test="${not empty sessionScope.flashError}">
            <div class="alert alert-danger" style="margin-bottom: 24px;">
                <c:out value="${sessionScope.flashError}" />
            </div>
            <c:remove var="flashError" scope="session" />
        </c:if>
