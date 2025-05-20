function showModal(message, callback) {
  // Create modal container
  const modal = document.createElement("div");
  modal.style.position = "fixed";
  modal.style.top = "0";
  modal.style.left = "0";
  modal.style.width = "100%";
  modal.style.height = "100%";
  modal.style.backgroundColor = "rgba(0, 0, 0, 0.5)"; // Matches overlay style
  modal.style.display = "flex";
  modal.style.justifyContent = "center";
  modal.style.alignItems = "center";
  modal.style.zIndex = "1000";
  modal.style.transition = "opacity 0.3s ease"; // Matches --transition-speed
  modal.style.opacity = "0";
  setTimeout(() => { modal.style.opacity = "1"; }, 10); // Fade-in effect

  // Create modal content
  const modalContent = document.createElement("div");
  modalContent.style.backgroundColor = "var(--background-color)"; // #FFFFFF
  modalContent.style.padding = "24px"; // Matches --spacing
  modalContent.style.borderRadius = "var(--border-radius)"; // 0.25rem
  modalContent.style.boxShadow = "var(--box-shadow)"; // 0 0.5rem 1rem rgba(0, 0, 0, 0.15)
  modalContent.style.maxWidth = "400px";
  modalContent.style.width = "90%";
  modalContent.style.textAlign = "center";
  modalContent.style.fontFamily = "'Lato', sans-serif"; // Matches body font
  modalContent.style.color = "var(--text-color)"; // #333333

  // Add message
  const messageText = document.createElement("p");
  messageText.textContent = message;
  messageText.style.marginBottom = "1rem";
  messageText.style.fontSize = "1rem";
  modalContent.appendChild(messageText);

  // Create close button
  const closeButton = document.createElement("button");
  closeButton.textContent = "OK";
  closeButton.style.backgroundColor = "var(--primary-color)"; // #D4AF37
  closeButton.style.border = "1px solid var(--primary-color)"; // #D4AF37
  closeButton.style.color = "white";
  closeButton.style.padding = "0.5rem 1.5rem"; // Matches button padding
  closeButton.style.borderRadius = "var(--border-radius)"; // 0.25rem
  closeButton.style.fontSize = "0.9rem";
  closeButton.style.fontWeight = "600";
  closeButton.style.textTransform = "uppercase";
  closeButton.style.letterSpacing = "0.5px";
  closeButton.style.cursor = "pointer";
  closeButton.style.transition = "background-color 0.3s ease, border-color 0.3s ease"; // Matches --transition-speed
  closeButton.onmouseover = () => {
    closeButton.style.backgroundColor = "var(--primary-dark)"; // #A38829
    closeButton.style.borderColor = "var(--primary-dark)"; // #A38829
  };
  closeButton.onmouseout = () => {
    closeButton.style.backgroundColor = "var(--primary-color)"; // #D4AF37
    closeButton.style.borderColor = "var(--primary-color)"; // #D4AF37
  };
  closeButton.onclick = () => {
    modal.style.opacity = "0";
    setTimeout(() => {
      document.body.removeChild(modal);
      if (callback) callback();
    }, 300); // Matches --transition-speed
  };
  modalContent.appendChild(closeButton);

  // Append modal content to modal container
  modal.appendChild(modalContent);

  // Append modal to the document body
  document.body.appendChild(modal);
}

function showConfirmModal(message, confirmCallback) {
  // Create modal container
  const modal = document.createElement("div");
  modal.style.position = "fixed";
  modal.style.top = "0";
  modal.style.left = "0";
  modal.style.width = "100%";
  modal.style.height = "100%";
  modal.style.backgroundColor = "rgba(0, 0, 0, 0.5)"; // Matches overlay style
  modal.style.display = "flex";
  modal.style.justifyContent = "center";
  modal.style.alignItems = "center";
  modal.style.zIndex = "1000";
  modal.style.transition = "opacity 0.3s ease"; // Matches --transition-speed
  modal.style.opacity = "0";
  setTimeout(() => { modal.style.opacity = "1"; }, 10); // Fade-in effect

  // Create modal content
  const modalContent = document.createElement("div");
  modalContent.style.backgroundColor = "var(--background-color)"; // #FFFFFF
  modalContent.style.padding = "24px"; // Matches --spacing
  modalContent.style.borderRadius = "var(--border-radius)"; // 0.25rem
  modalContent.style.boxShadow = "var(--box-shadow)"; // 0 0.5rem 1rem rgba(0, 0, 0, 0.15)
  modalContent.style.maxWidth = "400px";
  modalContent.style.width = "90%";
  modalContent.style.textAlign = "center";
  modalContent.style.fontFamily = "'Lato', sans-serif"; // Matches body font
  modalContent.style.color = "var(--text-color)"; // #333333

  // Add message
  const messageText = document.createElement("p");
  messageText.textContent = message;
  messageText.style.marginBottom = "1.5rem";
  messageText.style.fontSize = "1rem";
  modalContent.appendChild(messageText);

  // Create button container
  const buttonContainer = document.createElement("div");
  buttonContainer.style.display = "flex";
  buttonContainer.style.justifyContent = "center";
  buttonContainer.style.gap = "1rem";

  // Create Yes button
  const yesButton = document.createElement("button");
  yesButton.textContent = "Yes";
  yesButton.style.backgroundColor = "var(--primary-color)"; // #D4AF37
  yesButton.style.border = "1px solid var(--primary-color)"; // #D4AF37
  yesButton.style.color = "white";
  yesButton.style.padding = "0.5rem 1.5rem"; // Matches button padding
  yesButton.style.borderRadius = "var(--border-radius)"; // 0.25rem
  yesButton.style.fontSize = "0.9rem";
  yesButton.style.fontWeight = "600";
  yesButton.style.textTransform = "uppercase";
  yesButton.style.letterSpacing = "0.5px";
  yesButton.style.cursor = "pointer";
  yesButton.style.transition = "background-color 0.3s ease, border-color 0.3s ease"; // Matches --transition-speed
  yesButton.onmouseover = () => {
    yesButton.style.backgroundColor = "var(--primary-dark)"; // #A38829
    yesButton.style.borderColor = "var(--primary-dark)"; // #A38829
  };
  yesButton.onmouseout = () => {
    yesButton.style.backgroundColor = "var(--primary-color)"; // #D4AF37
    yesButton.style.borderColor = "var(--primary-color)"; // #D4AF37
  };
  yesButton.onclick = () => {
    modal.style.opacity = "0";
    setTimeout(() => {
      document.body.removeChild(modal);
      if (confirmCallback) confirmCallback();
    }, 300); // Matches --transition-speed
  };

  // Create No button
  const noButton = document.createElement("button");
  noButton.textContent = "No";
  noButton.style.backgroundColor = "var(--secondary-color)"; // #2C3E50
  noButton.style.border = "1px solid var(--secondary-color)"; // #2C3E50
  noButton.style.color = "white";
  noButton.style.padding = "0.5rem 1.5rem"; // Matches button padding
  noButton.style.borderRadius = "var(--border-radius)"; // 0.25rem
  noButton.style.fontSize = "0.9rem";
  noButton.style.fontWeight = "600";
  noButton.style.textTransform = "uppercase";
  noButton.style.letterSpacing = "0.5px";
  noButton.style.cursor = "pointer";
  noButton.style.transition = "background-color 0.3s ease, border-color 0.3s ease"; // Matches --transition-speed
  noButton.onmouseover = () => {
    noButton.style.backgroundColor = "#1A252F"; // Darker shade of secondary-color
    noButton.style.borderColor = "#1A252F";
  };
  noButton.onmouseout = () => {
    noButton.style.backgroundColor = "var(--secondary-color)"; // #2C3E50
    noButton.style.borderColor = "var(--secondary-color)"; // #2C3E50
  };
  noButton.onclick = () => {
    modal.style.opacity = "0";
    setTimeout(() => {
      document.body.removeChild(modal);
    }, 300); // Matches --transition-speed
  };

  // Append buttons to button container
  buttonContainer.appendChild(yesButton);
  buttonContainer.appendChild(noButton);

  // Append button container to modal content
  modalContent.appendChild(buttonContainer);

  // Append modal content to modal container
  modal.appendChild(modalContent);

  // Append modal to the document body
  document.body.appendChild(modal);
}

document.addEventListener("DOMContentLoaded", () => {
    console.log("customer-management.js: DOM fully loaded, initializing customers tab");

    // Delivery select and email field logic
    const deliverySelect = document.getElementById("export-delivery");
    const emailField = document.getElementById("email-field");
    if (deliverySelect && emailField) {
        // Set initial state
        emailField.style.display = deliverySelect.value === "email" ? "block" : "none";
        
        // Add change event listener
        deliverySelect.addEventListener("change", function() {
            emailField.style.display = this.value === "email" ? "block" : "none";
        });
    }

    const customersTab = document.getElementById("customers-tab-btn");
    if (customersTab) {
        console.log("customer-management.js: Customers tab button found, adding event listener");
        customersTab.addEventListener("shown.bs.tab", initializeCustomersTab);
        if (customersTab.classList.contains("active")) {
            console.log("customer-management.js: Customers tab is active, initializing immediately");
            initializeCustomersTab();
        }
    } else {
        console.log("customer-management.js: No customers tab button, initializing directly");
        initializeCustomersTab();
    }
});

function initializeCustomersTab() {
    console.log("customer-management.js: Initializing customers tab");
    const searchInput = document.getElementById("customer-search");
    const sortSelect = document.getElementById("customer-sort");
    const typeSelect = document.getElementById("customer-type");
    const exportBtn = document.getElementById("export-customers");
    const prevBtn = document.getElementById("prev-customer-page-btn");
    const nextBtn = document.getElementById("next-customer-page-btn");
    const customersTable = document.getElementById("customers-table");
    const shownCustomers = document.getElementById("shown-customers");
    const totalCustomersCount = document.getElementById("total-customers-count");

    let currentPage = 1;
    let searchQuery = "";
    let sortOption = "recent";
    let customerType = typeSelect ? typeSelect.value : "";

    if (!searchInput || !sortSelect || !exportBtn || !prevBtn || !nextBtn || !customersTable || !shownCustomers || !totalCustomersCount) {
        console.error("customer-management.js: Customer tab elements not found", { searchInput, sortSelect, exportBtn, prevBtn, nextBtn, customersTable, shownCustomers, totalCustomersCount });
        customersTable.innerHTML = `<tr><td colspan="6" class="text-center text-danger">Error: Required elements not found</td></tr>`;
        return;
    }

    function fetchCustomers() {
        console.log("customer-management.js: Fetching customers with params:", { searchQuery, sortOption, customerType, currentPage });
        const params = new URLSearchParams({
            search: searchQuery,
            sort: sortOption,
            customerType: customerType,
            page: currentPage
        });
        customersTable.innerHTML = `<tr><td colspan="6" class="text-center">Loading...</td></tr>`;
        fetch(`/SalonManage/get-customers?${params}`)
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => { throw new Error(err.error || `HTTP error: ${response.status}`); });
                }
                return response.json();
            })
            .then(data => {
                if (!data || !data.customers) {
                    throw new Error("Invalid response data");
                }
                updateCustomersTable(data);
                updatePagination(data);
            })
            .catch(error => {
                console.error("customer-management.js: Error fetching customers:", error);
                customersTable.innerHTML = `<tr><td colspan="6" class="text-center text-danger">Error: ${error.message}</td></tr>`;
            });
    }

    function updateCustomersTable(data) {
        console.log("customer-management.js: Updating customers table with data:", data);
        customersTable.innerHTML = "";
        if (!data.customers || data.customers.length === 0) {
            customersTable.innerHTML = `<tr><td colspan="6" class="text-center">No customers found</td></tr>`;
            return;
        }

        data.customers.forEach(customer => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${customer.firstName} ${customer.lastName}</td>
                <td>${customer.email || customer.phone || "N/A"}</td>
                <td>${customer.lastVisit ? new Date(customer.lastVisit).toLocaleDateString('en-IN') : "N/A"}</td>
                <td>${customer.totalVisits || 0}</td>
                <td>₹${customer.totalSpent ? customer.totalSpent.toFixed(2) : "0.00"}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary view-customer" data-id="${customer.userId}">
                        <i class="fas fa-eye"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-warning edit-customer" data-id="${customer.userId}">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger delete-customer" data-id="${customer.userId}">
                        <i class="fas fa-trash"></i>
                    </button>
                </td>
            `;
            customersTable.appendChild(row);
        });

        document.querySelectorAll(".view-customer").forEach(btn => {
            btn.addEventListener("click", () => {
                const userId = btn.dataset.id;
                fetch(`/SalonManage/customer-actions?action=view&userId=${userId}`)
                    .then(response => {
                        if (!response.ok) {
                            return response.json().then(err => { throw new Error(err.error || `HTTP error: ${response.status}`); });
                        }
                        return response.json();
                    })
                    .then(data => {
                        if (data.success) {
                            const customer = data.customer;
                            document.getElementById("viewName").textContent = `${customer.firstName} ${customer.lastName}`;
                            document.getElementById("viewEmail").textContent = customer.email || "N/A";
                            document.getElementById("viewPhone").textContent = customer.phone || "N/A";
                            document.getElementById("viewLastVisit").textContent = customer.lastVisit ? new Date(customer.lastVisit).toLocaleDateString('en-IN') : "N/A";
                            document.getElementById("viewTotalVisits").textContent = customer.totalVisits || 0;
                            document.getElementById("viewTotalSpent").textContent = customer.totalSpent ? `₹${customer.totalSpent.toFixed(2)}` : "₹0.00";
                            
                            const historyTable = document.getElementById("appointmentHistory");
                            historyTable.innerHTML = "";
                            if (data.appointments && data.appointments.length > 0) {
                                data.appointments.forEach(apt => {
                                    console.log("customer-management.js: Appointment data:", apt);
                                    let dateTimeStr = "N/A";
                                    if (apt.appointmentDate) {
                                        try {
                                            const [year, month, day] = apt.appointmentDate.split('-');
                                            const date = new Date(year, month - 1, day);
                                            if (isNaN(date.getTime())) {
                                                throw new Error("Invalid date");
                                            }
                                            dateTimeStr = date.toLocaleDateString('en-IN');
                                            if (apt.appointmentTime) {
                                                dateTimeStr += ` ${apt.appointmentTime}`;
                                            }
                                        } catch (e) {
                                            console.error("customer-management.js: Date parse error:", apt.appointmentDate, e);
                                            dateTimeStr = "Invalid Date";
                                        }
                                    }
                                    const row = document.createElement("tr");
                                    row.innerHTML = `
                                        <td>${dateTimeStr}</td>
                                        <td>${apt.service || "N/A"}</td>
                                        <td>${apt.amount ? `₹${parseFloat(apt.amount).toFixed(2)}` : "N/A"}</td>
                                    `;
                                    historyTable.appendChild(row);
                                });
                            } else {
                                historyTable.innerHTML = `<tr><td colspan="3" class="text-center">No appointments</td></tr>`;
                            }
                            new bootstrap.Modal(document.getElementById("viewCustomerModal")).show();
                        } else {
                            showModal("Error: " + data.error);
                        }
                    })
                    .catch(error => {
                        showModal("Failed to fetch customer: " + error.message);
                    });
            });
        });

        document.querySelectorAll(".edit-customer").forEach(btn => {
            btn.addEventListener("click", () => {
                const userId = btn.dataset.id;
                fetch(`/SalonManage/customer-actions?action=view&userId=${userId}`)
                    .then(response => {
                        if (!response.ok) {
                            return response.json().then(err => { throw new Error(err.error || `HTTP error: ${response.status}`); });
                        }
                        return response.json();
                    })
                    .then(data => {
                        if (data.success) {
                            const customer = data.customer;
                            document.getElementById("editUserId").value = customer.userId;
                            document.getElementById("editFirstName").value = customer.firstName;
                            document.getElementById("editLastName").value = customer.lastName;
                            document.getElementById("editEmail").value = customer.email || "";
                            document.getElementById("editPhone").value = customer.phone || "";
                            new bootstrap.Modal(document.getElementById("editCustomerModal")).show();
                        } else {
                            showModal("Error: " + data.error);
                        }
                    })
                    .catch(error => {
                        showModal("Failed to fetch customer: " + error.message);
                    });
            });
        });

        document.querySelectorAll(".delete-customer").forEach(btn => {
            btn.addEventListener("click", () => {
                const userId = btn.dataset.id;
                showConfirmModal("Are you sure you want to delete this customer?", () => {
                    fetch("/SalonManage/customer-actions", {
                        method: "POST",
                        headers: { "Content-Type": "application/x-www-form-urlencoded" },
                        body: `action=delete&userId=${userId}`
                    })
                        .then(response => {
                            if (!response.ok) {
                                return response.json().then(err => { throw new Error(err.error || `HTTP error: ${response.status}`); });
                            }
                            return response.json();
                        })
                        .then(data => {
                            if (data.success) {
                                showModal("Customer deleted successfully!", () => {
                                    fetchCustomers();
                                });
                            } else {
                                showModal("Error: " + data.error);
                            }
                        })
                        .catch(error => {
                            showModal("Failed to delete customer: " + error.message);
                        });
                });
            });
        });
    }

    function updatePagination(data) {
        console.log("customer-management.js: Updating pagination:", data);
        shownCustomers.textContent = data.customers ? data.customers.length : 0;
        totalCustomersCount.textContent = data.totalCustomers || 0;
        prevBtn.disabled = data.currentPage === 1;
        nextBtn.disabled = data.currentPage * data.pageSize >= data.totalCustomers;
    }

    searchInput.addEventListener("input", () => {
        searchQuery = searchInput.value.trim();
        currentPage = 1;
        console.log("customer-management.js: Search input changed:", searchQuery);
        fetchCustomers();
    });

    sortSelect.addEventListener("change", () => {
        sortOption = sortSelect.value;
        currentPage = 1;
        console.log("customer-management.js: Sort option changed:", sortOption);
        fetchCustomers();
    });

    if (typeSelect) {
        typeSelect.addEventListener("change", () => {
            customerType = typeSelect.value;
            currentPage = 1;
            console.log("customer-management.js: Customer type changed:", customerType);
            fetchCustomers();
        });
    }

    prevBtn.addEventListener("click", () => {
        if (currentPage > 1) {
            currentPage--;
            console.log("customer-management.js: Previous page clicked, new page:", currentPage);
            fetchCustomers();
        }
    });

    nextBtn.addEventListener("click", () => {
        currentPage++;
        console.log("customer-management.js: Next page clicked, new page:", currentPage);
        fetchCustomers();
    });

    exportBtn.addEventListener("click", () => {
        const modal = new bootstrap.Modal(document.getElementById("exportOptionsModal"));
        modal.show();
    });

    const exportForm = document.getElementById("export-options-form");
    if (exportForm) {
        exportForm.addEventListener("submit", (e) => {
            e.preventDefault();
            const formData = new FormData(e.target);
            const columns = formData.getAll("columns");
            const format = formData.get("format");
            const delivery = formData.get("delivery");
            const email = formData.get("email");
            if (delivery === "email" && !email) {
                showModal("Please enter an email address.");
                return;
            }
            if (!columns.length) {
                showModal("Please select at least one column.");
                return;
            }
            const params = new URLSearchParams({
                search: searchQuery,
                sort: sortOption,
                customerType: customerType,
                columns: columns.join(","),
                format: format,
                delivery: delivery
            });
            if (delivery === "email" && email) {
                params.append("email", email);
                fetch(`/SalonManage/export-customers?${params}`)
                    .then(response => {
                        if (!response.ok) {
                            return response.json().then(err => { throw new Error(err.error || `HTTP error: ${response.status}`); });
                        }
                        return response.json();
                    })
                    .then(data => {
                        showModal(data.message || "Report sent successfully!", () => {
                            bootstrap.Modal.getInstance(document.getElementById("exportOptionsModal")).hide();
                        });
                    })
                    .catch(error => {
                        showModal("Failed to send report: " + error.message);
                    });
            } else {
                window.location.href = `/SalonManage/export-customers?${params}`;
            }
        });
    }

    document.getElementById("updateCustomerBtn").addEventListener("click", () => {
        const form = document.getElementById("editCustomerForm");
        const formData = new FormData(form);
        const params = new URLSearchParams();
        formData.forEach((value, key) => params.append(key, value));
        params.append("action", "edit");
        fetch("/SalonManage/customer-actions", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: params
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => { throw new Error(err.error || `HTTP error: ${response.status}`); });
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    showModal("Customer updated successfully!", () => {
                        bootstrap.Modal.getInstance(document.getElementById("editCustomerModal")).hide();
                        fetchCustomers();
                    });
                } else {
                    showModal("Error: " + data.error);
                }
            })
            .catch(error => {
                showModal("Failed to update customer: " + error.message);
            });
    });

    // Expose fetchCustomers for external use (e.g., by add-customer.js)
    window.refreshCustomers = fetchCustomers;

    // Initial fetch
    console.log("customer-management.js: Performing initial customers fetch");
    fetchCustomers();
}