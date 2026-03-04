let currentPage = 0;
        let currentSortBy = 'id';
        let currentSortDir = 'asc';
        let currentSearchTerm = '';
        let searchTimeout = null;
        const pageSize = 10;
        let allUsersData = [];

        function formatCurrency(value) {
            if (value === null || value === undefined) return 'INR 0.00';
            const num = parseFloat(value);
            if (num >= 1e9) return 'INR ' + (num / 1e9).toFixed(2) + 'B';
            if (num >= 1e6) return 'INR ' + (num / 1e6).toFixed(2) + 'M';
            if (num >= 1000) return 'INR ' + (num / 1e3).toFixed(2) + 'K';
            return 'INR ' + num.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
        }

        function handleUserSearch() {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(() => {
                const searchVal = document.getElementById('userSearchInput').value.trim();
                if (currentSearchTerm !== searchVal) {
                    currentSearchTerm = searchVal;
                    currentPage = 0; // reset to first page on new search
                    fetchUsers();
                }
            }, 400); // 400ms debounce
        }

        async function fetchUsers() {
            try {
                const encodedSearch = encodeURIComponent(currentSearchTerm);
                const usersRes = await fetchWithAuth(`/api/admin/users?page=${currentPage}&size=${pageSize}&sortBy=${currentSortBy}&sortDir=${currentSortDir}&search=${encodedSearch}`);
                if (usersRes.ok) {
                    const pageData = await usersRes.json();
                    allUsersData = pageData.content; // Cache for modal use
                    const users = pageData.content;
                    const tbody = document.getElementById('userTableBody');

                    if (users.length === 0) {
                        tbody.innerHTML = '<tr><td colspan="6" style="text-align:center; padding: 20px;">No users found.</td></tr>';
                    } else {
                        tbody.innerHTML = '';
                        users.forEach(u => {
                            const statusCls = u.isActive !== false ? 'status-success' : 'status-danger';
                            const roleCls = u.role === 'ADMIN' ? 'status-danger' : (u.role === 'BUSINESS' ? 'status-info' : 'status-success');
                            const statusLabel = u.isActive !== false ? 'AUTHORIZED' : 'DEACTIVATED';

                            tbody.innerHTML += `
                                <tr style="border-bottom: 1px solid var(--border-color);">
                                    <td style="padding: 16px;"><span style="font-family: 'JetBrains Mono', monospace; font-weight: 700; color: var(--text-secondary); font-size: 0.75rem;">#${u.id}</span></td>
                                    <td style="padding: 16px;"><span style="font-weight: 600; color: var(--primary-color); font-size: 0.8125rem;">${u.fullName || '—'}</span></td>
                                    <td style="padding: 16px; color: var(--text-muted); font-size: 0.8125rem;">${u.email}</td>
                                    <td style="padding: 16px;"><span class="status-badge ${roleCls}" style="font-size: 0.625rem; letter-spacing: 0.05em;">${u.role}</span></td>
                                    <td style="padding: 16px;"><span class="status-badge ${statusCls}" style="font-size: 0.625rem; letter-spacing: 0.05em;">${statusLabel}</span></td>
                                    <td style="padding: 16px; text-align: right;">
                                        <div style="display: flex; gap: 8px; justify-content: flex-end;">
                                            <button onclick="openUserModal(${u.id})" class="btn btn-sm" style="padding: 6px 12px; font-size: 0.6875rem;">Registry Edit</button>
                                            <button onclick="openBalanceModal(${u.id}, '${u.fullName || u.email}')" class="btn btn-sm btn-secondary" style="padding: 6px 12px; font-size: 0.6875rem; background: #f8fafc; border: 1px solid #e2e8f0; color: var(--text-secondary);">Fiscal Adjust</button>
                                        </div>
                                    </td>
                                </tr>
                            `;
                        });
                    }
                    renderPagination(pageData.totalPages);
                }
            } catch (err) {
                console.error("Failed to fetch users", err);
            }
        }

        function renderPagination(totalPages) {
            const pgi = document.getElementById('paginationControls');
            if (!pgi) return;
            if (totalPages <= 1) {
                pgi.innerHTML = '';
                return;
            }
            let html = `<div style="display:flex; justify-content:center; align-items: center; gap:8px;">`;
            html += `<button class="btn btn-sm" style="padding: 8px 16px; font-size: 0.75rem; background: ${currentPage === 0 ? '#f1f5f9' : 'white'}; color: ${currentPage === 0 ? '#94a3b8' : 'var(--text-secondary)'}; border: 1px solid #e2e8f0;" onclick="changePage(${currentPage - 1})" ${currentPage === 0 ? 'disabled' : ''}>Previous</button>`;

            for (let i = 0; i < totalPages; i++) {
                if (i === currentPage) {
                    html += `<span style="padding: 8px 14px; background: var(--primary-color); color: white; border-radius: 4px; font-size: 0.75rem; font-weight: 700;">${i + 1}</span>`;
                } else {
                    html += `<button class="btn btn-sm" style="padding: 8px 14px; background: white; color: var(--text-secondary); border: 1px solid #e2e8f0; font-size: 0.75rem;" onclick="changePage(${i})">${i + 1}</button>`;
                }
            }

            html += `<button class="btn btn-sm" style="padding: 8px 16px; font-size: 0.75rem; background: ${currentPage === totalPages - 1 ? '#f1f5f9' : 'white'}; color: ${currentPage === totalPages - 1 ? '#94a3b8' : 'var(--text-secondary)'}; border: 1px solid #e2e8f0;" onclick="changePage(${currentPage + 1})" ${currentPage === totalPages - 1 ? 'disabled' : ''}>Next</button>`;
            html += `</div>`;
            pgi.innerHTML = html;
        }

        function changePage(page) {
            currentPage = page;
            fetchUsers();
        }

        function sortByColumn(col) {
            if (currentSortBy === col) {
                currentSortDir = currentSortDir === 'asc' ? 'desc' : 'asc';
            } else {
                currentSortBy = col;
                currentSortDir = 'asc';
            }
            currentPage = 0;
            fetchUsers();
            updateSortIcons();
        }

        function updateSortIcons() {
            const cols = ['id', 'fullName', 'email', 'role'];
            cols.forEach(c => {
                const icon = document.getElementById('sort-icon-' + c);
                if (icon) {
                    if (currentSortBy === c) {
                        icon.innerHTML = currentSortDir === 'asc' ? ' &uarr;' : ' &darr;';
                    } else {
                        icon.innerHTML = '';
                    }
                }
            });
        }
        document.addEventListener('DOMContentLoaded', async () => {
            const userStr = sessionStorage.getItem('user');

            if (!userStr || JSON.parse(userStr).role !== 'ADMIN') {
                document.body.innerHTML = "<h2 style='text-align:center; padding: 50px; color:red;'>Access Denied. Admins Only.</h2>";
                return;
            }

            try {
                // Fetch metrics
                const metricsRes = await fetchWithAuth('/api/admin/metrics');
                if (metricsRes.ok) {
                    const metrics = await metricsRes.json();
                    document.getElementById('metricTotalUsers').innerText = metrics.totalUsers || 0;
                    document.getElementById('metricTotalVolume').innerText = formatCurrency(metrics.totalVolume);
                    document.getElementById('metricTotalTx').innerText = metrics.totalTransactions || 0;

                    const statusEl = document.getElementById('metricSystemStatus');
                    statusEl.innerText = metrics.systemStatus || 'Unknown';
                    statusEl.style.color = '#16a34a';
                }

                // Fetch users paginated
                await fetchUsers();

                // Fetch Loans
                const loansRes = await fetchWithAuth('/api/admin/loans');
                if (loansRes.ok) {
                    const loans = await loansRes.json();
                    const tbody = document.getElementById('loanTableBody');
                    if (loans.length === 0) {
                        tbody.innerHTML = '<tr><td colspan="6" style="text-align:center; padding: 20px;">No loans found.</td></tr>';
                    } else {
                        tbody.innerHTML = '';
                        loans.forEach(loan => {
                            const statusCls = loan.status === 'APPROVED' ? 'status-success' : (loan.status === 'PENDING' ? 'status-warning' : 'status-danger');
                            const statusLabel = loan.status === 'PENDING' ? 'PENDING ADJUDICATION' : (loan.status === 'APPROVED' ? 'AUTHORIZED' : 'DECLINED');

                            let actions = '';
                            if (loan.status === 'PENDING') {
                                actions = `
                                    <div style="display: flex; gap: 8px; justify-content: flex-end;">
                                        <button onclick="updateLoanStatus(${loan.id}, 'APPROVED')" class="btn btn-sm" style="padding: 8px 16px; font-size: 0.6875rem;">Authorize</button>
                                        <button onclick="updateLoanStatus(${loan.id}, 'REJECTED')" class="btn btn-sm btn-secondary" style="padding: 8px 16px; font-size: 0.6875rem; background: #fff5f5; border: 1px solid #fed7d7; color: #c53030;">Decline</button>
                                    </div>
                                `;
                            }

                            tbody.innerHTML += `
                                <tr style="border-bottom: 1px solid var(--border-color);">
                                    <td style="padding: 16px;"><span style="font-family: 'JetBrains Mono', monospace; font-weight: 700; color: var(--text-secondary); font-size: 0.75rem;">#REG-${loan.id}</span></td>
                                    <td style="padding: 16px; font-weight: 600; color: var(--primary-color); font-size: 0.8125rem;">Entity #${loan.businessUserId}</td>
                                    <td style="padding: 16px; font-weight: 700; color: var(--primary-color); font-size: 0.875rem;">${formatCurrency(loan.amount)}</td>
                                    <td style="padding: 16px; color: var(--text-muted); font-size: 0.8125rem;">${loan.purpose}</td>
                                    <td style="padding: 16px;"><span class="status-badge ${statusCls}" style="font-size: 0.625rem; letter-spacing: 0.05em;">${statusLabel}</span></td>
                                    <td style="padding: 16px; text-align: right;">${actions}</td>
                                </tr>
                            `;
                        });
                    }
                }
            } catch (err) {
                console.error("Failed to load admin dashboard", err);
            }
        });



        // Modals & Actions
        function openBalanceModal(userId, name) {
            document.getElementById('adjustTargetId').value = userId;
            document.getElementById('adjustTargetName').innerText = name;
            document.getElementById('balanceModal').style.display = 'block';
            document.getElementById('adjustMsg').innerText = '';
        }

        function closeModal(modalId) {
            document.getElementById(modalId).style.display = 'none';
        }

        function toggleBusinessFields() {
            const role = document.getElementById('userRole').value;
            const biz = document.getElementById('businessFields');
            biz.style.display = role === 'BUSINESS' ? 'block' : 'none';
        }

        function openUserModal(userId = null) {
            const msgDiv = document.getElementById('userFormMsg');
            msgDiv.innerText = '';

            if (userId) {
                // Editing existing user
                document.getElementById('userModalTitle').innerText = 'Edit User Profile';
                const user = allUsersData.find(u => u.id === userId);
                if (user) {
                    document.getElementById('editUserId').value = user.id;
                    document.getElementById('userFullName').value = user.fullName || '';
                    document.getElementById('userEmail').value = user.email || '';
                    document.getElementById('userPhone').value = user.phoneNumber || '';
                    document.getElementById('userRole').value = user.role || 'PERSONAL';
                    document.getElementById('userSecQ').value = user.securityQuestion || '';
                    document.getElementById('userSecA').value = user.securityAnswer || ''; // We might not get this from the API, but fill if present.
                    document.getElementById('userSecA').removeAttribute('required'); // Security Answer shouldn't be strictly required for updates
                    document.getElementById('userStatus').value = user.isActive !== false ? "true" : "false";

                    document.getElementById('userPasswordLabel').innerText = 'Password (Optional)';
                    document.getElementById('userPassword').placeholder = 'Leave blank to keep unchanged';
                    document.getElementById('userPassword').removeAttribute('required');

                    if (user.role === 'BUSINESS') {
                        document.getElementById('bizName').value = user.businessName || '';
                        document.getElementById('bizType').value = user.businessType || '';
                        document.getElementById('bizTax').value = user.taxId || '';
                        document.getElementById('bizVerified').checked = user.isBusinessVerified;
                    }
                }
            } else {
                // Creating new user
                document.getElementById('userModalTitle').innerText = 'Create New User';
                document.getElementById('userForm').reset();
                document.getElementById('editUserId').value = '';
                document.getElementById('userStatus').value = 'true';
                document.getElementById('userPasswordLabel').innerText = 'Password';
                document.getElementById('userPassword').placeholder = 'Required for new users';
                document.getElementById('userPassword').setAttribute('required', 'true');
                document.getElementById('userSecA').setAttribute('required', 'true');
            }

            toggleBusinessFields();
            document.getElementById('userModal').style.display = 'block';
        }

        async function handleUserSubmit(e) {
            e.preventDefault();
            const msgDiv = document.getElementById('userFormMsg');
            const btn = document.getElementById('saveUserBtn');

            const userId = document.getElementById('editUserId').value;
            const password = document.getElementById('userPassword').value;

            const payload = {
                user: {
                    fullName: document.getElementById('userFullName').value,
                    email: document.getElementById('userEmail').value,
                    phoneNumber: document.getElementById('userPhone').value,
                    role: document.getElementById('userRole').value,
                    securityQuestion: document.getElementById('userSecQ').value,
                    securityAnswer: document.getElementById('userSecA').value
                },
                role: document.getElementById('userRole').value,
                isActive: document.getElementById('userStatus').value === "true"
            };

            if (payload.role === 'BUSINESS') {
                payload.user.businessName = document.getElementById('bizName').value;
                payload.user.businessType = document.getElementById('bizType').value;
                payload.user.taxId = document.getElementById('bizTax').value;
                payload.user.isBusinessVerified = document.getElementById('bizVerified').checked;
            }

            // Only send password if provided (for edits) or required (for creations)
            if (!userId || password.trim().length > 0) {
                if (userId) {
                    payload.newPassword = password;
                } else {
                    payload.password = password;
                }
            }

            btn.disabled = true;
            btn.innerText = 'Saving...';

            try {
                const url = userId ? `/api/admin/users/${userId}` : '/api/admin/users';
                const method = userId ? 'PUT' : 'POST';

                const res = await fetchWithAuth(url, {
                    method: method,
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                if (res.ok) {
                    msgDiv.innerText = `User successfully ${userId ? 'updated' : 'created'}!`;
                    msgDiv.style.color = "var(--success)";
                    setTimeout(() => { closeModal('userModal'); location.reload(); }, 1500);
                } else {
                    const data = await res.json();
                    msgDiv.innerText = data.message || `Failed to save user.`;
                    msgDiv.style.color = "red";
                }
            } catch (err) {
                msgDiv.innerText = "An error occurred connecting to the server.";
                msgDiv.style.color = "red";
            } finally {
                btn.disabled = false;
                btn.innerText = 'Save User Details';
            }
        }

        async function adjustBalance(action) {
            const userId = document.getElementById('adjustTargetId').value;
            const amount = parseFloat(document.getElementById('adjustAmount').value);
            const msgDiv = document.getElementById('adjustMsg');

            if (!amount || amount <= 0) {
                msgDiv.innerText = "Please enter a valid amount > 0";
                msgDiv.style.color = "red";
                return;
            }

            try {
                const res = await fetchWithAuth(`/api/admin/users/${userId}/balance/${action}`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ amount: amount })
                });

                if (res.ok) {
                    msgDiv.innerText = `Successfully ${action === 'add' ? 'added to' : 'deducted from'} user wallet.`;
                    msgDiv.style.color = "var(--success)";
                    setTimeout(() => { closeBalanceModal(); location.reload(); }, 1500);
                } else {
                    const data = await res.json();
                    msgDiv.innerText = data.message || `Failed to ${action} balance.`;
                    msgDiv.style.color = "red";
                }
            } catch (e) {
                msgDiv.innerText = "An error occurred.";
                msgDiv.style.color = "red";
            }
        }

        async function updateLoanStatus(loanId, status) {
            if (!confirm(`Are you sure you want to ${status.toLowerCase()} this loan?`)) return;

            try {
                const res = await fetchWithAuth(`/api/admin/loans/${loanId}/status`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ status: status })
                });

                if (res.ok) {
                    alert(`Loan ${status}!`);
                    location.reload(); // Quick refresh to update tables
                } else {
                    const data = await res.json();
                    alert(data.message || "Failed to update loan status.");
                }
            } catch (e) {
                alert("An error occurred.");
            }
        }


