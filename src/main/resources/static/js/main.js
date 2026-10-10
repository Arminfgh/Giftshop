// ===== Mobile navigation toggle (public site) =====
(function () {
    var navToggle = document.getElementById('navToggle');
    var mainNav = document.getElementById('mainNav');
    if (navToggle && mainNav) {
        navToggle.addEventListener('click', function () {
            mainNav.classList.toggle('open');
        });
    }
})();

// ===== Admin sidebar toggle (mobile) =====
(function () {
    var menuToggle = document.getElementById('adminMenuToggle');
    var sidebar = document.getElementById('adminSidebar');
    if (menuToggle && sidebar) {
        menuToggle.addEventListener('click', function () {
            sidebar.classList.toggle('open');
        });
    }
})();

// ===== Quantity + / - buttons on product page =====
(function () {
    var buttons = document.querySelectorAll('.qty-btn[data-action]');
    buttons.forEach(function (btn) {
        btn.addEventListener('click', function () {
            var control = btn.closest('.qty-control');
            if (!control) return;
            var input = control.querySelector('.qty-input');
            if (!input) return;
            var value = parseInt(input.value, 10) || 1;
            if (btn.getAttribute('data-action') === 'plus') {
                value++;
            } else if (value > 1) {
                value--;
            }
            input.value = value;
        });
    });
})();

// ===== Auto-hide toast messages after a few seconds =====
(function () {
    var toasts = document.querySelectorAll('.toast');
    toasts.forEach(function (toast) {
        setTimeout(function () {
            toast.style.transition = 'opacity .5s';
            toast.style.opacity = '0';
            setTimeout(function () { toast.style.display = 'none'; }, 500);
        }, 4000);
    });
})();
