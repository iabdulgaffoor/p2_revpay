document.addEventListener("DOMContentLoaded", function () {
            if (sessionStorage.getItem('jwtToken')) {
                window.location.href = "/dashboard";
            }
        });