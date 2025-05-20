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

function showConfirmModal(message, onConfirm) {
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

  // Create OK button
  const okButton = document.createElement("button");
  okButton.textContent = "OK";
  okButton.style.backgroundColor = "var(--primary-color)"; // #D4AF37
  okButton.style.border = "1px solid var(--primary-color)"; // #D4AF37
  okButton.style.color = "white";
  okButton.style.padding = "0.5rem 1.5rem"; // Matches button padding
  okButton.style.borderRadius = "var(--border-radius)"; // 0.25rem
  okButton.style.fontSize = "0.9rem";
  okButton.style.fontWeight = "600";
  okButton.style.textTransform = "uppercase";
  okButton.style.letterSpacing = "0.5px";
  okButton.style.cursor = "pointer";
  okButton.style.transition = "background-color 0.3s ease, border-color 0.3s ease"; // Matches --transition-speed
  okButton.onmouseover = () => {
    okButton.style.backgroundColor = "var(--primary-dark)"; // #A38829
    okButton.style.borderColor = "var(--primary-dark)"; // #A38829
  };
  okButton.onmouseout = () => {
    okButton.style.backgroundColor = "var(--primary-color)"; // #D4AF37
    okButton.style.borderColor = "var(--primary-color)"; // #D4AF37
  };
  okButton.onclick = () => {
    modal.style.opacity = "0";
    setTimeout(() => {
      document.body.removeChild(modal);
      if (onConfirm) onConfirm();
    }, 300); // Matches --transition-speed
  };
  buttonContainer.appendChild(okButton);

  // Create Cancel button
  const cancelButton = document.createElement("button");
  cancelButton.textContent = "Cancel";
  cancelButton.style.backgroundColor = "var(--dark-gray)"; // #6C757D
  cancelButton.style.border = "1px solid var(--dark-gray)"; // #6C757D
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
    cancelButton.style.backgroundColor = "#5a6268"; // Darker shade of --dark-gray
    cancelButton.style.borderColor = "#5a6268";
  };
  cancelButton.onmouseout = () => {
    cancelButton.style.backgroundColor = "var(--dark-gray)"; // #6C757D
    cancelButton.style.borderColor = "var(--dark-gray)"; // #6C757D
  };
  cancelButton.onclick = () => {
    modal.style.opacity = "0";
    setTimeout(() => {
      document.body.removeChild(modal);
    }, 300); // Matches --transition-speed
  };
  buttonContainer.appendChild(cancelButton);

  // Append buttons to modal content
  modalContent.appendChild(buttonContainer);

  // Append modal content to modal container
  modal.appendChild(modalContent);

  // Append modal to the document body
  document.body.appendChild(modal);
}

document.addEventListener("DOMContentLoaded", function () {
    console.log("Appointments script loaded");

    let currentPage = 1;
    const pageSize = 10;
    let staffList = [];
    let servicesList = [];
    let sortColumn = "date";
    let sortDirection = "asc";

    const formatTimeDisplay = (timeString) => {
        const [hours, minutes] = timeString.split(':').map(Number);
        const period = hours >= 12 ? 'PM' : 'AM';
        const displayHours = hours % 12 || 12;
        return `${displayHours}:${minutes.toString().padStart(2, '0')} ${period}`;
    };

    const attachButtonListeners = () => {
        console.log("Attaching button listeners");
        document.querySelectorAll(".delete-appointment").forEach(button => {
            button.removeEventListener("click", handleDelete);
            button.addEventListener("click", handleDelete);
        });
        document.querySelectorAll(".edit-appointment").forEach(button => {
            button.removeEventListener("click", handleEdit);
            button.addEventListener("click", handleEdit);
        });
    };

     const handleDelete = (e) => {
        const button = e.target;
        const appointmentId = button.getAttribute("data-id");
        showConfirmModal("Delete this appointment?", () => {
            fetch("/SalonManage/delete-appointment", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: `appointmentId=${encodeURIComponent(appointmentId)}`
            })
                .then(response => {
                    if (!response.ok) throw new Error(`Failed to delete appointment: ${response.status}`);
                    return response.json();
                })
                .then(data => {
                    console.log(data.message);
                    updateAppointments();
                })
                .catch(error => {
                    console.error("Error deleting appointment:", error);
                    showModal(`Failed to delete appointment: ${error.message}`);
                });
        });
    };

    const handleEdit = (e) => {
        const button = e.currentTarget;
        const appointmentId = button.getAttribute("data-id");
        console.log("Opening edit modal for appointment ID:", appointmentId);

        const modal = new bootstrap.Modal(document.querySelector("#editAppointmentModal"));
        const form = document.querySelector("#edit-appointment-form");

        if (!form) {
            console.error("Edit appointment form not found");
            return;
        }

        // Set appointment ID
        form.querySelector("#edit-appointment-id").value = appointmentId;

        // Prefill existing values
        form.querySelector("#edit-appointment-date").value = button.getAttribute("data-date") || "";
        const staffSelect = form.querySelector("#edit-appointment-staff");
        const staffId = button.getAttribute("data-staff-id");
        if (staffId && staffSelect) {
            staffSelect.value = staffId;
        }
        const timeContainer = form.querySelector("#edit-appointment-time-container");
        timeContainer.dataset.selectedTime = button.getAttribute("data-time") || "";

        // Load time slots
        loadTimeSlots(appointmentId, form);
        modal.show();
    };

    const loadTimeSlots = (appointmentId, form) => {
        const prefix = form.id === "edit-appointment-form" ? "edit-" : "";
        const staffSelect = form.querySelector(`#${prefix}appointment-staff`);
        const appointmentDate = form.querySelector(`#${prefix}appointment-date`);
        const timeSlotsContainer = form.querySelector(`#${prefix}appointment-time-container`);
        const defaultMessage = timeSlotsContainer.querySelector(".default-time-slots-message");

        if (!staffSelect || !appointmentDate || !timeSlotsContainer) {
            console.error("Required elements missing:", {
                staffSelect: !!staffSelect,
                appointmentDate: !!appointmentDate,
                timeSlotsContainer: !!timeSlotsContainer
            });
            return;
        }

        let selectedDate = appointmentDate.value;
        if (selectedDate) {
            try {
                const dateObj = new Date(selectedDate);
                if (isNaN(dateObj.getTime())) throw new Error("Invalid date");
                selectedDate = dateObj.toISOString().split('T')[0];
                console.log("Normalized date:", selectedDate);
            } catch (error) {
                console.error("Date normalization failed:", error);
                timeSlotsContainer.innerHTML = `
                    <div class="alert alert-danger">
                        <i class="fas fa-times-circle me-2"></i> Invalid date format.
                    </div>
                `;
                defaultMessage?.classList.add("d-none");
                return;
            }
        }

        const staffId = staffSelect.value;
        if (!staffId || !selectedDate) {
            timeSlotsContainer.innerHTML = `
                <div class="alert alert-danger">
                    <i class="fas fa-times-circle me-2"></i> Please select staff and date.
                </div>
            `;
            defaultMessage?.classList.add("d-none");
            return;
        }

        defaultMessage?.classList.add("d-none");
        timeSlotsContainer.innerHTML = `
            <div class="text-center py-3">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
                <p class="mt-2">Loading time slots...</p>
            </div>
        `;

        console.log("Fetching time slots for appointment ID:", appointmentId);
        const url = appointmentId
            ? `${window.location.origin}/SalonManage/FetchAppointmentServicesServlet?appointmentId=${appointmentId}`
            : `${window.location.origin}/SalonManage/service-list`;

        fetch(url)
            .then(response => {
                if (!response.ok) throw new Error(`Failed to fetch services: ${response.status}`);
                return response.json();
            })
            .then(data => {
                let serviceIds;
                if (appointmentId) {
                    if (!data.success || !data.data) throw new Error(data.message || "No services found");
                    serviceIds = data.data;
                } else {
                    const selectedServices = Array.from(form.querySelector(`#${prefix}appointment-service`).selectedOptions).map(option => option.value);
                    serviceIds = selectedServices;
                }
                return fetch(`${window.location.origin}/SalonManage/book-appointment?action=fetchTimeSlots&staffId=${staffId}&date=${selectedDate}&serviceIds=${encodeURIComponent(JSON.stringify(serviceIds))}`);
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
                            <i class="fas fa-exclamation-circle me-2"></i> No available time slots.
                            ${data.message ? `<br>Reason: ${data.message}` : ""}
                        </div>
                    `;
                    return;
                }

                data.timeSlots.forEach(slot => {
                    const button = document.createElement("button");
                    button.type = "button";
                    button.classList.add("btn", "btn-outline-primary", "m-1", "time-slot");
                    button.dataset.time = slot;
                    button.textContent = formatTimeDisplay(slot);
                    button.addEventListener("click", function (event) {
                        event.preventDefault();
                        timeSlotsContainer.querySelectorAll(".time-slot").forEach(b => {
                            b.classList.remove("btn-primary");
                            b.classList.add("btn-outline-primary");
                        });
                        this.classList.add("btn-primary");
                        this.classList.remove("btn-outline-primary");
                        timeSlotsContainer.dataset.selectedTime = slot;
                        console.log("Time slot selected:", slot);
                    });
                    timeSlotsContainer.appendChild(button);
                });

                const currentTime = timeSlotsContainer.dataset.selectedTime;
                if (currentTime) {
                    const matchingButton = Array.from(timeSlotsContainer.querySelectorAll(".time-slot")).find(b => b.dataset.time === currentTime);
                    if (matchingButton) {
                        matchingButton.classList.add("btn-primary");
                        matchingButton.classList.remove("btn-outline-primary");
                    }
                }
            })
            .catch(error => {
                console.error("Error loading time slots:", error);
                timeSlotsContainer.innerHTML = `
                    <div class="alert alert-danger">
                        <i class="fas fa-times-circle me-2"></i> Error loading time slots: ${error.message}
                    </div>
                `;
                defaultMessage?.classList.remove("d-none");
            });
    };

    const populateModalStaff = () => {
        const staffSelects = [
            document.querySelector("#appointment-staff"),
            document.querySelector("#edit-appointment-staff"),
            document.querySelector("#appointment-staff-filter")
        ];
        staffSelects.forEach(staffSelect => {
            if (staffSelect) {
                staffSelect.innerHTML = `<option value="" selected disabled>Select Staff</option>`;
                fetch(`${window.location.origin}/SalonManage/staff`)
                    .then(response => {
                        if (!response.ok) throw new Error("Failed to fetch staff list");
                        return response.json();
                    })
                    .then(data => {
                        staffList = data || [];
                        staffSelect.innerHTML = '<option value="">All Stylists</option>';
                        staffList.forEach(staff => {
                            staffSelect.innerHTML += `
                                <option value="${staff.staffId}">${staff.firstName} ${staff.lastName}</option>`;
                        });
                    })
                    .catch(error => {
                        console.error("Error fetching staff list:", error);
                        staffSelect.innerHTML = `<option value="" disabled>Error loading staff</option>`;
                    });
            }
        });
    };

    const populateServices = () => {
        fetch("/SalonManage/service-list")
            .then(response => {
                if (!response.ok) throw new Error("Failed to fetch services list");
                return response.json();
            })
            .then(data => {
                servicesList = data || [];
                const serviceSelects = [
                    document.querySelector("#appointment-service"),
                    document.querySelector("#edit-appointment-service")
                ];
                serviceSelects.forEach(serviceSelect => {
                    if (serviceSelect) {
                        serviceSelect.innerHTML = servicesList.map(category => `
                            <optgroup label="${category.categoryName}">
                                ${category.services.map(service => `
                                    <option value="${service.serviceId}">${service.name}</option>
                                `).join('')}
                            </optgroup>
                        `).join('');
                        if (servicesList.length === 0) {
                            serviceSelect.innerHTML = '<option value="" disabled>No services available</option>';
                        }
                    }
                });
            })
            .catch(error => {
                console.error("Error fetching services list:", error);
                showModal("Failed to load services list: " + error.message);
            });
    };

    const loadAppointments = (
        dateRange = "this-week",
        staff = "",
        status = "",
        search = "",
        page = currentPage,
        startDate = "",
        endDate = ""
    ) => {
        const appointmentsTable = document.querySelector("#appointments-table");
        if (!appointmentsTable) {
            console.error("Appointments table not found");
            return;
        }

        appointmentsTable.innerHTML = `
            <tr>
                <td colspan="9" class="text-center">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                </td>
            </tr>`;

        const queryParams = new URLSearchParams({
            dateRange,
            staff,
            status,
            search,
            page,
            limit: pageSize,
            sortColumn,
            sortDirection
        });
        if (dateRange === "custom" && startDate && endDate) {
            queryParams.append("startDate", startDate);
            queryParams.append("endDate", endDate);
        }

        console.log("Fetching appointments with params: " + queryParams.toString());

        fetch(`/SalonManage/fetchAppointments?${queryParams}`)
            .then(response => {
                if (!response.ok) throw new Error(`Failed to fetch appointments: ${response.status}`);
                return response.json();
            })
            .then(data => {
                appointmentsTable.innerHTML = "";
                if (!data.appointments || data.appointments.length === 0) {
                    appointmentsTable.innerHTML = `
                        <tr>
                            <td colspan="9" class="text-center">No appointments found.</td>
                        </tr>`;
                } else {
                    data.appointments.forEach(appointment => {
                        const isCompleted = appointment.status === "completed";
                        const isCancelled = appointment.status === "cancelled";
                        let actionsHtml = "";

                        if (isCancelled) {
                            actionsHtml = `
                                <button class="btn btn-secondary btn-sm" disabled>Cancelled</button>
                            `;
                        } else {
                            actionsHtml = `
                                <button class="btn btn-primary btn-sm edit-appointment" 
                                        data-id="${appointment.id}" 
                                        data-date="${appointment.date || ''}" 
                                        data-time="${appointment.time || ''}" 
                                        data-staff-id="${appointment.staffId || ''}" 
                                        data-service-ids="${appointment.serviceIds || ''}"
                                        data-status="${appointment.status || 'pending'}"
                                        data-notes="${appointment.notes || ''}"
                                        ${isCompleted ? 'disabled style="opacity: 0.5;"' : ''}>Edit</button>
                                <button class="btn btn-warning btn-sm complete-appointment" 
                                        data-id="${appointment.id}" 
                                        ${isCompleted ? 'disabled' : ''}>Mark as Completed</button>
                                <button class="btn btn-danger btn-sm delete-appointment" 
                                        data-id="${appointment.id}"
                                        ${isCompleted ? 'disabled style="opacity: 0.5;"' : ''}>Delete</button>
                            `;
                        }

                        appointmentsTable.innerHTML += `
                            <tr>
                                <td>${appointment.date || "N/A"}</td>
                                <td>${appointment.time || "N/A"}</td>
                                <td>${appointment.customerName || "N/A"}</td>
                                <td>${appointment.serviceName || "None"}</td>
                                <td>${appointment.staffName || "N/A"}</td>
                                <td>${appointment.status || "N/A"}</td>
                                <td>${appointment.price ? appointment.price.toFixed(2) : "0.00"}</td>
                                <td>${appointment.paymentType || "N/A"}</td>
                                <td>${actionsHtml}</td>
                            </tr>`;
                    });
                }

                document.getElementById("shown-appointments").textContent = data.appointments.length;
                document.getElementById("total-appointments").textContent = data.total;
                document.getElementById("prev-page-btn").disabled = page <= 1;
                document.getElementById("next-page-btn").disabled = page * pageSize >= data.total;

                attachButtonListeners();
                initializePopovers();
            })
            .catch(error => {
                console.error("Error fetching appointments:", error);
                appointmentsTable.innerHTML = `
                    <tr>
                        <td colspan="9" class="text-center text-danger">Error loading appointments: ${error.message}</td>
                    </tr>`;
                setTimeout(() => loadAppointments(dateRange, staff, status, search, page, startDate, endDate), 5000);
            });
    };

    const initializePopovers = () => {
        console.log("Initializing popovers for complete only");
        document.querySelectorAll(".complete-appointment").forEach(button => {
            if (!button.disabled) {
                const appointmentId = button.getAttribute("data-id");
                const popoverContent = `
                    <form class="complete-popover-form" data-id="${appointmentId}">
                        <input type="hidden" name="appointmentId" value="${appointmentId}">
                        <select class="form-select mb-1" name="paymentType" required>
                            <option value="">Not Set</option>
                            <option value="Cash">Cash</option>
                            <option value="Card">Card</option>
                            <option value="UPI">UPI</option>
                        </select>
                        <button type="submit" class="btn btn-sm btn-primary">Confirm</button>
                        <button type="button" class="btn btn-sm btn-secondary cancel-popover">Cancel</button>
                    </form>
                `;

                new bootstrap.Popover(button, {
                    html: true,
                    content: popoverContent,
                    trigger: 'click',
                    placement: 'top',
                    sanitize: false,
                    container: 'body'
                });

                button.dataset.popoverButton = 'complete';
            }
        });
    };

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

    document.addEventListener("submit", (e) => {
        if (e.target.classList.contains("complete-popover-form")) {
            e.preventDefault();
            const form = e.target;
            const appointmentId = form.querySelector('input[name="appointmentId"]').value;
            const paymentType = form.querySelector('select[name="paymentType"]').value;
            const body = `appointmentId=${encodeURIComponent(appointmentId)}&paymentType=${encodeURIComponent(paymentType)}`;
            console.log("Complete POST body:", body);
            const button = document.querySelector(`.complete-appointment[data-id="${appointmentId}"]`);
            fetch(`${window.location.origin}/SalonManage/mark-completed`, {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: body
            })
                .then(response => {
                    if (!response.ok) {
                        return response.json().then(err => {
                            throw new Error(`Failed to mark as completed: ${response.status} - ${err.message || 'Unknown error'}`);
                        });
                    }
                    return response.json();
                })
                .then(data => {
                    console.log(data.message);
                    hidePopover(button, form);
                    updateAppointments();
                })
                .catch(error => {
                    console.error("Error marking as completed:", error);
                    showModal(`Error: ${error.message}`);
                    hidePopover(button, form);
                });
        }
    });

    document.addEventListener("submit", (e) => {
        if (e.target.id === "edit-appointment-form") {
            e.preventDefault();
            const form = e.target;
            const appointmentId = form.querySelector("#edit-appointment-id").value;
            const staffId = form.querySelector("#edit-appointment-staff").value;
            const appointmentDate = form.querySelector("#edit-appointment-date").value;
            const timeContainer = form.querySelector("#edit-appointment-time-container");
            const appointmentTime = timeContainer.dataset.selectedTime;

            console.log("Submitting reschedule for appointment ID:", appointmentId, 
                        "Staff ID:", staffId, "Date:", appointmentDate, "Time:", appointmentTime);

            if (!staffId) {
                showModal("Please select a staff member.");
                return;
            }
            if (!appointmentDate) {
                showModal("Please select a date.");
                return;
            }
            if (!appointmentTime) {
                showModal("Please select a time slot.");
                return;
            }

            const submitBtn = form.querySelector("button[type='submit']");
            const spinner = submitBtn.querySelector(".spinner-border") || document.createElement("span");
            spinner.className = "spinner-border spinner-border-sm me-2";
            spinner.setAttribute("role", "status");
            spinner.innerHTML = `<span class="visually-hidden">Loading...</span>`;
            submitBtn.prepend(spinner);

            spinner.classList.remove("d-none");
            submitBtn.disabled = true;

            fetch(`${window.location.origin}/SalonManage/RescheduleAppointmentServlet`, {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: `appointmentId=${encodeURIComponent(appointmentId)}&staffId=${encodeURIComponent(staffId)}&appointmentDate=${encodeURIComponent(appointmentDate)}&appointmentTime=${encodeURIComponent(appointmentTime)}`
            })
                .then(response => {
                    if (!response.ok) {
                        return response.json().then(err => {
                            throw new Error(`Failed to reschedule appointment: ${response.status} - ${err.message || 'Unknown error'}`);
                        });
                    }
                    return response.json();
                })
                .then(data => {
                    console.log("Reschedule response:", data);
                    spinner.classList.add("d-none");
                    submitBtn.disabled = false;

                    if (data.success) {
                        const modal = bootstrap.Modal.getInstance(document.querySelector("#editAppointmentModal"));
                        modal.hide();
                        form.reset();
                        timeContainer.dataset.selectedTime = "";
                        timeContainer.innerHTML = `
                            <div class="default-time-slots-message text-muted text-center py-3">
                                Please select a staff and date to view available time slots.
                            </div>
                        `;

                        // Show success modal
                        const successModal = document.createElement("div");
                        successModal.className = "modal fade";
                        successModal.id = "rescheduleSuccessModal";
                        successModal.innerHTML = `
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h5 class="modal-title">Reschedule Successful</h5>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                    </div>
                                    <div class="modal-body">
                                        <p>Appointment has been rescheduled successfully.</p>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-primary" data-bs-dismiss="modal">OK</button>
                                    </div>
                                </div>
                            </div>
                        `;
                        document.body.appendChild(successModal);
                        const bsSuccessModal = new bootstrap.Modal(successModal);
                        bsSuccessModal.show();

                        successModal.addEventListener("hidden.bs.modal", () => {
                            successModal.remove();
                        });

                        updateAppointments();
                    } else {
                        console.error("Rescheduling failed:", data.message);
                        showModal(`Error rescheduling appointment: ${data.message}`);
                    }
                })
                .catch(error => {
                    console.error("Error rescheduling appointment:", error);
                    showModal(`Error: ${error.message}`);
                });
        }
    });

    const addAppointmentForm = document.querySelector("#add-appointment-form");

    const validateCustomer = async (emailOrPhone, inputElement, feedbackElement) => {
    if (!emailOrPhone) {
        if (inputElement) {
            inputElement.classList.remove('is-valid', 'is-invalid');
        }
        if (feedbackElement) {
            feedbackElement.innerHTML = '';
        }
        return false;
    }

    try {
        const response = await fetch(`${window.location.origin}/SalonManage/validate-customer`, {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: `emailOrPhone=${encodeURIComponent(emailOrPhone)}`
        });
        
        if (!response.ok) throw new Error(`Failed to validate customer: ${response.status}`);
        
        const data = await response.json();
        
        if (inputElement) {
            if (data.success) {
                inputElement.classList.add('is-valid');
                inputElement.classList.remove('is-invalid');
            } else {
                inputElement.classList.add('is-invalid');
                inputElement.classList.remove('is-valid');
            }
        }
        
        if (feedbackElement) {
            if (data.success) {
                feedbackElement.innerHTML = `
                    <div class="valid-feedback d-block">
                        <i class="fas fa-check-circle me-2"></i> Customer found
                    </div>
                `;
            } else {
                feedbackElement.innerHTML = `
                    <div class="invalid-feedback d-block">
                        <i class="fas fa-exclamation-circle me-2"></i> ${data.message || "Customer not found"}
                    </div>
                `;
            }
        }
        
        return data.success;
        
    } catch (error) {
        console.error("Error validating customer:", error);
        if (inputElement) {
            inputElement.classList.add('is-invalid');
            inputElement.classList.remove('is-valid');
        }
        if (feedbackElement) {
            feedbackElement.innerHTML = `
                <div class="invalid-feedback d-block">
                    <i class="fas fa-exclamation-circle me-2"></i> Error validating customer
                </div>
            `;
        }
        return false;
    }
};

    const setupFormListeners = (form) => {
        const prefix = form.id === "edit-appointment-form" ? "edit-" : "";
        const staffSelect = form.querySelector(`#${prefix}appointment-staff`);
        const appointmentDate = form.querySelector(`#${prefix}appointment-date`);
        const appointmentIdInput = form.querySelector(`#${prefix}appointment-id`);
        const customerInput = form.querySelector("#appointment-customer-email-or-phone");

        const updateTimeSlots = () => {
            const appointmentId = appointmentIdInput?.value || "";
            if (appointmentId && form.id === "edit-appointment-form") {
                loadTimeSlots(appointmentId, form);
            } else {
                loadTimeSlots("", form);
            }
        };

        if (customerInput) {
        const feedbackElement = document.createElement("div");
        feedbackElement.className = "feedback-container mt-1";
        customerInput.parentElement.appendChild(feedbackElement);

        // Add input event listener with debounce
        customerInput.addEventListener("input", debounce(async () => {
            const emailOrPhone = customerInput.value.trim();
            await validateCustomer(emailOrPhone, customerInput, feedbackElement);
        }, 500));
    }

        staffSelect?.addEventListener("change", updateTimeSlots);
        appointmentDate?.addEventListener("change", updateTimeSlots);
        appointmentDate?.addEventListener("input", function () {
            const value = this.value;
            if (value && !/^\d{4}-\d{2}-\d{2}$/.test(value)) {
                this.setCustomValidity("Please enter date in YYYY-MM-DD format");
            } else {
                this.setCustomValidity("");
            }
        });
    };

    document.querySelector("#save-appointment-btn")?.addEventListener("click", async () => {
    if (!addAppointmentForm.checkValidity()) {
        addAppointmentForm.reportValidity();
        return;
    }

    const customerInput = addAppointmentForm.querySelector("#appointment-customer-email-or-phone");
    const customerEmailOrPhone = customerInput.value;
    const serviceIds = Array.from(addAppointmentForm.querySelector('select[name="serviceIds"]').selectedOptions).map(option => option.value);
    const staffId = addAppointmentForm.querySelector('select[name="staffId"]').value;
    const status = addAppointmentForm.querySelector('select[name="status"]').value;
    const appointmentDate = addAppointmentForm.querySelector('input[name="appointmentDate"]').value;
    const timeContainer = addAppointmentForm.querySelector("#appointment-time-container");
    const appointmentTime = timeContainer.dataset.selectedTime;
    const notes = addAppointmentForm.querySelector('textarea[name="notes"]').value;

    if (!serviceIds.length) {
        showModal("Please select at least one service.");
        return;
    }
    if (!appointmentTime) {
        showModal("Please select a time slot.");
        return;
    }
    if (!customerEmailOrPhone) {
        showModal("Please enter customer email or phone.");
        return;
    }

    const saveBtn = document.querySelector("#save-appointment-btn");
    const spinner = document.createElement("span");
    spinner.className = "spinner-border spinner-border-sm me-2";
    spinner.setAttribute("role", "status");
    spinner.innerHTML = `<span class="visually-hidden">Loading...</span>`;
    saveBtn.prepend(spinner);
    spinner.classList.remove("d-none");
    saveBtn.disabled = true;

    try {
        // Check real-time validation result
        if (customerInput.classList.contains('is-invalid')) {
            throw new Error("Invalid customer email or phone");
        }

        const serviceIdsString = serviceIds.join(",");
        // Fixed: Properly encode each parameter separately
        const body = new URLSearchParams();
        body.append('customerEmailOrPhone', customerEmailOrPhone);
        body.append('serviceIds', serviceIdsString);
        body.append('staffId', staffId);
        body.append('status', status);
        body.append('appointmentDate', appointmentDate);
        body.append('appointmentTime', appointmentTime);
        body.append('notes', notes || ''); // Handle empty notes

        console.log("Add appointment POST body:", body.toString());

        const response = await fetch("/SalonManage/add-appointment", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: body
        });

        if (!response.ok) {
            const err = await response.json();
            throw new Error(`Failed to add appointment: ${response.status} - ${err.message || 'Unknown error'}`);
        }

        const data = await response.json();
        console.log("Appointment added successfully:", data.message);
        const modal = bootstrap.Modal.getInstance(document.querySelector("#addAppointmentModal"));
        modal.hide();
        addAppointmentForm.reset();
        timeContainer.dataset.selectedTime = "";
        timeContainer.innerHTML = `
            <div class="default-time-slots-message text-muted text-center py-3">
                Please select a staff and date to view available time slots.
            </div>
        `;
        const feedbackElement = customerInput.parentElement.querySelector(".feedback-container");
        if (feedbackElement) feedbackElement.remove();
        updateAppointments();
    } catch (error) {
        console.error("Error adding appointment:", error);
        showModal(`Failed to add appointment: ${error.message}`);
    } finally {
        spinner.remove();
        saveBtn.disabled = false;
    }
});

    const debounce = (func, wait) => {
        let timeout;
        return (...args) => {
            clearTimeout(timeout);
            timeout = setTimeout(() => func.apply(this, args), wait);
        };
    };

    const updateAppointments = () => {
        const dateFilter = document.querySelector("#appointment-date-filter");
        const staffFilter = document.querySelector("#appointment-staff-filter");
        const statusFilter = document.querySelector("#appointment-status-filter");
        const searchInput = document.querySelector("#appointment-search");
        const startDateInput = document.querySelector("#custom-start-date");
        const endDateInput = document.querySelector("#custom-end-date");

        const dateRange = dateFilter ? dateFilter.value : "this-week";
        const staff = staffFilter ? staffFilter.value : "";
        const status = statusFilter ? statusFilter.value : "";
        const search = searchInput ? searchInput.value.trim() : "";
        const startDate = startDateInput ? startDateInput.value : "";
        const endDate = endDateInput ? endDateInput.value : "";

        loadAppointments(dateRange, staff, status, search, currentPage, startDate, endDate);
    };

    const setupSortListeners = () => {
        const sortableHeaders = document.querySelectorAll(".table th[data-sort]");
        sortableHeaders.forEach(header => {
            header.addEventListener("click", () => {
                const column = header.dataset.sort;
                if (sortColumn === column) {
                    sortDirection = sortDirection === "asc" ? "desc" : "asc";
                } else {
                    sortColumn = column;
                    sortDirection = "asc";
                }
                sortableHeaders.forEach(h => h.classList.remove("sorted-asc", "sorted-desc"));
                header.classList.add(`sorted-${sortDirection}`);
                currentPage = 1;
                updateAppointments();
            });
        });
    };

    document.querySelector("#appointment-date-filter")?.addEventListener("change", () => {
        const customRange = document.querySelector("#custom-date-range");
        if (document.querySelector("#appointment-date-filter").value === "custom") {
            customRange.style.display = "block";
        } else {
            customRange.style.display = "none";
            updateAppointments();
        }
    });

    document.querySelector("#appointment-staff-filter")?.addEventListener("change", updateAppointments);
    document.querySelector("#appointment-status-filter")?.addEventListener("change", updateAppointments);
    document.querySelector("#appointment-search")?.addEventListener("input", debounce(updateAppointments, 300));
    document.querySelector("#custom-start-date")?.addEventListener("change", updateAppointments);
    document.querySelector("#custom-end-date")?.addEventListener("change", updateAppointments);

    document.getElementById("prev-page-btn")?.addEventListener("click", () => {
        if (currentPage > 1) {
            currentPage--;
            updateAppointments();
        }
    });

    document.getElementById("next-page-btn")?.addEventListener("click", () => {
        currentPage++;
        updateAppointments();
    });

    setupFormListeners(document.querySelector("#add-appointment-form"));
    setupFormListeners(document.querySelector("#edit-appointment-form"));
    setupSortListeners();
    populateModalStaff();
    populateServices();
    loadAppointments();
});