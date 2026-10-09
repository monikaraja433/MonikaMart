<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Incoming Orders - MonikaMart Seller Hub" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 12px;">
    <div>
        <h1 style="font-size: 1.8rem; font-weight: 700; color: var(--dark);">Incoming Orders Fulfillment</h1>
        <p style="color: var(--text-muted); font-size: 0.9rem;">Review buyer orders and advance shipping status</p>
    </div>
    <a href="${pageContext.request.contextPath}/seller/dashboard" class="btn btn-secondary btn-sm">&larr; Back to Dashboard</a>
</div>

<c:if test="${param.success == 'StatusUpdated'}">
    <div class="alert alert-success">Order status updated successfully.</div>
</c:if>
<c:if test="${not empty param.error}">
    <div class="alert alert-danger"><c:out value="${param.error}" /></div>
</c:if>

<c:choose>
    <c:when test="${empty orders}">
        <div class="surface-card" style="padding: 50px; text-align: center;">
            <p style="color: var(--text-muted); font-size: 1.1rem;">No incoming customer orders yet.</p>
        </div>
    </c:when>
    <c:otherwise>
        <div class="table-responsive">
            <table class="table">
                <thead>
                    <tr>
                        <th>Order ID</th>
                        <th>Buyer Name</th>
                        <th>Products Ordered</th>
                        <th>Shipping Address</th>
                        <th>Current Status</th>
                        <th>Advance Status (Workflow)</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="o" items="${orders}">
                        <tr>
                            <td><strong>#MKM-${o.id}</strong></td>
                            <td>
                                <div><c:out value="${o.buyerName}" /></div>
                                <div style="font-size: 0.8rem; color: var(--text-muted);"><c:out value="${o.buyerEmail}" /></div>
                            </td>
                            <td>
                                <ul style="list-style: none; padding: 0;">
                                    <c:forEach var="it" items="${o.items}">
                                        <li style="font-size: 0.85rem; margin-bottom: 4px;">
                                            &bull; <strong><c:out value="${it.productName}" /></strong> &times; ${it.quantity}
                                        </li>
                                    </c:forEach>
                                </ul>
                            </td>
                            <td style="max-width: 200px; font-size: 0.85rem; color: var(--text-muted);">
                                <c:out value="${o.shippingAddress}" />
                            </td>
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
                                <c:if test="${o.status != 'DELIVERED' && o.status != 'CANCELLED'}">
                                    <form action="${pageContext.request.contextPath}/seller/status-update" method="post" style="display: flex; gap: 6px; align-items: center;">
                                        <input type="hidden" name="orderId" value="${o.id}">
                                        <select name="newStatus" class="form-control" style="width: auto; padding: 4px 8px; font-size: 0.85rem;" required>
                                            <c:if test="${o.status == 'PENDING'}">
                                                <option value="CONFIRMED">Confirm Order</option>
                                                <option value="CANCELLED">Cancel Order</option>
                                            </c:if>
                                            <c:if test="${o.status == 'CONFIRMED'}">
                                                <option value="SHIPPED">Ship Package</option>
                                                <option value="CANCELLED">Cancel Order</option>
                                            </c:if>
                                            <c:if test="${o.status == 'SHIPPED'}">
                                                <option value="DELIVERED">Mark Delivered</option>
                                            </c:if>
                                        </select>
                                        <button type="submit" class="btn btn-primary btn-sm">Update</button>
                                    </form>
                                </c:if>
                                <c:if test="${o.status == 'DELIVERED'}">
                                    <span style="font-size: 0.85rem; color: var(--success); font-weight: 600;">✓ Completed</span>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
