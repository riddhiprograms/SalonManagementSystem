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

document.addEventListener("DOMContentLoaded", function () {
    console.log("Booking script loaded");

    // Step elements
    const step1 = document.getElementById("booking-step-1");
    const step2 = document.getElementById("booking-step-2");
    const step3 = document.getElementById("booking-step-3");
    const step4 = document.getElementById("booking-step-4");
    const stepSuccess = document.getElementById("booking-success");

    // Sidebar step items
    const stepNav1 = document.getElementById("step-nav-1");
    const stepNav2 = document.getElementById("step-nav-2");
    const stepNav3 = document.getElementById("step-nav-3");
    const stepNav4 = document.getElementById("step-nav-4");

    // Navigation buttons
    const step1NextButton = document.getElementById("step-1-next");
    const step2PrevButton = document.getElementById("step-2-prev");
    const step2NextButton = document.getElementById("step-2-next");
    const step3PrevButton = document.getElementById("step-3-prev");
    const step3NextButton = document.getElementById("step-3-next");
    const step4PrevButton = document.getElementById("step-4-prev");
    const confirmBookingBtn = document.getElementById("confirm-booking-btn");

    // Form and input elements
    const serviceCategoriesList = document.getElementById("service-categories-list");
    const serviceSearchInput = document.getElementById("service-search");
    const selectedServicesList = document.getElementById("selected-services-list");
    const noServicesAlert = document.getElementById("no-services-selected");
    const staffSelect = document.getElementById("staff-select");
    const appointmentDate = document.getElementById("appointment-date");
    const timeSlotsContainer = document.getElementById("time-slots-container");
    const customerForm = document.getElementById("customer-form");

    // Debug: Verify elements
    console.log("Confirm Booking Button:", confirmBookingBtn);
    console.log("Customer Form:", customerForm);
    console.log("Terms Checkbox:", document.getElementById("terms-checkbox"));

    // Shared variables
    let selectedServices = [];

    // Navigation function with sidebar highlighting
    function showStep(stepToShow) {
        [step1, step2, step3, step4, stepSuccess].forEach(step => {
            if (step) step.classList.add("d-none");
        });
        if (stepToShow) stepToShow.classList.remove("d-none");

        [stepNav1, stepNav2, stepNav3, stepNav4].forEach(nav => {
            if (nav) nav.classList.remove("active");
        });
        if (stepToShow === step1) stepNav1.classList.add("active");
        else if (stepToShow === step2) stepNav2.classList.add("active");
        else if (stepToShow === step3) stepNav3.classList.add("active");
        else if (stepToShow === step4) stepNav4.classList.add("active");
    }

    // Step 1: Load and search services
    const loadServices = (query = "") => {
        serviceCategoriesList.innerHTML = `
            <div class="text-center py-3">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
                <p class="mt-2">Loading services...</p>
            </div>
        `;

        fetch(`/SalonManage/services?query=${encodeURIComponent(query)}`)
            .then(response => response.json())
            .then(data => {
                serviceCategoriesList.innerHTML = "";
                if (data.error) {
                    serviceCategoriesList.innerHTML = `<div class="alert alert-danger">Failed to load services: ${data.error}</div>`;
                    return;
                }

                data.forEach(category => {
                    const categorySection = document.createElement("div");
                    categorySection.classList.add("service-category-section", "mb-4");
                    categorySection.innerHTML = `
                        <h4 class="fs-6 fw-semibold mb-3">${category.categoryName}</h4>
                        <div class="row g-3">
                            ${category.services
                                .filter(service => service.serviceId && service.duration > 0)
                                .map(service => `
                                    <div class="col-md-6">
                                        <div class="card service-selection-card h-100">
                                            <div class="card-body">
                                                <div class="form-check">
                                                    <input class="form-check-input service-checkbox" type="checkbox" 
                                                        value="${service.serviceId}" id="service-${service.serviceId}">
                                                    <label class="form-check-label w-100" for="service-${service.serviceId}">
                                                        <div class="d-flex justify-content-between align-items-start">
                                                            <div>
                                                                <p class="fw-medium mb-0">${service.name}</p>
                                                                <small class="text-muted d-block">${service.description}</small>
                                                                <small class="text-muted d-block">${service.duration} min</small>
                                                            </div>
                                                            <span class="service-price">₹${service.price.toFixed(2)}</span>
                                                        </div>
                                                    </label>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                `).join("")}
                        </div>
                    `;
                    serviceCategoriesList.appendChild(categorySection);
                });
            })
            .catch(error => {
                console.error("Error loading services:", error);
                serviceCategoriesList.innerHTML = `<div class="alert alert-danger">Failed to load services</div>`;
            });
    };

    // Update selected services UI
    const updateSelectedServices = () => {
        const checkedBoxes = document.querySelectorAll(".service-checkbox:checked");
        selectedServicesList.innerHTML = "";

        if (checkedBoxes.length === 0) {
            selectedServicesList.classList.add("d-none");
            noServicesAlert.classList.remove("d-none");
            return;
        }

        noServicesAlert.classList.add("d-none");
        selectedServicesList.classList.remove("d-none");

        checkedBoxes.forEach(checkbox => {
            const label = document.querySelector(`label[for="${checkbox.id}"]`);
            const serviceName = label.querySelector("p").textContent;
            const servicePrice = label.querySelector(".service-price").textContent;
            const serviceDuration = label.querySelectorAll("small")[1].textContent;

            const selectedItem = document.createElement("div");
            selectedItem.classList.add("d-flex", "justify-content-between", "align-items-center", "mb-2");
            selectedItem.innerHTML = `
                <div>
                    <strong>${serviceName}</strong><br>
                    <small class="text-muted">${serviceDuration}</small>
                </div>
                <div class="text-end">${servicePrice}</div>
            `;
            selectedServicesList.appendChild(selectedItem);
        });
    };

    // Step 2: Load staff
    const loadStaff = () => {
        staffSelect.innerHTML = `<option value="" selected disabled>Select a stylist</option>`;
        fetch("/SalonManage/staff")
            .then(response => response.json())
            .then(data => {
                data.forEach(staff => {
                    const option = document.createElement("option");
                    option.value = staff.staffId;
                    option.textContent = `${staff.firstName} ${staff.lastName} (${staff.specialties})`;
                    staffSelect.appendChild(option);
                });
            })
            .catch(error => {
                console.error("Error loading staff:", error);
                staffSelect.innerHTML = `<option value="" disabled>Error loading staff</option>`;
            });
    };

    // Step 2: Load time slots
    const loadTimeSlots = () => {
        const staffId = staffSelect.value;
        const selectedDate = appointmentDate.value;
        const defaultMessage = document.querySelector('.default-time-slots-message');

        if (defaultMessage) defaultMessage.classList.add('d-none');
        timeSlotsContainer.innerHTML = `
            <div class="text-center py-3">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
                <p class="mt-2">Loading time slots...</p>
            </div>
        `;

        console.log("Fetching time slots:", { staffId, selectedDate, selectedServices });

        fetch(`/SalonManage/book-appointment?action=fetchTimeSlots&staffId=${staffId}&date=${selectedDate}&serviceIds=${encodeURIComponent(JSON.stringify(selectedServices))}`)
            .then(response => {
                if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
                return response.json();
            })
            .then(data => {
                console.log("Time slots response:", data);
                timeSlotsContainer.innerHTML = "";
                if (!data.success || !data.timeSlots || data.timeSlots.length === 0) {
                    timeSlotsContainer.innerHTML = `
                        <div class="alert alert-warning">
                            <i class="fas fa-exclamation-circle me-2"></i> No available time slots for this date and services.
                            ${data.message ? `<br>Reason: ${data.message}` : ''}
                        </div>
                    `;
                    return;
                }

                data.timeSlots.forEach(slot => {
                    const button = document.createElement('button');
                    button.classList.add('time-slot');
                    button.dataset.time = slot;
                    button.textContent = formatTimeDisplay(slot);
                    button.addEventListener('click', function () {
                        document.querySelectorAll('.time-slot').forEach(b => b.classList.remove('selected'));
                        this.classList.add('selected');
                    });
                    timeSlotsContainer.appendChild(button);
                });
            })
            .catch(error => {
                console.error("Error loading time slots:", error);
                timeSlotsContainer.innerHTML = `
                    
                `;
                if (defaultMessage) defaultMessage.classList.remove('d-none');
            });
    };

    // Format time for display (e.g., "10:00:00" -> "10:00 AM")
    const formatTimeDisplay = (timeString) => {
        const [hours, minutes] = timeString.split(':').map(Number);
        const period = hours >= 12 ? 'PM' : 'AM';
        const displayHours = hours % 12 || 12;
        return `${displayHours}:${minutes.toString().padStart(2, '0')} ${period}`;
    };

    // Step 3: Pre-fill customer details
    const prefillCustomerDetails = () => {
        fetch("/SalonManage/user-details")
            .then(response => {
                if (!response.ok) throw new Error("Failed to fetch user details.");
                return response.json();
            })
            .then(data => {
                document.getElementById("customer-name").value = data.fullName || "";
                document.getElementById("customer-phone").value = data.phone || "";
                document.getElementById("customer-email").value = data.email || "";
                document.getElementById("customer-gender").value = data.gender || "other";
            })
            .catch(error => {
                console.error("Error fetching user details:", error);
                showModal("Failed to pre-fill customer details. Please fill them manually.");
            });
    };

    // Update booking summary
    const updateBookingSummary = () => {
        try {
            document.getElementById("summary-customer").textContent = 
                document.getElementById("customer-name").value || "Not Provided";

            const servicesContainer = document.getElementById("summary-services");
            servicesContainer.innerHTML = "";
            const checkedServices = document.querySelectorAll(".service-checkbox:checked");
            if (checkedServices.length === 0) {
                servicesContainer.textContent = "None Selected";
            } else {
                checkedServices.forEach(checkbox => {
                    const serviceDiv = document.createElement("div");
                    const label = document.querySelector(`label[for="${checkbox.id}"]`);
                    const name = label.querySelector(".fw-medium").textContent;
                    const price = label.querySelector(".service-price").textContent;
                    const duration = label.querySelectorAll("small")[1].textContent;
                    serviceDiv.textContent = `${name} - ${price} (${duration})`;
                    servicesContainer.appendChild(serviceDiv);
                });
            }

            const staffSelect = document.getElementById("staff-select");
            document.getElementById("summary-stylist").textContent = 
                staffSelect.options[staffSelect.selectedIndex]?.text || "Not Selected";

            const date = document.getElementById("appointment-date").value;
            const timeSlot = document.querySelector(".time-slot.selected");
            if (date && timeSlot) {
                const dateParts = date.split('-');
                const formattedDate = `${dateParts[2]}-${dateParts[1]}-${dateParts[0]}`;
                document.getElementById("summary-datetime").textContent = 
                    `${formattedDate} at ${formatTimeDisplay(timeSlot.dataset.time)}`;
            } else {
                document.getElementById("summary-datetime").textContent = "Not Selected";
            }

            let total = 0;
            checkedServices.forEach(checkbox => {
                const label = document.querySelector(`label[for="${checkbox.id}"]`);
                const priceText = label.querySelector(".service-price").textContent;
                total += parseFloat(priceText.replace(/[^\d.]/g, ''));
            });
            document.getElementById("summary-price").textContent = `₹${total.toFixed(2)}`;
        } catch (error) {
            console.error("Error updating booking summary:", error);
        }
    };

    // Form submission
    const submitBooking = async () => {
        console.log("Starting submitBooking");

        try {
            console.log("Checking user session");
            const userCheck = await fetch("/SalonManage/user-details");
            if (!userCheck.ok) {
                console.log("User not logged in, redirecting to login");
                showModal("Please log in to book an appointment.", () => {
                    window.location.href = "/SalonManage/login.jsp";
                });
                return false;
            }

            const staffId = staffSelect.value;
            const appointmentDateValue = appointmentDate.value;
            const selectedTimeSlot = document.querySelector(".time-slot.selected")?.dataset.time;
            const services = Array.from(document.querySelectorAll(".service-checkbox:checked")).map(cb => parseInt(cb.value));

            console.log("Form data:", { staffId, appointmentDateValue, selectedTimeSlot, services });

            if (!staffId) {
                showModal("Please select a stylist.");
                return false;
            }
            if (!appointmentDateValue) {
                showModal("Please select a date.");
                return false;
            }
            if (!selectedTimeSlot) {
                showModal("Please select a time slot.");
                return false;
            }
            if (services.length === 0) {
                showModal("Please select at least one service.");
                return false;
            }
            if (!customerForm.checkValidity()) {
                console.log("Form validation failed");
                customerForm.reportValidity();
                return false;
            }

            const formData = {
                staffId: String(staffId),
                appointmentDate: appointmentDateValue,
                appointmentTime: selectedTimeSlot,
                services: JSON.stringify(services),
                customerName: document.getElementById("customer-name").value || "",
                customerPhone: document.getElementById("customer-phone").value || "",
                customerEmail: document.getElementById("customer-email").value || "",
                customerGender: document.getElementById("customer-gender").value || "",
                customerNotes: document.getElementById("customer-notes").value || ""
            };

            console.log("Form data to be sent:", formData);

            console.log("Sending fetch request to /SalonManage/book-appointment");
            const response = await fetch(window.location.origin + "/SalonManage/book-appointment", {
                method: "POST",
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams(formData).toString()
            });

            const data = await response.json();
            console.log("Server response:", data);

            if (data.success) {
                console.log("Booking successful, showing success step");
                showStep(stepSuccess);
                document.getElementById("booking-reference").textContent = data.bookingRef || "N/A";

                // Render confirmation details
                const confirmationDetails = data.confirmationDetails;
                if (confirmationDetails) {
                    const detailsContainer = document.getElementById("confirmation-details");
                    detailsContainer.innerHTML = `
                        <table class="table table-bordered">
                            <tr><th>Customer Name</th><td>${confirmationDetails.customerName}</td></tr>
                            <tr><th>Phone</th><td>${confirmationDetails.customerPhone}</td></tr>
                            <tr><th>Email</th><td>${confirmationDetails.customerEmail}</td></tr>
                            <tr><th>Date</th><td>${confirmationDetails.appointmentDate}</td></tr>
                            <tr><th>Time</th><td>${formatTimeDisplay(confirmationDetails.appointmentTime)}</td></tr>
                            <tr><th>Stylist</th><td>${confirmationDetails.staffName}</td></tr>
                            <tr><th>Services</th><td>
                                ${confirmationDetails.services.map(service => `${service.name} (${service.duration})`).join('<br>')}
                            </td></tr>
                        </table>
                    `;
                } else {
                    document.getElementById("confirmation-details").innerHTML = "<p class='text-danger'>Confirmation details not available</p>";
                }

                // Handle download confirmation button
                const downloadBtn = document.getElementById("download-confirm-btn");
                downloadBtn.addEventListener("click", () => {
                    if (confirmationDetails && typeof jspdf !== 'undefined') {
                        const { jsPDF } = jspdf;
                        const doc = new jsPDF();
                        
                        // Set document properties
                        doc.setProperties({
                            title: `Booking Confirmation ${data.bookingRef}`,
                            author: 'Salon Management',
                            creator: 'Salon Management System'
                        });

                        // Add content
                        doc.setFontSize(16);
                        doc.text('Booking Confirmation', 20, 20);
                        doc.setFontSize(12);
                        doc.text(`Booking Reference: ${data.bookingRef}`, 20, 30);
                        doc.text(`Customer Name: ${confirmationDetails.customerName}`, 20, 40);
                        doc.text(`Phone: ${confirmationDetails.customerPhone}`, 20, 50);
                        doc.text(`Email: ${confirmationDetails.customerEmail}`, 20, 60);
                        doc.text(`Date: ${confirmationDetails.appointmentDate}`, 20, 70);
                        doc.text(`Time: ${formatTimeDisplay(confirmationDetails.appointmentTime)}`, 20, 80);
                        doc.text(`Stylist: ${confirmationDetails.staffName}`, 20, 90);
                        doc.text('Services:', 20, 100);
                        
                        // Add services
                        confirmationDetails.services.forEach((service, index) => {
                            doc.text(`- ${service.name} (${service.duration})`, 30, 110 + index * 10);
                        });

                        // Add footer
                        doc.setFontSize(10);
                        doc.text('Thank you for booking with us! Contact support@salon.com for assistance.', 20, 290);

                        // Save PDF
                        doc.save(`booking-confirmation-${data.bookingRef}.pdf`);
                    } else {
                        showModal("Unable to generate confirmation PDF. Please try again later.");
                        console.error("jsPDF not loaded or confirmation details missing");
                    }
                });

                return true;
            } else {
                console.log("Booking failed:", data.message);
                showModal(`Booking failed: ${data.message || "Unknown error"}`);
                return false;
            }
        } catch (error) {
            console.error("Error in submitBooking:", error);
            showModal(`Failed to book appointment: ${error.message}`);
            return false;
        }
    };

    // Event listeners
    serviceSearchInput.addEventListener("input", (event) => {
        loadServices(event.target.value.trim());
    });

    serviceCategoriesList.addEventListener("change", (event) => {
        if (event.target.classList.contains("service-checkbox")) {
            updateSelectedServices();
            loadTimeSlots();
        }
    });

    staffSelect.addEventListener("change", loadTimeSlots);
    appointmentDate.addEventListener("change", loadTimeSlots);

    step1NextButton.addEventListener("click", function (e) {
        e.preventDefault();
        const checkedBoxes = document.querySelectorAll(".service-checkbox:checked");
        if (checkedBoxes.length === 0) {
            showModal("Please select at least one service before proceeding.");
            return;
        }
        selectedServices = Array.from(checkedBoxes).map(cb => parseInt(cb.value));
        showStep(step2);
        loadTimeSlots();
    });

    step2PrevButton.addEventListener("click", function (e) {
        e.preventDefault();
        showStep(step1);
    });

    step2NextButton.addEventListener("click", function (e) {
        e.preventDefault();
        const staffId = document.getElementById("staff-select").value;
        const timeSlot = document.querySelector(".time-slot.selected");

        if (!staffId) {
            showModal("Please select a stylist");
            return;
        }
        if (!timeSlot) {
            showModal("Please select a time slot");
            return;
        }

        showStep(step3);
        prefillCustomerDetails();
    });

    step3PrevButton.addEventListener("click", function (e) {
        e.preventDefault();
        showStep(step2);
    });

    step3NextButton.addEventListener("click", function (e) {
        e.preventDefault();
        showStep(step4);
        updateBookingSummary();
    });

    step4PrevButton.addEventListener("click", function (e) {
        e.preventDefault();
        showStep(step3);
    });

    if (confirmBookingBtn) {
        confirmBookingBtn.addEventListener("click", function (e) {
            e.preventDefault();
            console.log("Confirm Booking button clicked");
            const termsCheckbox = document.getElementById("terms-checkbox");
            console.log("Terms Checkbox:", termsCheckbox);
            if (!termsCheckbox) {
                showModal("Terms checkbox not found. Please check the JSP.");
                return;
            }
            if (!termsCheckbox.checked) {
                showModal("Please agree to the terms and conditions");
                return;
            }
            submitBooking();
        });
    } else {
        console.error("Confirm Booking button not found in DOM");
        showModal("Confirm Booking button not found. Please check the JSP ID.");
    }

    // Initialize
    loadServices();
    loadStaff();
    showStep(step1);
});