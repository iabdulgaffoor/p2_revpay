document.addEventListener('DOMContentLoaded', async () => {
    const user = JSON.parse(sessionStorage.getItem('user'));
    const userId = sessionStorage.getItem('userId');
    if (!userId) return;

    document.getElementById('appMainContent').style.display = 'block';

    try {
        if (user) {
            document.getElementById('welcomeMsg').innerText = `Session: ${user.name || 'Anonymous'}`;
            const rb = document.getElementById('roleBadge');
            rb.innerText = user.role;
            rb.style.display = 'inline-block';

            if (user.role === 'BUSINESS') {
                document.querySelectorAll('.business-only').forEach(el => el.style.display = 'block');
            }
        }

        const walletRes = await fetchWithAuth(`/api/wallets/user/${userId}`);
        if (walletRes.ok) {
            const wallet = await walletRes.json();
            document.getElementById('walletBalance').innerText = `USD ${wallet.balance.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
        }

        if (user && user.role === 'BUSINESS') {
            const analyticsRes = await fetchWithAuth(`/api/analytics/business/${userId}/summary`);
            if (analyticsRes.ok) {
                const data = await analyticsRes.json();
                document.getElementById('totalReceived').innerText = `USD ${(data.totalReceived || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}`;
                document.getElementById('totalSent').innerText = `USD ${(data.totalSent || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}`;
            }
        }

        const notifyRes = await fetchWithAuth(`/api/notifications/user/${userId}/unread`);
        if (notifyRes.ok) {
            const unreadNotifs = await notifyRes.json();
            document.getElementById('notificationCount').innerText = unreadNotifs.length;
        }

        const reqRes = await fetchWithAuth(`/api/money-requests/user/${userId}/incoming`);
        if (reqRes.ok) {
            const incomingReqs = await reqRes.json();
            const pendingReqs = incomingReqs.filter(req => req.status === 'PENDING');
            document.getElementById('pendingRequestsCount').innerText = pendingReqs.length;
        }

        const transRes = await fetchWithAuth(`/api/transactions/user/${userId}`);
        if (transRes.ok) {
            const transactions = await transRes.json();
            if (transactions.length > 0) {
                document.getElementById('noTransactions').style.display = 'none';
                const table = document.getElementById('transactionsTable');
                const list = document.getElementById('transactionList');
                table.style.display = 'table';

                transactions.slice(0, 5).forEach(t => {
                    const isCredit = t.type === 'DEPOSIT' || t.type === 'ADD_FUNDS' || (t.recipientId == userId && t.type === 'SEND');
                    const dateObj = Array.isArray(t.timestamp)
                        ? new Date(t.timestamp[0], t.timestamp[1] - 1, t.timestamp[2])
                        : new Date(t.timestamp);
                    const dateStr = dateObj.toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: 'numeric' });

                    const amountPrefix = isCredit ? '+' : '-';
                    const amountColor = isCredit ? 'var(--success)' : 'var(--danger)';

                    let counterparty = '';
                    if (t.type === 'ADD_FUNDS' || t.type === 'DEPOSIT') counterparty = 'Registry Credit';
                    else if (t.type === 'WITHDRAW') counterparty = 'Liquidity Withdrawal';
                    else counterparty = (t.senderId == userId) ? t.recipientName : t.senderName;

                    let statusClass = t.status === 'COMPLETED' ? 'status-success' : (t.status === 'FAILED' ? 'status-danger' : 'status-warning');

                    list.innerHTML += `
                        <tr style="border-bottom: 1px solid var(--border-color);">
                            <td style="padding: 16px 24px; font-size: 0.875rem; color: var(--text-secondary);">${dateStr}</td>
                            <td style="padding: 16px 24px;"><span class="status-badge status-neutral" style="font-size: 0.625rem; text-transform: uppercase;">${t.type.replace('_', ' ')}</span></td>
                            <td style="padding: 16px 24px; font-weight: 700; color: ${amountColor}; font-size: 0.875rem;">${amountPrefix}USD ${t.amount.toLocaleString(undefined, { minimumFractionDigits: 2 })}</td>
                            <td style="padding: 16px 24px; font-size: 0.875rem; color: var(--primary-color); font-weight: 500;">${counterparty}</td>
                            <td style="padding: 16px 24px;"><span class="status-badge ${statusClass}" style="font-size: 0.625rem;">${t.status}</span></td>
                        </tr>`;
                });
            }
        }
    } catch (err) {
        console.error("Dashboard registry failure:", err);
    }
});
