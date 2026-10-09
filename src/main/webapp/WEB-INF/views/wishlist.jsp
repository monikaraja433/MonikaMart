<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="My Wishlist - MonikaMart" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<h1 style="font-size: 1.8rem; font-weight: 700; color: var(--dark); margin-bottom: 24px;">My Wishlist</h1>

<c:choose>
    <c:when test="${empty wishlistItems}">
        <div class="surface-card" style="padding: 50px 20px; text-align: center;">
            <div style="font-size: 3rem; margin-bottom: 12px;">❤️</div>
            <h2 style="font-size: 1.4rem; color: var(--dark); margin-bottom: 8px;">Your wishlist is empty</h2>
            <p style="color: var(--text-muted); margin-bottom: 24px;">Save products you love and purchase them anytime later.</p>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">Browse Products</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="products-grid">
            <c:forEach var="item" items="${wishlistItems}">
                <div class="card">
                    <div class="card-img-wrapper">
                        <a href="${pageContext.request.contextPath}/product-detail?id=${item.productId}">
                            <img src="<c:out value='${item.productImageUrl}' />" alt="<c:out value='${item.productName}' />" onerror="this.src='https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600'">
                        </a>
                        <span class="badge ${item.productStockQty > 0 ? 'badge-success' : 'badge-danger'} card-badge">
                            ${item.productStockQty > 0 ? 'In Stock' : 'Out of Stock'}
                        </span>
                    </div>

                    <div class="card-body">
                        <div class="card-category"><c:out value="${item.productCategory}" /></div>
                        <h3 class="card-title">
                            <a href="${pageContext.request.contextPath}/product-detail?id=${item.productId}">
                                <c:out value="${item.productName}" />
                            </a>
                        </h3>
                        <p class="card-desc"><c:out value="${item.productDescription}" /></p>

                        <div class="card-footer">
                            <div class="card-price">
                                ₹<fmt:formatNumber value="${item.productPrice}" pattern="#,##0.00" />
                            </div>
                            <div style="display: flex; gap: 6px;">
                                <form action="${pageContext.request.contextPath}/wishlist/move-to-cart" method="post" style="display:inline;">
                                    <input type="hidden" name="productId" value="${item.productId}">
                                    <button type="submit" class="btn btn-primary btn-sm" ${item.productStockQty <= 0 ? 'disabled' : ''}>Move to Cart</button>
                                </form>
                                <form action="${pageContext.request.contextPath}/wishlist/remove" method="post" style="display:inline;">
                                    <input type="hidden" name="productId" value="${item.productId}">
                                    <button type="submit" class="btn btn-danger btn-sm" title="Remove">&times;</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
