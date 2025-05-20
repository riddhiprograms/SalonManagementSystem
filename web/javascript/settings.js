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
    const settingsTab = document.getElementById("settings-tab-btn");
    if (settingsTab) {
        settingsTab.addEventListener("shown.bs.tab", initializeSettingsTab);
        if (settingsTab.classList.contains("active")) {
            initializeSettingsTab();
        }
    } else {
        initializeSettingsTab();
    }
});

function initializeSettingsTab() {
    const salonCard = document.getElementById("salon-card");
    const editSalonBtn = document.getElementById("edit-salon-btn");
    const cancelEditBtn = document.getElementById("cancel-edit-btn");
    const salonInfoForm = document.getElementById("salon-info-form");
    const hoursCard = document.getElementById("hours-card");
    const editHoursBtn = document.getElementById("edit-hours-btn");
    const cancelHoursBtn = document.getElementById("cancel-hours-btn");
    const businessHoursForm = document.getElementById("business-hours-form");
    const notificationCard = document.getElementById("notification-card");
    const editNotificationBtn = document.getElementById("edit-notification-btn");
    const cancelNotificationBtn = document.getElementById("cancel-notification-btn");
    const notificationSettingsForm = document.getElementById("notification-settings-form");

    if (!salonCard || !editSalonBtn || !cancelEditBtn || !salonInfoForm ||
        !hoursCard || !editHoursBtn || !cancelHoursBtn || !businessHoursForm ||
        !notificationCard || !editNotificationBtn || !cancelNotificationBtn || !notificationSettingsForm) {
        console.error("Settings tab elements not found");
        return;
    }

    fetchSalonInfo();
    fetchBusinessHours();
    fetchNotificationSettings();

    editSalonBtn.addEventListener("click", () => {
        salonCard.classList.add("flipped");
    });

    cancelEditBtn.addEventListener("click", () => {
        salonCard.classList.remove("flipped");
        fetchSalonInfo();
    });

    salonInfoForm.addEventListener("submit", handleSalonFormSubmit);

    editHoursBtn.addEventListener("click", () => {
        console.log("Edit hours button clicked, flipping card");
        hoursCard.classList.add("flipped");
    });

    cancelHoursBtn.addEventListener("click", () => {
        console.log("Cancel hours button clicked, resetting form and flipping back");
        hoursCard.classList.remove("flipped");
        businessHoursForm.reset();
        fetchBusinessHours();
    });

    businessHoursForm.addEventListener("submit", handleBusinessHoursFormSubmit);

    editNotificationBtn.addEventListener("click", () => {
        console.log("Edit notification button clicked, flipping card");
        notificationCard.classList.add("flipped");
    });

    cancelNotificationBtn.addEventListener("click", () => {
        console.log("Cancel notification button clicked, resetting form and flipping back");
        notificationCard.classList.remove("flipped");
        notificationSettingsForm.reset();
        fetchNotificationSettings();
    });

    notificationSettingsForm.addEventListener("submit", handleNotificationSettingsFormSubmit);
}

function fetchSalonInfo() {
    console.log("Fetching salon info...");
    fetch("/SalonManage/get-salon-info")
        .then((response) => {
            if (!response.ok) {
                return response.text().then((text) => {
                    throw new Error(`HTTP error! status: ${response.status}, body: ${text.substring(0, 100)}...`);
                });
            }
            return response.json();
        })
        .then((data) => {
            console.log("Salon data received:", data);
            updateSalonDisplay(data);
            populateSalonForm(data);
        })
        .catch((error) => {
            console.error("Error fetching salon info:", error);
            const displayDiv = document.getElementById("salon-info-display");
            if (displayDiv) {
                displayDiv.innerHTML = `<p class="text-danger">Error loading salon information: ${error.message}</p>`;
            }
        });
}

function updateSalonDisplay(data) {
    const displayFields = {
        "display-salon-name": data.salonName,
        "display-branch-name": data.branchName,
        "display-address": data.address,
        "display-phone": data.phone,
        "display-email": data.email
    };

    for (const [id, value] of Object.entries(displayFields)) {
        const element = document.getElementById(id);
        if (element) {
            element.textContent = value || "N/A";
        } else {
            console.error(`Element with ID ${id} not found`);
        }
    }
}

function populateSalonForm(data) {
    const formFields = {
        "salon-name": data.salonName,
        "salon-branch": data.branchName,
        "salon-address": data.address,
        "salon-phone": data.phone,
        "salon-email": data.email
    };

    for (const [id, value] of Object.entries(formFields)) {
        const element = document.getElementById(id);
        if (element) {
            element.value = value || "";
        } else {
            console.error(`Element with ID ${id} not found`);
        }
    }
}

function handleSalonFormSubmit(event) {
    event.preventDefault();
    
    const formData = {
        salonName: document.getElementById("salon-name").value.trim(),
        branchName: document.getElementById("salon-branch").value.trim(),
        address: document.getElementById("salon-address").value.trim(),
        phone: document.getElementById("salon-phone").value.trim(),
        email: document.getElementById("salon-email").value.trim()
    };

    console.log("Form data to submit:", formData);

    if (!validateSalonData(formData)) {
        return;
    }

    fetch("/SalonManage/update-salon", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(formData)
    })
    .then((response) => {
        if (!response.ok) {
            return response.json().then(err => {
                throw new Error(err.error || `HTTP error! status: ${response.status}`);
            });
        }
        return response.json();
    })
    .then((data) => {
        showModal(data.message || "Salon information updated successfully!");
        document.getElementById("salon-card").classList.remove("flipped");
        fetchSalonInfo();
    })
    .catch((error) => {
        console.error("Update error:", error);
        showModal(`Failed to update salon info: ${error.message}`);
    });
}

function validateSalonData(data) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const phoneRegex = /^\d{10}$/;

    if (!data.salonName) {
        showModal("Please enter a salon name");
        return false;
    }
    if (!data.branchName) {
        showModal("Please enter a branch name");
        return false;
    }
    if (!data.address) {
        showModal("Please enter an address");
        return false;
    }
    if (!data.phone) {
        showModal("Please enter a phone number");
        return false;
    }
    if (!data.email) {
        showModal("Please enter an email address");
        return false;
    }
    if (!emailRegex.test(data.email)) {
        showModal("Please enter a valid email address");
        return false;
    }
    if (!phoneRegex.test(data.phone)) {
        showModal("Please enter a valid 10-digit phone number");
        return false;
    }
    return true;
}

function fetchBusinessHours() {
    console.log("Fetching business hours...");
    fetch("/SalonManage/get-business-hours")
        .then((response) => {
            if (!response.ok) {
                return response.text().then((text) => {
                    throw new Error(`HTTP error! status: ${response.status}, body: ${text.substring(0, 100)}...`);
                });
            }
            return response.json();
        })
        .then((data) => {
            console.log("Business hours data received:", data);
            updateBusinessHoursDisplay(data);
            populateBusinessHoursForm(data);
        })
        .catch((error) => {
            console.error("Error fetching business hours:", error);
            const displayDiv = document.getElementById("hours-display");
            if (displayDiv) {
                displayDiv.innerHTML = `<p class="text-danger">Error loading business hours: ${error.message}</p>`;
            }
        });
}

function updateBusinessHoursDisplay(data) {
    const hoursTableBody = document.getElementById("hours-table-body");
    if (!hoursTableBody) {
        console.error("Hours table body not found");
        return;
    }

    hoursTableBody.innerHTML = data.map(hour => `
        <tr>
            <td>${hour.day}</td>
            <td>${hour.isClosed ? 'Closed' : (hour.openTime || 'N/A')}</td>
            <td>${hour.isClosed ? 'Closed' : (hour.closeTime || 'N/A')}</td>
            <td>${hour.isClosed ? 'Closed' : 'Open'}</td>
        </tr>
    `).join('');
}

function populateBusinessHoursForm(data) {
    const form = document.getElementById("business-hours-form");
    if (!form) {
        console.error("Business hours form not found");
        return;
    }

    data.forEach(hour => {
        const openInput = form.querySelector(`input[name="open[${hour.day}]"]`);
        const closeInput = form.querySelector(`input[name="close[${hour.day}]"]`);
        const closedInput = form.querySelector(`input[name="closed[${hour.day}]"]`);

        if (openInput && closeInput && closedInput) {
            openInput.value = hour.isClosed ? '' : (hour.openTime || '');
            closeInput.value = hour.isClosed ? '' : (hour.closeTime || '');
            closedInput.checked = hour.isClosed;
        } else {
            console.error(`Form elements for ${hour.day} not found`);
        }
    });
}

function handleBusinessHoursFormSubmit(event) {
    event.preventDefault();

    const formData = new FormData(event.target);
    const hours = [];
    const days = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];

    for (const day of days) {
        const openTime = formData.get(`open[${day}]`);
        const closeTime = formData.get(`close[${day}]`);
        const isClosed = formData.get(`closed[${day}]`) === "on";

        if (!isClosed && (!openTime || !closeTime)) {
            showModal(`Please provide open and close times for ${day} or mark it as closed.`);
            return;
        }

        hours.push({
            day,
            openTime: openTime || null,
            closeTime: closeTime || null,
            isClosed
        });
    }

    console.log("Business hours data to submit:", hours);

    fetch("/SalonManage/update-business-hours", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(hours)
    })
        .then((response) => {
            if (!response.ok) {
                return response.json().then(err => {
                    throw new Error(err.error || `HTTP error! status: ${response.status}`);
                });
            }
            return response.json();
        })
        .then((data) => {
            showModal(data.message || "Business hours updated successfully!");
            document.getElementById("hours-card").classList.remove("flipped");
            fetchBusinessHours();
        })
        .catch((error) => {
            console.error("Business hours update error:", error);
            showModal(`Failed to update business hours: ${error.message}`);
        });
}

function fetchNotificationSettings() {
    console.log("Fetching notification settings...");
    fetch("/SalonManage/get-notification-settings")
        .then((response) => {
            if (!response.ok) {
                return response.text().then((text) => {
                    throw new Error(`HTTP error! status: ${response.status}, body: ${text.substring(0, 100)}...`);
                });
            }
            return response.json();
        })
        .then((data) => {
            console.log("Notification settings received:", data);
            updateNotificationSettingsDisplay(data);
            populateNotificationSettingsForm(data);
        })
        .catch((error) => {
            console.error("Error fetching notification settings:", error);
            const displayDiv = document.getElementById("notification-settings-display");
            if (displayDiv) {
                displayDiv.innerHTML = `<p class="text-danger">Error loading notification settings: ${error.message}</p>`;
            }
        });
}

function updateNotificationSettingsDisplay(data) {
    const emailDisplay = document.getElementById("display-email-notifications");
    const smsDisplay = document.getElementById("display-sms-notifications");
    const reminderDisplay = document.getElementById("display-reminder-time");

    if (!emailDisplay || !smsDisplay || !reminderDisplay) {
        console.error("Notification display elements not found");
        return;
    }

    emailDisplay.textContent = data.emailNotifications ? "Enabled" : "Disabled";
    emailDisplay.className = `badge ${data.emailNotifications ? "bg-success" : "bg-danger"}`;
    smsDisplay.textContent = data.smsNotifications ? "Enabled" : "Disabled";
    smsDisplay.className = `badge ${data.smsNotifications ? "bg-success" : "bg-danger"}`;
    reminderDisplay.textContent = formatReminderTime(data.reminderTime);
}

function populateNotificationSettingsForm(data) {
    const emailInput = document.getElementById("email-notifications");
    const smsInput = document.getElementById("sms-notifications");
    const reminderInput = document.getElementById("reminder-time");

    if (!emailInput || !smsInput || !reminderInput) {
        console.error("Notification form elements not found");
        return;
    }

    emailInput.checked = data.emailNotifications;
    smsInput.checked = data.smsNotifications;
    reminderInput.value = data.reminderTime;
}

function handleNotificationSettingsFormSubmit(event) {
    event.preventDefault();

    const formData = {
        emailNotifications: document.getElementById("email-notifications").checked,
        smsNotifications: document.getElementById("sms-notifications").checked,
        reminderTime: parseInt(document.getElementById("reminder-time").value)
    };

    console.log("Notification settings to submit:", formData);

    if (!validateNotificationSettings(formData)) {
        return;
    }

    fetch("/SalonManage/update-notification-settings", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(formData)
    })
        .then((response) => {
            if (!response.ok) {
                return response.json().then(err => {
                    throw new Error(err.error || `HTTP error! status: ${response.status}`);
                });
            }
            return response.json();
        })
        .then((data) => {
            showModal(data.message || "Notification settings updated successfully!");
            document.getElementById("notification-card").classList.remove("flipped");
            fetchNotificationSettings();
        })
        .catch((error) => {
            console.error("Notification settings update error:", error);
            showModal(`Failed to update notification settings: ${error.message}`);
        });
}

function validateNotificationSettings(data) {
    if (![1, 2, 3, 7].includes(data.reminderTime)) {
        showModal("Please select a valid reminder time.");
        return false;
    }
    return true;
}

function formatReminderTime(days) {
    switch (days) {
        case 1: return "1 day before";
        case 2: return "2 days before";
        case 3: return "3 days before";
        case 7: return "1 week before";
        default: return "N/A";
    }
}