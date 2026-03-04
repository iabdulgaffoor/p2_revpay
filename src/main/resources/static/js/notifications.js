document.addEventListener('DOMContentLoaded', function () {
    fetchNotifications();
});

async function fetchNotifications() {
    const userId = sessionStorage.getItem('userId');
    if (!userId) return;

    try {
        const response = await fetchWithAuth(`/api/notifications/user/${userId}`);
        if (response.ok) {
            const data = await response.json();
            const list = document.getElementById('notificationsList');
            list.innerHTML = '';
            if (data.length === 0) {
                list.innerHTML = '<div class="card" style="padding: 32px; text-align: center; color: var(--text-muted);">No active system dispatches found in registry.</div>';
                return;
            }

            // Strictly sort by date DESC so they don't jump around when read
            data.sort((a, b) => {
                const dateA = Array.isArray(a.createdAt) ? new Date(...a.createdAt) : new Date(a.createdAt);
                const dateB = Array.isArray(b.createdAt) ? new Date(...b.createdAt) : new Date(b.createdAt);
                return dateB - dateA;
            });

            data.forEach(n => {
                const d = document.createElement('div');
                d.className = 'card notification-item' + (n.read ? '' : ' unread');
                d.style.padding = '24px';
                d.style.boxShadow = 'none';
                d.style.border = '1px solid var(--border-color)';
                if (!n.read) d.style.borderLeft = '4px solid var(--accent-color)';

                const dateObj = Array.isArray(n.createdAt)
                    ? new Date(n.createdAt[0], n.createdAt[1] - 1, n.createdAt[2], n.createdAt[3], n.createdAt[4])
                    : new Date(n.createdAt);

                const dateStr = dateObj.toLocaleDateString(undefined, {
                    month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit'
                });

                let actionBtn = '';
                if (n.type === 'INVOICE') {
                    // Persistent View button for invoices
                    const btnColor = n.read ? 'var(--text-muted)' : 'var(--primary-color)';
                    actionBtn = `<button class="btn btn-sm" style="font-size: 0.75rem; padding: 6px 12px; background: ${btnColor}; color: white;" onclick="redirectToInvoice(${n.id})">${n.read ? 'Review Invoice' : 'View Invoice'}</button>`;
                } else if (!n.read) {
                    if (n.type === 'MONEY_REQUEST') {
                        actionBtn = `<button class="btn btn-sm" style="font-size: 0.75rem; padding: 6px 12px; background: var(--accent-color); color: white;" onclick="redirectToRequests(${n.id})">Settle Now</button>`;
                    } else {
                        actionBtn = `<button class="btn btn-sm btn-secondary" style="font-size: 0.75rem; padding: 6px 12px;" onclick="markAsRead(${n.id})">Acknowledge</button>`;
                    }
                }

                d.innerHTML = `
                    <div style="display: flex; justify-content: space-between; align-items: flex-start; gap: 24px;">
                        <div style="flex: 1;">
                            <div style="font-weight: ${n.read ? '500' : '700'}; color: var(--primary-color); font-size: 0.9375rem; margin-bottom: 8px;">${n.message}</div>
                            <div style="color: var(--text-muted); font-size: 0.75rem; display: flex; align-items: center; gap: 8px;">
                                <span>${dateStr}</span>
                                ${!n.read ? '<span style="color: var(--accent-color); font-weight: 700; text-transform: uppercase; font-size: 0.625rem; letter-spacing: 0.05em;">New Dispatch</span>' : ''}
                            </div>
                        </div>
                        <div style="flex-shrink: 0;">${actionBtn}</div>
                    </div>
                `;
                list.appendChild(d);
            });
        }
    } catch (error) {
        document.getElementById('notificationsList').innerHTML = '<div class="card" style="padding: 32px; color: var(--danger);">Registry synchronization failure.</div>';
    }
}

async function markAsRead(id) {
    try {
        const response = await fetchWithAuth(`/api/notifications/${id}/read`, { method: 'PUT' });
        if (response.ok) {
            fetchNotifications();
        }
    } catch (error) {
        console.error('Acknowledgement failed.', error);
    }
}

async function redirectToRequests(id) {
    try {
        const res = await fetchWithAuth(`/api/notifications/${id}`);
        if (res.ok) {
            const n = await res.json();
            if (n.targetId) sessionStorage.setItem('pendingRequestId', n.targetId);
        }
        // NOTE: We no longer mark MONEY_REQUEST as read here. 
        // It will be marked as read by the backend once the request is accepted or declined.
        window.location.href = '/transaction/requests';
    } catch (error) {
        window.location.href = '/transaction/requests';
    }
}

async function redirectToInvoice(id) {
    try {
        const res = await fetchWithAuth(`/api/notifications/${id}`);
        if (res.ok) {
            const n = await res.json();
            if (n.targetId) sessionStorage.setItem('pendingInvoiceId', n.targetId);
        }
        // Mark as read only if it was unread, so we can still view it later
        await fetchWithAuth(`/api/notifications/${id}/read`, { method: 'PUT' });
        window.location.href = '/transaction/requests';
    } catch (error) {
        window.location.href = '/transaction/requests';
    }
}