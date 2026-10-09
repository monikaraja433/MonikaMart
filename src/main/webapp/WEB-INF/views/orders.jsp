<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="My Orders - MonikaMart" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<h1 style="font-size: 1.8rem; font-weight: 700; color: var(--dark); margin-bottom: 24px;">My Order History</h1>

<c:choose>
    <c:when test="${empty orders}">
        <div class="surface-card" style="padding: 50px 20px; text-align: center;">
            <div style="font-size: 3rem; margin-bottom: 12px;">📦</div>
            <h2 style="font-size: 1.4rem; color: var(--dark); margin-bottom: 8px;">No orders found</h2>
            <p style="color: var(--text-muted); margin-bottom: 24px;">You haven't placed any orders on MonikaMart yet.</p>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">Start Shopping</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="table-responsive">
            <table class="table">
                <thead>
                    <tr>
                        <th>Order ID</th>
                        <th>Date Placed</th>
                        <th>Items</th>
                        <th>Total Amount</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="o" items="${orders}">
                        <tr>
                            <td><strong>#MKM-${o.id}</strong></td>
                            <td><fmt:formatDate value="${o.createdAt}" pattern="dd MMM yyyy, hh:mm a" /></td>
                            <td>${o.items.size()} item(s)</td>
                            <td style="font-weight: 700; color: var(--dark);">₹<fmt:formatNumber value="${o.totalAmount}" pattern="#,##0.00" /></td>
                            <td>
                                <c:choose>
                                    <c:when test="${o.status == 'DELIVERED'}"><span class="badge badge-success">Delivered</span></c:when>
                                    <c:when test="${o.status == 'SHIPPED'}"><span class="badge badge-info">Shipped</span></c:when>
                                    <c:when test="${o.status == 'CONFIRMED'}"><span class="badge badge-primary">Confirmed</span></c:when>
                                    <c:when test="${o.status == 'PENDING'}"><span class="badge badge-warning">Pending</span></c:when>
                                    <c:otherwise><span class="badge badge-danger">Cancelled</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/order-detail?id=${o.id}" class="btn btn-secondary btn-sm">View Details &rarr;</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
