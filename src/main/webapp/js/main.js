// MonikaMart Main JavaScript
document.addEventListener('DOMContentLoaded', () => {
    // Demo Account Autofill for quick login
    window.fillCredentials = function(email, password) {
        const emailInput = document.getElementById('email');
        const passwordInput = document.getElementById('password');
        if (emailInput && passwordInput) {
            emailInput.value = email;
            passwordInput.value = password;
        }
    };

    // Auto-fade dismissible alerts after 4 seconds
    const alerts = document.querySelectorAll('.alert-success');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s ease';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        }, 4000);
    });
});
