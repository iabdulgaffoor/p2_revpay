function toggleBusinessFields() {
            const type = document.getElementById("accountType").value;
            const bFields = document.getElementById("businessFields");
            if (type === "BUSINESS") {
                bFields.style.display = "block";
                document.getElementById("businessName").required = true;
                document.getElementById("taxId").required = true;
            } else {
                bFields.style.display = "none";
                document.getElementById("businessName").required = false;
                document.getElementById("taxId").required = false;
            }
        }

        async function submitRegister() {
            const type = document.getElementById("accountType").value;
            const endpoint = type === "BUSINESS" ? '/api/auth/register/business' : '/api/auth/register/personal';
            const btn = document.querySelector('#registerForm button[type="submit"]');

            const payload = {
                user: {
                    fullName: document.getElementById("fullName").value,
                    email: document.getElementById("email").value,
                    phoneNumber: document.getElementById("phone").value,
                    securityQuestion: document.getElementById("securityQuestion").value,
                    securityAnswer: document.getElementById("securityAnswer").value,
                    transactionPin: document.getElementById("transactionPin").value,
                    businessName: document.getElementById("businessName").value,
                    businessType: document.getElementById("businessType").value,
                    taxId: document.getElementById("taxId").value,
                    businessAddress: document.getElementById("businessAddress").value
                },
                password: document.getElementById("password").value
            };

            setButtonLoading(btn, true, 'Registering...');
            try {
                const response = await fetch(endpoint, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                if (!response.ok) {
                    const err = await response.json();
                    throw new Error(err.message || "Registration failed");
                }

                document.getElementById("successMessage").innerText = "Registration successful! You may now login.";
                document.getElementById("successMessage").style.display = "block";
                document.getElementById("errorMessage").style.display = "none";
                document.getElementById("registerForm").reset();
            } catch (error) {
                document.getElementById("errorMessage").innerText = error.message;
                document.getElementById("errorMessage").style.display = "block";
                document.getElementById("successMessage").style.display = "none";
            } finally {
                setButtonLoading(btn, false);
            }
        }