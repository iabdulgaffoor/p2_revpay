document.addEventListener('DOMContentLoaded', loadPaymentMethods);

        async function loadPaymentMethods() {
            const userId = sessionStorage.getItem('userId');
            if (!userId) return;
            try {
                const response = await fetchWithAuth(`/api/payment-methods/user/${userId}`);
                document.getElementById('pmLoader').style.display = 'none';

                if (response.ok) {
                    const methods = await response.json();
                    const listDiv = document.getElementById('pmList');
                    listDiv.innerHTML = '';

                    if (methods.length > 0) {
                        methods.forEach(pm => {
                            const typeLabel = pm.type.replace(/_/g, ' ');
                            const isDefaultBadge = pm.isDefault ? '<span class="status-badge status-success" style="font-size: 0.7rem; padding: 2px 8px;">Primary</span>' : '';

                            listDiv.innerHTML += `
                                <div style="padding: 16px 20px; border: 1px solid var(--border-color); border-radius: 8px; display:flex; justify-content:space-between; align-items:center;">
                                    <div>
                                        <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 4px;">
                                            <span style="font-weight: 700; color: var(--primary-color); font-size: 0.9375rem; text-transform: uppercase;">${typeLabel}</span>
                                            ${isDefaultBadge}
                                        </div>
                                        <div style="color: var(--text-muted); font-size: 0.8125rem;">Ref: •••• ${pm.accountNumberMasked.slice(-4)}</div>
                                    </div>
                                    <button onclick="deleteMethod(${pm.id})" class="btn btn-sm btn-danger">Deauthorize</button>
                                </div>`;
                        });
                        listDiv.style.display = 'flex';
                        document.getElementById('noPm').style.display = 'none';
                    } else {
                        document.getElementById('noPm').style.display = 'block';
                        listDiv.style.display = 'none';
                    }
                }
            } catch (err) {
                document.getElementById('pmLoader').innerText = "System error during instrument synchronization.";
            }
        }

        async function handleAddPayment() {
            const userId = sessionStorage.getItem('userId');
            const msg = document.getElementById('addMsg');
            const submitBtn = document.querySelector('#addPaymentForm button[type="submit"]');

            const payload = {
                userId: parseInt(userId),
                type: document.getElementById('methodType').value,
                accountNumberMasked: document.getElementById('cardNumber').value,
                expiryDate: document.getElementById('expiryDate').value,
                billingAddress: document.getElementById('billingAddress').value,
                isDefault: document.getElementById('isDefault').checked
            };

            setButtonLoading(submitBtn, true, 'Authorizing...');

            try {
                const response = await fetchWithAuth(`/api/payment-methods/user/${userId}`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });
                if (response.ok) {
                    msg.innerText = "Instrument successfully authorized.";
                    msg.style.color = "var(--accent-color)";
                    document.getElementById('addPaymentForm').reset();
                    loadPaymentMethods();
                } else {
                    const data = await response.json();
                    throw new Error(data.message || "Authorization failed.");
                }
            } catch (err) {
                msg.innerText = err.message;
                msg.style.color = "var(--danger)";
            } finally {
                setButtonLoading(submitBtn, false);
            }
        }

        async function deleteMethod(id) {
            if (!confirm("Are you sure you want to deauthorize this instrument? This action is irrevocable.")) return;
            const userId = sessionStorage.getItem('userId');
            try {
                const response = await fetchWithAuth(`/api/payment-methods/${id}/user/${userId}`, {
                    method: 'DELETE'
                });
                if (response.ok) {
                    loadPaymentMethods();
                }
            } catch (err) { }
        }