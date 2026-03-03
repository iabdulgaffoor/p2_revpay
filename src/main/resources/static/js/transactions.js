document.addEventListener("DOMContentLoaded", function () {
            if (!sessionStorage.getItem('jwtToken') || !sessionStorage.getItem('userId')) {
                window.location.href = "/login";
                return;
            }
            loadTransactions();
        });

        let allTransactions = [];

        async function loadTransactions() {
            const userId = sessionStorage.getItem('userId');
            const loader = document.getElementById('syncLoader');
            try {
                const res = await fetchWithAuth(`/api/transactions/user/${userId}`);
                if (res.ok) {
                    const data = await res.json();
                    allTransactions = data || [];
                    loader.style.display = 'none';
                    renderTable(allTransactions);
                }
            } catch (err) {
                loader.innerText = "Ledger synchronization failure.";
            }
        }

        function renderTable(transactions) {
            const tbody = document.querySelector('#transactionsTable tbody');
            const noRes = document.getElementById('noResults');
            tbody.innerHTML = '';

            if (transactions.length === 0) {
                noRes.style.display = 'block';
                return;
            } else {
                noRes.style.display = 'none';
            }

            transactions.forEach(tx => {
                const tr = document.createElement('tr');
                let statusClass = tx.status === 'COMPLETED' ? 'status-success' : (tx.status === 'FAILED' ? 'status-danger' : 'status-warning');

                const typeLabel = tx.type.replace('_', ' ');
                const isDebit = tx.type === 'SEND' || tx.type === 'WITHDRAW';
                const counterparty = tx.type === 'SEND' || tx.type === 'WITHDRAW' ? (tx.recipientName || 'Registry') : (tx.senderName || 'Registry');

                tr.innerHTML = `
                    <td style="padding: 16px; font-family: monospace; font-size: 0.75rem; color: var(--text-muted);">${tx.id}</td>
                    <td style="padding: 16px; font-size: 0.8125rem;">${new Date(tx.timestamp).toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: 'numeric' })}</td>
                    <td style="padding: 16px;"><span class="status-badge status-info" style="font-size: 0.625rem; font-weight:700;">${typeLabel}</span></td>
                    <td style="padding: 16px; text-align: right; font-weight: 700; color: ${isDebit ? 'var(--danger)' : 'var(--success)'};">
                        ${isDebit ? '-' : '+'} ${tx.amount.toLocaleString(undefined, { minimumFractionDigits: 2 })}
                    </td>
                    <td style="padding: 16px;"><span class="status-badge ${statusClass}" style="font-size: 0.625rem;">${tx.status}</span></td>
                    <td style="padding: 16px; font-size: 0.8125rem; font-weight: 500;">${counterparty}</td>
                    <td style="padding: 16px; color: var(--text-muted); font-size: 0.75rem; max-width: 200px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${tx.note || '-'}</td>
                `;
                tbody.appendChild(tr);
            });
        }

        function applyFilters() {
            const query = document.getElementById('searchQuery').value.toLowerCase();
            const type = document.getElementById('filterType').value;
            const status = document.getElementById('filterStatus').value;
            const startDate = document.getElementById('startDate').value;
            const endDate = document.getElementById('endDate').value;

            const filtered = allTransactions.filter(tx => {
                let matchesQuery = true;
                if (query) {
                    const searchString = `${tx.id} ${tx.senderName || ''} ${tx.recipientName || ''} ${tx.note || ''}`.toLowerCase();
                    matchesQuery = searchString.includes(query);
                }

                const matchesType = type ? tx.type === type : true;
                const matchesStatus = status ? tx.status === status : true;

                let matchesDate = true;
                if (startDate || endDate) {
                    const txDate = new Date(tx.timestamp);
                    if (startDate) {
                        const start = new Date(startDate);
                        start.setHours(0, 0, 0, 0);
                        if (txDate < start) matchesDate = false;
                    }
                    if (endDate) {
                        const end = new Date(endDate);
                        end.setHours(23, 59, 59, 999);
                        if (txDate > end) matchesDate = false;
                    }
                }

                return matchesQuery && matchesType && matchesStatus && matchesDate;
            });

            renderTable(filtered);
        }

        async function exportCSV() {
            const userId = sessionStorage.getItem('userId');
            const token = sessionStorage.getItem('jwtToken');
            const url = `/api/transactions/user/${userId}/export/csv`;

            try {
                const response = await fetch(url, {
                    headers: { 'Authorization': 'Bearer ' + token }
                });
                if (!response.ok) throw new Error("Audit trail export failure.");

                const blob = await response.blob();
                const downloadUrl = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = downloadUrl;
                a.download = `RevPay_Audit_Trail_${new Date().toISOString().split('T')[0]}.csv`;
                document.body.appendChild(a);
                a.click();
                a.remove();
                window.URL.revokeObjectURL(downloadUrl);
            } catch (err) {
                alert(err.message);
            }
        }