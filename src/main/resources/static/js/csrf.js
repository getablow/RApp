document.addEventListener('DOMContentLoaded', function() {
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        let csrfInput = form.querySelector('input[name="_csrf"]');
        if (!csrfInput) {
            csrfInput = document.createElement('input');
            csrfInput.setAttribute('type', 'hidden');
            csrfInput.setAttribute('name', '_csrf');
            form.appendChild(csrfInput);
        }
        csrfInput.setAttribute('value', csrfToken);
    });

    const ajaxHeaders = {
        'Content-Type': 'application/json',
        [csrfHeader]: csrfToken
    };

    window.csrfFetch = function(url, options = {}) {
        options.headers = Object.assign({}, options.headers, ajaxHeaders);
        return fetch(url, options);
    };
});