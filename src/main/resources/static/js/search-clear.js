/**
 * @file search-clear.js
 * @description Implements a clean-URL strategy for search resets.
 * Prevents empty query parameters from cluttering the address bar.
 */
document.addEventListener('DOMContentLoaded', () => {

    const clearBtn = document.getElementById('clear');
    const searchForm = document.getElementById('searchForm');
    const keywordInput = document.getElementById('keyword');

    if (!clearBtn || !searchForm) return;

    /**
     * Resets search by clearing input and removing its name attribute.
     * Ensures the browser submits a base URL without empty params (e.g., '?keyword=').
     */
    clearBtn.addEventListener('click', () => {
        if (keywordInput) {
            keywordInput.value = '';

            // Removing 'name' excludes this field from the GET request structure
            keywordInput.removeAttribute('name');
        }

        searchForm.submit();
    });
});