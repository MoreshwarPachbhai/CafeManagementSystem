// This wrapper waits for the HTML to be fully loaded before running any code.
document.addEventListener('DOMContentLoaded', () => {

    console.log("Orders Dashboard JS loaded!");

    // --- 1. SELECTORS ---
    const toggleButtonContainer = document.querySelector('.order-status-toggle');
    const onProcessList = document.getElementById('on-process-list');
    const completeList = document.getElementById('complete-list');
    
    // Get the table bodies (where we will add the rows)
    const onProcessTableBody = onProcessList.querySelector('.order-list-table');
    const completeTableBody = completeList.querySelector('.order-list-table');
    
    // Get the toggle buttons
    const onProcessBtn = toggleButtonContainer.querySelector('.status-btn[data-status="processing"]');
    const completeBtn = toggleButtonContainer.querySelector('.status-btn[data-status="complete"]');


    // --- 2. RENDER FUNCTION ---
    /**
     * Creates the HTML for a single order row.
     * @param {object} order - The order data from the API (OrderListDto)
     * @param {string} status - The status, to decide which button to show
     */
    function createOrderRowHtml(order, status) {
        let actionButtonHtml = '';
        if (status === 'ON_PROCESS') {
            actionButtonHtml = `<button class="btn-action btn-complete" data-order-id="${order.orderId}">Complete</button>`;
        } else if (status === 'COMPLETE') {
            actionButtonHtml = `<button class="btn-action btn-paid" data-order-id="${order.orderId}">Paid</button>`;
        }

        return `
            <div class="order-list-row" data-order-id="${order.orderId}">
                <span>${order.orderId}</span>
                <span>${order.tableNo}</span>
                <span>${order.totalPrice.toFixed(2)}</span>
                <span>
                    ${actionButtonHtml}
                </span>
            </div>
        `;
    }

    /**
     * Fetches orders from the API based on status and renders them to the correct list.
     * @param {string} status - e.g., "ON_PROCESS" or "COMPLETE"
     */
    async function fetchAndRenderOrders(status) {
        console.log(`Fetching orders with status: ${status}`);
        
        // 1. Determine which list container to target
        let targetTableBody;
        if (status === 'ON_PROCESS') {
            targetTableBody = onProcessTableBody;
        } else if (status === 'COMPLETE') {
            targetTableBody = completeTableBody;
        } else {
            return; // Do nothing if status is unknown
        }

        // 2. Clear all *old* rows (but not the header)
        targetTableBody.querySelectorAll('.order-list-row').forEach(row => row.remove());

        try {
            // 3. Call the new API endpoint
            const response = await fetch(`/api/orders?status=${status}`);
            if (!response.ok) {
                throw new Error(`API Error: ${response.status}`);
            }
            const orders = await response.json();

            // 4. Check if any orders were returned
            if (orders.length === 0) {
                // You can add a "No orders" message here if you want
                console.log(`No orders found with status: ${status}`);
                return;
            }

            // 5. Create and append the new rows
            orders.forEach(order => {
                const rowHtml = createOrderRowHtml(order, status);
                // We use insertAdjacentHTML to add the row *after* the header
                targetTableBody.insertAdjacentHTML('beforeend', rowHtml);
            });

        } catch (error) {
            console.error(`Failed to fetch ${status} orders:`, error);
            // You can show an error message in the UI here
        }
    }


    // --- 3. EVENT LISTENERS ---

    // Listener for the "On process" / "Complete" toggle buttons
    toggleButtonContainer.addEventListener('click', (event) => {
        const clickedButton = event.target.closest('.status-btn');
        if (!clickedButton || clickedButton.classList.contains('active')) {
            return; // Do nothing if it's not a button or already active
        }

        const status = clickedButton.dataset.status;

        // Update active state on buttons
        onProcessBtn.classList.remove('active');
        completeBtn.classList.remove('active');
        clickedButton.classList.add('active');

        // Show/hide the correct lists
        if (status === 'processing') {
            onProcessList.style.display = 'block';
            completeList.style.display = 'none';
            fetchAndRenderOrders('ON_PROCESS');
        } else {
            onProcessList.style.display = 'none';
            completeList.style.display = 'block';
            fetchAndRenderOrders('COMPLETE');
        }
    });
    /**
     * Handles clicks on the dynamic "Complete" and "Paid" buttons
     */
    async function handleActionButtonClick(event) {
        const clickedButton = event.target;
        let orderId;
        let newStatus;

        // Determine which button was clicked
        if (clickedButton.classList.contains('btn-complete')) {
            orderId = clickedButton.dataset.orderId;
            newStatus = 'COMPLETE';
        } else if (clickedButton.classList.contains('btn-paid')) {
            orderId = clickedButton.dataset.orderId;
            newStatus = 'PAID';
        } else {
            return; // Click was not on an action button
        }

        console.log(`Updating order ${orderId} to status ${newStatus}`);

        try {
            // Call the new PATCH API endpoint
            const response = await fetch(`/api/orders/${orderId}/status`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ status: newStatus })
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText);
            }

            // Success! Remove the row from the UI.
            const rowToRemove = clickedButton.closest('.order-list-row');
            if (rowToRemove) {
                rowToRemove.remove();
            }
            console.log(`Order ${orderId} updated successfully.`);
            
            // Optional: If you want to be extra sure, you can reload both lists
            // fetchAndRenderOrders('ON_PROCESS');
            // fetchAndRenderOrders('COMPLETE');

        } catch (error) {
            console.error(`Failed to update order ${orderId}:`, error);
            // alert(`Error: ${error.message}`);
        }
    }

    // Add one listener to the main list container to catch all button clicks
    document.querySelector('.order-list-main').addEventListener('click', handleActionButtonClick);


    // --- 4. INITIAL PAGE LOAD ---
    // Load the "On process" orders by default when the page opens
    fetchAndRenderOrders('ON_PROCESS');

});