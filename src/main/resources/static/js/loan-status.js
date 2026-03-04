document.addEventListener('DOMContentLoaded', loadLoans);

        async function loadLoans() {
            const userStr = sessionStorage.getItem('user');
            if (!userStr || JSON.parse(userStr).role !== 'BUSINESS') {
                window.location.href = '/dashboard';
                return;
            }
            const user = JSON.parse(userStr);
            const container = document.getElementById('loanContainer');
            const loader = document.getElementById('loader');
            const tbody = document.getElementById('loanBody');

            try {
                const res = await fetchWithAuth(`/api/loans/business/${user.id}`);
                if (res.ok) {
                    const loans = await res.json();
                    tbody.innerHTML = '';

                    if (loans.length === 0) {
                        tbody.innerHTML = '<tr><td colspan="5" style="text-align:center; padding: 48px; color: var(--text-muted); font-size: 0.875rem;">No active credit facilities found in registry.</td></tr>';
                    } else {
                        loans.reverse().forEach(loan => {
                            const statusCls = loan.status === 'APPROVED' ? 'status-success' :
                                (loan.status === 'PENDING' ? 'status-warning' :
                                    (loan.status === 'REJECTED' ? 'status-danger' : 'status-info'));

                            const applyDate = loan.appliedAt ? new Date(loan.appliedAt).toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' }) : 'N/A';

                            tbody.innerHTML += `
                                <tr style="border-bottom: 1px solid var(--border-color);">
                                    <td style="padding: 20px 24px;"><span style="font-weight: 600; font-family: monospace; font-size: 0.875rem; color: var(--primary-color);">#FACILITY-${loan.id}</span></td>
                                    <td style="padding: 20px 24px; color: var(--text-secondary); font-size: 0.875rem;">${applyDate}</td>
                                    <td style="padding: 20px 24px;">
                                        <div style="font-weight: 600; color: var(--primary-color); font-size: 0.9375rem;">INR ${loan.amount.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</div>
                                        <div style="font-size: 0.75rem; color: var(--text-muted);">${loan.tenureMonths} Months Repayment Amortization</div>
                                    </td>
                                    <td style="padding: 20px 24px;">
                                        <div style="font-weight: 700; color: var(--primary-color); font-size: 0.9375rem;">INR ${loan.emi ? loan.emi.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }) : '0.00'}<span style="font-size: 0.75rem; font-weight: 500; color: var(--text-muted);">/mo</span></div>
                                        <div style="font-size: 0.75rem; color: var(--text-muted);">Fixed ${loan.interestRate}% P.A. APR</div>
                                    </td>
                                    <td style="padding: 20px 24px;"><span class="status-badge ${statusCls}" style="font-size: 0.6875rem; padding: 4px 10px;">${loan.status}</span></td>
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

