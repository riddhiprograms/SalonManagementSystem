document.addEventListener("DOMContentLoaded", function () {
    console.log("Services script loaded");

    let allServices = [];
    let categories = [];
    const contextPath = "/SalonManage";

    // Format price for display
    const formatPrice = (price) => {
        return `₹${parseFloat(price).toFixed(2)}`;
    };

    // Attach event listeners to filter elements and action buttons
    const attachListeners = () => {
        console.log("Attaching listeners");
        const categoryFilter = document.querySelector("#service-category-filter");
        const statusFilter = document.querySelector("#service-status-filter");
        const searchInput = document.querySelector("#service-search");

        if (categoryFilter) {
            categoryFilter.removeEventListener("change", loadServices);
            categoryFilter.addEventListener("change", loadServices);
        }
        if (statusFilter) {
            statusFilter.removeEventListener("change", loadServices);
            statusFilter.addEventListener("change", loadServices);
        }
        if (searchInput) {
            searchInput.removeEventListener("input", loadServices);
            searchInput.addEventListener("input", loadServices);
        }

        // Attach listeners for edit and delete buttons
        document.querySelectorAll(".edit-service").forEach(button => {
            button.removeEventListener("click", initializePopover);
            button.addEventListener("click", initializePopover);
        });
        document.querySelectorAll(".delete-service").forEach(button => {
            button.removeEventListener("click", handleDelete);
            button.addEventListener("click", handleDelete);
        });
    };

    // Initialize popover for edit button
    const initializePopover = (e) => {
        const button = e.target.closest(".edit-service");
        const serviceId = button.getAttribute("data-id");
        const service = allServices.find(s => s.service_id == serviceId);

        if (!service) {
            console.error("Service not found for ID:", serviceId);
            alert("Service not found");
            return;
        }

        console.log("Service data:", JSON.stringify(service, null, 2));
        console.log("Available categories:", JSON.stringify(categories, null, 2));

        // Validate service.category_name
        if (!service.category_name) {
            console.warn(`Service ID ${serviceId} has no category_name`);
        } else if (!categories.some(c => c.category_name === service.category_name)) {
            console.warn(`Service category_name "${service.category_name}" not found in categories`);
        }

        let categoryOptions = '<option value="" disabled>Select Category</option>';
        categories.forEach(category => {
            const isSelected = service.category_name && service.category_name.trim() === category.category_name.trim() ? 'selected' : '';
            categoryOptions += `<option value="${category.category_name}" ${isSelected}>${category.category_name}</option>`;
        });

        const popoverContent = `
            <form class="edit-service-form" data-id="${serviceId}">
                <input type="hidden" name="serviceId" value="${serviceId}">
                <input type="text" class="form-control mb-1" name="name" value="${service.name || ''}" required>
                <select class="form-select mb-1" name="categoryName" required>
                    ${categoryOptions}
                </select>
                <input type="number" class="form-control mb-1" name="duration" value="${service.duration || ''}" required min="1">
                <input type="number" class="form-control mb-1" name="price" value="${parseFloat(service.price) || ''}" required min="0" step="0.01">
                <textarea class="form-control mb-1" name="description" required>${service.description || ''}</textarea>
                <select class="form-select mb-1" name="status" required>
                    <option value="active" ${service.status === 'active' ? 'selected' : ''}>Active</option>
                    <option value="inactive" ${service.status === 'inactive' ? 'selected' : ''}>Inactive</option>
                </select>
                <button type="submit" class="btn btn-sm btn-primary">Save</button>
                <button type="button" class="btn btn-sm btn-secondary cancel-popover">Cancel</button>
            </form>
        `;

        console.log("Generated popover content:", popoverContent);

        // Destroy any existing popover
        const existingPopover = bootstrap.Popover.getInstance(button);
        if (existingPopover) {
            existingPopover.dispose();
        }

        new bootstrap.Popover(button, {
            html: true,
            content: popoverContent,
            trigger: 'click',
            placement: 'top',
            sanitize: false,
            container: 'body'
        }).show();

        button.dataset.popoverButton = 'edit';
    };

    // Handle delete button click
    const handleDelete = (e) => {
        const button = e.target.closest(".delete-service");
        const serviceId = button.getAttribute("data-id");
        if (confirm("Delete this service?")) {
            fetch(`${contextPath}/delete-service`, {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: `serviceId=${encodeURIComponent(serviceId)}`
            })
                .then(response => {
                    if (!response.ok) throw new Error(`Failed to delete service: ${response.status}`);
                    return response.json();
                })
                .then(data => {
                    console.log("Delete response:", data);
                    if (data.success) {
                        loadServices();
                    } else {
                        alert(`Failed to delete service: ${data.message}`);
                    }
                })
                .catch(error => {
                    console.error("Error deleting service:", error);
                    alert(`Failed to delete service: ${error.message}`);
                });
        }
    };

    // Handle edit form submission
    document.addEventListener("submit", (e) => {
        if (e.target.classList.contains("edit-service-form")) {
            e.preventDefault();
            const form = e.target;
            const serviceId = form.querySelector('input[name="serviceId"]').value;

            // Collect form data
            const formData = {};
            form.querySelectorAll('input, select, textarea').forEach(input => {
                if (input.name) formData[input.name] = input.value.trim();
            });
            console.log("Raw form inputs:", {
                serviceId: form.querySelector('input[name="serviceId"]').value,
                name: form.querySelector('input[name="name"]').value,
                categoryName: form.querySelector('select[name="categoryName"]').value,
                duration: form.querySelector('input[name="duration"]').value,
                price: form.querySelector('input[name="price"]').value,
                description: form.querySelector('textarea[name="description"]').value,
                status: form.querySelector('select[name="status"]').value
            });
            console.log("Form data to send:", JSON.stringify(formData, null, 2));

            // Validate required fields
            if (!formData.serviceId || !formData.name || !formData.categoryName || 
                !formData.duration || !formData.price || !formData.description || !formData.status) {
                console.error("Validation failed: Missing required fields", formData);
                alert("All fields are required");
                return;
            }

            // Validate numeric fields
            if (isNaN(formData.serviceId)) {
                console.error("Validation failed: Invalid serviceId", formData.serviceId);
                alert("Invalid service ID");
                return;
            }
            if (isNaN(formData.duration) || formData.duration < 1) {
                console.error("Validation failed: Invalid duration", formData.duration);
                alert("Duration must be a positive number");
                return;
            }
            if (isNaN(formData.price) || formData.price < 0) {
                console.error("Validation failed: Invalid price", formData.price);
                alert("Price must be a valid non-negative number");
                return;
            }

            // Validate categoryName
            if (formData.categoryName === "" || !categories.some(c => c.category_name.trim() === formData.categoryName)) {
                console.error("Validation failed: Invalid categoryName", formData.categoryName);
                alert("Please select a valid category");
                return;
            }

            const urlEncodedData = new URLSearchParams(formData).toString();
            console.log("Sending update request with body:", urlEncodedData);

            const button = document.querySelector(`.edit-service[data-id="${serviceId}"]`);
            fetch(`${contextPath}/update-service`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: urlEncodedData
            })
                .then(response => {
                    console.log("Raw response status:", response.status);
                    return response.text().then(text => {
                        let data;
                        try {
                            data = JSON.parse(text);
                        } catch (e) {
                            throw new Error(`Invalid JSON response: ${text}`);
                        }
                        if (!response.ok) {
                            throw new Error(data.message || `HTTP error ${response.status}`);
                        }
                        return data;
                    });
                })
                .then(data => {
                    console.log("Update response:", JSON.stringify(data, null, 2));
                    if (data.success) {
                        hidePopover(button, form);
                        loadServices();
                    } else {
                        alert(data.message);
                    }
                })
                .catch(error => {
                    console.error("Error updating service:", error.message);
                    alert(error.message);
                    hidePopover(button, form);
                });
        }
    });

    // Handle add service form submission
    document.addEventListener("submit", (e) => {
    if (e.target.matches("#add-service-form")) {
        e.preventDefault();
        const form = e.target;
        const nameInput = form.querySelector('input[name="name"]');
        const categoryInput = form.querySelector('select[name="categoryName"]');
        const durationInput = form.querySelector('input[name="duration"]');
        const priceInput = form.querySelector('input[name="price"]');
        const descriptionInput = form.querySelector('textarea[name="description"]');
        const statusInput = form.querySelector('input[name="status"]');

        const payload = {
            name: nameInput ? nameInput.value.trim() : '',
            categoryName: categoryInput ? categoryInput.value.trim() : '',
            duration: durationInput ? parseInt(durationInput.value.trim(), 10) : null,
            price: priceInput ? parseFloat(priceInput.value.trim()) : null,
            description: descriptionInput ? descriptionInput.value.trim() : '',
            status: statusInput && statusInput.checked ? 'active' : 'inactive'
        };

        console.log("Add service payload:", JSON.stringify(payload, null, 2));

        // Validate required fields
        if (!payload.name) {
            console.error("Validation failed: Service name is missing or empty");
            alert("Service name is required");
            return;
        }
        if (!payload.categoryName) {
            console.error("Validation failed: Category is missing");
            alert("Category is required");
            return;
        }
        if (!payload.duration || isNaN(payload.duration)) {
            console.error("Validation failed: Duration is missing or invalid");
            alert("Duration is required and must be a number");
            return;
        }
        if (!payload.price || isNaN(payload.price)) {
            console.error("Validation failed: Price is missing or invalid");
            alert("Price is required and must be a number");
            return;
        }
        if (!payload.description) {
            console.error("Validation failed: Description is missing");
            alert("Description is required");
            return;
        }

        // Validate numeric fields
        if (payload.duration < 15) {
            console.error("Validation failed: Invalid duration", payload.duration);
            alert("Duration must be at least 15 minutes");
            return;
        }
        if (payload.price < 0) {
            console.error("Validation failed: Invalid price", payload.price);
            alert("Price must be a non-negative number");
            return;
        }

        // Validate categoryName
        if (!categories.some(c => c.category_name.trim() === payload.categoryName)) {
            console.error("Validation failed: Invalid categoryName", payload.categoryName);
            alert("Please select a valid category");
            return;
        }

        fetch(`${contextPath}/saveService`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        })
            .then(response => {
                console.log("Save service response status:", response.status);
                return response.json().catch(err => {
                    throw new Error(`Invalid JSON response: ${err.message}`);
                });
            })
            .then(data => {
                console.log("Save service response:", JSON.stringify(data, null, 2));
                if (data.success) {
                    const modal = bootstrap.Modal.getInstance(document.querySelector("#addServiceModal"));
                    modal.hide();
                    form.reset();
                    loadServices();
                } else {
                    alert(data.message);
                }
            })
            .catch(error => {
                console.error("Error saving service:", error.message);
                alert(`Failed to save service: ${error.message}`);
            });
    }
});

// Handle add category form submission
// Global flag to prevent concurrent submissions
let isSubmittingCategory = false;

document.addEventListener("submit", (e) => {
    if (e.target.matches("#add-category-form")) {
        e.preventDefault(); // Prevent native form submission
        if (isSubmittingCategory) {
            console.log("Submission blocked: already in progress");
            return;
        }

        const form = e.target;
        const categoryNameInput = form.querySelector('input[name="categoryName"]');
        const saveButton = form.querySelector('button[type="submit"]');

        const payload = {
            categoryName: categoryNameInput ? categoryNameInput.value.trim() : ''
        };

        console.log("Add category form submitted:", new Date().toISOString(), JSON.stringify(payload, null, 2));

        // Validate required field
        if (!payload.categoryName) {
            console.error("Validation failed: Category name is missing or empty");
            alert("Category name is required");
            return;
        }

        // Disable button to prevent double-clicks
        saveButton.disabled = true;
        isSubmittingCategory = true;

        fetch(`${contextPath}/saveCategory`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        })
            .then(response => {
                console.log("Save category response status:", response.status);
                return response.json().catch(err => {
                    throw new Error(`Invalid JSON response: ${err.message}`);
                });
            })
            .then(data => {
                console.log("Save category response:", JSON.stringify(data, null, 2));
                if (data.success) {
                    const modal = bootstrap.Modal.getInstance(document.querySelector("#addCategoryModal"));
                    modal.hide();
                    form.reset();

                    // Add new category to categories array if not already present
                    if (!categories.some(c => c.category_name === payload.categoryName)) {
                        categories.push({ category_name: payload.categoryName });
                    }

                    // Update category dropdown
                    const serviceCategorySelect = document.querySelector("#service-category");
                    if (serviceCategorySelect) {
                        serviceCategorySelect.innerHTML = '<option value="" disabled selected>Select Category</option>';
                        categories.forEach(category => {
                            const isSelected = category.category_name === payload.categoryName ? 'selected' : '';
                            serviceCategorySelect.innerHTML += `
                                <option value="${category.category_name}" ${isSelected}>${category.category_name}</option>`;
                        });
                    }

                    // Update category filter (if applicable)
                    const categoryFilter = document.querySelector("#service-category-filter");
                    if (categoryFilter) {
                        categoryFilter.innerHTML = '<option value="">All Categories</option>';
                        categories.forEach(category => {
                            categoryFilter.innerHTML += `
                                <option value="${category.category_name}">${category.category_name}</option>`;
                        });
                    }
                } else {
                    alert(data.message);
                }
            })
            .catch(error => {
                console.error("Error saving category:", error.message);
                alert(`Failed to save category: ${error.message}`);
            })
            .finally(() => {
                // Re-enable button and reset flag
                saveButton.disabled = false;
                isSubmittingCategory = false;
            });
    }
});

    // Hide popover
    const hidePopover = (button, form) => {
        if (!button) {
            console.warn("Popover button not provided");
            return;
        }
        const popoverInstance = bootstrap.Popover.getInstance(button);
        if (popoverInstance) {
            popoverInstance.dispose();
        }
        const popoverElement = form.closest('.popover');
        if (popoverElement) {
            popoverElement.remove();
        }
    };

    // Handle cancel button in popover
    document.addEventListener("click", (e) => {
        if (e.target.classList.contains("cancel-popover")) {
            const form = e.target.closest('form');
            const serviceId = form.querySelector('input[name="serviceId"]').value;
            const button = document.querySelector(`.edit-service[data-id="${serviceId}"]`);
            if (button) {
                hidePopover(button, form);
            }
        }
    });

    // Fetch and populate categories
    const loadCategories = () => {
        console.log("Fetching categories from:", `${contextPath}/categories`);
        fetch(`${contextPath}/categories`, {
            method: "GET",
            headers: { "Content-Type": "application/json" }
        })
            .then(response => {
                if (!response.ok) throw new Error(`Failed to fetch categories: ${response.status}`);
                return response.json();
            })
            .then(data => {
                console.log("Categories response:", JSON.stringify(data, null, 2));
                categories = data.categories || [];
                const categoryFilter = document.querySelector("#service-category-filter");
                const serviceCategorySelect = document.querySelector("#service-category");

                if (categoryFilter) {
                    categoryFilter.innerHTML = '<option value="">All Categories</option>';
                    categories.forEach(category => {
                        categoryFilter.innerHTML += `
                            <option value="${category.category_name}">${category.category_name}</option>`;
                    });
                } else {
                    console.error("Category filter select not found");
                }

                if (serviceCategorySelect) {
                    serviceCategorySelect.innerHTML = '<option value="" disabled selected>Select Category</option>';
                    categories.forEach(category => {
                        serviceCategorySelect.innerHTML += `
                            <option value="${category.category_name}">${category.category_name}</option>`;
                    });
                } else {
                    console.error("Service category select not found");
                }
            })
            .catch(error => {
                console.error("Error fetching categories:", error);
                const categoryFilter = document.querySelector("#service-category-filter");
                if (categoryFilter) {
                    categoryFilter.innerHTML = '<option value="" disabled>Failed to load categories</option>';
                }
                const serviceCategorySelect = document.querySelector("#service-category");
                if (serviceCategorySelect) {
                    serviceCategorySelect.innerHTML = '<option value="" disabled>Failed to load categories</option>';
                }
                alert(`Failed to load categories: ${error.message}`);
            });
    };

    // Fetch and render services
    const loadServices = () => {
        const categoryFilter = document.querySelector("#service-category-filter");
        const statusFilter = document.querySelector("#service-status-filter");
        const searchInput = document.querySelector("#service-search");
        const servicesTable = document.querySelector("#services-table");

        if (!servicesTable) {
            console.error("Services table not found");
            return;
        }

        const category = categoryFilter ? categoryFilter.value : "";
        const status = statusFilter ? statusFilter.value : "";
        const search = searchInput ? searchInput.value.trim() : "";

        console.log("Fetching services with params:", { category, status, search });

        servicesTable.innerHTML = `
            <tr>
                <td colspan="6" class="text-center">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                </td>
            </tr>`;

        const queryParams = new URLSearchParams({ category, status, search });

        fetch(`${contextPath}/services/admin?${queryParams}`, {
            method: "GET",
            headers: { "Content-Type": "application/json" }
        })
            .then(response => {
                if (!response.ok) throw new Error(`Failed to fetch services: ${response.status}`);
                return response.json();
            })
            .then(data => {
                console.log("Services response:", JSON.stringify(data, null, 2));
                servicesTable.innerHTML = "";
                if (!data.success || !data.services || data.services.length === 0) {
                    servicesTable.innerHTML = `
                        <tr>
                            <td colspan="6" class="text-center">No services found.</td>
                        </tr>`;
                    return;
                }

                allServices = data.services;
                renderServices(allServices);
                attachListeners();
            })
            .catch(error => {
                console.error("Error fetching services:", error);
                servicesTable.innerHTML = `
                    <tr>
                        <td colspan="6" class="text-center text-danger">Error loading services: ${error.message}</td>
                    </tr>`;
                setTimeout(() => loadServices(), 5000);
            });
    };

    // Render services to table
    const renderServices = (services) => {
        console.log("Rendering services:", services);
        const servicesTable = document.querySelector("#services-table");
        if (!servicesTable) {
            console.error("Services table not found during render");
            return;
        }

        servicesTable.innerHTML = "";
        if (services.length === 0) {
            servicesTable.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center">No services found.</td>
                </tr>`;
            return;
        }

        services.forEach(service => {
            const statusBadge = service.status === "active"
                ? '<span class="badge bg-success">Active</span>'
                : '<span class="badge bg-danger">Inactive</span>';
            servicesTable.innerHTML += `
                <tr>
                    <td>${service.name}</td>
                    <td>${service.category_name}</td>
                    <td>${service.duration} mins</td>
                    <td>${formatPrice(service.price)}</td>
                    <td>${statusBadge}</td>
                    <td>
                        <button class="btn btn-sm btn-outline-primary edit-service" data-id="${service.service_id}">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-danger delete-service" data-id="${service.service_id}">
                            <i class="fas fa-trash"></i>
                        </button>
                    </td>
                </tr>`;
        });
    };

    // Initialize
    console.log("Initializing services page");
    loadCategories();
    loadServices();
    attachListeners();
});