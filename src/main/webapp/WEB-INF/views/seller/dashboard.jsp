<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Seller Hub - MonikaMart" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 28px; flex-wrap: wrap; gap: 12px;">
    <div>
        <h1 style="font-size: 1.8rem; font-weight: 700; color: var(--dark);">Merchant Dashboard</h1>
        <p style="color: var(--text-muted); font-size: 0.9rem;">Manage inventory, fulfill orders, and monitor sales</p>
    </div>
    <div style="display: flex; gap: 10px;">
        <a href="${pageContext.request.contextPath}/seller/orders" class="btn btn-secondary">📦 Incoming Orders (${orderCount})</a>
        <a href="${pageContext.request.contextPath}/seller/product-form" class="btn btn-primary">+ Add New Product</a>
    </div>
</div>

<c:if test="${param.success == 'ProductSaved'}">
    <div class="alert alert-success">Product listing saved successfully.</div>
</c:if>
<c:if test="${param.success == 'ProductDeleted'}">
    <div class="alert alert-success">Product listing removed successfully.</div>
</c:if>

<!-- Metrics Overview Cards (Feature O3 / Week 4) -->
<div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 20px; margin-bottom: 32px;">
    <div style="background: white; border-radius: var(--radius-md); border: 1px solid var(--border-color); padding: 20px;">
        <div style="font-size: 0.85rem; color: var(--text-muted); font-weight: 600;">ACTIVE LISTINGS</div>
        <div style="font-size: 2rem; font-weight: 800; color: var(--primary); margin-top: 6px;">${productCount}</div>
    </div>
    <div style="background: white; border-radius: var(--radius-md); border: 1px solid var(--border-color); padding: 20px;">
        <div style="font-size: 0.85rem; color: var(--text-muted); font-weight: 600;">TOTAL ORDERS</div>
        <div style="font-size: 2rem; font-weight: 800; color: var(--info); margin-top: 6px;">${orderCount}</div>
    </div>
    <div style="background: white; border-radius: var(--radius-md); border: 1px solid var(--border-color); padding: 20px;">
        <div style="font-size: 0.85rem; color: var(--text-muted); font-weight: 600;">GROSS SALES REVENUE</div>
        <div style="font-size: 2rem; font-weight: 800; color: var(--success); margin-top: 6px;">₹<fmt:formatNumber value="${totalSales}" pattern="#,##0.00" /></div>
    </div>
</div>

<!-- Seller Listings Table (Feature F2) -->
<h3 style="font-size: 1.25rem; font-weight: 700; color: var(--dark); margin-bottom: 16px;">My Catalog Listings</h3>

<c:choose>
    <c:when test="${empty products}">
        <div style="background: white; border-radius: var(--radius-md); border: 1px solid var(--border-color); padding: 40px; text-align: center;">
            <p style="color: var(--text-muted); margin-bottom: 16px;">You haven't listed any products yet.</p>
            <a href="${pageContext.request.contextPath}/seller/product-form" class="btn btn-primary">+ Create Your First Listing</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="table-responsive">
            <table class="table">
                <thead>
                    <tr>
                        <th>Product</th>
                        <th>Category</th>
                        <th>Price</th>
                        <th>Stock Qty</th>
                        <th>Status</th>
                        <th>Rating</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="p" items="${products}">
                        <tr>
                            <td>
                                <div style="display: flex; gap: 10px; align-items: center;">
                                    <img src="<c:out value='${p.imageUrl}' />" alt="<c:out value='${p.name}' />" style="width: 44px; height: 44px; object-fit: cover; border-radius: var(--radius-sm);" onerror="this.src='https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600'">
                                    <strong><c:out value="${p.name}" /></strong>
                                </div>
                            </td>
                            <td><span class="badge badge-info"><c:out value="${p.category}" /></span></td>
                            <td style="font-weight: 700;">₹<fmt:formatNumber value="${p.price}" pattern="#,##0.00" /></td>
                            <td>
                                <span class="${p.stockQty < 5 ? 'badge badge-warning' : 'badge badge-success'}">
                                    ${p.stockQty} left
                                </span>
                            </td>
                            <td>
                                <span class="badge ${p.active ? 'badge-success' : 'badge-danger'}">
                                    ${p.active ? 'Active' : 'Inactive'}
                                </span>
                            </td>
                            <td>★ <fmt:formatNumber value="${p.averageRating}" maxFractionDigits="1" minFractionDigits="1" /> (${p.reviewCount})</td>
                            <td>
                                <div style="display: flex; gap: 6px;">
                                    <a href="${pageContext.request.contextPath}/seller/product-form?id=${p.id}" class="btn btn-secondary btn-sm">Edit</a>
                                    <form action="${pageContext.request.contextPath}/seller/product-delete" method="post" onsubmit="return confirm('Deactivate this listing?');">
                                        <input type="hidden" name="productId" value="${p.id}">
                                        <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
