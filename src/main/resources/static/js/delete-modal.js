/**
 * @file delete-modal.js
 * @description Manages dynamic content injection for the deletion confirmation modal.
 */
document.addEventListener('DOMContentLoaded', () => {

    const deleteForm = document.getElementById('deleteForm');
    const modalClientName = document.getElementById('modalClientName');
    const modalClientEmail = document.getElementById('modalClientEmail');
    const modalClientAddress = document.getElementById('modalClientAddress');
    const deleteButtons = document.querySelectorAll('.delete-btn');

    if (!deleteButtons.length || !deleteForm) return;

    // Updates modal state when a delete button is triggered. Extracts client metadata and reconfigures the form's POST destination.
    deleteButtons.forEach((button) => {
        button.addEventListener('click', () => {
            const {
                clientId = '',
                clientName = '',
                clientEmail = '',
                clientAddress = '-'
            } = button.dataset;

            // Update UI feedback labels
            if (modalClientName) modalClientName.textContent = clientName;
            if (modalClientEmail) modalClientEmail.textContent = clientEmail;
            if (modalClientAddress) modalClientAddress.textContent = clientAddress;

            // Build the correct URL using the correct variable: clientId
            const baseUrl = window.CLIENTS_URL || '/clients';
            deleteForm.action = `${baseUrl}/${clientId}/delete`;
        });
    });

});