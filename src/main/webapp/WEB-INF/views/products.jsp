<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Product Catalog - MonikaMart" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<c:if test="${param.registered == 'true' && empty sessionScope.flashSuccess}">
    <div class="alert alert-success">Welcome to MonikaMart! Your account was registered successfully.</div>
</c:if>

<div class="grid-catalog">
    <!-- Category Sidebar -->
    <aside class="filter-sidebar">
        <h3>Categories</h3>
        <ul class="category-list">
            <li>
                <a href="${pageContext.request.contextPath}/products?category=All&keyword=<c:out value='${keyword}' />&sortBy=<c:out value='${sortBy}' />"
                   class="${empty selectedCategory || selectedCategory == 'All' ? 'active' : ''}">
                   All Products
                </a>
            </li>
            <c:forEach var="cat" items="${categories}">
                <li>
                    <a href="${pageContext.request.contextPath}/products?category=<c:out value='${cat}' />&keyword=<c:out value='${keyword}' />&sortBy=<c:out value='${sortBy}' />"
                       class="${selectedCategory == cat ? 'active' : ''}">
                       <c:out value="${cat}" />
                    </a>
                </li>
            </c:forEach>
        </ul>
    </aside>

    <!-- Main Products View -->
    <section>
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; flex-wrap: wrap; gap: 12px;">
            <div>
                <h2 style="font-size: 1.5rem; font-weight: 700; color: var(--dark);">
                    <c:choose>
                        <c:when test="${not empty selectedCategory && selectedCategory != 'All'}">
                            <c:out value="${selectedCategory}" />
                        </c:when>
                        <c:when test="${not empty keyword}">
                            Search results for "<c:out value="${keyword}" />"
                        </c:when>
                        <c:otherwise>All Products</c:otherwise>
                    </c:choose>
                </h2>
                <p style="color: var(--text-muted); font-size: 0.85rem;"><c:out value="${totalProducts}" /> item(s) available</p>
            </div>

            <!-- Sort By Form -->
            <form action="${pageContext.request.contextPath}/products" method="get" style="display: flex; align-items: center; gap: 8px;">
                <input type="hidden" name="keyword" value="<c:out value='${keyword}' />">
                <input type="hidden" name="category" value="<c:out value='${selectedCategory}' />">
                <label for="sortBy" style="font-size: 0.85rem; color: var(--text-muted); font-weight: 600;">Sort by:</label>
                <select id="sortBy" name="sortBy" class="form-control" style="width: auto; padding: 6px 12px;" onchange="this.form.submit()">
                    <option value="newest" ${sortBy == 'newest' ? 'selected' : ''}>Newest Arrivals</option>
                    <option value="price_asc" ${sortBy == 'price_asc' ? 'selected' : ''}>Price: Low to High</option>
                    <option value="price_desc" ${sortBy == 'price_desc' ? 'selected' : ''}>Price: High to Low</option>
                    <option value="rating" ${sortBy == 'rating' ? 'selected' : ''}>Customer Rating</option>
                </select>
            </form>
        </div>

        <c:choose>
            <c:when test="${empty products}">
                <div style="background: white; border-radius: var(--radius-md); padding: 48px; text-align: center; border: 1px solid var(--border-color);">
                    <p style="font-size: 1.2rem; color: var(--dark); margin-bottom: 8px;">No matching products found</p>
                    <p style="color: var(--text-muted); margin-bottom: 20px;">Try adjusting your keyword search or category filter.</p>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary">Clear All Filters</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="products-grid">
                    <c:forEach var="p" items="${products}">
                        <div class="card">
                            <div class="card-img-wrapper">
                                <a href="${pageContext.request.contextPath}/product-detail?id=${p.id}">
                                    <img src="<c:out value='${p.imageUrl}' />" alt="<c:out value='${p.name}' />" loading="lazy" onerror="this.src='https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600'">
                                </a>
                                <span class="badge ${p.stockQty > 0 ? 'badge-success' : 'badge-danger'} card-badge">
                                    ${p.stockQty > 0 ? 'In Stock' : 'Out of Stock'}
                                </span>
                            </div>

                            <div class="card-body">
                                <div class="card-category"><c:out value="${p.category}" /></div>
                                <h3 class="card-title">
                                    <a href="${pageContext.request.contextPath}/product-detail?id=${p.id}">
                                        <c:out value="${p.name}" />
                                    </a>
                                </h3>
                                <p class="card-desc"><c:out value="${p.description}" /></p>

                                <div class="rating-stars">
                                    <span>★</span>
                                    <strong><fmt:formatNumber value="${p.averageRating}" maxFractionDigits="1" minFractionDigits="1" /></strong>
                                    <span class="rating-count">(<c:out value="${p.reviewCount}" />)</span>
                                </div>

                                <div class="card-footer">
                                    <div class="card-price">
                                        ₹<fmt:formatNumber value="${p.price}" pattern="#,##0.00" />
                                    </div>
                                    <div style="display: flex; gap: 6px;">
                                        <!-- Wishlist Button (O1) -->
                                        <form action="${pageContext.request.contextPath}/wishlist/add" method="post" style="display:inline;">
                                            <input type="hidden" name="productId" value="${p.id}">
                                            <button type="submit" class="btn btn-secondary btn-sm" title="Add to Wishlist">❤️</button>
                                        </form>

                                        <!-- Add to Cart (F4) -->
                                        <c:if test="${p.stockQty > 0}">
                                            <form action="${pageContext.request.contextPath}/cart/add" method="post" style="display:inline;">
                                                <input type="hidden" name="productId" value="${p.id}">
                                                <input type="hidden" name="quantity" value="1">
                                                <button type="submit" class="btn btn-primary btn-sm">+ Cart</button>
                                            </form>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <div style="display: flex; justify-content: center; gap: 8px; margin-top: 36px;">
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <a href="${pageContext.request.contextPath}/products?page=${i}&category=<c:out value='${selectedCategory}' />&keyword=<c:out value='${keyword}' />&sortBy=<c:out value='${sortBy}' />"
                               class="btn ${currentPage == i ? 'btn-primary' : 'btn-secondary'} btn-sm">
                                ${i}
                            </a>
                        </c:forEach>
                    </div>
                </c:if>
            </c:otherwise>
        </c:choose>
    </section>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
