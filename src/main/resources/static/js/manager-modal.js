/**
 * @file manager-modal.js
 * @description Manages automatic modal triggering based on server-side state.
 */
document.addEventListener('DOMContentLoaded', () => {

    // Handle "Add Client" validation errors
    if (window.showAddModal) {
        const addModalEl = document.getElementById('clientModal');
        if (addModalEl) {
            const addModal = new bootstrap.Modal(addModalEl);
            addModal.show();
        }
    }

    // Handle "Edit Client" validation errors
    if (window.showEditModal && window.editClientId) {
        const editModalEl = document.getElementById('editModal');
        if (editModalEl) {
            const editModal = new bootstrap.Modal(editModalEl);
            editModal.show();
        }
    }

    // Optional: Focus first invalid field for better UX
    const firstInvalid = document.querySelector('.is-invalid');
    if (firstInvalid) {
        setTimeout(() => {
            if (document.body.classList.contains('modal-open')) {
                firstInvalid.focus();
            }
        }, 300);
    }
});