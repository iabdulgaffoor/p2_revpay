document.addEventListener('DOMContentLoaded', loadInvoices);

        async function loadInvoices() {
            const userStr = sessionStorage.getItem('user');
            if (!userStr || JSON.parse(userStr).role !== 'BUSINESS') {
                window.location.href = '/dashboard';
                return;
            }
            const user = JSON.parse(userStr);
            const container = document.getElementById('invoiceContainer');
            const loader = document.getElementById('loader');
            const tbody = document.getElementById('invoiceBody');

            try {
                const res = await fetchWithAuth(`/api/invoices/business/${user.id}`);
                if (res.ok) {
                    const invoices = await res.json();
                    tbody.innerHTML = '';

                    if (invoices.length === 0) {
                        tbody.innerHTML = '<tr><td colspan="6" style="text-align:center; padding: 48px; color: var(--text-muted); font-size: 0.875rem;">No registered statements found in ledger.</td></tr>';
                    } else {
                        invoices.reverse().forEach(inv => {
                            const statusCls = inv.status === 'PAID' ? 'status-success' :
                                (inv.status === 'SENT' ? 'status-info' :
                                    (inv.status === 'OVERDUE' ? 'status-danger' : 'status-neutral'));

                            let actionHtml = '';
                            if (inv.status === 'DRAFT') {
                                actionHtml = `<button onclick="sendInvoice(${inv.id})" class="btn btn-sm">Authorize Dispatch</button>`;
                            } else if (inv.status === 'SENT' || inv.status === 'OVERDUE') {
                                actionHtml = `<button onclick="markAsPaid(${inv.id})" class="btn btn-sm btn-secondary" style="background: var(--bg-color); color: var(--text-secondary); border: 1px solid var(--border-color);">Acknowledge Settlement</button>`;
                            }

                            tbody.innerHTML += `
                                <tr style="border-bottom: 1px solid var(--border-color);">
                                    <td style="padding: 20px 24px;"><span style="font-weight: 600; font-family: monospace; font-size: 0.875rem; color: var(--primary-color);">#STMT-${inv.id}</span></td>
                                    <td style="padding: 20px 24px;">
                                        <div style="font-weight: 500; color: var(--primary-color); font-size: 0.9375rem;">${inv.customerName}</div>
                                        <div style="font-size: 0.75rem; color: var(--text-muted);">${inv.customerEmail}</div>
                                    </td>
                                    <td style="padding: 20px 24px; color: var(--text-secondary); font-size: 0.875rem;">${inv.dueDate}</td>
                                    <td style="padding: 20px 24px; font-weight: 700; color: var(--primary-color); font-size: 0.9375rem;">INR ${inv.totalAmount.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</td>
                                    <td style="padding: 20px 24px;"><span class="status-badge ${statusCls}" style="font-size: 0.6875rem; padding: 4px 10px;">${inv.status}</span></td>
                                    <td style="padding: 20px 24px; text-align: right;">${actionHtml}</td>
                                </tr>
                            `;
                        });
                    }
                    loader.style.display = 'none';
                    container.style.display = 'block';
                } else {
                    throw new Error("Registry synchronization failure.");
                }
            } catch (err) {
                loader.style.display = 'none';
                const errDiv = document.getElementById('errorMsg');
                errDiv.innerText = err.message;
                errDiv.style.display = 'block';
            }
        }

        async function sendInvoice(id) {
            if (!confirm("Confirm authorization for statement dispatch to client?")) return;
            const btn = event.target;
            setButtonLoading(btn, true, 'Processing...');
            try {
                const res = await fetchWithAuth(`/api/invoices/${id}/send-notification`, { method: 'POST' });
                if (res.ok) {
                    loadInvoices();
                } else {
                    alert('Dispatch authorization failed.');
                }
            } catch (e) { console.error(e); }
            finally { setButtonLoading(btn, false); }
        }

        async function markAsPaid(id) {
            if (!confirm("Acknowledge full settlement of this statement?")) return;
            const btn = event.target;
            setButtonLoading(btn, true, 'Updating Ledger...');
            try {
                const res = await fetchWithAuth(`/api/invoices/${id}/pay`, { method: 'PUT' });
                if (res.ok) {
                    loadInvoices();
                } else {
                    alert('Ledger update failed.');
                }
            } catch (e) { console.error(e); }
            finally { setButtonLoading(btn, false); }
        }

