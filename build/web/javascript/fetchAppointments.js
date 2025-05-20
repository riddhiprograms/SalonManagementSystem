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
    console.log("fetchAppointments.js loaded");

    // DOM elements
    const appointmentsContainer = document.getElementById("appointments-container");
    const appointmentsLoading = document.getElementById("appointments-loading");
    const appointmentsError = document.getElementById("appointments-error");

    // Verify DOM elements
    if (!appointmentsContainer) {
        console.error("Appointments container not found. Ensure <div id='appointments-container'> exists in the HTML.");
        return;
    }
    console.log("Appointments container found:", appointmentsContainer);

    // Function to fetch appointments
    const loadAppointments = () => {
        const servletUrl = `${window.location.origin}/SalonManage/FetchAppointmentsServlet`;
        console.log("Fetching appointments from:", servletUrl);

        // Show loading state
        if (appointmentsLoading) {
            appointmentsLoading.classList.remove("d-none");
        }
        if (appointmentsError) {
            appointmentsError.classList.add("d-none");
        }
        appointmentsContainer.innerHTML = `
            <div class="text-center">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
            </div>
        `;

        fetch(servletUrl, {
            method: "GET",
            headers: { "Content-Type": "application/json" }
        })
            .then(response => {
                console.log("Fetch response status:", response.status);
                if (!response.ok) {
                    throw new Error(`Failed to fetch appointments: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                console.log("Appointments response:", JSON.stringify(data, null, 2));
                if (appointmentsLoading) {
                    appointmentsLoading.classList.add("d-none");
                }

                if (!data.success || !data.data) {
                    console.error("Invalid response:", data.message || "No data returned");
                    renderAppointmentsError(`Failed to load appointments: ${data.message || "Unknown error"}`);
                    return;
                }

                // Ensure data.data is an array
                const appointments = Array.isArray(data.data) ? data.data : [];
                console.log("Parsed appointments:", appointments);
                renderAppointments(appointments);
            })
            .catch(error => {
                console.error("Error fetching appointments:", error);
                if (appointmentsLoading) {
                    appointmentsLoading.classList.add("d-none");
                }
                renderAppointmentsError(`Error loading appointments: ${error.message}`);
                // Retry after 5 seconds
                setTimeout(() => loadAppointments(), 5000);
            });
    };

    // Function to render appointments
    const renderAppointments = (appointments) => {
        console.log("Rendering appointments:", appointments);
        if (appointmentsError) {
            appointmentsError.classList.add("d-none");
        }

        if (!appointments || appointments.length === 0) {
            appointmentsContainer.innerHTML = `
                <div class="alert alert-info d-flex align-items-center">
                    <i class="fas fa-info-circle me-2"></i>
                    No upcoming appointments. <a href="booking.jsp" class="text-decoration-none ms-2 text-primary">Book Now</a>
                </div>
            `;
            return;
        }

        let html = '';
        appointments.forEach((appointment, index) => {
            console.log(`Processing appointment ${index}:`, appointment);
            const statusClass = {
                'pending': 'bg-warning text-dark',
                'confirmed': 'bg-success',
                'cancelled': 'bg-danger',
                'completed': 'bg-primary'
            }[appointment.status?.toLowerCase()] || 'bg-secondary';

            html += `
                <div class="appointment-card">
                    <div class="card">
                        <div class="card-body p-4">
                            <div class="d-flex justify-content-between align-items-start">
                                <div>
                                    <span class="status-badge badge ${statusClass}">${appointment.status || 'Unknown'}</span>
                                    <h5 class="fw-bold mb-1">${appointment.serviceName || 'Not specified'}</h5>
                                    <p class="text-muted mb-3">${appointment.date || 'N/A'} at ${appointment.time || 'N/A'}</p>
                                    <div class="mb-2">
                                        <i class="fas fa-user-tie me-2 text-primary"></i>
                                        <span>Stylist: ${appointment.stylist || 'Not assigned'}</span>
                                    </div>
                                    <div class="mb-2">
                                        <i class="fas fa-rupee-sign me-2 text-primary"></i>
                                        <span>Total: ₹${(appointment.totalAmount || 0).toFixed(2)}</span>
                                    </div>
                                    ${appointment.paymentType ? `
                                        <div>
                                            <i class="fas fa-credit-card me-2 text-primary"></i>
                                            <span>Payment: ${appointment.paymentType}</span>
                                        </div>
                                    ` : ''}
                                </div>
                                <div class="ms-3 text-end">
                                    <button class="btn btn-outline-secondary mb-2" onclick="rescheduleAppointment(${appointment.id || 0})">
                                        <i class="fas fa-edit me-1"></i> Reschedule
                                    </button>
                                    <button class="btn btn-outline-danger" onclick="cancelAppointment(${appointment.id || 0})">
                                        <i class="fas fa-times me-1"></i> Cancel
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            `;
        });

        console.log("Generated HTML:", html);
        appointmentsContainer.innerHTML = html;
    };

    // Function to render error state
    const renderAppointmentsError = (message) => {
        if (appointmentsError) {
            appointmentsError.classList.remove("d-none");
            appointmentsError.innerHTML = `
                <i class="fas fa-exclamation-circle me-2"></i> ${message}
            `;
        }
        appointmentsContainer.innerHTML = '';
    };

    // Format time for display (e.g., "10:00:00" -> "10:00 AM")
    const formatTimeDisplay = (timeString) => {
        const [hours, minutes] = timeString.split(':').map(Number);
        const period = hours >= 12 ? 'PM' : 'AM';
        const displayHours = hours % 12 || 12;
        return `${displayHours}:${minutes.toString().padStart(2, '0')} ${period}`;
    };

    // Load staff for reschedule form
    const loadStaff = (selectElement) => {
        selectElement.innerHTML = `<option value="" selected disabled>Select a stylist</option>`;
        fetch(`${window.location.origin}/SalonManage/staff`)
            .then(response => response.json())
            .then(data => {
                data.forEach(staff => {
                    const option = document.createElement("option");
                    option.value = staff.staffId;
                    option.textContent = `${staff.firstName} ${staff.lastName} (${staff.specialties})`;
                    selectElement.appendChild(option);
                });
            })
            .catch(error => {
                console.error("Error loading staff:", error);
                selectElement.innerHTML = `<option value="" disabled>Error loading staff</option>`;
            });
    };

    // Load time slots for reschedule form
    const loadTimeSlots = (appointmentId, staffId, date, timeSlotsContainer) => {
        timeSlotsContainer.innerHTML = `
            <div class="text-center py-3">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
                <p class="mt-2">Loading time slots...</p>
            </div>
        `;

        // Fetch service IDs for the appointment
        fetch(`${window.location.origin}/SalonManage/FetchAppointmentServicesServlet?appointmentId=${appointmentId}`)
            .then(response => {
                if (!response.ok) throw new Error(`Failed to fetch services: ${response.status}`);
                return response.json();
            })
            .then(data => {
                if (!data.success || !data.data) {
                    throw new Error(data.message || "No services found");
                }
                const serviceIds = data.data;
                // Fetch time slots
                return fetch(`${window.location.origin}/SalonManage/book-appointment?action=fetchTimeSlots&staffId=${staffId}&date=${date}&serviceIds=${encodeURIComponent(JSON.stringify(serviceIds))}`);
            })
            .then(response => {
                if (!response.ok) throw new Error(`Failed to fetch time slots: ${response.status}`);
                return response.json();
            })
            .then(data => {
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
                    button.classList.add('btn', 'btn-outline-primary', 'm-1', 'time-slot');
                    button.dataset.time = slot;
                    button.textContent = formatTimeDisplay(slot);
                    button.addEventListener('click', function () {
                        document.querySelectorAll('.time-slot').forEach(b => b.classList.remove('btn-primary'));
                        this.classList.add('btn-primary');
                        this.classList.remove('btn-outline-primary');
                    });
                    timeSlotsContainer.appendChild(button);
                });
            })
            .catch(error => {
                console.error("Error loading time slots:", error);
                timeSlotsContainer.innerHTML = `
                    <div class="alert alert-danger">
                        <i class="fas fa-exclamation-circle me-2"></i> Failed to load time slots: ${error.message}
                    </div>
                `;
            });
    };

    // Handle cancel form submission
    const cancelForm = document.getElementById("cancelConfirmationForm");
    if (cancelForm) {
        cancelForm.addEventListener("submit", function (e) {
            e.preventDefault();
            console.log("Cancel form submitted");
            const appointmentId = document.getElementById("appointmentIdToCancel").value;
            console.log("Submitting cancellation for appointment ID:", appointmentId);

            const submitBtn = document.getElementById("proceedCancelBtn");
            const spinner = submitBtn.querySelector(".spinner-border");

            // Show loading state
            spinner.classList.remove("d-none");
            submitBtn.disabled = true;

            jQuery.ajax({
                url: `${window.location.origin}/SalonManage/CancelAppointmentServlet`,
                type: "POST",
                data: {
                    appointmentId: appointmentId
                },
                success: function (data) {
                    console.log("Cancel response:", JSON.stringify(data, null, 2));
                    spinner.classList.add("d-none");
                    submitBtn.disabled = false;

                    if (data.success) {
                        // Hide confirmation form
                        cancelForm.classList.remove("show");
                        cancelForm.style.display = "none";
                        document.body.classList.remove("modal-open");
                        const backdrop = document.querySelector(".modal-backdrop");
                        if (backdrop) backdrop.remove();
                        console.log("Cancel confirmation form hidden");

                        // Show success modal
                        const successModal = new bootstrap.Modal(document.getElementById("cancelSuccessModal"));
                        successModal.show();
                        console.log("Cancel success modal shown");

                        // Reload appointments after a short delay
                        setTimeout(() => {
                            loadAppointments();
                            console.log("Appointments reloaded after cancellation");
                        }, 1000);
                    } else {
                        console.error("Cancellation failed:", data.message);
                        showModal("Error cancelling appointment: " + data.message);
                    }
                },
                error: function (xhr, status, error) {
                    console.error("Error cancelling appointment:", error, "Status:", status, "Response:", xhr.responseText);
                    spinner.classList.add("d-none");
                    submitBtn.disabled = false;
                    showModal("Error cancelling appointment: " + (xhr.responseJSON?.message || error));
                }
            });
        });
    } else {
        console.error("Cancel confirmation form not found. Ensure <form id='cancelConfirmationForm'> exists.");
        showModal("Error: Cancel confirmation form not found.");
    }

    // Handle reschedule form submission
    const rescheduleForm = document.getElementById("rescheduleForm");
    if (rescheduleForm) {
        rescheduleForm.addEventListener("submit", function (e) {
            e.preventDefault();
            console.log("Reschedule form submitted");
            const appointmentId = document.getElementById("rescheduleAppointmentId").value;
            const staffId = document.getElementById("rescheduleStaffSelect").value;
            const appointmentDate = document.getElementById("rescheduleAppointmentDate").value;
            const selectedTimeSlot = document.querySelector(".time-slot.btn-primary")?.dataset.time;

            console.log("Submitting reschedule for appointment ID:", appointmentId, 
                        "Staff ID:", staffId, "Date:", appointmentDate, "Time:", selectedTimeSlot);

            if (!staffId) {
                showModal("Please select a stylist.");
                return;
            }
            if (!appointmentDate) {
                showModal("Please select a date.");
                return;
            }
            if (!selectedTimeSlot) {
                showModal("Please select a time slot.");
                return;
            }

            const submitBtn = document.getElementById("proceedRescheduleBtn");
            const spinner = submitBtn.querySelector(".spinner-border");

            // Show loading state
            spinner.classList.remove("d-none");
            submitBtn.disabled = true;

            jQuery.ajax({
                url: `${window.location.origin}/SalonManage/RescheduleAppointmentServlet`,
                type: "POST",
                data: {
                    appointmentId: appointmentId,
                    staffId: staffId,
                    appointmentDate: appointmentDate,
                    appointmentTime: selectedTimeSlot
                },
                success: function (data) {
                    console.log("Reschedule response:", JSON.stringify(data, null, 2));
                    spinner.classList.add("d-none");
                    submitBtn.disabled = false;

                    if (data.success) {
                        // Hide reschedule form
                        rescheduleForm.classList.remove("show");
                        rescheduleForm.style.display = "none";
                        document.body.classList.remove("modal-open");
                        const backdrop = document.querySelector(".modal-backdrop");
                        if (backdrop) backdrop.remove();
                        console.log("Reschedule form hidden");

                        // Show success modal
                        const successModal = new bootstrap.Modal(document.getElementById("rescheduleSuccessModal"));
                        successModal.show();
                        console.log("Reschedule success modal shown");

                        // Reload appointments after a short delay
                        setTimeout(() => {
                            loadAppointments();
                            console.log("Appointments reloaded after rescheduling");
                        }, 1000);
                    } else {
                        console.error("Rescheduling failed:", data.message);
                        showModal("Error rescheduling appointment: " + data.message);
                    }
                },
                error: function (xhr, status, error) {
                    console.error("Error rescheduling appointment:", error, "Status:", status, "Response:", xhr.responseText);
                    spinner.classList.add("d-none");
                    submitBtn.disabled = false;
                    showModal("Error rescheduling appointment: " + (xhr.responseJSON?.message || error));
                }
            });
        });

        // Load staff on form open
        const staffSelect = document.getElementById("rescheduleStaffSelect");
        loadStaff(staffSelect);

        // Update time slots on staff or date change
        const appointmentDate = document.getElementById("rescheduleAppointmentDate");
        const timeSlotsContainer = document.getElementById("rescheduleTimeSlots");
        const appointmentIdInput = document.getElementById("rescheduleAppointmentId");

        const updateTimeSlots = () => {
            const appointmentId = appointmentIdInput.value;
            const staffId = staffSelect.value;
            const date = appointmentDate.value;
            if (appointmentId && staffId && date) {
                loadTimeSlots(appointmentId, staffId, date, timeSlotsContainer);
            }
        };

        staffSelect.addEventListener("change", updateTimeSlots);
        appointmentDate.addEventListener("change", updateTimeSlots);
    } else {
        console.error("Reschedule form not found. Ensure <form id='rescheduleForm'> exists.");
        showModal("Error: Reschedule form not found.");
    }

    // Handle cancel/reschedule form dismissal
    document.querySelectorAll("#cancelConfirmationForm [data-dismiss='form'], #rescheduleForm [data-dismiss='form']").forEach(btn => {
        btn.addEventListener("click", function () {
            console.log("Form dismissed");
            const form = btn.closest(".modal");
            form.classList.remove("show");
            form.style.display = "none";
            document.body.classList.remove("modal-open");
            const backdrop = document.querySelector(".modal-backdrop");
            if (backdrop) backdrop.remove();
        });
    });

    // Handle success modal dismissal
    document.querySelectorAll("#cancelSuccessModal [data-bs-dismiss='modal'], #rescheduleSuccessModal [data-bs-dismiss='modal']").forEach(btn => {
        btn.addEventListener("click", function () {
            console.log("Success modal dismissed");
            const modal = bootstrap.Modal.getInstance(btn.closest(".modal"));
            modal.hide();
        });
    });

    // Initialize
    console.log("Initializing appointments section");
    loadAppointments();
});

window.rescheduleAppointment = function (id) {
    console.log("Reschedule appointment:", id);
    const rescheduleForm = document.getElementById("rescheduleForm");
    if (!rescheduleForm) {
        console.error("Reschedule form not found. Ensure <form id='rescheduleForm'> exists.");
        showModal("Error: Reschedule form not found.");
        return;
    }
    try {
        document.getElementById("rescheduleAppointmentId").value = id;
        rescheduleForm.classList.add("show");
        rescheduleForm.style.display = "block";
        document.body.classList.add("modal-open");
        const backdrop = document.createElement("div");
        backdrop.className = "modal-backdrop fade show";
        document.body.appendChild(backdrop);
        console.log("Reschedule form shown for ID:", id);
    } catch (error) {
        console.error("Error showing reschedule form:", error);
        showModal("Error opening reschedule form: " + error.message);
    }
};

window.cancelAppointment = function (id) {
    console.log("Cancel appointment :", id);
    document.getElementById("appointmentIdToCancel").value = id;
    const cancelForm = document.getElementById("cancelConfirmationForm");
    if (!cancelForm) {
        console.error("Cancel confirmation form not found. Ensure <form id='cancelConfirmationForm'> exists.");
        showModal("Error: Cancellation form not found.");
        return;
    }
    try {
        cancelForm.classList.add("show");
        cancelForm.style.display = "block";
        document.body.classList.add("modal-open");
        const backdrop = document.createElement("div");
        backdrop.className = "modal-backdrop fade show";
        document.body.appendChild(backdrop);
        console.log("Cancel confirmation form shown for ID:", id);
    } catch (error) {
        console.error("Error showing cancel confirmation form:", error);
        showModal("Error opening cancellation form: " + error.message);
    }
};