let itemCount = 0;

        document.addEventListener('DOMContentLoaded', () => {
            const userStr = sessionStorage.getItem('user');
            if (!userStr || JSON.parse(userStr).role !== 'BUSINESS') {
                window.location.href = '/dashboard';
                return;
            }
            addLineItem();
        });

        function addLineItem() {
            itemCount++;
            const tbody = document.getElementById('itemsBody');
            const tr = document.createElement('tr');
            tr.id = `itemRow_${itemCount}`;
            tr.style.borderTop = '1px solid var(--border-color)';
            tr.innerHTML = `
                <td style="padding: 16px 20px;"><input type="text" placeholder="Specify service or resource" style="width: 100%; padding: 10px; border: 1px solid var(--border-color); border-radius: 4px; background: var(--bg-color);" class="item-desc" required></td>
                <td style="padding: 16px 20px;"><input type="number" min="1" class="item-qty" value="1" style="width: 100%; padding: 10px; border: 1px solid var(--border-color); border-radius: 4px; background: var(--bg-color);" onchange="calculateTotal()" required></td>
                <td style="padding: 16px 20px;"><input type="number" min="0" step="0.01" class="item-price" value="0.00" style="width: 100%; padding: 10px; border: 1px solid var(--border-color); border-radius: 4px; background: var(--bg-color);" onchange="calculateTotal()" required></td>
                <td style="padding: 16px 20px;"><input type="number" min="0" step="0.01" class="item-tax" value="0.00" style="width: 100%; padding: 10px; border: 1px solid var(--border-color); border-radius: 4px; background: var(--bg-color);" onchange="calculateTotal()"></td>
                <td class="item-line-total" style="padding: 16px 20px; font-weight: 600; color: var(--primary-color);">0.00</td>
                <td style="padding: 16px 20px; text-align: center;"><button type="button" style="background: none; border: none; color: var(--danger); cursor: pointer; font-size: 0.75rem; font-weight: 600; text-transform: uppercase;" onclick="removeItem(${itemCount})">Remove</button></td>
            `;
            tbody.appendChild(tr);
            calculateTotal();
        }

        function removeItem(id) {
            const row = document.getElementById(`itemRow_${id}`);
            if (row) {
                row.remove();
                calculateTotal();
            }
        }

        function calculateTotal() {
            let grandTotal = 0;
            const rows = document.querySelectorAll('#itemsBody tr');

            rows.forEach(row => {
                const qty = parseFloat(row.querySelector('.item-qty').value) || 0;
                const price = parseFloat(row.querySelector('.item-price').value) || 0;
                const tax = parseFloat(row.querySelector('.item-tax').value) || 0;

                const lineTotal = (qty * price) + tax;
                row.querySelector('.item-line-total').innerText = lineTotal.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 });
                grandTotal += lineTotal;
            });

            document.getElementById('grandTotalText').innerText = `USD ${grandTotal.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
        }

        async function submitInvoice() {
            const msg = document.getElementById('invoiceMsg');
            const items = [];
            const btn = document.querySelector('#createInvoiceForm button[type="submit"]');

            const rows = document.querySelectorAll('#itemsBody tr');
            if (rows.length === 0) {
                msg.innerText = "Error: At least one line item is required for authorization.";
                msg.style.color = 'var(--danger)';
                return;
            }

            rows.forEach(row => {
                items.push({
                    description: row.querySelector('.item-desc').value,
                    quantity: parseInt(row.querySelector('.item-qty').value),
                    unitPrice: parseFloat(row.querySelector('.item-price').value),
                    tax: parseFloat(row.querySelector('.item-tax').value) || 0.00
                });
            });

            const user = JSON.parse(sessionStorage.getItem('user'));

            const payload = {
                customerName: document.getElementById('custName').value,
                customerEmail: document.getElementById('custEmail').value,
                customerAddress: document.getElementById('custAddress').value,
                dueDate: document.getElementById('dueDate').value,
                paymentTerms: document.getElementById('paymentTerms').value,
                items: items
            };

            setButtonLoading(btn, true, 'Generating Statement Documents...');
            try {
                const res = await fetchWithAuth(`/api/invoices/business/${user.id}`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                if (res.ok) {
                    msg.innerText = "Statement successfully authorized. Redirecting to registry...";
                    msg.style.color = 'var(--accent-color)';
                    setTimeout(() => window.location.href = '/invoice/manage', 1500);
                } else {
                    const data = await res.json();
                    throw new Error(data.message || "Fiscal authorization failure.");
                }
            } catch (err) {
                msg.innerText = err.message;
                msg.style.color = 'var(--danger)';
                setButtonLoading(btn, false);
            }
        }