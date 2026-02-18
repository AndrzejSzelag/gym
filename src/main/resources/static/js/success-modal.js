/**
 * @file success-modal.js
 * @description Global success notification with automatic dismissal and focus management.
 */

/**
 * Displays a success message in a static modal.
 * Triggers a 3-second auto-dismiss timer to streamline UX.
 * @param {string} message - Success notification text.
 */
function showSuccessModal(message) {
    const modalEl = document.getElementById('successModal');
    const messageText = document.getElementById('successMessageText');

    if (!modalEl) return;

    if (messageText && message) {
        messageText.textContent = message;
    }

    const modal = new bootstrap.Modal(modalEl, { backdrop: 'static' });

    /**
     * Post-dismissal focus management and DOM cleanup.
     * Restores page accessibility (WCAG) and purges Bootstrap artifacts.
     */
    modalEl.addEventListener('hidden.bs.modal', () => {
        const landingElement = document.querySelector('.renew-btn') || document.body;
        landingElement?.focus();

        // Purge Bootstrap style injections and backdrop elements
        document.body.classList.remove('modal-open');
        document.querySelectorAll('.modal-backdrop').forEach(b => b.remove());
        document.body.style = '';
    }, { once: true });

    modal.show();

    // Auto-dismiss logic to prevent unnecessary manual interaction
    setTimeout(() => {
        const modalInstance = bootstrap.Modal.getInstance(modalEl);
        modalInstance?.hide();
    }, 3000);
}

/**
 * Automatically triggers the success modal if a message is present
 * in the global configuration (provided by Thymeleaf).
 */
document.addEventListener('DOMContentLoaded', () => {
    if (window.hasSuccessMessage && window.successMessage) {
        showSuccessModal(window.successMessage);
    }
});