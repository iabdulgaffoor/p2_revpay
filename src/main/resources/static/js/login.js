async function submitLogin() {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const btn = document.querySelector('#loginForm button[type="submit"]');

    setButtonLoading(btn, true, 'Logging in...');
    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email: email, password: password })
        });

        if (!response.ok) {
            let errMsg = 'Invalid credentials';
            try {
                const errData = await response.json();
                errMsg = errData.message || errMsg;
            } catch (e) { }
            throw new Error(errMsg);
        }

        const data = await response.json();
        if (data.token && data.user) {
            handleLoginSuccess(data.user, data.token);
        }
    } catch (error) {
        const errorDiv = document.getElementById("errorMessage");
        errorDiv.innerText = error.message;
        errorDiv.style.display = "block";
    } finally {
        setButtonLoading(btn, false);
    }
}

function showForgotPasswordForm() {
    document.getElementById("loginForm").style.display = "none";
    document.getElementById("resetPasswordForm").style.display = "none";
    document.getElementById("forgotPasswordForm").style.display = "block";
    document.getElementById("registerText").style.display = "none";
}

function showLoginForm() {
    document.getElementById("forgotPasswordForm").style.display = "none";
    document.getElementById("resetPasswordForm").style.display = "none";
    document.getElementById("loginForm").style.display = "block";
    document.getElementById("registerText").style.display = "block";
}

async function submitForgotPassword() {
    const email = document.getElementById("resetEmail").value;
    const btn = document.querySelector('#forgotPasswordForm button[type="submit"]');

    setButtonLoading(btn, true, 'Sending email...');
    try {
        const response = await fetch('/api/auth/forgot-password', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email: email })
        });
        const data = await response.json();
        alert(data.message);
        document.getElementById("forgotPasswordForm").style.display = "none";
        document.getElementById("resetPasswordForm").style.display = "block";
    } catch (err) {
        const errorDiv = document.getElementById("forgotErrorMessage");
        errorDiv.innerText = "Error requesting password reset.";
        errorDiv.style.display = "block";
    } finally {
        setButtonLoading(btn, false);
    }
}

async function submitResetPassword() {
    const email = document.getElementById("resetEmail").value;
    const newPassword = document.getElementById("newPassword").value;
    const btn = document.querySelector('#resetPasswordForm button[type="submit"]');

    setButtonLoading(btn, true, 'Resetting...');
    try {
        const response = await fetch('/api/auth/reset-password', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email: email, newPassword: newPassword })
        });

        if (!response.ok) throw new Error("Password reset failed");

        const data = await response.json();
        alert(data.message);
        showLoginForm();
    } catch (err) {
        const errorDiv = document.getElementById("resetErrorMessage");
        errorDiv.innerText = err.message;
        errorDiv.style.display = "block";
    } finally {
        setButtonLoading(btn, false);
    }
}
