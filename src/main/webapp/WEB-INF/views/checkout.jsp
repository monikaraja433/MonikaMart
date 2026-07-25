<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Checkout - MonikaMart" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<h1 style="font-size: 1.8rem; font-weight: 700; color: var(--dark); margin-bottom: 24px;">Complete Your Order</h1>

<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger"><c:out value="${errorMessage}" /></div>
</c:if>

<div style="display: grid; grid-template-columns: 3fr 2fr; gap: 32px; align-items: start;">
    <!-- Shipping & Payment Form -->
    <div style="background: white; border-radius: var(--radius-lg); border: 1px solid var(--border-color); padding: 32px;">
        <form action="${pageContext.request.contextPath}/checkout/place" method="post">
            <h3 style="font-size: 1.2rem; font-weight: 700; color: var(--dark); margin-bottom: 16px;">1. Shipping Details</h3>
            
            <div class="form-group">
                <label class="form-label" for="shippingAddress">Delivery Address *</label>
                <textarea id="shippingAddress" name="shippingAddress" class="form-control" rows="3" required placeholder="House/Flat No., Street, Area, City, State, PIN code"><c:out value="${defaultAddress}" /></textarea>
            </div>

            <h3 style="font-size: 1.2rem; font-weight: 700; color: var(--dark); margin-top: 28px; margin-bottom: 16px;">2. Mock Payment Gateway</h3>
            <p style="color: var(--text-muted); font-size: 0.85rem; margin-bottom: 16px;">
                Note: This uses simulated instant mock payment confirmation for student project evaluation. No real money will be charged.
            </p>

            <div style="display: flex; flex-direction: column; gap: 10px; margin-bottom: 24px;">
                <label style="display: flex; align-items: center; gap: 10px; padding: 12px; border: 1px solid var(--border-color); border-radius: var(--radius-md); cursor: pointer;">
                    <input type="radio" name="paymentMethod" value="MOCK_UPI" checked>
                    <div>
                        <strong>Mock UPI / QR Pay</strong>
                        <div style="font-size: 0.8rem; color: var(--text-muted);">Simulated Google Pay / PhonePe / Paytm</div>
                    </div>
                </label>
                <label style="display: flex; align-items: center; gap: 10px; padding: 12px; border: 1px solid var(--border-color); border-radius: var(--radius-md); cursor: pointer;">
                    <input type="radio" name="paymentMethod" value="MOCK_CARD">
                    <div>
                        <strong>Mock Credit / Debit Card</strong>
                        <div style="font-size: 0.8rem; color: var(--text-muted);">Simulated Visa / Mastercard / RuPay</div>
                    </div>
                </label>
                <label style="display: flex; align-items: center; gap: 10px; padding: 12px; border: 1px solid var(--border-color); border-radius: var(--radius-md); cursor: pointer;">
                    <input type="radio" name="paymentMethod" value="MOCK_NETBANKING">
                    <div>
                        <strong>Mock Net Banking</strong>
                        <div style="font-size: 0.8rem; color: var(--text-muted);">Simulated SBI / HDFC / ICICI</div>
                    </div>
                </label>
            </div>

            <button type="submit" class="btn btn-primary" style="width: 100%; padding: 14px; font-size: 1.05rem;">
                Confirm Order &amp; Pay ₹<fmt:formatNumber value="${cartTotal}" pattern="#,##0.00" />
            </button>
        </form>
    </div>

    <!-- Review Items Summary -->
    <div style="background: white; border-radius: var(--radius-lg); border: 1px solid var(--border-color); padding: 24px;">
        <h3 style="font-size: 1.2rem; font-weight: 700; color: var(--dark); margin-bottom: 16px;">Items in Order</h3>
        
        <div style="display: flex; flex-direction: column; gap: 14px; margin-bottom: 20px;">
            <c:forEach var="item" items="${cartItems}">
                <div style="display: flex; justify-content: space-between; align-items: center; font-size: 0.9rem;">
                    <div>
                        <div style="font-weight: 600; color: var(--dark);"><c:out value="${item.productName}" /></div>
                        <div style="color: var(--text-muted); font-size: 0.8rem;">Qty: ${item.quantity} &times; ₹<fmt:formatNumber value="${item.productPrice}" pattern="#,##0.00" /></div>
                    </div>
                    <div style="font-weight: 600;">
                        ₹<fmt:formatNumber value="${item.itemTotal}" pattern="#,##0.00" />
                    </div>
                </div>
            </c:forEach>
        </div>

        <div style="border-top: 1px solid var(--border-color); padding-top: 16px;">
            <div style="display: flex; justify-content: space-between; font-size: 1.25rem; font-weight: 800; color: var(--dark);">
                <span>Total Payable:</span>
                <span>₹<fmt:formatNumber value="${cartTotal}" pattern="#,##0.00" /></span>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
