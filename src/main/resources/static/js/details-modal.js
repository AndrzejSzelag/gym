/**
 * @file details-modal.js
 * @description Manages asynchronous loading of client details via AJAX.
 * Handles session timeouts, error states, and automated DOM cleanup for injected fragments.
 */
document.addEventListener('DOMContentLoaded', () => {

    const container = document.getElementById('details-modal');
    if (!container) return;

    const clearContainer = () => container.innerHTML = '';

    // Redirects to login if the response indicates session expiry.
    const handleSessionTimeout = (response) => {
        if (response.redirected || response.url.includes('/login')) {
            window.location.replace('/login?timeout=true');
            return true;
        }
        return false;
    };

    let loading = false;

    // Fetches client detail HTML fragment and initializes the Bootstrap modal.
    const loadClientDetails = async (clientId) => {
        if (loading) return;
        loading = true;
        clearContainer();

        try {
            // Using global window.CLIENTS_URL instead of hardcoded string
            const baseUrl = window.CLIENTS_URL || '/clients';
            const response = await fetch(`${baseUrl}/${clientId}/details`, { credentials: 'same-origin' });

            if (handleSessionTimeout(response)) return;
            if (!response.ok) throw new Error(`HTTP error: ${response.status}`);

            const html = await response.text();
            if (!html) return;

            container.innerHTML = html;
            const modalEl = container.querySelector('.modal');
            if (!modalEl) return;

            const modal = new bootstrap.Modal(modalEl);

            // Cleanup: purge injected fragment from DOM upon closure
            modalEl.addEventListener('hidden.bs.modal', clearContainer, { once: true });
            modal.show();

        } catch (err) {
            console.error('Failed to load client details:', err);
            clearContainer();
        } finally {
            loading = false;
        }
    };

    // Global listener for dynamic detail buttons
    document.addEventListener('click', async (event) => {
        const btn = event.target.closest('.details-ajax-btn');
        if (btn && !loading) {
            const { clientId } = btn.dataset;
            if (clientId) {
                btn.classList.add('opacity-50', 'pe-none');
                await loadClientDetails(clientId);
                btn.classList.remove('opacity-50', 'pe-none');
            }
        }
    });

});