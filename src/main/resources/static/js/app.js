// app.js
document.addEventListener('DOMContentLoaded', () => {
    checkAuthState();
});

function checkAuthState() {
    const token = sessionStorage.getItem('token');
    const userStr = sessionStorage.getItem('user');

    const authLinks = document.getElementById('authenticatedLinks');
    const loginLink = document.getElementById('loginLink');
    const registerLink = document.getElementById('registerLink');
    const logoutLink = document.getElementById('logoutLink');
    const navDashboard = document.getElementById('navDashboard');
    const navTransactions = document.getElementById('navTransactions');
    const navRequests = document.getElementById('navRequests');
    const navCards = document.getElementById('navCards');
    const navNotifications = document.getElementById('navNotifications');
    const navProfile = document.getElementById('navProfile');
    const navAdmin = document.getElementById('navAdmin');
    const navInvoices = document.getElementById('navInvoices');
    const navLoans = document.getElementById('navLoans');
    const navAnalytics = document.getElementById('navAnalytics');

    if (token && userStr) {
        const user = JSON.parse(userStr);

        if (authLinks) {
            authLinks.style.display = 'flex';
            authLinks.style.gap = '1.5rem';
            authLinks.style.alignItems = 'center';
        }
        if (loginLink) loginLink.style.display = 'none';
        if (registerLink) registerLink.style.display = 'none';
        if (logoutLink) logoutLink.style.display = 'inline';

        // Role-based visibility
        if (user.role === 'BUSINESS') {
            if (navInvoices) navInvoices.style.display = 'inline';
            if (navLoans) navLoans.style.display = 'inline';
            if (navAnalytics) navAnalytics.style.display = 'inline';

            // Show business-specific dashboard widgets if we're on the dashboard
            const businessKpis = document.querySelectorAll('.business-only');
            businessKpis.forEach(el => el.style.display = 'flex');
        } else if (user.role === 'ADMIN') {
            if (navAdmin) navAdmin.style.display = 'inline';
        }

        // Show welcome message if it exists
        const welcomeMsg = document.getElementById('welcomeMsg');
        if (welcomeMsg) {
            welcomeMsg.innerText = `Welcome back, ${user.fullName.split(' ')[0]}!`;
        }

        const roleBadge = document.getElementById('roleBadge');
        if (roleBadge) {
            roleBadge.innerText = user.role;
            roleBadge.style.display = 'inline-block';
        }

        // Show app main content (hides it until auth is checked on protected pages)
        const appMainContent = document.getElementById('appMainContent');
        if (appMainContent) {
            appMainContent.style.display = 'block';
        }
    } else {
        if (authLinks) authLinks.style.display = 'none';
        if (loginLink) loginLink.style.display = 'inline';
        if (registerLink) registerLink.style.display = 'inline';
        if (logoutLink) logoutLink.style.display = 'none';

        // If we are on a protected page, redirect to login
        const protectedPaths = ['/dashboard', '/transaction', '/wallet', '/profile', '/settings', '/notifications', '/money-request', '/payment-method'];
        const currentPath = window.location.pathname;
        const isProtected = protectedPaths.some(p => currentPath.startsWith(p));

        if (isProtected) {
            window.location.href = '/login';
        } else {
            const appMainContent = document.getElementById('appMainContent');
            if (appMainContent) {
                appMainContent.style.display = 'block';
            }
        }
    }
}

function handleLoginSuccess(user, token) {
    sessionStorage.setItem('token', token);
    sessionStorage.setItem('jwtToken', token); // Some templates use this
    sessionStorage.setItem('user', JSON.stringify(user));
    sessionStorage.setItem('userId', user.id);
    window.location.href = '/dashboard';
}

function handleLogout() {
    sessionStorage.clear();
    window.location.href = '/login';
}

async function fetchWithAuth(url, options = {}) {
    const token = sessionStorage.getItem('token') || sessionStorage.getItem('jwtToken');

    if (!token) {
        handleLogout();
        throw new Error('No authentication token found');
    }

    const headers = new Headers(options.headers || {});
    headers.set('Authorization', `Bearer ${token}`);

    if (!headers.has('Content-Type') && !(options.body instanceof FormData)) {
        headers.set('Content-Type', 'application/json');
    }

    const config = {
        ...options,
        headers
    };

    const response = await fetch(url, config);
    if (response.status === 401 || response.status === 403) {
        handleLogout();
    }
    return response;
}

/**
 * Toggles a loading spinner state on a given button element
 * @param {HTMLButtonElement} button - The button to toggle
 * @param {boolean} isLoading - Whether to show the loading state
 * @param {string} loadingText - Text to show while loading (e.g. 'Processing...')
 */
function setButtonLoading(button, isLoading, loadingText = 'Processing...') {
    if (!button) return;

    if (isLoading) {
        button.dataset.originalText = button.innerHTML;
        button.innerHTML = `<span class="spinner"></span>${loadingText}`;
        button.disabled = true;
    } else {
        if (button.dataset.originalText) {
            button.innerHTML = button.dataset.originalText;
        }
        button.disabled = false;
    }
}
