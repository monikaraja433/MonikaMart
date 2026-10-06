<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Administration Panel - MonikaMart" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 28px; flex-wrap: wrap; gap: 12px;">
    <div>
        <h1 style="font-size: 1.8rem; font-weight: 700; color: var(--dark);">Platform Administration</h1>
        <p style="color: var(--text-muted); font-size: 0.9rem;">Global platform metrics, user registry, and listing moderation</p>
    </div>
    <div style="display: flex; gap: 10px;">
        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary">👥 User Directory</a>
        <a href="${pageContext.request.contextPath}/admin/listings" class="btn btn-primary">📋 Listing Moderation</a>
    </div>
</div>

<!-- Platform Metric Cards -->
<div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 20px; margin-bottom: 36px;">
    <div style="background: white; border-radius: var(--radius-md); border: 1px solid var(--border-color); padding: 22px;">
        <div style="font-size: 0.85rem; color: var(--text-muted); font-weight: 600;">REGISTERED USERS</div>
        <div style="font-size: 2.2rem; font-weight: 800; color: var(--primary); margin-top: 6px;">${totalUsers}</div>
    </div>
    <div style="background: white; border-radius: var(--radius-md); border: 1px solid var(--border-color); padding: 22px;">
        <div style="font-size: 0.85rem; color: var(--text-muted); font-weight: 600;">CATALOG PRODUCTS</div>
        <div style="font-size: 2.2rem; font-weight: 800; color: var(--info); margin-top: 6px;">${totalProducts}</div>
    </div>
    <div style="background: white; border-radius: var(--radius-md); border: 1px solid var(--border-color); padding: 22px;">
        <div style="font-size: 0.85rem; color: var(--text-muted); font-weight: 600;">TOTAL ORDERS PLACED</div>
        <div style="font-size: 2.2rem; font-weight: 800; color: var(--warning); margin-top: 6px;">${totalOrders}</div>
    </div>
    <div style="background: white; border-radius: var(--radius-md); border: 1px solid var(--border-color); padding: 22px;">
        <div style="font-size: 0.85rem; color: var(--text-muted); font-weight: 600;">TOTAL PLATFORM REVENUE</div>
        <div style="font-size: 2.2rem; font-weight: 800; color: var(--success); margin-top: 6px;">₹<fmt:formatNumber value="${totalRevenue}" pattern="#,##0.00" /></div>
    </div>
</div>

<!-- Live System Activity & Notifications (Admin Notification Audit Section) -->
<div style="margin-bottom: 36px;">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; flex-wrap: wrap; gap: 8px;">
        <h3 style="font-size: 1.25rem; font-weight: 700; color: var(--dark); margin: 0;">
            🔔 Live Activity & Notifications
        </h3>
        <span style="font-size: 0.85rem; color: var(--text-muted);">Audit log showing user/seller logins, registrations, product changes, orders, and reviews</span>
    </div>

    <div style="background: white; border-radius: var(--radius-md); border: 1px solid var(--border-color); overflow: hidden;">
        <c:choose>
            <c:when test="${empty recentActivities}">
                <div style="padding: 30px; text-align: center; color: var(--text-muted);">
                    No activities recorded yet.
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table" style="margin-bottom: 0;">
                        <thead>
                            <tr style="background: #f8fafc;">
                                <th style="width: 130px;">Event</th>
                                <th>Activity Description</th>
                                <th style="width: 230px;">User / Initiator</th>
                                <th style="width: 220px;">Date & Time</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="act" items="${recentActivities}">
                                <tr>
                                    <td>
                                        <c:choose>
                                            <c:when test="${act.activityType == 'LOGIN'}">
                                                <span class="badge badge-info">🔑 LOGIN</span>
                                            </c:when>
                                            <c:when test="${act.activityType == 'REGISTRATION'}">
                                                <span class="badge badge-primary">✨ REGISTER</span>
                                            </c:when>
                                            <c:when test="${act.activityType == 'PRODUCT_ADDED'}">
                                                <span class="badge badge-success">📦 PROD ADD</span>
                                            </c:when>
                                            <c:when test="${act.activityType == 'PRODUCT_UPDATED'}">
                                                <span class="badge badge-warning">✏️ PROD EDIT</span>
                                            </c:when>
                                            <c:when test="${act.activityType == 'PRODUCT_DELETED'}">
                                                <span class="badge badge-danger">🗑️ PROD DEL</span>
                                            </c:when>
                                            <c:when test="${act.activityType == 'ORDER_PLACED'}">
                                                <span class="badge badge-success">🛒 ORDER</span>
                                            </c:when>
                                            <c:when test="${act.activityType == 'REVIEW_SUBMITTED'}">
                                                <span class="badge badge-secondary">⭐ REVIEW</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-primary"><c:out value="${act.activityType}" /></span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td style="font-weight: 500; color: #1e293b;">
                                        <c:out value="${act.description}" />
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty act.userEmail}">
                                                <span style="font-size: 0.85rem; color: var(--text-muted);"><c:out value="${act.userEmail}" /></span>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="font-size: 0.85rem; color: var(--text-muted);">System</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td style="font-size: 0.85rem; color: var(--text-muted); white-space: nowrap;">
                                        <fmt:formatDate value="${act.createdAt}" pattern="dd MMM yyyy, hh:mm:ss a" />
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- Recent Orders Across Entire Platform -->
<h3 style="font-size: 1.25rem; font-weight: 700; color: var(--dark); margin-bottom: 16px;">Recent System Transactions</h3>

<div class="table-responsive">
    <table class="table">
        <thead>
            <tr>
                <th>Order ID</th>
                <th>Buyer</th>
                <th>Total Value</th>
                <th>Payment Status</th>
                <th>Order Status</th>
                <th>Placed Date</th>
                <th>Inspect</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="o" items="${recentOrders}">
                <tr>
                    <td><strong>#MKM-${o.id}</strong></td>
                    <td>
                        <div><c:out value="${o.buyerName}" /></div>
                        <div style="font-size: 0.8rem; color: var(--text-muted);"><c:out value="${o.buyerEmail}" /></div>
                    </td>
                    <td style="font-weight: 700;">₹<fmt:formatNumber value="${o.totalAmount}" pattern="#,##0.00" /></td>
                    <td><span class="badge badge-success"><c:out value="${o.paymentStatus}" /></span></td>
                    <td>
                        <span class="badge ${o.status == 'DELIVERED' ? 'badge-success' : (o.status == 'CANCELLED' ? 'badge-danger' : 'badge-primary')}">
                            <c:out value="${o.status}" />
                        </span>
                    </td>
                    <td><fmt:formatDate value="${o.createdAt}" pattern="dd MMM yyyy, hh:mm a" /></td>
                    <td>
                        <a href="${pageContext.request.contextPath}/order-detail?id=${o.id}" class="btn btn-secondary btn-sm">Inspect</a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
