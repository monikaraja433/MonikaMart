// MonikaMart Main JavaScript
document.addEventListener('DOMContentLoaded', () => {
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
