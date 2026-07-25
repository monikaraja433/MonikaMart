<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="${product.id != null ? 'Edit Product' : 'Add Product'} - MonikaMart" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="form-card" style="max-width: 650px;">
    <h2 style="font-size: 1.6rem; font-weight: 700; color: var(--dark); margin-bottom: 8px;">
        ${product.id != null ? 'Edit Product Listing' : 'Create New Product Listing'}
    </h2>
    <p style="color: var(--text-muted); font-size: 0.9rem; margin-bottom: 24px;">
        Provide comprehensive product specifications to attract buyers
    </p>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger"><c:out value="${errorMessage}" /></div>
    </c:if>

    <form action="${pageContext.request.contextPath}/seller/product-save" method="post">
        <input type="hidden" name="id" value="${product.id}">

        <div class="form-group">
            <label class="form-label" for="name">Product Name *</label>
            <input type="text" id="name" name="name" class="form-control" required placeholder="e.g. Sony WH-1000XM5 Headphones" value="<c:out value='${product.name}' />">
            <c:if test="${not empty fieldErrors.name}"><div class="field-error"><c:out value="${fieldErrors.name}" /></div></c:if>
        </div>

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px;">
            <div class="form-group">
                <label class="form-label" for="category">Category *</label>
                <input type="text" id="category" name="category" list="categoryOptions" class="form-control" required placeholder="e.g. Electronics" value="<c:out value='${product.category}' />">
                <datalist id="categoryOptions">
                    <option value="Electronics">
                    <option value="Books">
                    <option value="Stationery">
                    <option value="Home & Office">
                    <option value="Fashion">
                </datalist>
                <c:if test="${not empty fieldErrors.category}"><div class="field-error"><c:out value="${fieldErrors.category}" /></div></c:if>
            </div>

            <div class="form-group">
                <label class="form-label" for="price">Price (₹ INR) *</label>
                <input type="number" id="price" name="price" step="0.01" min="1" class="form-control" required placeholder="999.00" value="${product.price}">
                <c:if test="${not empty fieldErrors.price}"><div class="field-error"><c:out value="${fieldErrors.price}" /></div></c:if>
            </div>
        </div>

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px;">
            <div class="form-group">
                <label class="form-label" for="stockQty">Stock Inventory Quantity *</label>
                <input type="number" id="stockQty" name="stockQty" min="0" class="form-control" required placeholder="25" value="${product.stockQty != null ? product.stockQty : 10}">
                <c:if test="${not empty fieldErrors.stockQty}"><div class="field-error"><c:out value="${fieldErrors.stockQty}" /></div></c:if>
            </div>

            <div class="form-group">
                <label class="form-label" for="imageUrl">Product Image URL</label>
                <input type="url" id="imageUrl" name="imageUrl" class="form-control" placeholder="https://images.unsplash.com/..." value="<c:out value='${product.imageUrl}' />">
            </div>
        </div>

        <div class="form-group">
            <label class="form-label" for="description">Full Description *</label>
            <textarea id="description" name="description" class="form-control" rows="4" required placeholder="Highlight key features, technical specifications, and benefits..."><c:out value='${product.description}' /></textarea>
        </div>

        <div style="display: flex; justify-content: flex-end; gap: 12px; margin-top: 24px;">
            <a href="${pageContext.request.contextPath}/seller/dashboard" class="btn btn-secondary">Cancel</a>
            <button type="submit" class="btn btn-primary">Save Listing</button>
        </div>
    </form>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
