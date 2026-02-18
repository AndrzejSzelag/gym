/**
 * @file renew-modal.js
 * @description Manages membership renewal logic within the modal.
 * Handles dynamic date calculations, UI state updates, and form action mapping.
 */
document.addEventListener('DOMContentLoaded', () => {
    const renewModal = document.getElementById('renewModal');
    const renewForm = document.getElementById('renewForm');
    const renewClientName = document.getElementById('renewClientName');
    const renewClientExpiration = document.getElementById('renewClientExpiration');
    const newExpirationPreview = document.getElementById('newExpirationPreview');

    // Extract localized text for missing membership status from data-attributes
    const noMembershipText = renewModal?.dataset.noMembershipText || '-';

    if (!renewModal || !renewForm || !newExpirationPreview) return;

    /**
     * Formats a Date object into a string (dd.MM.yyyy).
     * @param {Date} date - The date object to format.
     * * $1s {string} Formatted date string.
     */
    const formatDate = (date) => {
        const d = String(date.getDate()).padStart(2, '0');
        const m = String(date.getMonth() + 1).padStart(2, '0');
        const y = date.getFullYear();
        return `${d}.${m}.${y}`;
    };

    /**
     * Calculates a 30-day extension from a given date.
     * @param {Date|string} fromDate - The starting date for calculation.
     * * $1s {string} New expiration date in dd.MM.yyyy format.
     */
    const calculateNewExpiration = (fromDate) => {
        const newDate = new Date(fromDate);
        newDate.setDate(newDate.getDate() + 30);
        return formatDate(newDate);
    };

    /**
     * Event delegation for renewal and assignment triggers (.renew-btn, .assign-btn).
     * Updates modal content and form action based on client metadata.
     */
    document.addEventListener('click', (event) => {
        const btn = event.target.closest('.renew-btn, .assign-btn');
        if (!btn) return;

        // Retrieve data from button data-attributes (mapped by Thymeleaf)
        // Changed "id" to "clientId" for consistency across all scripts
        const { clientId = '', clientName: name = '', clientExpiration: exp = '' } = btn.dataset;

        // Build the correct URL using the global CLIENTS_URL or fallback
        const baseUrl = window.CLIENTS_URL || '/clients';
        renewForm.action = `${baseUrl}/${clientId}/renew`;

        if (renewClientName) renewClientName.textContent = name || '';

        const now = new Date();
        now.setHours(0, 0, 0, 0);

        let startDate = new Date(now);
        const hasExpiration = exp && exp.trim() !== '' && exp !== 'null';

        if (hasExpiration) {
            // Convert YYYY-MM-DD to JS Date (replace "-" with "/" for cross-browser compatibility)
            const membershipValidity = new Date(exp.replace(/-/g, '/'));
            membershipValidity.setHours(0, 0, 0, 0);

            // Extend from current expiry only if it's still valid (in the future)
            if (!isNaN(membershipValidity.getTime()) && membershipValidity >= now) {
                startDate = new Date(membershipValidity);
            }
        }

        // 1. Update the preview of the new expiration date (dd.MM.yyyy)
        newExpirationPreview.textContent = calculateNewExpiration(startDate);

        // 2. Update the current expiration status badge
        if (renewClientExpiration) {
            if (hasExpiration) {
                const currentExpDate = new Date(exp.replace(/-/g, '/'));
                renewClientExpiration.textContent = formatDate(currentExpDate);
                renewClientExpiration.className = 'badge bg-secondary shadow-sm';
            } else {
                // If no membership exists, show fallback text and warning styling
                renewClientExpiration.textContent = noMembershipText;
                renewClientExpiration.className = 'badge bg-warning text-dark shadow-sm';
            }
        }
    });
});