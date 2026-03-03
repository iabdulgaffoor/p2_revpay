document.addEventListener('DOMContentLoaded', async () => {
            const userStr = sessionStorage.getItem('user');
            if (!userStr || JSON.parse(userStr).role !== 'BUSINESS') {
                window.location.href = '/dashboard';
                return;
            }

            const user = JSON.parse(userStr);
            const loader = document.getElementById('loader');
            const content = document.getElementById('dashboardContent');
            const errorMsg = document.getElementById('errorMsg');

            try {
                const res = await fetchWithAuth(`/api/analytics/business/${user.id}/summary`);
                if (res.ok) {
                    const data = await res.json();

                    document.getElementById('valTotalReceived').innerText = `USD ${(data.totalReceived || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}`;
                    document.getElementById('valTotalSent').innerText = `USD ${(data.totalSent || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}`;
                    document.getElementById('valOutstanding').innerText = `USD ${(data.outstandingInvoiceAmount || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}`;
                    document.getElementById('valPendingCount').innerText = data.pendingInvoicesCount || 0;

                    loader.style.display = 'none';
                    content.style.display = 'block';

                    initCharts(data);
                } else {
                    throw new Error("Telemetry synchronization failure.");
                }
            } catch (err) {
                loader.style.display = 'none';
                errorMsg.innerText = err.message;
                errorMsg.style.display = 'block';
            }
        });

        function initCharts(data) {
            Chart.defaults.font.family = "'Inter', sans-serif";
            Chart.defaults.color = '#64748b';

            // Cash Flow Protocol
            const ctxFlow = document.getElementById('cashflowChart').getContext('2d');
            new Chart(ctxFlow, {
                type: 'bar',
                data: {
                    labels: ['Aggregate Inflow', 'Operational Outflow'],
                    datasets: [{
                        data: [data.totalReceived || 0, data.totalSent || 0],
                        backgroundColor: ['rgba(15, 23, 42, 0.8)', 'rgba(239, 68, 68, 0.8)'],
                        borderRadius: 6,
                        barThickness: 60
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: { legend: { display: false } },
                    scales: {
                        y: {
                            beginAtZero: true,
                            grid: { borderDash: [5, 5], color: '#e2e8f0' },
                            ticks: { callback: value => 'USD ' + value.toLocaleString() }
                        },
                        x: { grid: { display: false } }
                    }
                }
            });

            // Statement Amortization
            const ctxInv = document.getElementById('invoiceChart').getContext('2d');
            const pending = data.outstandingInvoiceAmount || 0;
            const paid = (data.totalReceived || 0) * 0.4;

            new Chart(ctxInv, {
                type: 'doughnut',
                data: {
                    labels: ['Authorized Settlement', 'Outstanding Obligations'],
                    datasets: [{
                        data: [paid, pending],
                        backgroundColor: ['#111111', '#999999'],
                        borderWidth: 0,
                        hoverOffset: 12
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    cutout: '75%',
                    plugins: {
                        legend: { position: 'bottom', labels: { padding: 20, usePointStyle: true } }
                    }
                }
            });
        }