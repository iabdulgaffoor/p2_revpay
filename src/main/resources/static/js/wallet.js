document.addEventListener("DOMContentLoaded", function () {
            if (!sessionStorage.getItem('jwtToken') || !sessionStorage.getItem('userId')) {
                window.location.href = "/login";
                return;
            }
            loadWalletBalance();
        });

        async function loadWalletBalance() {
            const userId = sessionStorage.getItem('userId');
            try {
                const res = await fetchWithAuth(`/api/wallets/user/${userId}`);
                if (res.ok) {
                    const data = await res.json();
                    document.getElementById('walletBalance').innerText = data.balance.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 });
                }
            } catch (e) {
                console.error("Balance synchronization failure:", e);
            }
        }

        async function addFunds() {
            const btn = document.querySelector('#addFundsForm button[type="submit"]');
            const amount = document.getElementById('addAmount').value;
            const userId = sessionStorage.getItem('userId');
            const msg = document.getElementById('addMsg');

            setButtonLoading(btn, true, 'Processing Inflow...');
            try {
                const res = await fetchWithAuth(`/api/wallets/user/${userId}/add?amount=${amount}`, { method: 'POST' });
                if (res.ok) {
                    msg.innerText = "Liquidity inflow authorized by registry.";
                    msg.style.color = "var(--accent-color)";
                    msg.style.display = "block";
                    loadWalletBalance();
                    document.getElementById('addFundsForm').reset();
                } else {
                    throw new Error("Inflow authorization failure.");
                }
            } catch (err) {
                msg.innerText = err.message;
                msg.style.color = "var(--danger)";
                msg.style.display = "block";
            } finally {
                setButtonLoading(btn, false);
            }
        }

        async function withdrawFunds() {
            const btn = document.querySelector('#withdrawFundsForm button[type="submit"]');
            const amount = document.getElementById('withdrawAmount').value;
            const userId = sessionStorage.getItem('userId');
            const msg = document.getElementById('withdrawMsg');

            setButtonLoading(btn, true, 'Requisitioning...');
            try {
                const res = await fetchWithAuth(`/api/wallets/user/${userId}/withdraw?amount=${amount}`, { method: 'POST' });
                if (res.ok) {
                    msg.innerText = "Liquidity disbursement requisitioned.";
                    msg.style.color = "var(--accent-color)";
                    msg.style.display = "block";
                    loadWalletBalance();
                    document.getElementById('withdrawFundsForm').reset();
                } else {
                    const data = await res.json();
                    throw new Error(data.message || "Disbursement requisition failure.");
                }
            } catch (err) {
                msg.innerText = err.message;
                msg.style.color = "var(--danger)";
                msg.style.display = "block";
            } finally {
                setButtonLoading(btn, false);
            }
        }