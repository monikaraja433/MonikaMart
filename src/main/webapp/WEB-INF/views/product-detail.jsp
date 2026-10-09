<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="${product.name} - MonikaMart" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="surface-card" style="padding: 32px; margin-bottom: 32px;">
    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 40px; align-items: start;">
        
        <!-- Product Image -->
        <div style="border-radius: var(--radius-md); overflow: hidden; background: var(--surface-alt); border: 1px solid var(--border-color); max-height: 420px; display: flex; align-items: center; justify-content: center;">
            <img src="<c:out value='${product.imageUrl}' />" alt="<c:out value='${product.name}' />" style="width: 100%; height: 100%; object-fit: contain; max-height: 400px;" onerror="this.src='https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600'">
        </div>

        <!-- Product Information -->
        <div>
            <div style="display: flex; gap: 8px; margin-bottom: 8px;">
                <span class="badge badge-info"><c:out value="${product.category}" /></span>
                <span class="badge ${product.stockQty > 0 ? 'badge-success' : 'badge-danger'}">
                    ${product.stockQty > 0 ? 'In Stock (' += product.stockQty += ' units left)' : 'Out of Stock'}
                </span>
            </div>

            <h1 style="font-size: 1.8rem; font-weight: 700; color: var(--dark); margin-bottom: 12px; line-height: 1.2;">
                <c:out value="${product.name}" />
            </h1>

            <p style="color: var(--text-muted); font-size: 0.9rem; margin-bottom: 16px;">
                Sold by: <strong><c:out value="${product.sellerName}" /></strong>
            </p>

            <!-- Rating Summary -->
            <div class="rating-stars" style="font-size: 1.1rem; margin-bottom: 20px;">
                <span>★</span>
                <strong><fmt:formatNumber value="${avgRating}" maxFractionDigits="1" minFractionDigits="1" /></strong>
                <span class="rating-count">(${reviews.size()} customer reviews)</span>
            </div>

            <div style="font-size: 2rem; font-weight: 800; color: var(--primary); margin-bottom: 24px;">
                ₹<fmt:formatNumber value="${product.price}" pattern="#,##0.00" />
            </div>

            <div style="margin-bottom: 28px; line-height: 1.7; color: var(--text-primary);">
                <c:out value="${product.description}" />
            </div>

            <c:if test="${product.stockQty > 0}">
                <form action="${pageContext.request.contextPath}/cart/add" method="post" style="display: flex; gap: 12px; align-items: center; margin-bottom: 20px;">
                    <input type="hidden" name="productId" value="${product.id}">
                    <label for="quantity" style="font-weight: 600; font-size: 0.9rem;">Qty:</label>
                    <input type="number" id="quantity" name="quantity" value="1" min="1" max="${product.stockQty}" class="form-control" style="width: 80px;">
                    <button type="submit" class="btn btn-primary" style="padding: 10px 24px;">🛍️ Add to Cart</button>
                </form>
            </c:if>

            <form action="${pageContext.request.contextPath}/wishlist/add" method="post">
                <input type="hidden" name="productId" value="${product.id}">
                <button type="submit" class="btn btn-secondary">❤️ Save to Wishlist</button>
            </form>
        </div>
    </div>
</div>

<!-- Customer Reviews Section (Feature F8) -->
<div class="surface-card" style="padding: 32px;">
    <h3 style="font-size: 1.4rem; font-weight: 700; color: var(--dark); margin-bottom: 20px;">
        Customer Reviews &amp; Ratings
    </h3>

    <c:choose>
        <c:when test="${empty reviews}">
            <p style="color: var(--text-muted); font-size: 0.95rem;">No customer reviews yet. Buy this product to leave a review once delivered!</p>
        </c:when>
        <c:otherwise>
            <div style="display: flex; flex-direction: column; gap: 16px;">
                <c:forEach var="rev" items="${reviews}">
                    <div style="border-bottom: 1px solid var(--border-color); padding-bottom: 16px;">
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px;">
                            <strong><c:out value="${rev.buyerName}" /></strong>
                            <div class="rating-stars" style="margin-bottom: 0;">
                                <c:forEach begin="1" end="${rev.rating}">★</c:forEach>
                                <span style="color: var(--text-muted); font-size: 0.8rem; margin-left: 6px;">
                                    <fmt:formatDate value="${rev.createdAt}" pattern="dd MMM yyyy" />
                                </span>
                            </div>
                        </div>
                        <p style="color: var(--text-primary); font-size: 0.95rem;"><c:out value="${rev.comment}" /></p>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
