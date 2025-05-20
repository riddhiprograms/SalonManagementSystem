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

function showConfirmModal(message, callback) {
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

  // Create button container
  const buttonContainer = document.createElement("div");
  buttonContainer.style.display = "flex";
  buttonContainer.style.justifyContent = "center";
  buttonContainer.style.gap = "1rem";

  // Create confirm button
  const confirmButton = document.createElement("button");
  confirmButton.textContent = "Yes";
  confirmButton.style.backgroundColor = "var(--primary-color)"; // #D4AF37
  confirmButton.style.border = "1px solid var(--primary-color)"; // #D4AF37
  confirmButton.style.color = "white";
  confirmButton.style.padding = "0.5rem 1.5rem"; // Matches button padding
  confirmButton.style.borderRadius = "var(--border-radius)"; // 0.25rem
  confirmButton.style.fontSize = "0.9rem";
  confirmButton.style.fontWeight = "600";
  confirmButton.style.textTransform = "uppercase";
  confirmButton.style.letterSpacing = "0.5px";
  confirmButton.style.cursor = "pointer";
  confirmButton.style.transition = "background-color 0.3s ease, border-color 0.3s ease"; // Matches --transition-speed
  confirmButton.onmouseover = () => {
    confirmButton.style.backgroundColor = "var(--primary-dark)"; // #A38829
    confirmButton.style.borderColor = "var(--primary-dark)"; // #A38829
  };
  confirmButton.onmouseout = () => {
    confirmButton.style.backgroundColor = "var(--primary-color)"; // #D4AF37
    confirmButton.style.borderColor = "var(--primary-color)"; // #D4AF37
  };
  confirmButton.onclick = () => {
    modal.style.opacity = "0";
    setTimeout(() => {
      document.body.removeChild(modal);
      if (callback) callback(true);
    }, 300); // Matches --transition-speed
  };
  buttonContainer.appendChild(confirmButton);

  // Create cancel button
  const cancelButton = document.createElement("button");
  cancelButton.textContent = "No";
  cancelButton.style.backgroundColor = "var(--secondary-color)"; // #2C3E50
  cancelButton.style.border = "1px solid var(--secondary-color)"; // #2C3E50
  cancelButton.style.color = "white";
  cancelButton.style.padding = "0.5rem 1.5rem"; // Matches button padding
  cancelButton.style.borderRadius = "var(--border-radius)"; // 0.25rem
  cancelButton.style.fontSize = "0.9rem";
  cancelButton.style.fontWeight = "600";
  cancelButton.style.textTransform = "uppercase";
  cancelButton.style.letterSpacing = "0.5px";
  cancelButton.style.cursor = "pointer";
  cancelButton.style.transition = "background-color 0.3s ease, border-color 0.3s ease"; // Matches --transition-speed
  cancelButton.onmouseover = () => {
    cancelButton.style.backgroundColor = "var(--secondary-dark)"; // Darker shade, e.g., #1A252F
    cancelButton.style.borderColor = "var(--secondary-dark)"; // #1A252F
  };
  cancelButton.onmouseout = () => {
    cancelButton.style.backgroundColor = "var(--secondary-color)"; // #2C3E50
    cancelButton.style.borderColor = "var(--secondary-color)"; // #2C3E50
  };
  cancelButton.onclick = () => {
    modal.style.opacity = "0";
    setTimeout(() => {
      document.body.removeChild(modal);
      if (callback) callback(false);
    }, 300); // Matches --transition-speed
  };
  buttonContainer.appendChild(cancelButton);

  // Append button container to modal content
  modalContent.appendChild(buttonContainer);

  // Append modal content to modal container
  modal.appendChild(modalContent);

  // Append modal to the document body
  document.body.appendChild(modal);
}

const contextPath = "/SalonManage";
const pageSize = 10;
let currentPage = 1;
let currentCategory = "";
let currentStock = "";
let currentSearch = "";
let currentSort = "name";

document.addEventListener("DOMContentLoaded", () => {
    console.log("Loading inventory.js, contextPath:", contextPath);
    loadCategories(); // Load categories for dropdowns
    loadProducts();
    loadLowStockProducts();
    // Category filter
    document.getElementById("inventory-category-filter").addEventListener("change", (e) => {
        currentCategory = e.target.value;
        currentPage = 1;
        loadProducts();
    });

    // Stock status filter
    document.getElementById("inventory-stock-filter").addEventListener("change", (e) => {
        currentStock = e.target.value;
        currentPage = 1;
        loadProducts();
    });

    // Sort select
    document.getElementById("inventory-sort").addEventListener("change", (e) => {
        currentSort = e.target.value;
        currentPage = 1;
        loadProducts();
    });

    // Search input (with debounce)
    document.getElementById("inventory-search").addEventListener("input", debounce((e) => {
        currentSearch = e.target.value;
        currentPage = 1;
        loadProducts();
    }, 300));

    // Edit and Delete button listeners
    document.getElementById("inventory-table").addEventListener("click", (e) => {
        if (e.target.classList.contains("edit-product")) {
            const productId = e.target.dataset.id;
            const product = productsCache.find(p => p.id == productId);
            if (product) {
                populateEditModal(product);
            } else {
                console.error("Product not found in cache for ID:", productId);
                showModal("Error: Product not found");
            }
        } else if (e.target.classList.contains("delete-product")) {
            const productId = e.target.dataset.id;
            showConfirmModal(`Are you sure you want to delete this product (ID: ${productId})?`, (confirmed) => {
                if (confirmed) {
                    deleteProduct(productId);
                }
            });
        }
    });

    // Edit form submission
    document.getElementById("edit-product-form").addEventListener("submit", (e) => {
        e.preventDefault();
        if (validateEditForm()) {
            updateProduct();
        }
    });

    // Add form submission
    document.getElementById("save-inventory-btn").addEventListener("click", (e) => {
        e.preventDefault();
        if (validateAddForm()) {
            addProduct();
        }
    });
});

// Cache products and categories
let productsCache = [];
let categoriesCache = [];

// Debounce function
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

function loadLowStockProducts() {
    fetch(`${contextPath}/lowStock`)
        .then(response => {
            if (!response.ok) {
                return response.json().then(errorData => {
                    throw new Error(errorData.error || `HTTP error! status: ${response.status}`);
                });
            }
            return response.json();
        })
        .then(products => {
            console.log("Low stock products:", products);
            const alertDiv = document.getElementById("low-stock-alert");
            const list = document.getElementById("low-stock-list");
            list.innerHTML = "";
            if (products.length > 0) {
                alertDiv.classList.remove("d-none");
                products.forEach(product => {
                    list.innerHTML += `<li>${product.name} (${product.category}): ${product.stockQuantity} units (Reorder Level: ${product.reorderLevel})</li>`;
                });
            } else {
                alertDiv.classList.add("d-none");
            }
        })
        .catch(error => {
            console.error("Error loading low stock products:", error);
        });
}

// Load categories
function loadCategories() {
    fetch(`${contextPath}/product-categories`)
        .then(response => {
            if (!response.ok) {
                return response.json().then(errorData => {
                    throw new Error(errorData.error || `HTTP error! status: ${response.status}`);
                });
            }
            return response.json();
        })
        .then(categories => {
            console.log("Received categories:", categories);
            categoriesCache = categories;
            populateCategoryDropdowns();
        })
        .catch(error => {
            console.error("Error loading categories:", error);
            showModal("Error loading categories: " + error.message);
        });
}

// Populate category dropdowns
function populateCategoryDropdowns() {
    const addDropdown = document.getElementById("product-category");
    const editDropdown = document.getElementById("edit-product-category");
    const filterDropdown = document.getElementById("inventory-category-filter");

    // Clear existing options
    addDropdown.innerHTML = '<option value="">Select Category</option>';
    editDropdown.innerHTML = '<option value="">Select Category</option>';
    filterDropdown.innerHTML = '<option value="">All Categories</option>';

    // Populate options
    categoriesCache.forEach(category => {
        const option = `<option value="${category.name}">${category.name}</option>`;
        addDropdown.insertAdjacentHTML("beforeend", option);
        editDropdown.insertAdjacentHTML("beforeend", option);
        filterDropdown.insertAdjacentHTML("beforeend", option);
    });
}

// Load products
function loadProducts() {
    const queryParams = new URLSearchParams({
        page: currentPage,
        pageSize: pageSize,
        category: currentCategory,
        stock: currentStock,
        search: currentSearch,
        sort: currentSort
    });
    const url = `${contextPath}/inventory?${queryParams}`;
    console.log("Fetching products from:", url);

    document.getElementById("inventory-table").innerHTML = `
        <tr>
            <td colspan="6" class="text-center">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
            </td>
        </tr>`;

    fetch(url)
        .then(response => {
            console.log("Response status:", response.status);
            if (!response.ok) {
                return response.json().then(errorData => {
                    throw new Error(errorData.error || `HTTP error! status: ${response.status}`);
                });
            }
            return response.json();
        })
        .then(data => {
            console.log("Received product data:", JSON.stringify(data, null, 2));
            productsCache = data.products;
            renderProducts(data.products, data.totalProducts, data.currentPage);
            loadLowStockProducts();
        })
        .catch(error => {
            console.error("Error loading products:", error);
            document.getElementById("inventory-table").innerHTML = `
                <tr><td colspan="6" class="text-center text-danger">Error loading products: ${error.message}</td></tr>
            `;
        });
}

// Render products in table
function renderProducts(products, totalProducts, currentPage) {
    const tbody = document.getElementById("inventory-table");
    tbody.innerHTML = "";

    if (!products || products.length === 0) {
        tbody.innerHTML = `<tr><td colspan="6" class="text-center">No products found</td></tr>`;
        return;
    }

    products.forEach(product => {
        const statusBadge = product.status === "In Stock" ? "bg-success" :
                           product.status === "Low Stock" ? "bg-warning" :
                           "bg-danger";
        const price = product.price || 0;
        const formattedPrice = isNaN(price) ? "₹0.00" : `₹${parseFloat(price).toFixed(2)}`;
        const row = `
            <tr>
                <td>${product.name || '-'}</td>
                <td>${product.category || '-'}</td>
                <td>${product.stockQuantity ?? 0}</td>
                <td>${formattedPrice}</td>
                <td><span class="badge ${statusBadge}">${product.status || 'Unknown'}</span></td>
                <td>
                    <button class="btn btn-sm btn-outline-primary me-2 edit-product" data-id="${product.id || ''}">Edit</button>
                    <button class="btn btn-sm btn-outline-danger delete-product" data-id="${product.id || ''}">Delete</button>
                </td>
            </tr>
        `;
        tbody.insertAdjacentHTML("beforeend", row);
    });

    updatePagination(totalProducts || 0, currentPage || 1);
}

// Update pagination controls
function updatePagination(totalProducts, currentPage) {
    const prevBtn = document.getElementById("prev-product-page-btn");
    const nextBtn = document.getElementById("next-product-page-btn");
    prevBtn.disabled = currentPage === 1;
    nextBtn.disabled = currentPage * pageSize >= totalProducts;

    document.getElementById("shown-products").textContent = Math.min(pageSize, totalProducts - (currentPage - 1) * pageSize);
   document.getElementById("total-products-count").textContent = totalProducts;

    const prevBtnClone = prevBtn.cloneNode(true);
    const nextBtnClone = nextBtn.cloneNode(true);
    prevBtn.parentNode.replaceChild(prevBtnClone, prevBtn);
    nextBtn.parentNode.replaceChild(nextBtnClone, nextBtn);

    prevBtnClone.addEventListener("click", () => {
        if (currentPage > 1) {
            currentPage--;
            loadProducts();
        }
    });

    nextBtnClone.addEventListener("click", () => {
        currentPage++;
        loadProducts();
    });
}

// Populate edit modal
function populateEditModal(product) {
    console.log("Populating edit modal for product:", product);
    if (!product.id) {
        console.error("Product ID is missing:", product);
        showModal("Error: Invalid product ID");
        return;
    }

    document.getElementById("edit-product-id").value = product.id;
    document.getElementById("edit-product-name").value = product.name || '';
    document.getElementById("edit-product-category").value = product.category || '';
    document.getElementById("edit-product-stock").value = product.stockQuantity ?? 0;
    document.getElementById("edit-product-price").value = product.price != null ? product.price : '0.00';
    document.getElementById("edit-product-reorder").value = product.minStockLevel ?? 0;
    document.getElementById("edit-product-brand").value = product.brand || '';
    document.getElementById("edit-product-supplier").value = product.supplier || '';
    document.getElementById("edit-product-expiry").value = product.expiryDate || '';
    document.getElementById("edit-product-description").value = product.description || '';

    const modal = new bootstrap.Modal(document.getElementById("editInventoryModal"));
    modal.show();
}

// Validate edit form
function validateEditForm() {
    const form = document.getElementById("edit-product-form");
    const id = form.querySelector("#edit-product-id").value;
    const name = form.querySelector("#edit-product-name").value;
    const category = form.querySelector("#edit-product-category").value;
    const stockQuantity = form.querySelector("#edit-product-stock").value;
    const price = form.querySelector("#edit-product-price").value;
    const reorderLevel = form.querySelector("#edit-product-reorder").value;

    if (!id || isNaN(id) || parseInt(id) <= 0) {
        showModal("Valid Product ID is required");
        return false;
    }
    if (!name.trim()) {
        showModal("Product name is required");
        return false;
    }
    if (!category) {
        showModal("Category is required");
        return false;
    }
    if (stockQuantity === '' || isNaN(stockQuantity) || parseInt(stockQuantity) < 0) {
        showModal("Valid stock quantity is required");
        return false;
    }
    if (price === '' || isNaN(price) || parseFloat(price) < 0) {
        showModal("Valid price is required");
        return false;
    }
    if (reorderLevel === '' || isNaN(reorderLevel) || parseInt(reorderLevel) < 0) {
        showModal("Valid reorder level is required");
        return false;
    }
    return true;
}

// Update product
function updateProduct() {
    const form = document.getElementById("edit-product-form");
    const productData = {
        id: parseInt(form.querySelector("#edit-product-id").value),
        name: form.querySelector("#edit-product-name").value,
        category: form.querySelector("#edit-product-category").value,
        stockQuantity: parseInt(form.querySelector("#edit-product-stock").value),
        price: parseFloat(form.querySelector("#edit-product-price").value),
        reorderLevel: parseInt(form.querySelector("#edit-product-reorder").value),
        brand: form.querySelector("#edit-product-brand").value || null,
        supplier: form.querySelector("#edit-product-supplier").value || null,
        expiryDate: form.querySelector("#edit-product-expiry").value || null,
        description: form.querySelector("#edit-product-description").value || null
    };

    console.log("Sending update request with JSON:", JSON.stringify(productData, null, 2));

    fetch(`${contextPath}/editProduct`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(productData)
    })
    .then(response => {
        console.log("Update response status:", response.status);
        if (!response.ok) {
            return response.json().then(errorData => {
                throw new Error(errorData.error || `HTTP error! status: ${response.status}`);
            });
        }
        return response.json();
    })
    .then(data => {
        console.log("Update response:", data);
        showModal(data.message || "Product updated successfully");
        const modal = bootstrap.Modal.getInstance(document.getElementById("editInventoryModal"));
        modal.hide();
        loadProducts();
    })
    .catch(error => {
        console.error("Error updating product:", error);
        showModal("Error updating product: " + error.message);
    });
}

// Validate add form
function validateAddForm() {
    const form = document.getElementById("add-inventory-form");
    const name = form.querySelector("#product-name").value;
    const category = form.querySelector("#product-category").value;
    const stockQuantity = form.querySelector("#product-stock").value;
    const price = form.querySelector("#product-price").value;
    const reorderLevel = form.querySelector("#product-min-stock").value;

    if (!name.trim()) {
        showModal("Product name is required");
        return false;
    }
    if (!category) {
        showModal("Category is required");
        return false;
    }
    if (stockQuantity === '' || isNaN(stockQuantity) || parseInt(stockQuantity) < 0) {
        showModal("Valid stock quantity is required");
        return false;
    }
    if (price === '' || isNaN(price) || parseFloat(price) < 0) {
        showModal("Valid price is required");
        return false;
    }
    if (reorderLevel === '' || isNaN(reorderLevel) || parseInt(reorderLevel) < 0) {
        showModal("Valid reorder level is required");
        return false;
    }
    return true;
}

// Add product
function addProduct() {
    const form = document.getElementById("add-inventory-form");
    const productData = {
        name: form.querySelector("#product-name").value,
        category: form.querySelector("#product-category").value,
        stockQuantity: parseInt(form.querySelector("#product-stock").value),
        price: parseFloat(form.querySelector("#product-price").value),
        reorderLevel: parseInt(form.querySelector("#product-min-stock").value),
        brand: form.querySelector("#product-brand").value || null,
        supplier: form.querySelector("#product-supplier").value || null,
        expiryDate: form.querySelector("#product-expiry").value || null,
        description: form.querySelector("#product-description").value || null
    };

    console.log("Sending add request with JSON:", JSON.stringify(productData, null, 2));

    fetch(`${contextPath}/addProduct`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(productData)
    })
    .then(response => {
        console.log("Add response status:", response.status);
        if (!response.ok) {
            return response.json().then(errorData => {
                throw new Error(errorData.error || `HTTP error! status: ${response.status}`);
            });
        }
        return response.json();
    })
    .then(data => {
        console.log("Add response:", data);
        showModal(data.message || "Product added successfully");
        const modal = bootstrap.Modal.getInstance(document.getElementById("addInventoryModal"));
        modal.hide();
        form.reset();
        loadProducts();
    })
    .catch(error => {
        console.error("Error adding product:", error);
        showModal("Error adding product: " + error.message);
    });
}

// Delete product
function deleteProduct(productId) {
    const deleteData = {
        id: parseInt(productId)
    };

    console.log("Sending delete request with JSON:", JSON.stringify(deleteData, null, 2));

    fetch(`${contextPath}/deleteProduct`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(deleteData)
    })
    .then(response => {
        console.log("Delete response status:", response.status);
        if (!response.ok) {
            return response.json().then(errorData => {
                throw new Error(errorData.error || `HTTP error! status: ${response.status}`);
            });
        }
        return response.json();
    })
    .then(data => {
        console.log("Delete response:", data);
        showModal(data.message || "Product deleted successfully");
        loadProducts();
    })
    .catch(error => {
        console.error("Error deleting product:", error);
        showModal("Error deleting product: " + error.message);
    });
}