document.addEventListener('DOMContentLoaded', function () {
            loadPreferences();
            fetchNotifications();
        });

        async function loadPreferences() {
            const userId = sessionStorage.getItem('userId');
            if (!userId) return;

            try {
                const response = await fetchWithAuth(`/api/users/${userId}`);
                if (response.ok) {
                    const data = await response.json();
                    document.getElementById('prefTransaction').checked = data.transactionAlerts !== false;
                    document.getElementById('prefSecurity').checked = data.securityAlerts !== false;
                }
            } catch (err) {
                console.error("Registry lookup failed.", err);
            }
        }

        async function savePreferences() {
            const userId = sessionStorage.getItem('userId');
            const msg = document.getElementById('prefMsg');
            const btn = event.target;

            const payload = {
                transactionAlerts: document.getElementById('prefTransaction').checked,
                securityAlerts: document.getElementById('prefSecurity').checked
            };

            setButtonLoading(btn, true, 'Updating protocols...');
            try {
                const response = await fetchWithAuth(`/api/users/${userId}/preferences`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                if (response.ok) {
                    msg.innerText = "Dispatch protocols successfully updated.";
                    msg.style.color = "var(--accent-color)";
                    msg.style.display = "block";
                    setTimeout(() => msg.style.display = 'none', 3000);
                } else {
                    throw new Error("Synchronization failure.");
                }
            } catch (err) {
                msg.innerText = err.message;
                msg.style.color = "var(--danger)";
                msg.style.display = "block";
            } finally {
                setButtonLoading(btn, false);
            }
        }

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

                    data.sort((a, b) => {
                        if (a.read === b.read) {
                            const dateA = Array.isArray(a.createdAt) ? new Date(...a.createdAt) : new Date(a.createdAt);
                            const dateB = Array.isArray(b.createdAt) ? new Date(...b.createdAt) : new Date(b.createdAt);
                            return dateB - dateA;
                        }
                        return a.read ? 1 : -1;
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
                        if (!n.read) {
                            actionBtn = `<button class="btn btn-sm btn-secondary" style="font-size: 0.75rem; padding: 6px 12px;" onclick="markAsRead(${n.id})">Acknowledge</button>`;
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