// This wrapper waits for the HTML to be fully loaded before running any code.
document.addEventListener('DOMContentLoaded', () => {

    // --- Page-specific Logic ---
    // We check which page we're on by looking for key elements.

    const loginForm = document.getElementById('loginForm');
    const categoryList = document.querySelector('.category-list');
    const contentGrid = document.querySelector('.content-grid');


    // --- 2. DASHBOARD PAGE LOGIC ---
    if (categoryList && contentGrid) {
        console.log("Dashboard page script loaded.");

        // --- SELECTORS ---
        const orderListContainer = document.getElementById('order-list-container');
        const searchInput = document.querySelector('.search-bar input'); 
        const orderForm = document.querySelector('.order-form'); 
        const cancelButton = document.querySelector('.btn-cancel'); 
        const orderTitle = document.querySelector('.order-title'); 
        const defaultOrderTitle = orderTitle.textContent; 

        // --- STATE VARIABLES ---
        let currentOrder = []; 
        let pendingOrderId = null; 
        let searchDebounceTimer; 
        let isCreatingOrder = false; 

        // --- NEW FUNCTION: Load Categories ---
        async function loadCategories() {
            try {
                // Call the new API
                const response = await fetch('/api/menu-items/categories');
                if (!response.ok) throw new Error("Failed to fetch categories");
                const categories = await response.json();

                // Clear the existing hard-coded list
                categoryList.innerHTML = ''; 

                if (categories.length === 0) {
                    categoryList.innerHTML = '<li style="padding:10px">No categories found</li>';
                    return;
                }

                // Loop through and create the list items
                categories.forEach((cat, index) => {
                    const li = document.createElement('li');
                    const link = document.createElement('a');
                    link.href = "#";
                    link.className = 'category-link'; // Important for styling
                    link.textContent = cat;
                    link.dataset.category = cat; // Important for click logic
                    
                    // Make the first one active by default
                    if (index === 0) {
                        link.classList.add('active');
                        // Load items for this first category immediately
                        fetchAndRenderItems(cat);
                    }
                    
                    li.appendChild(link);
                    categoryList.appendChild(li);
                });

            } catch (error) {
                console.error(error);
                categoryList.innerHTML = '<li style="padding:10px; color:red">Error loading categories</li>';
            }
        }
        
        function syncMenuButtonsWithOrderState() {
            const allAddButtons = contentGrid.querySelectorAll('.add-btn');
            allAddButtons.forEach(btn => {
                const itemId = btn.dataset.itemId;
                const itemIsInOrder = currentOrder.some(orderItem => orderItem.id === itemId);
                if (itemIsInOrder) {
                    btn.classList.add('added');
                    btn.textContent = 'Added';
                    btn.disabled = true;
                } else {
                    btn.classList.remove('added');
                    btn.textContent = '+ Add';
                    btn.disabled = false;
                }
            });
        }

        
        function renderOrderList() {
            if (currentOrder.length === 0) {
                orderListContainer.innerHTML = ''; 
                return;
            }
            let total = 0;
            let headerHtml = `
                <div class="order-header">
                    <span>Sr.No</span>
                    <span class="order-item-name">Item Name</span>
                    <span>Quantity</span>
                    <span>Price</span>
                </div>
            `;
            let itemsHtml = currentOrder.map((item, index) => {
                const itemTotal = item.price * item.quantity;
                total += itemTotal;
                return `
                    <div class="order-item" data-item-id="${item.id}">
                        <span>${index + 1}.</span>
                        <span class="order-item-name">${item.name}</span>
                        <div class="quantity-controls">
                            <button class="quantity-btn minus" data-action="minus">-</button>
                            <span class="quantity-display">${item.quantity}</span>
                            <button class="quantity-btn plus" data-action="plus">+</button>
                        </div>
                        <span>${itemTotal.toFixed(2)}</span>
                    </div>
                `;
            }).join('');
            let totalHtml = `
                <div class="total-row">
                    <span class="total-label">Total Amount</span>
                    <span class="total-price">${total.toFixed(2)}</span>
                </div>
            `;
            orderListContainer.innerHTML = headerHtml + itemsHtml + totalHtml;
        }

        
        function renderItemsToGrid(items, noItemsMessage) {
            contentGrid.innerHTML = ''; 
            if (items.length === 0) {
                contentGrid.innerHTML = `<div class="no-items">${noItemsMessage}</div>`;
                return;
            }
            items.forEach(item => {
                const card = document.createElement('div');
                card.className = 'menu-card';
                card.innerHTML = `
                    <h3 class="card-title">${item.name}</h3>
                    <div class="card-footer">
                        <span class="price">${item.price.toFixed(2)}</span>
                        <button class="add-btn" data-item-id="${item.itemId}">+ Add</button>
                    </div>
                `;
                contentGrid.appendChild(card);
            });
            syncMenuButtonsWithOrderState();
        }

        
        async function fetchAndRenderItems(categoryName) {
            contentGrid.innerHTML = '<div class="loading">Loading items...</div>';
            try {
                const response = await fetch(`/api/menu-items?category=${encodeURIComponent(categoryName)}`);
                if (!response.ok) {
                    throw new Error(`API Error: ${response.status} ${response.statusText}`);
                }
                const items = await response.json();
                renderItemsToGrid(items, 'No items found in this category.');
            } catch (error) {
                console.error('Error fetching menu items:', error);
                contentGrid.innerHTML = `<div class="error">Could not load items. API call failed: ${error.message}</div>`;
            }
        }

        
        async function handleContentGridClick(event) {
            const clickedButton = event.target.closest('.add-btn');
            if (!clickedButton || clickedButton.disabled || isCreatingOrder) {
                return;
            }
            if (pendingOrderId === null) {
                isCreatingOrder = true; 
                try {
                    console.log("Creating pending order...");
                    const response = await fetch('/api/orders/create-pending', {
                        method: 'POST'
                    });
                    if (!response.ok) {
                        throw new Error("Failed to create pending order.");
                    }
                    const newOrder = await response.json();
                    pendingOrderId = newOrder.orderId; 
                    orderTitle.textContent = `Order Id : ${pendingOrderId}`; 
                    console.log("Pending order created with ID:", pendingOrderId);
                } catch (error) {
                    console.error("Error creating pending order:", error);
                    isCreatingOrder = false; 
                    return; 
                }
                isCreatingOrder = false; 
            }
            const itemId = clickedButton.dataset.itemId;
            const card = clickedButton.closest('.menu-card');
            const itemName = card.querySelector('.card-title').textContent;
            const itemPrice = parseFloat(card.querySelector('.price').textContent);
            const existingItem = currentOrder.find(item => item.id === itemId);
            if (existingItem) {
                existingItem.quantity++;
            } else {
                currentOrder.push({
                    id: itemId,
                    name: itemName,
                    price: itemPrice,
                    quantity: 1
                });
            }
            renderOrderList();
            syncMenuButtonsWithOrderState();
        }

       
        function handleOrderListClick(event) {
            const clickedButton = event.target.closest('.quantity-btn');
            if (!clickedButton) return;
            const action = clickedButton.dataset.action;
            const itemRow = clickedButton.closest('.order-item');
            const itemId = itemRow.dataset.itemId;
            const itemInOrder = currentOrder.find(item => item.id === itemId);
            if (!itemInOrder) return; 
            if (action === 'plus') {
                itemInOrder.quantity++;
            } else if (action === 'minus') {
                itemInOrder.quantity--;
            }
            if (itemInOrder.quantity === 0) {
                currentOrder = currentOrder.filter(item => item.id !== itemId);
            }
            renderOrderList();
            syncMenuButtonsWithOrderState();
        }

        
        // --- THIS FUNCTION IS NOW FIXED ---
        async function handleSearch() {
            const query = searchInput.value.trim();

            if (query === '') {
                const activeLink = categoryList.querySelector('.category-link.active');
                if (activeLink) {
                    fetchAndRenderItems(activeLink.dataset.category);
                }
                return;
            }
            
            categoryList.querySelectorAll('.category-link').forEach(link => {
                link.classList.remove('active');
            });

            contentGrid.innerHTML = '<div class="loading">Searching...</div>';
            try {
                // --- FIX 1: This now calls the correct SEARCH API ---
                const response = await fetch(`/api/menu-items/search?q=${encodeURIComponent(query)}`);
                if (!response.ok) {
                    throw new Error(`API Error: ${response.status} ${response.statusText}`);
                }
                const items = await response.json();
                renderItemsToGrid(items, `No items found matching "${query}".`); 
            } catch (error) {
                console.error('Error searching items:', error);
                contentGrid.innerHTML = `<div class="error">Search failed: ${error.message}</div>`;
            }
        }
        // --- END OF FIX ---

        
        function debounce(func, delay) {
            return function(...args) {
                clearTimeout(searchDebounceTimer);
                searchDebounceTimer = setTimeout(() => {
                    func.apply(this, args);
                }, delay);
            };
        }

        
        async function handleCancelOrder(event) {
            if (event) {
                event.preventDefault(); 
            }
            console.log("Cancel button clicked.");
            if (pendingOrderId !== null) {
                console.log(`Cancelling pending order ID: ${pendingOrderId}`);
                try {
                    await fetch(`/api/orders/cancel/${pendingOrderId}`, {
                        method: 'DELETE'
                    });
                    console.log("Pending order cancelled on server.");
                } catch (error) {
                    console.error("Error cancelling pending order:", error);
                }
            }
            if (orderForm) {
                orderForm.reset(); 
            }
            currentOrder = [];
            pendingOrderId = null; 
            orderTitle.textContent = defaultOrderTitle; 
            renderOrderList();
            syncMenuButtonsWithOrderState();
        }
        
        
        // --- THIS FUNCTION IS NOW FIXED ---
        async function handleOrderSubmit(event) {
            event.preventDefault(); 
            console.log("Order button clicked!");
            
            // --- 1. VALIDATION ---
            if (currentOrder.length === 0 || pendingOrderId === null) {
                console.error("Cannot place an empty order.");
                return; 
            }

            // --- FIX 2: Use new robust ID selectors ---
            const staffId = document.getElementById('form-input-staff-id').value.trim();
            const tableNo = document.getElementById('form-input-table-no').value.trim();
            const customerName = document.getElementById('form-input-cust-name').value.trim();
            const customerContact = document.getElementById('form-input-cust-contact').value.trim();
            // --- END OF FIX 2 ---

            if (!staffId || !tableNo || !customerName || !customerContact) {
                console.error("All form fields are required.");
                // We should show a user-friendly message here
                return;
            }

            // --- 3. FORMAT PAYLOAD (Same as before) ---
            const itemsPayload = currentOrder.map(item => ({
                itemId: item.id,
                quantity: item.quantity
            }));
            const orderRequestPayload = {
                staffId: staffId,
                tableNo: tableNo,
                customerName: customerName,
                customerContact: customerContact,
                items: itemsPayload
            };

            console.log(`Submitting order to ID: ${pendingOrderId}`, orderRequestPayload);

            // --- 4. SEND TO BACKEND API (Same as before) ---
            try {
                const response = await fetch(`/api/orders/submit/${pendingOrderId}`, {
                    method: 'PATCH', 
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(orderRequestPayload)
                });

                if (response.ok) {
                    const resultMessage = await response.text();
                    console.log("Order placed!", resultMessage);
                    
                    // --- FIX 3: REDIRECT TO "ON PROCESS" PAGE ---
                    // This completes your workflow
                    window.location.href = '/orders_dashboard';
                    // --- END OF FIX 3 ---
                    
                } else {
                    const errorText = await response.text();
                    console.error("Failed to place order:", errorText);
                }

            } catch (error) {
                console.error("Network error submitting order:", error);
            }
        }
        
        // --- EVENT LISTENERS (No changes here) ---
        categoryList.addEventListener('click', (event) => {
            if (event.target.classList.contains('category-link')) {
                event.preventDefault();
                if(searchInput) searchInput.value = '';
                categoryList.querySelectorAll('.category-link').forEach(link => link.classList.remove('active'));
                event.target.classList.add('active');
                const category = event.target.dataset.category;
                fetchAndRenderItems(category);
            }
        });

        contentGrid.addEventListener('click', handleContentGridClick);
        orderListContainer.addEventListener('click', handleOrderListClick);

        if (searchInput) {
            searchInput.addEventListener('input', debounce(handleSearch, 300)); 
        }
        
        if (cancelButton) {
            cancelButton.addEventListener('click', handleCancelOrder);
        }

        if (orderForm) {
            orderForm.addEventListener('submit', handleOrderSubmit);
        }

        // --- INITIAL PAGE LOAD (No changes here) ---
        loadCategories();
    }

   // =================================================================
    // --- 8. ORDER BUTTON SOUND LOGIC (DEBUG VERSION) ---
    // =================================================================
    
    const orderBtn = document.querySelector('.btn-order');
    const orderForm = document.querySelector('.order-form');
    const sound = document.getElementById('orderSound'); 

    console.log("🔍 [Debug] Sound Logic Initializing...");
    console.log("   - Button found?", !!orderBtn);
    console.log("   - Form found?", !!orderForm);
    console.log("   - Audio tag found?", !!sound);

    if (orderBtn && orderForm) {
        
        function once(el, evt, timeout = 5000) {
            return new Promise((resolve) => {
                let done = false;
                function cleanup(val) { if (done) return; done = true; resolve(val); }
                const handler = (e) => cleanup(e);
                el.addEventListener(evt, handler, { once: true });
                if (timeout > 0) setTimeout(() => cleanup(null), timeout);
            });
        }

        orderBtn.addEventListener('click', async function(ev) {
            console.log("🖱️ [Debug] Order Button Clicked!");
            
            // 1. Prevent Default
            ev.preventDefault(); 
            console.log("   - Default action prevented (form stopped).");

            // 2. Check Settings
            const settingValue = localStorage.getItem('soundAlert');
            console.log("   - LocalStorage 'soundAlert' value:", settingValue);
            
            const shouldPlay = (settingValue === 'true');

            if (shouldPlay) {
                if (sound) {
                    console.log("🔊 [Debug] Attempting to play sound...");
                    let playStarted = false;
                    try {
                        sound.currentTime = 0;
                        const playPromise = sound.play();
                        if (playPromise && typeof playPromise.then === 'function') {
                            await Promise.race([ playPromise, new Promise(r => setTimeout(r, 2000)) ]);
                        }
                        await once(sound, 'playing', 1000).catch(()=>null);
                        playStarted = !sound.paused;
                        console.log("   - Play started successfully?", playStarted);
                    } catch (err) {
                        console.warn("❌ [Debug] Sound Error:", err);
                    }

                    if (playStarted) {
                        const durationMs = (isFinite(sound.duration) && sound.duration > 0) 
                            ? Math.ceil(sound.duration * 1000) + 200 : 4000;
                        console.log(`   - Waiting ${durationMs}ms for sound to finish.`);
                        await Promise.race([ once(sound, 'ended', durationMs + 100), new Promise(r => setTimeout(r, durationMs)) ]);
                    }
                } else {
                     console.warn("⚠️ [Debug] Sound setting is ON, but <audio> tag is missing!");
                }
            } else {
                console.log("🔇 [Debug] Sound skipped (Setting is OFF or NULL).");
            }

            // 3. Submit Form
            console.log("🚀 [Debug] Resuming Form Submission...");
            const submitEvent = new Event('submit', { bubbles: true, cancelable: true });
            orderForm.dispatchEvent(submitEvent);
        });
    } else {
        console.warn("❌ [Debug] Logic skipped: Missing .btn-order or .order-form");
    }
});

