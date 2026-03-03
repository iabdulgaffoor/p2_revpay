document.addEventListener('DOMContentLoaded', async () => {
            const userId = sessionStorage.getItem('userId');
            if (!userId) {
                window.location.href = '/login';
                return;
            }

            try {
                const response = await fetchWithAuth(`/api/users/${userId}`);
                if (response.ok) {
                    const data = await response.json();
                    document.getElementById('profName').innerText = data.fullName;
                    document.getElementById('profEmail').innerText = data.email;
                    document.getElementById('profPhone').innerText = data.phoneNumber || 'Registry record missing';
                    document.getElementById('profRole').innerText = data.role;
                    document.getElementById('profQuestion').innerText = data.securityQuestion;

                    if (data.role === 'BUSINESS') {
                        document.getElementById('businessInfo').style.display = 'block';
                        document.getElementById('profBusName').innerText = data.businessName || 'Entity unregistered';
                        document.getElementById('profBusType').innerText = data.businessType || 'General classification';
                    }

                    document.getElementById('loader').style.display = 'none';
                    document.getElementById('profileDetails').style.display = 'block';
                } else {
                    throw new Error("Identity synchronization failure.");
                }
            } catch (err) {
                document.getElementById('loader').style.display = 'none';
                document.getElementById('profError').innerText = err.message;
                document.getElementById('profError').style.display = 'block';
            }
        });