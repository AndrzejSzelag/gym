/**
 * @file server-modals.js
 * @description Bridge between Spring Boot Flash Attributes and UI Modals.
 * Automatically triggers Success or Error notifications based on server-side state.
 */
document.addEventListener('DOMContentLoaded', () => {

    /**
     * Resolves and executes the global modal handler for server-side messages.
     * @param {string|null} message - Message content injected by Thymeleaf.
     * @param {string} handlerName - Name of the global function (showSuccessModal/showErrorModal).
     */
    const triggerNotification = (message, handlerName) => {
        if (!message) return;

        const handler = window[handlerName];
        if (typeof handler === 'function') {
            handler(message);
        } else {
            console.warn(`[Notification] Modal handler "${handlerName}" not found.`);
        }
    };

    // Global variables are populated via Thymeleaf fragments in the main layout
    triggerNotification(window.successMessage, 'showSuccessModal');
    triggerNotification(window.errorMessage, 'showErrorModal');
});