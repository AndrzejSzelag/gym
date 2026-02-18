/**
 * @file fetch-interceptor.js
 * @description Global interceptor for the Fetch API.
 * Standardizes the application's response to session expiration (401) and missing resources (404).
 */
(() => {
    const { fetch: originalFetch } = window;

    /**
     * Proxies the global fetch function to inject cross-cutting concerns
     * such as automated security redirects and error handling.
     */
    window.fetch = async (...args) => {
        const response = await originalFetch(...args);

        /**
         * 401 Unauthorized: Session timed out.
         * Forcefully purges modal artifacts to prevent UI freezes before redirection.
         */
        if (response.status === 401) {
            // Dismiss all active Bootstrap modal instances
            document.querySelectorAll('.modal.show').forEach(modal => {
                bootstrap.Modal.getInstance(modal)?.hide();
            });

            // Clean up DOM artifacts and body style injections
            document.querySelectorAll('.modal-backdrop').forEach(b => b.remove());
            document.body.classList.remove('modal-open');
            document.body.style.paddingRight = '';

            setTimeout(() => {
                window.location.replace('/login?timeout=true');
            }, 250);

            throw new Error('Session expired: Redirecting to login.');
        }

        /**
         * 404 Not Found: Missing resource.
         * Rejects with a structured object for localized UI error handling.
         */
        if (response.status === 404) {
            return Promise.reject({ notFound: true, url: args[0] });
        }

        return response;
    };
})();