/**
 * @file form-validation.js
 * @description Native Bootstrap validation handler for client forms.
 * Prevents submission if required fields are missing or invalid.
 */
document.addEventListener('DOMContentLoaded', () => {
    // Select all forms that should have validation (add/edit)
    const forms = document.querySelectorAll('.needs-validation');

    forms.forEach(form => {
        form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }

            form.classList.add('was-validated');
        }, false);
    });
});