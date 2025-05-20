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

document.addEventListener("DOMContentLoaded", () => {
    console.log("add-customer.js: DOM fully loaded, setting up Add Customer functionality");

    function setupAddCustomer() {
        try {
            let addCustomerBtn = document.getElementById("add-customer-btn");
            if (addCustomerBtn) {
                console.log("add-customer.js: Add Customer button found:", addCustomerBtn);
                bindAddCustomerEvent(addCustomerBtn);
            } else {
                console.warn("add-customer.js: Add Customer button not found, setting up event delegation and retry");
                // Retry finding the button after a short delay
                setTimeout(() => {
                    addCustomerBtn = document.getElementById("add-customer-btn");
                    if (addCustomerBtn) {
                        console.log("add-customer.js: Add Customer button found on retry:", addCustomerBtn);
                        bindAddCustomerEvent(addCustomerBtn);
                    } else {
                        console.error("add-customer.js: Add Customer button still not found after retry");
                    }
                }, 1000);
                // Set up event delegation
                document.addEventListener("click", (event) => {
                    const target = event.target.closest("#add-customer-btn");
                    if (target) {
                        console.log("add-customer.js: Add Customer button clicked via delegation");
                        handleAddCustomerClick();
                    }
                });
            }
        } catch (error) {
            console.error("add-customer.js: Error in Add Customer button setup:", error);
            showModal("Failed to initialize Add Customer button. Please check the console for details.");
        }
    }

    function bindAddCustomerEvent(button) {
        button.addEventListener("click", handleAddCustomerClick);
    }

    function handleAddCustomerClick() {
        console.log("add-customer.js: Handling Add Customer button click");
        const formContainer = document.getElementById("addCustomerFormContainer");
        const form = document.getElementById("add-customer-form");
        if (!formContainer || !form) {
            console.error("add-customer.js: Add customer form or container not found", { formContainer, form });
            showModal("Form or container not found. Please check the page setup.");
            return;
        }

        // Reset form and validation states
        form.reset();
        form.querySelectorAll(".is-invalid").forEach(input => input.classList.remove("is-invalid"));
        form.classList.remove("was-validated");

        // Show form
        console.log("add-customer.js: Showing form");
        formContainer.classList.remove("hidden");
        formContainer.classList.add("show");

        // Focus on first input
        const firstInput = document.getElementById("customer-firstName");
        if (firstInput) {
            console.log("add-customer.js: Focusing on first input");
            firstInput.focus();
        } else {
            console.error("add-customer.js: First name input not found");
        }
    }

    try {
        const cancelCustomerBtn = document.getElementById("cancel-customer-btn");
        if (cancelCustomerBtn) {
            console.log("add-customer.js: Cancel Customer button found:", cancelCustomerBtn);
            cancelCustomerBtn.addEventListener("click", () => {
                console.log("add-customer.js: Cancel Customer button clicked");
                const formContainer = document.getElementById("addCustomerFormContainer");
                const form = document.getElementById("add-customer-form");
                if (formContainer && form) {
                    formContainer.classList.remove("show");
                    formContainer.classList.add("hidden");
                    form.reset();
                    form.classList.remove("was-validated");
                } else {
                    console.error("add-customer.js: Form or container not found on cancel");
                }
            });
        } else {
            console.error("add-customer.js: Cancel Customer button not found");
        }

        const saveCustomerBtn = document.getElementById("save-customer-btn");
        if (saveCustomerBtn) {
            console.log("add-customer.js: Save Customer button found:", saveCustomerBtn);
            saveCustomerBtn.addEventListener("click", async (event) => {
                console.log("add-customer.js: Save Customer button clicked");
                event.preventDefault();

                const form = document.getElementById("add-customer-form");
                const formContainer = document.getElementById("addCustomerFormContainer");
                if (!form || !formContainer) {
                    console.error("add-customer.js: Add customer form or container not found", { form, formContainer });
                    showModal("Form or container not found. Please try again.");
                    return;
                }

                console.log("add-customer.js: Form found:", form);

                // Validate form
                if (!form.checkValidity()) {
                    console.log("add-customer.js: Form validation failed");
                    form.classList.add("was-validated");
                    return;
                }

                // Disable button
                saveCustomerBtn.disabled = true;
                saveCustomerBtn.textContent = "Saving...";
                console.log("add-customer.js: Button disabled, processing form");

                // Reset error states
                form.querySelectorAll(".is-invalid").forEach(input => input.classList.remove("is-invalid"));
                const emailFeedback = document.getElementById("email-feedback");
                const phoneFeedback = document.getElementById("phone-feedback");
                if (emailFeedback) emailFeedback.textContent = "Please enter a valid email address.";
                if (phoneFeedback) phoneFeedback.textContent = "Please enter a valid 10-digit phone number.";

                try {
                    const formData = new FormData(form);
                    const customerData = {
                        action: "add",
                        firstName: formData.get("firstName").trim(),
                        lastName: formData.get("lastName").trim(),
                        email: formData.get("email").trim(),
                        phone: formData.get("phone").trim().replace(/\D/g, ''),
                        gender: formData.get("gender") || null,
                        userType: "walk-in-customer"
                    };

                    console.log("add-customer.js: Sending customer data:", customerData);

                    // Check for duplicates
                    console.log("add-customer.js: Checking duplicates...");
                    const checkResponse = await fetch("/SalonManage/add-customer/check-duplicate", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json",
                            "Accept": "application/json"
                        },
                        body: JSON.stringify({
                            email: customerData.email,
                            phone: customerData.phone
                        })
                    });

                    if (!checkResponse.ok) {
                        const errorData = await checkResponse.json();
                        console.error("add-customer.js: Duplicate check failed:", errorData);
                        throw new Error(errorData.error || `HTTP error: ${checkResponse.status}`);
                    }

                    const checkData = await checkResponse.json();
                    console.log("add-customer.js: Duplicate check response:", checkData);

                    if (!checkData.success) {
                        if (checkData.emailExists) {
                            const emailInput = document.getElementById("customer-email");
                            if (emailInput) {
                                emailInput.classList.add("is-invalid");
                                if (emailFeedback) emailFeedback.textContent = "Email already exists";
                            }
                        }
                        if (checkData.phoneExists) {
                            const phoneInput = document.getElementById("customer-phone");
                            if (phoneInput) {
                                phoneInput.classList.add("is-invalid");
                                if (phoneFeedback) phoneFeedback.textContent = "Phone number already exists";
                            }
                        }
                        return;
                    }

                    // Add the customer
                    console.log("add-customer.js: Adding customer...");
                    const addResponse = await fetch("/SalonManage/add-customer", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json",
                            "Accept": "application/json"
                        },
                        body: JSON.stringify(customerData)
                    });

                    if (!addResponse.ok) {
                        const errorData = await addResponse.json();
                        console.error("add-customer.js: Add customer failed:", errorData);
                        throw new Error(errorData.error || `HTTP error: ${addResponse.status}`);
                    }

                    const addData = await addResponse.json();
                    console.log("add-customer.js: Add customer response:", addData);

                    if (addData.success) {
                        console.log("add-customer.js: Customer added successfully");
                        formContainer.classList.remove("show");
                        formContainer.classList.add("hidden");
                        form.reset();
                        form.classList.remove("was-validated");
                        showModal("Customer added successfully!");
                        if (typeof window.refreshCustomers === "function") {
                            console.log("add-customer.js: Calling refreshCustomers");
                            window.refreshCustomers();
                        }
                    } else {
                        console.error("add-customer.js: Add customer response unsuccessful:", addData);
                        throw new Error(addData.error || "Failed to add customer");
                    }
                } catch (error) {
                    console.error("add-customer.js: Error adding customer:", error);
                    showModal(`Error: ${error.message}`);
                } finally {
                    saveCustomerBtn.disabled = false;
                    saveCustomerBtn.textContent = "Save Customer";
                    console.log("add-customer.js: Button re-enabled");
                }
            });
        } else {
            console.error("add-customer.js: Save Customer button not found");
            showModal("Save Customer button not found. Please check the page setup.");
        }
    } catch (error) {
        console.error("add-customer.js: Error in form setup:", error);
        showModal("Failed to initialize Add Customer form. Please check the console for details.");
    }

    // Initial setup
    setupAddCustomer();
});