document.addEventListener('DOMContentLoaded', async () => {
            const userId = sessionStorage.getItem('userId');
            if (!userId) return;

            const select = document.getElementById('paymentMethodSelect');
            const noCardsWarning = document.getElementById('noCardsWarning');

            try {
                const res = await fetchWithAuth(`/api/payment-methods/user/${userId}`);
                if (res.ok) {
                    const methods = await res.json();
                    select.innerHTML = '';

                    const cards = methods.filter(m => m.type === 'DEBIT_CARD' || m.type === 'CREDIT_CARD');

                    if (cards.length === 0) {
                        select.innerHTML = '<option value="" disabled selected>No instruments available</option>';
                        select.disabled = true;
                        noCardsWarning.style.display = 'block';
                        document.querySelector('#sendMoneyForm button[type="submit"]').disabled = true;
                    } else {
                        select.innerHTML = '<option value="" disabled>-- Select Instrument --</option>';
                        cards.forEach(m => {
                            const last4 = m.accountNumberMasked ? m.accountNumberMasked.slice(-4) : '****';
                            const typeLabel = m.type === 'CREDIT_CARD' ? 'Credit' : 'Debit';
                            const defLabel = m.isDefault ? ' [Primary]' : '';
                            const opt = document.createElement('option');
                            opt.value = m.id;
                            opt.textContent = `${typeLabel} Instrument Registry (•••• ${last4})${defLabel}`;
                            if (m.isDefault) opt.selected = true;
                            select.appendChild(opt);
                        });
                    }
                }
            } catch (e) {
                select.innerHTML = '<option value="" disabled selected>System synchronization failed</option>';
            }
        });

        async function handleSendMoney() {
            const btn = document.querySelector('#sendMoneyForm button[type="submit"]');

            const selectedCard = document.getElementById('paymentMethodSelect').value;
            const errorDiv = document.getElementById('errorMessage');
            const successDiv = document.getElementById('successMessage');
            const pinHelp = document.getElementById('pinHelpMsg');

            if (!selectedCard) {
                errorDiv.innerText = 'Functional funding source selection is mandatory.';
                errorDiv.style.display = 'block';
                return;
            }

            setButtonLoading(btn, true, 'Processing Authorization...');

            const payload = {
                senderId: parseInt(sessionStorage.getItem('userId')),
                recipientIdentifier: document.getElementById('recipientIdentifier').value,
                amount: parseFloat(document.getElementById('amount').value),
                description: document.getElementById('description').value,
                transactionPin: document.getElementById('pin').value,
                paymentMethodId: parseInt(selectedCard)
            };

            try {
                const response = await fetchWithAuth('/api/transactions/send', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                if (response.ok) {
                    successDiv.innerText = "Transaction Successfully Authorized. Ledger records updated.";
                    successDiv.style.display = 'block';
                    errorDiv.style.display = 'none';
                    pinHelp.style.display = 'none';
                    document.getElementById('sendMoneyForm').reset();
                } else {
                    const data = await response.json();
                    throw new Error(data.message || "Disbursement failed.");
                }
            } catch (err) {
                errorDiv.innerText = err.message;
                errorDiv.style.display = 'block';
                successDiv.style.display = 'none';
                if (err.message && err.message.toLowerCase().includes('pin')) {
                    pinHelp.style.display = 'block';
                } else {
                    pinHelp.style.display = 'none';
                }
            } finally {
                setButtonLoading(btn, false);
            }
        }