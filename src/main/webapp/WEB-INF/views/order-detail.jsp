<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Order #MKM-${order.id} - MonikaMart" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 12px;">
    <div>
        <h1 style="font-size: 1.8rem; font-weight: 700; color: var(--dark);">
            Order #MKM-${order.id}
        </h1>
        <p style="color: var(--text-muted); font-size: 0.9rem;">
            Placed on <fmt:formatDate value="${order.createdAt}" pattern="dd MMMM yyyy, hh:mm a" />
            <c:if test="${not empty order.trackingNumber}">
                &bull; Tracking No: <strong><c:out value="${order.trackingNumber}" /></strong>
            </c:if>
        </p>
    </div>
    <a href="${pageContext.request.contextPath}/orders" class="btn btn-secondary btn-sm">&larr; Back to Orders</a>
</div>

<c:if test="${param.success == 'OrderPlaced' && empty sessionScope.flashSuccess}">
    <div class="alert alert-success">Order placed successfully!</div>
</c:if>
<c:if test="${param.reviewSuccess == 'true'}">
    <div class="alert alert-success">⭐ Thank you for your review! It has been published on the product page.</div>
</c:if>
<c:if test="${not empty param.reviewError}">
    <div class="alert alert-danger"><c:out value="${param.reviewError}" /></div>
</c:if>

<!-- Order Status Workflow Stepper (Feature O2) -->
<div class="surface-card" style="padding: 28px; margin-bottom: 30px;">
    <h3 style="font-size: 1.1rem; font-weight: 700; color: var(--dark); margin-bottom: 20px;">Order Progress</h3>
    <div class="order-stepper">
        <div class="step-item ${order.status == 'PENDING' || order.status == 'CONFIRMED' || order.status == 'SHIPPED' || order.status == 'DELIVERED' ? 'completed' : ''}">
            <div class="step-icon">1</div>
            <div class="step-title">Order Placed</div>
        </div>
        <div class="step-item ${order.status == 'CONFIRMED' || order.status == 'SHIPPED' || order.status == 'DELIVERED' ? 'completed' : ''}">
            <div class="step-icon">2</div>
            <div class="step-title">Confirmed</div>
        </div>
        <div class="step-item ${order.status == 'SHIPPED' || order.status == 'DELIVERED' ? 'completed' : (order.status == 'CONFIRMED' ? 'active' : '')}">
            <div class="step-icon">3</div>
            <div class="step-title">Shipped</div>
        </div>
        <div class="step-item ${order.status == 'DELIVERED' ? 'completed' : (order.status == 'SHIPPED' ? 'active' : '')}">
            <div class="step-icon">4</div>
            <div class="step-title">Delivered</div>
        </div>
    </div>
</div>

<div class="layout-2col-sidebar">
    <!-- Items Purchased -->
    <div class="surface-card" style="padding: 24px;">
        <h3 style="font-size: 1.2rem; font-weight: 700; color: var(--dark); margin-bottom: 16px;">Items Purchased</h3>

        <div style="display: flex; flex-direction: column; gap: 20px;">
            <c:forEach var="item" items="${order.items}">
                <div style="display: flex; gap: 16px; align-items: center; padding-bottom: 16px; border-bottom: 1px solid var(--border-color);">
                    <img src="<c:out value='${item.productImageUrl}' />" alt="<c:out value='${item.productName}' />" style="width: 60px; height: 60px; object-fit: cover; border-radius: var(--radius-sm);" onerror="this.src='https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600'">
                    <div style="flex: 1;">
                        <a href="${pageContext.request.contextPath}/product-detail?id=${item.productId}" style="font-weight: 600; color: var(--dark);">
                            <c:out value="${item.productName}" />
                        </a>
                        <div style="color: var(--text-muted); font-size: 0.85rem;">
                            Seller: <c:out value="${item.sellerName}" /> &bull; Qty: ${item.quantity} &times; ₹<fmt:formatNumber value="${item.unitPrice}" pattern="#,##0.00" />
                        </div>

                        <!-- Review Submission Form for Delivered Items (Feature F8) -->
                        <c:if test="${order.status == 'DELIVERED' && sessionScope.user.role == 'BUYER'}">
                            <div style="margin-top: 12px; padding: 12px; background: var(--surface-alt); border-radius: var(--radius-sm); border: 1px dashed var(--border-strong);">
                                <span style="font-size: 0.85rem; font-weight: 600; color: var(--primary);">Leave a Verified Product Review:</span>
                                <form action="${pageContext.request.contextPath}/reviews/add" method="post" style="display: flex; gap: 8px; margin-top: 6px; flex-wrap: wrap;">
                                    <input type="hidden" name="orderId" value="${order.id}">
                                    <input type="hidden" name="productId" value="${item.productId}">
                                    <select name="rating" class="form-control" style="width: auto; padding: 4px 8px; font-size: 0.85rem;" required>
                                        <option value="5">⭐⭐⭐⭐⭐ (5/5 Excellent)</option>
                                        <option value="4">⭐⭐⭐⭐ (4/5 Very Good)</option>
                                        <option value="3">⭐⭐⭐ (3/5 Average)</option>
                                        <option value="2">⭐⭐ (2/5 Poor)</option>
                                        <option value="1">⭐ (1/5 Terrible)</option>
                                    </select>
                                    <input type="text" name="comment" class="form-control" placeholder="Write brief product feedback..." required style="flex: 1; min-width: 200px; padding: 4px 8px; font-size: 0.85rem;">
                                    <button type="submit" class="btn btn-primary btn-sm">Post Review</button>
                                </form>
                            </div>
                        </c:if>
                    </div>
                    <div style="font-weight: 700; color: var(--dark);">
                        ₹<fmt:formatNumber value="${item.subtotal}" pattern="#,##0.00" />
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>

    <!-- Order Meta Card -->
    <div class="surface-card" style="padding: 24px;">
        <h3 style="font-size: 1.2rem; font-weight: 700; color: var(--dark); margin-bottom: 16px;">Delivery Details</h3>
        
        <p style="font-size: 0.85rem; color: var(--text-muted); margin-bottom: 4px;">Shipping Address:</p>
        <p style="color: var(--dark); font-size: 0.95rem; line-height: 1.5; margin-bottom: 20px;">
            <c:out value="${order.shippingAddress}" />
        </p>

        <p style="font-size: 0.85rem; color: var(--text-muted); margin-bottom: 4px;">Payment Method:</p>
        <p style="color: var(--dark); font-size: 0.95rem; margin-bottom: 20px;">
            <c:out value="${order.paymentMethod}" /> &bull; <span class="badge badge-success"><c:out value="${order.paymentStatus}" /></span>
        </p>

        <div style="border-top: 1px solid var(--border-color); padding-top: 16px; margin-top: 16px;">
            <div style="display: flex; justify-content: space-between; font-size: 1.3rem; font-weight: 800; color: var(--dark);">
                <span>Total Amount:</span>
                <span>₹<fmt:formatNumber value="${order.totalAmount}" pattern="#,##0.00" /></span>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
