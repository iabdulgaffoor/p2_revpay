document.addEventListener('DOMContentLoaded', async () => {
    await loadIncoming();
    await loadOutgoing();
    checkPendingTriggers();
});

async function checkPendingTriggers() {
    const invId = sessionStorage.getItem('pendingInvoiceId');
    const reqId = sessionStorage.getItem('pendingRequestId');

    if (invId) {
        sessionStorage.removeItem('pendingInvoiceId');
        viewInvoice(invId);
    } else if (reqId) {
        sessionStorage.removeItem('pendingRequestId');
        // Find the request in the loaded data or fetch it
        try {
            const res = await fetchWithAuth(`/api/money-requests/${reqId}`);
            if (res.ok) {
                const req = await res.json();
                openPinModal(req.id, req.amount, req.requesterName || 'Originator');
            }
        } catch (e) {
            console.error("Failed to trigger pending request.", e);
        }
    }
}

async function sendRequest() {
    const btn = document.querySelector('#reqForm button[type="submit"]');
    const msg = document.getElementById('reqMsg');

    setButtonLoading(btn, true, 'Processing protocol...');
    msg.innerText = "";

    const user = JSON.parse(sessionStorage.getItem('user'));
    const identifier = document.getElementById('reqIdentifier').value;
    const amount = document.getElementById('reqAmount').value;
    const purpose = document.getElementById('reqPurpose').value;

    try {
        let targetRes = await fetchWithAuth(`/api/users/identifier?identifier=${encodeURIComponent(identifier)}`);
        if (!targetRes.ok) throw new Error("Target identity not discovered in registry.");
        const targetUser = await targetRes.json();

        const payload = {
            requesterId: user.id,
            requesteeId: targetUser.id,
            amount: parseFloat(amount),
            purpose: purpose
        };

        const reqRes = await fetchWithAuth(`/api/money-requests/send`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (reqRes.ok) {
            msg.innerText = "Solicitation dispatched successfully.";
            msg.style.color = "var(--accent-color)";
            document.getElementById('reqForm').reset();
            loadOutgoing();
        } else {
            const data = await reqRes.json();
            throw new Error(data.message || "Solicitation protocol failure.");
        }
    } catch (err) {
        msg.innerText = err.message;
        msg.style.color = "var(--danger)";
    } finally {
        setButtonLoading(btn, false);
    }
}

async function loadIncoming() {
    const user = JSON.parse(sessionStorage.getItem('user'));
    const listObj = document.getElementById('incomingList');
    const loader = document.getElementById('incomingLoader');
    const countBadge = document.getElementById('incomingCount');

    try {
        const res = await fetchWithAuth(`/api/money-requests/user/${user.id}/incoming`);
        if (res.ok) {
            const data = await res.json();
            listObj.innerHTML = '';
            const pendingCount = data.filter(r => r.status === 'PENDING').length;

            if (pendingCount > 0) {
                countBadge.innerText = pendingCount;
                countBadge.style.display = 'inline-block';
            } else {
                countBadge.style.display = 'none';
            }

            if (data.length === 0) {
                listObj.innerHTML = '<div style="padding: 40px; text-align: center; border: 1px dashed var(--border-color); border-radius: 8px; color: var(--text-muted); font-size: 0.8125rem;">No active solicitations in incoming registry.</div>';
            } else {
                data.reverse().forEach(req => {
                    const dateStr = new Date(req.createdAt).toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });
                    let actions = '';
                    if (req.status === 'PENDING') {
                        actions = `
                            <div style="margin-top: 24px; display: flex; gap: 12px; padding-top: 20px; border-top: 1px solid var(--border-color);">
                                <button class="btn" style="padding: 10px 20px; font-size: 0.8125rem;" onclick="openPinModal(${req.id}, ${req.amount}, '${req.requesterName || 'Originator'}')">Authorize Settlement</button>
                                <button class="btn btn-secondary" style="background: transparent; color: var(--danger); border: 1px solid #fecaca; padding: 10px 20px; font-size: 0.8125rem;" onclick="declineReq(${req.id})">Decline</button>
                            </div>
                        `;
                    }
                    let statusClass = req.status === 'ACCEPTED' ? 'status-success' : (req.status === 'DECLINED' || req.status === 'CANCELLED' ? 'status-danger' : 'status-warning');

                    let invoiceBtn = '';
                    if (req.invoiceId) {
                        invoiceBtn = `<button class="btn btn-sm btn-secondary" style="background: transparent; border: 1px solid var(--border-color); color: var(--primary-color); padding: 6px 12px; font-size: 0.75rem; margin-top: 8px;" onclick="viewInvoice(${req.invoiceId})">View Detailed Statement</button>`;
                    }

                    listObj.innerHTML += `
                        <div class="card" style="padding: 32px; border: 1px solid var(--border-color); box-shadow: none;">
                            <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 24px;">
                                <div>
                                    <div style="font-weight: 700; font-size: 0.875rem; color: var(--primary-color);">Originator identity: ${req.requesterName || 'Verified Entity'}</div>
                                    <div style="color: var(--text-muted); font-size: 0.75rem; margin-top: 4px;">Filing date: ${dateStr}</div>
                                </div>
                                <div style="text-align: right;">
                                    <span class="status-badge ${statusClass}" style="font-size: 0.625rem; padding: 4px 10px; display: block;">${req.status}</span>
                                    ${invoiceBtn}
                                </div>
                            </div>
                            <div style="font-size: 1.5rem; font-weight: 700; color: var(--primary-color); margin-bottom: 12px;">INR ${req.amount.toLocaleString(undefined, { minimumFractionDigits: 2 })}</div>
                            <div style="color: var(--text-secondary); font-size: 0.875rem; line-height: 1.6; background: var(--bg-color); padding: 16px; border-radius: 6px;">${req.purpose}</div>
                            ${actions}
                        </div>
                    `;
                });
            }
            loader.style.display = 'none';
            listObj.style.display = 'flex';
        }
    } catch (e) { loader.innerText = "Registry synchronization failure."; }
}

async function loadOutgoing() {
    const user = JSON.parse(sessionStorage.getItem('user'));
    const listObj = document.getElementById('outgoingList');
    const loader = document.getElementById('outgoingLoader');

    try {
        const res = await fetchWithAuth(`/api/money-requests/user/${user.id}/outgoing`);
        if (res.ok) {
            const data = await res.json();
            listObj.innerHTML = '';
            if (data.length === 0) {
                listObj.innerHTML = '<div style="padding: 40px; text-align: center; border: 1px dashed var(--border-color); border-radius: 8px; color: var(--text-muted); font-size: 0.8125rem;">No solicitation records in outgoing registry.</div>';
            } else {
                data.reverse().forEach(req => {
                    const dateStr = new Date(req.createdAt).toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });
                    let actions = '';
                    if (req.status === 'PENDING') {
                        actions = `
                            <div style="margin-top: 24px;">
                                <button class="btn btn-secondary" style="background: transparent; color: var(--text-secondary); border: 1px solid var(--border-color); padding: 8px 16px; font-size: 0.8125rem;" onclick="cancelReq(${req.id})">Revoke Solicitation</button>
                            </div>
                        `;
                    }
                    let statusClass = req.status === 'ACCEPTED' ? 'status-success' : (req.status === 'DECLINED' || req.status === 'CANCELLED' ? 'status-danger' : 'status-warning');
                    listObj.innerHTML += `
                        <div class="card" style="padding: 32px; border: 1px solid var(--border-color); box-shadow: none; opacity: ${req.status === 'PENDING' ? '1' : '0.7'};">
                            <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 24px;">
                                <div>
                                    <div style="font-weight: 700; font-size: 0.875rem; color: var(--primary-color);">Target identity: ${req.requesteeName || 'Verified Entity'}</div>
                                    <div style="color: var(--text-muted); font-size: 0.75rem; margin-top: 4px;">Filing date: ${dateStr}</div>
                                </div>
                                <span class="status-badge ${statusClass}" style="font-size: 0.625rem; padding: 4px 10px;">${req.status}</span>
                            </div>
                            <div style="font-size: 1.5rem; font-weight: 700; color: var(--primary-color); margin-bottom: 12px;">INR ${req.amount.toLocaleString(undefined, { minimumFractionDigits: 2 })}</div>
                            <div style="color: var(--text-secondary); font-size: 0.875rem; line-height: 1.6;">${req.purpose}</div>
                            ${actions}
                        </div>
                    `;
                });
            }
            loader.style.display = 'none';
            listObj.style.display = 'flex';
        }
    } catch (e) { loader.innerText = "Registry synchronization failure."; }
}

function openPinModal(id, amount, requester) {
    document.getElementById('acceptReqId').value = id;
    document.getElementById('pinModalText').innerText = `Authorized settlement of INR ${amount.toFixed(2)} to ${requester} identity. Assets will be immediately disbursed from liquid balance.`;
    document.getElementById('pinModal').style.display = 'block';
    document.getElementById('pinError').style.display = 'none';
}

function closePinModal() {
    document.getElementById('pinModal').style.display = 'none';
    document.getElementById('acceptPin').value = '';
}

async function confirmAccept() {
    const reqId = document.getElementById('acceptReqId').value;
    const pin = document.getElementById('acceptPin').value;
    const user = JSON.parse(sessionStorage.getItem('user'));
    const errDiv = document.getElementById('pinError');
    const btn = document.querySelector('#pinModal button.btn');

    if (!pin) { errDiv.innerText = "Authorization PIN is mandatory for registry verification."; errDiv.style.display = 'block'; return; }

    setButtonLoading(btn, true, 'Verifying...');
    try {
        const res = await fetchWithAuth(`/api/money-requests/${reqId}/accept/user/${user.id}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ pin: pin })
        });

        if (res.ok) {
            sessionStorage.removeItem('pendingRequestId');
            closePinModal();
            loadIncoming();
        } else {
            const data = await res.json();
            throw new Error(data.message || "Authorization failed.");
        }
    } catch (e) {
        errDiv.innerText = e.message;
        errDiv.style.display = 'block';
    } finally {
        setButtonLoading(btn, false);
    }
}

async function declineReq(id) {
    if (!confirm("Decline this settlement solicitation? This action is recorded in registry ledger.")) return;
    const user = JSON.parse(sessionStorage.getItem('user'));
    await fetchWithAuth(`/api/money-requests/${id}/decline/user/${user.id}`, { method: 'POST' });
    sessionStorage.removeItem('pendingRequestId');
    loadIncoming();
}

async function cancelReq(id) {
    if (!confirm("Revoke this solicitation? Registry archive will maintain the record.")) return;
    const user = JSON.parse(sessionStorage.getItem('user'));
    await fetchWithAuth(`/api/money-requests/${id}/cancel/user/${user.id}`, { method: 'POST' });
    loadOutgoing();
}

async function viewInvoice(id) {
    try {
        const res = await fetchWithAuth(`/api/invoices/${id}`);
        if (!res.ok) throw new Error("Registry record unavailable.");
        const inv = await res.json();

        document.getElementById('invIdText').innerText = `#STMT-${inv.id}`;
        document.getElementById('invBusinessName').innerText = inv.businessUserName || "Verified Business Entity";
        document.getElementById('invBusinessEmail').innerText = `Registry ID: business_user_${inv.businessUserId}`;
        document.getElementById('invDueDate').innerText = inv.dueDate;
        document.getElementById('invTerms').innerText = inv.paymentTerms || "Standard Net-30 Settlement";

        const itemsBody = document.getElementById('invItemsBody');
        itemsBody.innerHTML = '';
        let subtotal = 0;
        let totalTax = 0;

        inv.items.forEach(item => {
            const lineTotal = (item.quantity * item.unitPrice) + (item.tax || 0);
            subtotal += (item.quantity * item.unitPrice);
            totalTax += (item.tax || 0);

            itemsBody.innerHTML += `
                <tr style="border-bottom: 1px solid var(--border-color);">
                    <td style="padding: 16px 20px; font-size: 0.875rem; color: var(--primary-color);">${item.description}</td>
                    <td style="padding: 16px 20px; text-align: right; font-size: 0.875rem; color: var(--primary-color);">${item.quantity}</td>
                    <td style="padding: 16px 20px; text-align: right; font-size: 0.875rem; font-weight: 600; color: var(--primary-color);">INR ${lineTotal.toLocaleString(undefined, { minimumFractionDigits: 2 })}</td>
                </tr>
            `;
        });

        document.getElementById('invSubtotal').innerText = `INR ${subtotal.toLocaleString(undefined, { minimumFractionDigits: 2 })}`;
        document.getElementById('invTax').innerText = `INR ${totalTax.toLocaleString(undefined, { minimumFractionDigits: 2 })}`;
        document.getElementById('invTotal').innerText = `INR ${inv.totalAmount.toLocaleString(undefined, { minimumFractionDigits: 2 })}`;

        document.getElementById('invoiceModal').style.display = 'block';
    } catch (err) {
        alert(err.message);
    }
}

function closeInvoiceModal() {
    document.getElementById('invoiceModal').style.display = 'none';
}
