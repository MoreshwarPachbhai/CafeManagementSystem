console.log("🚀 Settings Dashboard JS is executing!");

document.addEventListener('DOMContentLoaded', () => {
    console.log("✅ DOM Content Loaded");

    // =================================================================
    // --- 0. SETTINGS (Dark Mode & Sound) - FIXED SYNC ---
    // =================================================================
    const userId = 1; // TODO: Replace with real logged-in user ID later

    function applyDarkMode(enabled) {
        if (enabled) document.documentElement.classList.add('dark');
        else document.documentElement.classList.remove('dark');
    }

    const darkEl = document.getElementById('darkModeToggle');
    const soundEl = document.getElementById('soundAlertToggle');

    if (darkEl && soundEl) {
        // 1. Handle Dark Mode (Prioritize Local Storage to prevent flash)
        const localPref = localStorage.getItem('darkMode');
        if (localPref !== null) {
            const isDark = (localPref === 'true');
            applyDarkMode(isDark);
            darkEl.checked = isDark;
        }

        // 2. ALWAYS Fetch Server Settings (Fix: This is now outside the 'else')
        fetch(`/api/settings/${userId}`)
            .then(r => { if (!r.ok) throw new Error(r.status); return r.json(); })
            .then(data => {
                console.log("📥 [Debug] Server Settings Loaded:", data);
                
                // Sync Dark Mode (Server is source of truth if local is missing)
                if (localPref === null) {
                    const isDark = !!data.darkMode;
                    applyDarkMode(isDark);
                    darkEl.checked = isDark;
                }

                // Sync Sound Alert (Crucial Fix: This now runs every time)
                const isSound = !!data.soundAlert;
                console.log("   - Sound is:", isSound ? "ON" : "OFF");
                soundEl.checked = isSound;
                
                // Update LocalStorage so main.js can see it
                localStorage.setItem('soundAlert', isSound ? 'true' : 'false');
            })
            .catch(err => {
                console.error('Failed loading server settings:', err);
            });

        // --- Event Listeners (Same as before) ---
        darkEl.addEventListener('change', function () {
            const enabled = this.checked;
            applyDarkMode(enabled);
            localStorage.setItem('darkMode', enabled ? 'true' : 'false');
            fetch(`/api/settings/${userId}/dark-mode?enabled=${enabled}`, { method: 'POST' });
        });

        soundEl.addEventListener('change', function () {
            const enabled = this.checked;
            console.log("🖱️ Toggled Sound to:", enabled);
            // Save to LocalStorage immediately
            localStorage.setItem('soundAlert', enabled ? 'true' : 'false');
            // Save to Server
            fetch(`/api/settings/${userId}/sound-alert?enabled=${enabled}`, { method: 'POST' })
                .then(r => { if(!r.ok) console.error("Save failed"); });
        });
    }
    
    // =================================================================
    // --- 1. GENERIC ACCORDION LOGIC ---
    // =================================================================
    document.body.addEventListener('click', (event) => {
        const header = event.target.closest('.clickable-header');
        
        if (header) {
            const group = header.closest('.settings-item-group');
            if (group) {
                const content = group.querySelector('.expandable-content');
                const arrow = header.querySelector('.settings-item-arrow');

                if (content && arrow) {
                    const isOpen = content.style.maxHeight;
                    
                    if (isOpen) {
                        // CLOSE
                        content.style.maxHeight = null; 
                        arrow.classList.remove('rotate-90');
                    } else {
                        // OPEN - Load dynamic lists if needed
                        if (group.id === 'delete-staff-group') {
                            loadStaffListForDelete(content);
                        } else if (group.id === 'edit-staff-group') {
                            loadStaffListForEdit(content);
                        } else if (group.id === 'delete-menu-item-group') {
                            loadMenuListForDelete(content);
                        } else if (group.id === 'edit-menu-item-group') {
                            loadMenuListForEdit(content);
                        } else {
                            // For static content, just open
                            content.style.maxHeight = content.scrollHeight + "px";
                        }
                        arrow.classList.add('rotate-90');
                    }
                }
            }
        }
    });

    // =================================================================
    // --- 2. ADD STAFF LOGIC ---
    // =================================================================
    document.body.addEventListener('submit', async (event) => {
        if (event.target.id === 'add-staff-form') {
            event.preventDefault(); 
            const form = event.target;
            
            const nameInput = form.querySelector('#new-staff-name');
            const usernameInput = form.querySelector('#new-staff-username');
            const passwordInput = form.querySelector('#new-staff-password');

            if (!nameInput || !usernameInput || !passwordInput) return;

            const payload = {
                name: nameInput.value,
                username: usernameInput.value,
                password: passwordInput.value
            };

            try {
                const response = await fetch('/api/staff/add', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                if (response.ok) {
                    alert("🎉 Staff added!");
                    form.reset();
                    const header = form.closest('.settings-item-group').querySelector('.clickable-header');
                    if(header) header.click(); 
                } else {
                    const txt = await response.text();
                    alert("❌ Error: " + txt);
                }
            } catch (error) {
                console.error(error);
                alert("❌ Network Error");
            }
        }
    });

    // =================================================================
    // --- 3. DELETE STAFF LOGIC ---
    // =================================================================
    async function loadStaffListForDelete(contentElement) {
        const listContainer = document.getElementById('delete-staff-list');
        if (!listContainer) return;

        listContainer.innerHTML = '<p style="padding: 20px;">Loading...</p>';
        if (contentElement) contentElement.style.maxHeight = "500px"; 

        try {
            const response = await fetch('/api/staff/all');
            const staffList = await response.json();

            if (staffList.length === 0) {
                listContainer.innerHTML = '<p style="padding: 20px;">No staff found.</p>';
            } else {
                let html = '';
                staffList.forEach(staff => {
                    html += `
                        <div class="staff-list-item">
                            <div class="staff-info">
                                <span class="staff-name">${staff.name}</span>
                                <span class="staff-username">@${staff.username}</span>
                            </div>
                            <button class="btn-delete-action" data-staff-id="${staff.staffId}">Delete</button>
                        </div>
                    `;
                });
                listContainer.innerHTML = html;
            }
            if (contentElement) contentElement.style.maxHeight = contentElement.scrollHeight + "px";

        } catch (error) {
            console.error(error);
            listContainer.innerHTML = '<p style="padding: 20px; color: red;">Error.</p>';
        }
    }

    // Handle Delete Staff Click
    document.body.addEventListener('click', async (event) => {
        if (event.target.classList.contains('btn-delete-action') && !event.target.classList.contains('btn-delete-menu-item')) {
            const btn = event.target;
            const staffId = btn.dataset.staffId;
            
            if (confirm("Are you sure you want to delete this staff member?")) {
                try {
                    const response = await fetch(`/api/staff/delete/${staffId}`, { method: 'DELETE' });
                    if (response.ok) {
                        alert("Staff deleted successfully");
                        const group = document.getElementById('delete-staff-group');
                        if (group) group.querySelector('.clickable-header').click();
                    } else {
                        alert("Failed to delete");
                    }
                } catch (error) {
                    alert("Network error");
                }
            }
        }
    });

    // =================================================================
    // --- 4. EDIT STAFF LOGIC ---
    // =================================================================
    async function loadStaffListForEdit(contentElement) {
        const listContainer = document.getElementById('edit-staff-list');
        if (!listContainer) return;

        document.getElementById('edit-staff-list').style.display = 'block';
        document.getElementById('edit-staff-form-container').style.display = 'none';

        listContainer.innerHTML = '<p style="padding: 20px;">Loading...</p>';
        if (contentElement) contentElement.style.maxHeight = "500px";

        try {
            const response = await fetch('/api/staff/all');
            const staffList = await response.json();

            if (staffList.length === 0) {
                listContainer.innerHTML = '<p style="padding: 20px;">No staff found.</p>';
            } else {
                let html = '';
                staffList.forEach(staff => {
                    html += `
                        <div class="staff-list-item">
                            <div class="staff-info">
                                <span class="staff-name">${staff.name}</span>
                                <span class="staff-username">@${staff.username}</span>
                            </div>
                            <button class="btn-edit-action" 
                                style="background-color: #007bff; color: white; padding: 6px 14px; border-radius: 6px; border:none; cursor:pointer;"
                                data-staff-id="${staff.staffId}"
                                data-staff-name="${staff.name}"
                                data-staff-username="${staff.username}">
                                Edit
                            </button>
                        </div>
                    `;
                });
                listContainer.innerHTML = html;
            }
            if (contentElement) contentElement.style.maxHeight = contentElement.scrollHeight + "px";

        } catch (error) {
            console.error(error);
            listContainer.innerHTML = '<p style="padding: 20px; color: red;">Error.</p>';
        }
    }

    document.body.addEventListener('click', (event) => {
        if (event.target.classList.contains('btn-edit-action')) {
            const btn = event.target;
            document.getElementById('edit-staff-id').value = btn.dataset.staffId;
            document.getElementById('edit-staff-name').value = btn.dataset.staffName;
            document.getElementById('edit-staff-username').value = btn.dataset.staffUsername;
            document.getElementById('edit-staff-password').value = ''; 

            document.getElementById('edit-staff-list').style.display = 'none';
            document.getElementById('edit-staff-form-container').style.display = 'block';
            
            const group = document.getElementById('edit-staff-group');
            const content = group.querySelector('.expandable-content');
            content.style.maxHeight = content.scrollHeight + "px";
        }
    });

    document.body.addEventListener('click', (event) => {
        if (event.target.id === 'cancel-edit-btn') {
            document.getElementById('edit-staff-list').style.display = 'block';
            document.getElementById('edit-staff-form-container').style.display = 'none';
        }
    });

    document.body.addEventListener('submit', async (event) => {
        if (event.target.id === 'edit-staff-form') {
            event.preventDefault();
            const id = document.getElementById('edit-staff-id').value;
            const name = document.getElementById('edit-staff-name').value;
            const username = document.getElementById('edit-staff-username').value;
            const password = document.getElementById('edit-staff-password').value;
            const payload = { name, username, password };

            try {
                const response = await fetch(`/api/staff/update/${id}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                if (response.ok) {
                    alert("✅ Updated successfully!");
                    loadStaffListForEdit(null); 
                    document.getElementById('edit-staff-list').style.display = 'block';
                    document.getElementById('edit-staff-form-container').style.display = 'none';
                } else {
                    alert("❌ Failed to update");
                }
            } catch (error) {
                alert("Network Error");
            }
        }
    });

    // =================================================================
    // --- 5. ADD MENU ITEM LOGIC ---
    // =================================================================
    document.body.addEventListener('submit', async (event) => {
        if (event.target.id === 'add-menu-item-form') {
            event.preventDefault(); 
            const form = event.target;
            
            const nameInput = form.querySelector('#new-item-name');
            const priceInput = form.querySelector('#new-item-price');
            const categoryInput = form.querySelector('#new-item-category');

            if (!nameInput || !priceInput || !categoryInput) return;

            const payload = {
                name: nameInput.value,
                price: parseFloat(priceInput.value),
                category: categoryInput.value
            };

            try {
                const response = await fetch('/api/menu-items/add', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                if (response.ok) {
                    alert("🎉 Menu Item added!");
                    form.reset();
                    const header = form.closest('.settings-item-group').querySelector('.clickable-header');
                    if(header) header.click(); 
                } else {
                    const txt = await response.text();
                    alert("❌ Error: " + txt);
                }
            } catch (error) {
                console.error(error);
                alert("❌ Network Error");
            }
        }
    });

    // =================================================================
    // --- 6. DELETE MENU ITEM LOGIC ---
    // =================================================================
    
    async function loadMenuListForDelete(contentElement) {
        const listContainer = document.getElementById('delete-menu-list');
        if (!listContainer) return;

        listContainer.innerHTML = '<p style="padding: 20px;">Loading...</p>';
        if (contentElement) contentElement.style.maxHeight = "500px";

        try {
            const response = await fetch('/api/menu-items/all');
            const items = await response.json();

            if (items.length === 0) {
                listContainer.innerHTML = '<p style="padding: 20px;">No items found.</p>';
            } else {
                let html = '';
                items.forEach(item => {
                    html += `
                        <div class="staff-list-item"> <!-- Re-using staff list styles -->
                            <div class="staff-info">
                                <span class="staff-name">${item.name}</span>
                                <span class="staff-username">${item.category} - ₹${item.price}</span>
                            </div>
                            <button class="btn-delete-action btn-delete-menu-item" data-item-id="${item.itemId}">Delete</button>
                        </div>
                    `;
                });
                listContainer.innerHTML = html;
            }
            if (contentElement) contentElement.style.maxHeight = contentElement.scrollHeight + "px";

        } catch (error) {
            console.error(error);
            listContainer.innerHTML = '<p style="padding: 20px; color: red;">Error.</p>';
        }
    }

    // Handle Delete Menu Item Click
    document.body.addEventListener('click', async (event) => {
        if (event.target.classList.contains('btn-delete-menu-item')) {
            const btn = event.target;
            const itemId = btn.dataset.itemId;
            
            if (confirm("Are you sure you want to delete this item?")) {
                try {
                    const response = await fetch(`/api/menu-items/delete/${itemId}`, { method: 'DELETE' });
                    if (response.ok) {
                        alert("Item deleted successfully");
                        const group = document.getElementById('delete-menu-item-group');
                        if (group) group.querySelector('.clickable-header').click();
                    } else {
                        alert("Failed to delete");
                    }
                } catch (error) {
                    alert("Network error");
                }
            }
        }
    });

    // =================================================================
    // --- 7. EDIT MENU ITEM LOGIC ---
    // =================================================================
    
    async function loadMenuListForEdit(contentElement) {
        const listContainer = document.getElementById('edit-menu-list');
        if (!listContainer) return;

        document.getElementById('edit-menu-list').style.display = 'block';
        document.getElementById('edit-menu-form-container').style.display = 'none';

        listContainer.innerHTML = '<p style="padding: 20px;">Loading...</p>';
        if (contentElement) contentElement.style.maxHeight = "500px";

        try {
            const response = await fetch('/api/menu-items/all');
            const items = await response.json();

            if (items.length === 0) {
                listContainer.innerHTML = '<p style="padding: 20px;">No items found.</p>';
            } else {
                let html = '';
                items.forEach(item => {
                    html += `
                        <div class="staff-list-item">
                            <div class="staff-info">
                                <span class="staff-name">${item.name}</span>
                                <span class="staff-username">${item.category} - ₹${item.price}</span>
                            </div>
                            <button class="btn-edit-menu-action" 
                                style="background-color: #007bff; color: white; padding: 6px 14px; border-radius: 6px; border:none; cursor:pointer;"
                                data-item-id="${item.itemId}"
                                data-item-name="${item.name}"
                                data-item-price="${item.price}"
                                data-item-category="${item.category}">
                                Edit
                            </button>
                        </div>
                    `;
                });
                listContainer.innerHTML = html;
            }
            if (contentElement) contentElement.style.maxHeight = contentElement.scrollHeight + "px";

        } catch (error) {
            console.error(error);
            listContainer.innerHTML = '<p style="padding: 20px; color: red;">Error.</p>';
        }
    }

    document.body.addEventListener('click', (event) => {
        if (event.target.classList.contains('btn-edit-menu-action')) {
            const btn = event.target;
            document.getElementById('edit-menu-id').value = btn.dataset.itemId;
            document.getElementById('edit-menu-name').value = btn.dataset.itemName;
            document.getElementById('edit-menu-price').value = btn.dataset.itemPrice;
            document.getElementById('edit-menu-category').value = btn.dataset.itemCategory;

            document.getElementById('edit-menu-list').style.display = 'none';
            document.getElementById('edit-menu-form-container').style.display = 'block';
            
            const group = document.getElementById('edit-menu-item-group');
            const content = group.querySelector('.expandable-content');
            content.style.maxHeight = content.scrollHeight + "px";
        }
    });

    document.body.addEventListener('click', (event) => {
        if (event.target.id === 'cancel-menu-edit-btn') {
            document.getElementById('edit-menu-list').style.display = 'block';
            document.getElementById('edit-menu-form-container').style.display = 'none';
        }
    });

    // Handle "Save Changes" Form Submit (FIXED with Error Handling)
    document.body.addEventListener('submit', async (event) => {
        if (event.target.id === 'edit-menu-form') {
            event.preventDefault();
            
            // Debugging: Check the ID we are trying to update
            const id = document.getElementById('edit-menu-id').value;
            console.log("Attempting to update Item ID:", id);

            if (!id) {
                alert("❌ Error: Item ID is missing. Please close and try clicking 'Edit' again.");
                return;
            }

            const name = document.getElementById('edit-menu-name').value;
            const price = document.getElementById('edit-menu-price').value;
            const category = document.getElementById('edit-menu-category').value;

            const payload = { name, price: parseFloat(price), category };
            console.log("Sending Payload:", payload);

            try {
                const response = await fetch(`/api/menu-items/update/${id}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                // Always read the text response
                const responseText = await response.text(); 

                if (response.ok) {
                    alert("✅ Success: " + responseText);
                    loadMenuListForEdit(null); 
                    document.getElementById('edit-menu-list').style.display = 'block';
                    document.getElementById('edit-menu-form-container').style.display = 'none';
                } else {
                    console.error("Server Error:", responseText);
                    alert("❌ Failed: " + responseText);
                }
            } catch (error) {
                console.error(error);
                alert("❌ Network Error: check console for details");
            }
        }
    });
});