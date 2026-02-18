/**
 * @file error-modal.js
 * @description Global singleton interface for displaying system errors.
 * Ensures UI consistency and focus management after modal dismissal.
 */

/**
 * Displays the error modal with a sanitized message.
 * Uses a static backdrop to enforce user acknowledgment.
 * @param {string} message - Localized or system error message.
 */
function showErrorModal(message) {
    const modalEl = document.getElementById('errorModal');
    const messageText = document.getElementById('errorMessageText');

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
        const focusLanding =
            document.querySelector('.renew-btn') ||
            document.querySelector('.search-btn') ||
            document.body;

        focusLanding?.focus();

        // Purge Bootstrap style injections and backdrop elements
        document.body.classList.remove('modal-open');
        document.querySelectorAll('.modal-backdrop').forEach(b => b.remove());
        document.body.style = '';
    }, { once: true });

    modal.show();
}

/**
 * Automatically triggers the error modal if an error message is present
 * in the global configuration (provided by Thymeleaf).
 */
document.addEventListener('DOMContentLoaded', () => {
    if (window.hasErrorMessage && window.errorMessage) {
        showErrorModal(window.errorMessage);
    }
});