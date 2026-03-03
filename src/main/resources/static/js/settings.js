document.addEventListener('DOMContentLoaded', async () => {
            document.getElementById('syncDate').innerText = new Date().toLocaleString();

            const userStr = sessionStorage.getItem('user');
            if (userStr) {
                const user = JSON.parse(userStr);
                document.getElementById('profFullName').value = user.fullName || '';
                document.getElementById('profPhone').value = user.phoneNumber || '';
                document.getElementById('profSecurityQuestion').value = user.securityQuestion || '';

                if (user.role === 'BUSINESS') {
                    document.getElementById('businessUpdateFields').style.display = 'block';
                    document.getElementById('profBusinessName').value = user.businessName || '';
                    document.getElementById('profBusinessType').value = user.businessType || '';
                    document.getElementById('profTaxId').value = user.taxId || '';
                    document.getElementById('profBusinessAddress').value = user.businessAddress || '';

                    document.getElementById('profBusinessName').required = true;
                    document.getElementById('profTaxId').required = true;
                }
            }
        });

        async function updateProfile() {
            const msg = document.getElementById('profileMsg');
            const btn = document.querySelector('#profileForm button[type="submit"]');
            const userStr = sessionStorage.getItem('user');
            if (!userStr) return;
            let user = JSON.parse(userStr);

            user.fullName = document.getElementById('profFullName').value;
            user.phoneNumber = document.getElementById('profPhone').value;

            if (user.role === 'BUSINESS') {
                user.businessName = document.getElementById('profBusinessName').value;
                user.businessType = document.getElementById('profBusinessType').value;
                user.taxId = document.getElementById('profTaxId').value;
                user.businessAddress = document.getElementById('profBusinessAddress').value;
            }

            setButtonLoading(btn, true, 'Synchronizing Registry...');
            try {
                const response = await fetchWithAuth(`/api/users/${user.id}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(user)
                });

                if (response.ok) {
                    const updatedUser = await response.json();
                    sessionStorage.setItem('user', JSON.stringify(updatedUser));
                    msg.innerText = "Entity registry successfully synchronized.";
                    msg.style.color = 'var(--accent-color)';
                } else {
                    const data = await response.json();
                    throw new Error(data.message || "Registry synchronization failed.");
                }
            } catch (err) {
                msg.innerText = err.message;
                msg.style.color = 'var(--danger)';
            } finally {
                setButtonLoading(btn, false);
            }
        }

        async function updatePassword() {
            const currentPassword = document.getElementById('currentPassword').value;
            const newPassword = document.getElementById('newPassword').value;
            const msg = document.getElementById('passMsg');
            const btn = document.querySelector('#passwordForm button[type="submit"]');
            const userId = sessionStorage.getItem('userId');

            setButtonLoading(btn, true, 'Validating Credentials...');
            try {
                const response = await fetchWithAuth(`/api/users/${userId}/password`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ currentPassword, newPassword })
                });

                if (response.ok) {
                    msg.innerText = "Credentials successfully localized and secured.";
                    msg.style.color = 'var(--accent-color)';
                    document.getElementById('passwordForm').reset();
                } else {
                    const data = await response.json();
                    throw new Error(data.message || "Credential validation failed.");
                }
            } catch (err) {
                msg.innerText = err.message;
                msg.style.color = 'var(--danger)';
            } finally {
                setButtonLoading(btn, false);
            }
        }

        async function updatePin() {
            const newPin = document.getElementById('newPin').value;
            const msg = document.getElementById('pinMsg');
            const btn = document.querySelector('#pinForm button[type="submit"]');
            const userId = sessionStorage.getItem('userId');

            setButtonLoading(btn, true, 'Updating Protection Layer...');
            try {
                const response = await fetchWithAuth(`/api/users/${userId}/pin`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ newPin: newPin })
                });

                if (response.ok) {
                    msg.innerText = "Authorization PIN successfully registered.";
                    msg.style.color = 'var(--accent-color)';
                    document.getElementById('pinForm').reset();
                } else {
                    const data = await response.json();
                    throw new Error(data.message || "PIN registration failed.");
                }
            } catch (err) {
                msg.innerText = err.message;
                msg.style.color = 'var(--danger)';
            } finally {
                setButtonLoading(btn, false);
            }
        }