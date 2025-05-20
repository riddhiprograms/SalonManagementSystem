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
    console.log("changePassword.js loaded");

    const passwordForm = document.getElementById("passwordForm");
    const currentPassword = document.getElementById("current-password");
    const newPassword = document.getElementById("new-password");
    const confirmPassword = document.getElementById("confirm-password");
    const changePasswordBtn = document.getElementById("changePasswordBtn");
    const confirmError = document.getElementById("confirm-error");
    const progressBar = document.querySelector(".progress-bar");
    const strengthText = document.querySelector(".password-strength-text");

    if (!passwordForm || !currentPassword || !newPassword || !confirmPassword || !changePasswordBtn) {
        console.error("Required form elements not found.");
        return;
    }

    // Password visibility toggle
    document.querySelectorAll(".toggle-password").forEach(button => {
        button.addEventListener("click", function () {
            const input = this.parentElement.querySelector("input");
            const icon = this.querySelector("i");
            if (input.type === "password") {
                input.type = "text";
                icon.classList.remove("fa-eye");
                icon.classList.add("fa-eye-slash");
            } else {
                input.type = "password";
                icon.classList.remove("fa-eye-slash");
                icon.classList.add("fa-eye");
            }
        });
    });

    // Password strength checker
    newPassword.addEventListener("input", function () {
        const password = this.value;
        const isValid = password.length >= 8;

        progressBar.style.width = isValid ? "100%" : "25%";
        progressBar.setAttribute("aria-valuenow", isValid ? 100 : 25);

        if (isValid) {
            progressBar.classList.remove("bg-danger");
            progressBar.classList.add("bg-success");
            strengthText.textContent = "Password strength: Valid";
        } else {
            progressBar.classList.remove("bg-success");
            progressBar.classList.add("bg-danger");
            strengthText.textContent = "Password strength: Too Short";
        }
    });

    // Confirm password validation
    confirmPassword.addEventListener("input", function () {
        if (this.value !== newPassword.value) {
            confirmError.textContent = "Passwords do not match";
            this.setCustomValidity("Passwords do not match");
        } else {
            confirmError.textContent = "";
            this.setCustomValidity("");
        }
    });

    // Form submission via AJAX
    passwordForm.addEventListener("submit", function (e) {
        e.preventDefault();
        console.log("Password form submitted");

        if (!passwordForm.checkValidity()) {
            passwordForm.reportValidity();
            return;
        }

        const spinner = changePasswordBtn.querySelector(".spinner-border");
        spinner.classList.remove("d-none");
        changePasswordBtn.disabled = true;

        jQuery.ajax({
            url: `${window.location.origin}/SalonManage/ChangePasswordServlet`,
            type: "POST",
            data: {
                currentPassword: currentPassword.value,
                newPassword: newPassword.value,
                confirmPassword: confirmPassword.value
            },
            success: function (data) {
                console.log("Change password response:", JSON.stringify(data, null, 2));
                spinner.classList.add("d-none");
                changePasswordBtn.disabled = false;

                if (data.success) {
                    // Show success modal
                    const successModal = new bootstrap.Modal(document.getElementById("passwordSuccessModal"));
                    successModal.show();
                    console.log("Password success modal shown");

                    // Reset form and strength indicator
                    passwordForm.reset();
                    progressBar.style.width = "25%";
                    strengthText.textContent = "Password strength: Too Short";
                    progressBar.classList.remove("bg-success");
                    progressBar.classList.add("bg-danger");
                } else {
                    // Show error modal
                    showModal(`Error: ${data.message}`);
                }
            },
            error: function (xhr, status, error) {
                console.error("Error changing password:", error, "Status:", status, "Response:", xhr.responseText);
                spinner.classList.add("d-none");
                changePasswordBtn.disabled = false;
                showModal(`Error: Failed to change password: ${xhr.responseJSON?.message || error}`);
            }
        });
    });

    // Handle success modal dismissal
    document.querySelectorAll("#passwordSuccessModal [data-bs-dismiss='modal']").forEach(btn => {
        btn.addEventListener("click", function () {
            console.log("Password success modal dismissed");
            const modal = bootstrap.Modal.getInstance(btn.closest(".modal"));
            modal.hide();
        });
    });
});