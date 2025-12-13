// This wrapper waits for the HTML to be fully loaded before running any code.
document.addEventListener('DOMContentLoaded', () => {

    console.log("History Dashboard JS loaded!");

    // --- 1. SELECTORS ---
    const datePicker = document.getElementById('history-date-picker');
    const historyTableBody = document.querySelector('#history-list .order-list-table');
    
    // --- 2. RENDER FUNCTION ---
    /**
     * Creates the HTML for a single history order row.
     * @param {object} order - The order data from the API (HistoryOrderDto)
     */
    function createHistoryRowHtml(order) {
        
        // Format the date to be more readable
        // The date from the backend is a full timestamp (e.g., 2025-11-03T14:30:00)
        let formattedDate = 'N/A';
        if (order.date) {
            try {
                // Create a Date object
                const dateObj = new Date(order.date);
                
                // Format as "YYYY-MM-DD hh:mm AM/PM"
                // You can customize this format
                const localDate = dateObj.toLocaleDateString('en-US', {
                    year: 'numeric',
                    month: '2-digit',
                    day: '2-digit'
                });
                
                const localTime = dateObj.toLocaleTimeString('en-US', {
                    hour: '2-digit',
                    minute: '2-digit',
                    hour12: true
                });
                
                formattedDate = `${localDate} ${localTime}`;
            } catch (e) {
                console.error("Error formatting date:", order.date, e);
            }
        }
        
        const staffId = order.staffId || 'N/A'; // Handle if staffId is null

        return `
            <div class="order-list-row" data-order-id="${order.orderId}">
                <span>${staffId}</span>
                <span>${order.orderId}</span>
                <span>${formattedDate}</span>
                <span>${order.totalPrice.toFixed(2)}</span>
            </div>
        `;
    }

    /**
     * Fetches "PAID" orders from the API and renders them.
     * @param {string | null} date - The date to filter by (e.g., "2025-11-03")
     */
    async function fetchAndRenderHistory(date = null) {
        
        // 1. Build the URL
        let apiUrl = '/api/orders/history';
        if (date) {
            apiUrl += `?date=${date}`;
            console.log(`Fetching history for date: ${date}`);
        } else {
            console.log("Fetching all history");
        }
        
        // 2. Clear all *old* rows (but not the header)
        historyTableBody.querySelectorAll('.order-list-row').forEach(row => row.remove());

        try {
            // 3. Call the new API endpoint
            const response = await fetch(apiUrl);
            if (!response.ok) {
                throw new Error(`API Error: ${response.status}`);
            }
            const orders = await response.json();

            // 4. Check if any orders were returned
            if (orders.length === 0) {
                const message = date ? "No orders found for this date." : "No history found.";
                historyTableBody.insertAdjacentHTML('beforeend', `<div class="no-items">${message}</div>`);
                return;
            }

            // 5. Create and append the new rows
            orders.forEach(order => {
                const rowHtml = createHistoryRowHtml(order);
                historyTableBody.insertAdjacentHTML('beforeend', rowHtml);
            });

        } catch (error) {
            console.error('Failed to fetch history:', error);
            historyTableBody.insertAdjacentHTML('beforeend', `<div class="error">Could not load history.</div>`);
        }
    }


    // --- 3. EVENT LISTENERS ---

    // Listener for the date picker
    datePicker.addEventListener('change', (event) => {
        const selectedDate = event.target.value;
        fetchAndRenderHistory(selectedDate);
    });
    

    // --- 4. INITIAL PAGE LOAD ---
    // Load all "PAID" orders (no date filter) by default
    fetchAndRenderHistory();

});