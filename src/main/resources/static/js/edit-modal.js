/**
 * @file edit-modal.js
 * @description Manages the Edit Client modal lifecycle.
 * Specifically handles re-opening the modal after failed server-side validation.
 */
document.addEventListener('DOMContentLoaded', () => {

    const modalEl = document.getElementById('editModal');
    const editForm = document.getElementById('editForm');

    if (!modalEl) return;

    /**
     * Prevents sending empty address strings to the server.
     * Excludes empty fields from the POST request to ensure backend receives nulls.
     */
    if (editForm) {
        editForm.addEventListener('submit', () => {
            const addressFields = editForm.querySelectorAll(
                '[id^="editStreet"], [id^="editPostCode"], [id^="editCity"], [id^="editHomeNumber"], [id^="editStreetNumber"]'
            );

            addressFields.forEach(input => {
                if (!input.value.trim()) {
                    input.removeAttribute('name');
                }
            });
        });
    }

    /**
     * Manual cleanup of Bootstrap artifacts.
     * Prevents UI freezing when modals are triggered during page reloads.
     */
    modalEl.addEventListener('hidden.bs.modal', () => {
        document.body.classList.remove('modal-open');
        document.querySelectorAll('.modal-backdrop').forEach(el => el.remove());
        document.body.style = '';
    }, { once: true });

});