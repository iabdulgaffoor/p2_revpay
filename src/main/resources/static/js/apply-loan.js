document.addEventListener('DOMContentLoaded', () => {
            const userStr = sessionStorage.getItem('user');
            if (!userStr || JSON.parse(userStr).role !== 'BUSINESS') {
                window.location.href = '/dashboard';
            }
        });

        async function applyForLoan() {
            const btn = document.querySelector('#loanForm button[type="submit"]');
            const msg = document.getElementById('loanMsg');
            const user = JSON.parse(sessionStorage.getItem('user'));

            const payload = {
                amount: parseFloat(document.getElementById('loanAmount').value),
                tenureMonths: parseInt(document.getElementById('loanTenure').value),
                purpose: document.getElementById('loanPurpose').value
            };

            setButtonLoading(btn, true, 'Processing Requisition...');
            try {
                const res = await fetchWithAuth(`/api/loans/business/${user.id}`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                if (res.ok) {
                    msg.innerText = "Credit requisition authorized for underwriting. Redirecting to portfolio registry...";
                    msg.style.color = 'var(--accent-color)';
                    setTimeout(() => window.location.href = '/loan/status', 2000);
                } else {
                    const data = await res.json();
                    throw new Error(data.message || "Requisition authorization failure.");
                }
            } catch (err) {
                msg.innerText = err.message;
                msg.style.color = 'var(--danger)';
                setButtonLoading(btn, false);
            }
        }

        async function simulateLoan() {
            const principal = document.getElementById('simAmount').value;
            const tenure = document.getElementById('simTenure').value;

            try {
                const res = await fetchWithAuth(`/api/loans/simulate?principal=${principal}&tenureMonths=${tenure}&interestRate=9.5`);
                if (res.ok) {
                    const data = await res.json();
                    document.getElementById('simResults').style.display = 'block';
                    document.getElementById('resEmi').innerText = `USD ${data.monthlyEMI.toFixed(2)}`;
                    document.getElementById('resInterest').innerText = `USD ${data.totalInterest.toFixed(2)}`;
                    document.getElementById('resTotal').innerText = `USD ${data.totalPayment.toFixed(2)}`;
                } else {
                    alert("Assessment failure.");
                }
            } catch (err) {
                console.error(err);
            }
        }