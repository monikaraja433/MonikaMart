<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Listing Moderation - MonikaMart Admin" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 12px;">
    <div>
        <h1 style="font-size: 1.8rem; font-weight: 700; color: var(--dark);">Product Listing Moderation</h1>
        <p style="color: var(--text-muted); font-size: 0.9rem;">Review and moderate merchant products across the platform</p>
    </div>
    <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-secondary btn-sm">&larr; Back to Admin Hub</a>
</div>

<c:if test="${param.success == 'StatusUpdated'}">
    <div class="alert alert-success">Listing moderation status updated successfully.</div>
</c:if>

<div class="table-responsive">
    <table class="table">
        <thead>
            <tr>
                <th>Product</th>
                <th>Seller</th>
                <th>Category</th>
                <th>Price</th>
                <th>Stock</th>
                <th>Moderation Status</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="p" items="${products}">
                <tr>
                    <td>
                        <div style="display: flex; gap: 10px; align-items: center;">
                            <img src="<c:out value='${p.imageUrl}' />" alt="<c:out value='${p.name}' />" style="width: 44px; height: 44px; object-fit: cover; border-radius: var(--radius-sm);" onerror="this.src='https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600'">
                            <div>
                                <a href="${pageContext.request.contextPath}/product-detail?id=${p.id}" target="_blank" style="font-weight: 600; color: var(--dark);">
                                    <c:out value="${p.name}" />
                                </a>
                                <div style="font-size: 0.8rem; color: var(--text-muted);">ID: #${p.id}</div>
                            </div>
                        </div>
                    </td>
                    <td><c:out value="${p.sellerName}" /></td>
                    <td><span class="badge badge-info"><c:out value="${p.category}" /></span></td>
                    <td style="font-weight: 700;">₹<fmt:formatNumber value="${p.price}" pattern="#,##0.00" /></td>
                    <td>${p.stockQty} units</td>
                    <td>
                        <span class="badge ${p.active ? 'badge-success' : 'badge-danger'}">
                            ${p.active ? 'Active / Visible' : 'Suspended / Hidden'}
                        </span>
                    </td>
                    <td>
                        <form action="${pageContext.request.contextPath}/admin/moderate" method="post">
                            <input type="hidden" name="productId" value="${p.id}">
                            <input type="hidden" name="active" value="${!p.active}">
                            <button type="submit" class="btn ${p.active ? 'btn-danger' : 'btn-primary'} btn-sm">
                                ${p.active ? 'Deactivate' : 'Reactivate'}
                            </button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
