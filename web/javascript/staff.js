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

document.addEventListener("DOMContentLoaded", function () {
      console.log("Admin script loaded, version: p2g3h4i5-e7f8-6a9b-3c4d-1f2g3h4i5e6f");

      // Add Staff Modal - Save Button
      const saveStaffButton = document.getElementById("save-staff-btn");
      const addStaffForm = document.getElementById("add-staff-form");

      if (!addStaffForm) {
          console.error("Add staff form not found");
          return;
      }
      if (!saveStaffButton) {
          console.error("Save staff button not found");
          return;
      }

      console.log("Add staff form found:", addStaffForm);
      console.log("Form inputs:", addStaffForm.querySelectorAll('input, select'));

      saveStaffButton.addEventListener("click", async function (event) {
          event.preventDefault();

          // Collect form data
          const formData = {
              firstName: addStaffForm.querySelector('[name="staff-first_name"]')?.value || '',
              lastName: addStaffForm.querySelector('[name="staff-last_name"]')?.value || '',
              role: addStaffForm.querySelector('[name="staff-role"]')?.value || '',
              specialties: addStaffForm.querySelector('[name="staff-specialization"]')?.value || '',
              experience: addStaffForm.querySelector('[name="staff-experience"]')?.value || '0',
              phone: addStaffForm.querySelector('[name="staff-phone"]')?.value || '',
              email: addStaffForm.querySelector('[name="staff-email"]')?.value || '',
              imageBase64: null
          };

          // Handle image as Base64
          const imageInput = addStaffForm.querySelector('[name="staffImage"]');
          if (!imageInput) {
              console.error("Image input not found");
              showModal("Error: Image upload field not found.");
              return;
          }

          if (imageInput.files && imageInput.files[0]) {
              try {
                  formData.imageBase64 = await new Promise((resolve, reject) => {
                      const reader = new FileReader();
                      reader.onload = () => resolve(reader.result.split(',')[1]);
                      reader.onerror = reject;
                      reader.readAsDataURL(imageInput.files[0]);
                  });
              } catch (error) {
                  console.error("Error reading image:", error);
                  showModal("Failed to read image file.");
                  return;
              }
          } else {
              console.log("No image selected");
          }

          console.log("Form data:", formData);

          // Submit as JSON
          fetch(window.location.origin + "/SalonManage/add-staff", {
              method: "POST",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify(formData)
          })
          .then(response => {
              console.log("Add staff response status:", response.status);
              return response.json();
          })
          .then(data => {
              console.log("Add staff response data:", data);
              if (data.success) {
                  showModal("Staff member added successfully!");
                  location.reload();
              } else {
                  showModal("Failed to add staff member: " + (data.message || "Unknown error"));
              }
          })
          .catch(error => {
              console.error("Error adding staff member:", error);
              showModal("An error occurred while adding the staff member.");
          });
      });

      // Fetch and render staff
      const staffCardsContainer = document.getElementById("staff-cards");
      if (staffCardsContainer) {
          fetch(window.location.origin + "/SalonManage/get-staff")
              .then(response => {
                  if (!response.ok) throw new Error("Failed to fetch staff list");
                  return response.json();
              })
              .then(staffList => {
                  staffCardsContainer.innerHTML = "";
                  if (!staffList || staffList.length === 0) {
                      staffCardsContainer.innerHTML = `
                          <div class="col-12 text-center py-5">
                              <div class="alert alert-info">
                                  <i class="fas fa-users-slash me-2"></i>
                                  No staff members found. Add your first team member to get started.
                              </div>
                          </div>
                      `;
                      return;
                  }
                  staffList.forEach(staff => {
                      console.log("Staff imageUrl:", staff.imageUrl);
                      const staffCard = document.createElement("div");
                      staffCard.className = "col-md-6 col-lg-4 mb-4";
                      staffCard.innerHTML = `
                          <div class="flip-card">
                              <div class="flip-card-inner">
                                  <div class="flip-card-front">
                                      <div class="card h-100 shadow-sm">
                                          <div class="card-body text-center p-4">
                                              <div class="staff-image-container mx-auto mb-3">
                                                  <img src="${staff.imageUrl || '/SalonManage/images/default-avatar.jpg'}" 
                                                       alt="${staff.firstName} ${staff.lastName}" 
                                                       class="admin-staff-image rounded-circle"
                                                       onload="console.log('Image loaded: ${staff.imageUrl || '/SalonManage/images/default-avatar.jpg'}')"
                                                       onerror="console.log('Image failed: ${staff.imageUrl || '/SalonManage/images/default-avatar.jpg'}')">
                                              </div>
                                              <h3 class="fs-5 fw-semibold mb-1">${staff.firstName} ${staff.lastName}</h3>
                                              <p class="staff-role mb-2 text-primary fw-medium">${staff.role}</p>
                                              <div class="staff-details text-muted mb-3">
                                                  ${staff.specialties ? `<p class="mb-1"><i class="fas fa-star me-1"></i> ${staff.specialties}</p>` : ''}
                                                  ${staff.experience ? `<p class="mb-1"><i class="fas fa-briefcase me-1"></i> ${staff.experience} years experience</p>` : ''}
                                                  ${staff.phone ? `<p class="mb-1"><i class="fas fa-phone me-1"></i> ${staff.phone}</p>` : ''}
                                              </div>
                                              <div class="d-flex justify-content-center gap-2">
                                                  <button class="btn btn-sm btn-primary flip-card-btn" 
                                                          data-id="${staff.staffId}">
                                                      <i class="fas fa-edit me-1"></i> Edit
                                                  </button>
                                                  <button class="btn btn-sm btn-outline-danger delete-staff" data-id="${staff.staffId}">
                                                      <i class="fas fa-trash me-1"></i> Delete
                                                  </button>
                                              </div>
                                          </div>
                                      </div>
                                  </div>
                                  <div class="flip-card-back">
                                      <h5 class="playfair-font mb-3">Edit Staff Member</h5>
                                      <form class="edit-staff-form" id="edit-staff-form-${staff.staffId}">
                                          <div class="row g-3">
                                              <input type="hidden" name="id" value="${staff.staffId}">
                                              <div class="col-12">
                                                  <label for="edit-first_name-${staff.staffId}" class="form-label">First Name</label>
                                                  <input type="text" class="form-control" id="edit-first_name-${staff.staffId}" name="firstName" value="${staff.firstName || ''}" required>
                                              </div>
                                              <div class="col-12">
                                                  <label for="edit-last_name-${staff.staffId}" class="form-label">Last Name</label>
                                                  <input type="text" class="form-control" id="edit-last_name-${staff.staffId}" name="lastName" value="${staff.lastName || ''}" required>
                                              </div>
                                              <div class="col-12">
                                                  <label for="edit-role-${staff.staffId}" class="form-label">Role</label>
                                                  <select class="form-select" id="edit-role-${staff.staffId}" name="role" required>
                                                      <option value="" disabled>Select Role</option>
                                                      <option value="Junior Hair Stylist">Junior Hair Stylist</option>
                                                      <option value="Senior Hair Stylist">Senior Hair Stylist</option>
                                                      <option value="Master Hair Stylist">Master Hair Stylist</option>
                                                      <option value="Beauty Therapist">Beauty Therapist</option>
                                                      <option value="Salon Manager">Salon Manager</option>
                                                  </select>
                                              </div>
                                              <div class="col-12">
                                                  <label for="edit-phone-${staff.staffId}" class="form-label">Phone Number</label>
                                                  <input type="tel" class="form-control" id="edit-phone-${staff.staffId}" name="phone" value="${staff.phone || ''}" required>
                                              </div>
                                              <div class="col-12">
                                                  <label for="edit-email-${staff.staffId}" class="form-label">Email</label>
                                                  <input type="email" class="form-control" id="edit-email-${staff.staffId}" name="email" value="${staff.email || ''}" required>
                                              </div>
                                              <div class="col-12">
                                                  <label for="edit-specialization-${staff.staffId}" class="form-label">Specialization</label>
                                                  <input type="text" class="form-control" id="edit-specialization-${staff.staffId}" name="specialties" value="${staff.specialties || ''}">
                                              </div>
                                              <div class="col-12">
                                                  <label for="edit-experience-${staff.staffId}" class="form-label">Experience (years)</label>
                                                  <input type="number" class="form-control" id="edit-experience-${staff.staffId}" name="experience" value="${staff.experience || 0}" min="0">
                                              </div>
                                              <div class="col-12 d-flex gap-2">
                                                  <button type="button" class="btn btn-secondary cancel-flip" data-id="${staff.staffId}">Cancel</button>
                                                  <button type="submit" class="btn btn-primary save-flip" data-id="${staff.staffId}">Save Changes</button>
                                              </div>
                                          </div>
                                      </form>
                                  </div>
                              </div>
                          </div>
                      `;
                      staffCardsContainer.appendChild(staffCard);
                  });
              })
              .catch(error => {
                  console.error("Error loading staff data:", error);
                  staffCardsContainer.innerHTML = `
                      <div class="col-12 text-center py-5">
                          <div class="alert alert-danger">
                              <i class="fas fa-exclamation-circle me-2"></i>
                              Failed to load staff members. Please try again later.
                          </div>
                      </div>
                  `;
              });
      } else {
          console.error("Staff cards container not found");
      }

      // Function to delete staff
      function deleteStaff(staffId) {
          showConfirmModal("Are you sure you want to delete this staff member?", (confirmed) => {
              if (confirmed) {
                  fetch(window.location.origin + "/SalonManage/delete-staff", {
                      method: "POST",
                      headers: { "Content-Type": "application/x-www-form-urlencoded" },
                      body: `id=${staffId}`,
                  })
                  .then(response => {
                      if (!response.ok) throw new Error("Failed to delete staff");
                      return response.json();
                  })
                  .then(data => {
                      if (data.success) {
                          showModal("Staff member deleted successfully!");
                          location.reload();
                      } else {
                          showModal("Failed to delete staff member: " + (data.message || "Unknown error"));
                      }
                  })
                  .catch(error => {
                      console.error("Error deleting staff:", error);
                      showModal("Error deleting staff: " + error.message);
                  });
              }
          });
      }

      // Fetch staff data for editing
      function fetchStaffData(staffId) {
          console.log("Fetching staff data for ID:", staffId);
          return fetch(`${window.location.origin}/SalonManage/get-staff/${staffId}`)
              .then(response => {
                  if (!response.ok) throw new Error(`Failed to fetch staff data: ${response.status}`);
                  return response.json();
              })
              .then(staff => {
                  console.log("Fetched staff data:", staff);
                  return staff;
              })
              .catch(error => {
                  console.error("Error fetching staff data:", error);
                  showModal("Unable to load staff details: " + error.message);
                  throw error;
              });
      }

      // Populate and flip card
      function populateFlipCard(staffId, staff) {
          console.log("Populating flip card for staff ID:", staffId, "with data:", staff);
          const form = document.getElementById(`edit-staff-form-${staffId}`);
          const flipCard = form.closest('.flip-card');
          if (!form || !flipCard) {
              console.error("Flip card or form not found for staff ID:", staffId);
              showModal("Error: Edit form not found");
              return;
          }

          // Log specific field values
          console.log("Setting specialties:", staff.specialties !== undefined ? staff.specialties : 'empty');
          console.log("Setting experience:", staff.experience !== null && staff.experience !== undefined ? staff.experience : 0);

          // Populate form fields
          form.querySelector(`[name="id"]`).value = staff.staffId || '';
          form.querySelector(`[name="firstName"]`).value = staff.firstName || '';
          form.querySelector(`[name="lastName"]`).value = staff.lastName || '';
          form.querySelector(`[name="phone"]`).value = staff.phone || '';
          form.querySelector(`[name="email"]`).value = staff.email || '';
          const specialtiesInput = form.querySelector(`[name="specialties"]`);
          specialtiesInput.value = staff.specialties !== undefined ? staff.specialties : '';
          const experienceInput = form.querySelector(`[name="experience"]`);
          const experienceValue = staff.experience !== null && staff.experience !== undefined ? staff.experience : 0;
          experienceInput.value = experienceValue;
          experienceInput.valueAsNumber = experienceValue;

          // Handle role select
          const roleSelect = form.querySelector(`[name="role"]`);
          if (staff.role) {
              const option = Array.from(roleSelect.options).find(opt => opt.value === staff.role);
              if (option) {
                  roleSelect.value = staff.role;
                  console.log("Role set to:", staff.role);
              } else {
                  console.warn(`Role "${staff.role}" not found in select options`);
                  roleSelect.value = "";
              }
          } else {
              roleSelect.value = "";
              console.warn("No role provided in staff data");
          }

          // Verify DOM values
          console.log("DOM specialties value:", specialtiesInput.value);
          console.log("DOM experience value:", experienceInput.value);
          console.log("DOM role value:", roleSelect.value);

          // Flip card
          document.querySelectorAll('.flip-card.flipped').forEach(card => {
              if (card !== flipCard) card.classList.remove('flipped');
          });
          flipCard.classList.add('flipped');
          console.log("Card flipped for staff ID:", staffId);
      }

      // Save staff changes
      function saveStaffChanges(staffId) {
          const form = document.getElementById(`edit-staff-form-${staffId}`);
          const flipCard = form.closest('.flip-card');

          // Collect form data as URL-encoded string
          const formData = {};
          form.querySelectorAll('input, select').forEach(input => {
              if (input.name) formData[input.name] = input.value;
          });
          console.log("Form data to send:", formData);
          const urlEncodedData = new URLSearchParams(formData).toString();

          console.log("Saving staff changes for ID:", staffId);
          fetch(`${window.location.origin}/SalonManage/edit-staff`, {
              method: 'POST',
              headers: {
                  'Content-Type': 'application/x-www-form-urlencoded'
              },
              body: urlEncodedData
          })
          .then(response => {
              console.log("Response status:", response.status);
              if (!response.ok) throw new Error(`Failed to save staff changes: ${response.status}`);
              return response.json();
          })
          .then(data => {
              console.log("Response data:", data);
              if (data.success) {
                  showModal('Staff member updated successfully!');
                  flipCard.classList.remove('flipped');
                  location.reload();
              } else {
                  showModal('Failed to update staff member: ' + (data.message || 'Unknown error'));
              }
          })
          .catch(error => {
              console.error('Error saving staff changes:', error);
              showModal('Error updating staff: ' + error.message);
          });
      }

      // Handle card flip and submission
      document.addEventListener('click', function(e) {
          const flipBtn = e.target.closest('.flip-card-btn');
          const cancelBtn = e.target.closest('.cancel-flip');
          const saveBtn = e.target.closest('.save-flip');
          const deleteBtn = e.target.closest('.delete-staff');

          if (flipBtn) {
              e.preventDefault();
              const staffId = flipBtn.dataset.id;
              console.log("Flip card button clicked for staff ID:", staffId);
              if (staffId) {
                  fetchStaffData(staffId)
                      .then(staff => populateFlipCard(staffId, staff))
                      .catch(error => {
                          console.error("Failed to fetch staff data:", error);
                      });
              } else {
                  console.error("No staff ID found in button data-id");
                  showModal("Error: Staff ID not found");
              }
          }

          if (cancelBtn) {
              e.preventDefault();
              const staffId = cancelBtn.dataset.id;
              const flipCard = document.getElementById(`edit-staff-form-${staffId}`).closest('.flip-card');
              if (flipCard) {
                  flipCard.classList.remove('flipped');
                  console.log("Card flipped back for staff ID:", staffId);
              }
          }

          if (saveBtn) {
              e.preventDefault();
              const staffId = saveBtn.dataset.id;
              saveStaffChanges(staffId);
          }

          if (deleteBtn) {
              e.preventDefault();
              const staffId = deleteBtn.dataset.id;
              deleteStaff(staffId);
          }
      });

      // Handle form submission
      document.addEventListener('submit', function(e) {
          const form = e.target.closest('.edit-staff-form');
          if (form) {
              e.preventDefault();
              const staffId = form.id.replace('edit-staff-form-', '');
              saveStaffChanges(staffId);
          }
      });

      // Debug: Check Bootstrap and modal availability
      console.log("Bootstrap availability:", {
          bootstrap: typeof bootstrap,
          bootstrapModal: typeof bootstrap?.Modal,
          addStaffModal: !!document.getElementById('addStaffModal')
      });
});