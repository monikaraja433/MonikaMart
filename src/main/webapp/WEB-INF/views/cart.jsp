<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Shopping Cart - MonikaMart" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<h1 style="font-size: 1.8rem; font-weight: 700; color: var(--dark); margin-bottom: 24px;">Shopping Cart</h1>

<c:if test="${param.added == 'true' && empty sessionScope.flashSuccess}">
    <div class="alert alert-success">Product added to cart successfully!</div>
</c:if>

<c:if test="${not empty sessionScope.cartError}">
    <div class="alert alert-danger"><c:out value="${sessionScope.cartError}" /></div>
    <c:remove var="cartError" scope="session" />
</c:if>

<c:choose>
    <c:when test="${empty cartItems}">
        <div class="surface-card" style="padding: 50px 20px; text-align: center;">
            <div style="font-size: 3rem; margin-bottom: 12px;">🛒</div>
            <h2 style="font-size: 1.4rem; color: var(--dark); margin-bottom: 8px;">Your cart is currently empty</h2>
            <p style="color: var(--text-muted); margin-bottom: 24px;">Explore our catalog and find high-quality products today.</p>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">Start Shopping</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="layout-2col-sidebar">
            <!-- Cart Items Table -->
            <div class="table-responsive">
                <table class="table">
                    <thead>
                        <tr>
                            <th>Product</th>
                            <th>Unit Price</th>
                            <th>Quantity</th>
                            <th>Subtotal</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${cartItems}">
                            <tr>
                                <td>
                                    <div style="display: flex; gap: 12px; align-items: center;">
                                        <img src="<c:out value='${item.productImageUrl}' />" alt="<c:out value='${item.productName}' />" style="width: 50px; height: 50px; object-fit: cover; border-radius: var(--radius-sm);" onerror="this.src='https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600'">
                                        <div>
                                            <a href="${pageContext.request.contextPath}/product-detail?id=${item.productId}" style="font-weight: 600; color: var(--dark);">
                                                <c:out value="${item.productName}" />
                                            </a>
                                            <div style="font-size: 0.8rem; color: var(--text-muted);">
                                                Sold by: <c:out value="${item.sellerName}" />
                                            </div>
                                        </div>
                                    </div>
                                </td>
                                <td>₹<fmt:formatNumber value="${item.productPrice}" pattern="#,##0.00" /></td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/cart/update" method="post" style="display: flex; gap: 6px; align-items: center;">
                                        <input type="hidden" name="cartItemId" value="${item.id}">
                                        <input type="number" name="quantity" value="${item.quantity}" min="1" max="${item.productStockQty}" class="form-control" style="width: 65px; padding: 4px 8px;">
                                        <button type="submit" class="btn btn-secondary btn-sm" title="Update Quantity">✓</button>
                                    </form>
                                </td>
                                <td style="font-weight: 700; color: var(--dark);">
                                    ₹<fmt:formatNumber value="${item.itemTotal}" pattern="#,##0.00" />
                                </td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/cart/remove" method="post">
                                        <input type="hidden" name="cartItemId" value="${item.id}">
                                        <button type="submit" class="btn btn-danger btn-sm" title="Remove item">&times;</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <!-- Order Summary Card -->
            <div class="surface-card" style="padding: 24px;">
                <h3 style="font-size: 1.2rem; font-weight: 700; color: var(--dark); margin-bottom: 16px;">Order Summary</h3>
                
                <div style="display: flex; justify-content: space-between; margin-bottom: 12px; color: var(--text-muted);">
                    <span>Total Items:</span>
                    <span>${cartItems.size()}</span>
                </div>

                <div style="display: flex; justify-content: space-between; margin-bottom: 12px; color: var(--text-muted);">
                    <span>Delivery Charges:</span>
                    <span style="color: var(--success); font-weight: 600;">FREE</span>
                </div>

                <div style="display: flex; justify-content: space-between; margin-top: 16px; padding-top: 16px; border-top: 1px solid var(--border-color); font-size: 1.3rem; font-weight: 800; color: var(--dark);">
                    <span>Total Amount:</span>
                    <span>₹<fmt:formatNumber value="${cartTotal}" pattern="#,##0.00" /></span>
                </div>

                <a href="${pageContext.request.contextPath}/checkout" class="btn btn-primary" style="width: 100%; margin-top: 24px; padding: 12px;">
                    Proceed to Checkout
                </a>

                <div style="text-align: center; margin-top: 12px;">
                    <a href="${pageContext.request.contextPath}/products" style="font-size: 0.85rem; color: var(--text-muted);">
                        &larr; Continue Shopping
                    </a>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
