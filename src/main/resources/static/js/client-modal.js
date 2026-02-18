/**
 * @file client-modal.js
 * @description Manages the Client Add modal lifecycle, including state persistence
 * for validation errors and automated DOM cleanup after closing.
 */
document.addEventListener('DOMContentLoaded', () => {
    const modalEl = document.getElementById('clientModal');
    const form = document.getElementById('addClientForm');
    if (!modalEl || !form) return;

    // Reset the visibility flag on manual dismissal to prevent unexpected behavior.
    modalEl.querySelectorAll('[data-bs-dismiss="modal"], .btn-close').forEach(btn => {
        btn.addEventListener('click', () => {
            window.showAddModal = false;
        });
    });

    // Cleans up form state and removes validation artifacts on modal hide. Ensures a fresh state for the next opening.
    modalEl.addEventListener('hidden.bs.modal', () => {

        // Reset all input fields to their default empty values
        form.querySelectorAll('input, textarea, select').forEach(el => el.value = '');

        // Remove CSS error classes and clear validation feedback messages
        form.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
        form.querySelectorAll('.invalid-feedback').forEach(el => el.textContent = '');
    });

});