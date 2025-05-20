(function($) {
    $(document).ready(function() {
        const profileInfoContainer = document.getElementById("profile-info");
        const appointmentsContainer = document.getElementById("appointments-list");
        const appointmentsLoading = document.getElementById("appointments-loading");
        const appointmentsError = document.getElementById("appointments-error");

        if (profileInfoContainer) {
            document.getElementById("first-name").textContent = "Loading...";
            document.getElementById("last-name").textContent = "Loading...";
            document.getElementById("phone").textContent = "Loading...";
            document.getElementById("gender").textContent = "Loading...";
            document.getElementById("email").textContent = "Loading...";

            const loadProfile = (retryCount = 1) => {
                const servletUrl = window.location.origin + "/SalonManage/FetchUserProfileServlet";
                console.log(`Fetching user profile from: ${servletUrl}, Attempt: ${retryCount}`);

                fetch(servletUrl)
                    .then((response) => {
                        if (!response.ok) {
                            throw new Error(`HTTP error! Status: ${response.status}`);
                        }
                        return response.json();
                    })
                    .then((data) => {
                        console.log("Fetch profile response:", data);
                        if (data.success && data.data) {
                            renderUserProfile(data.data);
                            populateEditModal(data.data);
                        } else {
                            console.error("Server error:", data.message || "No data returned");
                            renderError(`Failed to load profile: ${data.message || "Unknown error"}`);
                            tryFallback();
                        }
                    })
                    .catch((error) => {
                        console.error("Error fetching profile:", { error: error.message, status: error.status });
                        if (retryCount > 0) {
                            console.log("Retrying fetch...");
                            setTimeout(() => loadProfile(retryCount - 1), 1000);
                        } else {
                            let errorMsg = "Unable to load profile data. ";
                            if (error.message.includes("404")) {
                                errorMsg += "Profile service not found. Verify the servlet URL or contact support.";
                            } else if (error.message.includes("401")) {
                                errorMsg += "Session expired. Please log in again.";
                            } else if (error.message.includes("500")) {
                                errorMsg += "Server error. Please try again later or contact support.";
                            } else {
                                errorMsg += "Please try refreshing the page or contact support.";
                            }
                            renderError(errorMsg);
                            tryFallback();
                        }
                    });
            };

            const renderUserProfile = (data) => {
                console.log("Rendering user profile:", data);
                document.getElementById("first-name").textContent = data.firstName || "Not provided";
                document.getElementById("last-name").textContent = data.lastName || "Not provided";
                document.getElementById("phone").textContent = data.phone || "Not provided";
                document.getElementById("gender").textContent = data.gender || "Not provided";
                document.getElementById("email").textContent = data.email || "Not provided";
            };

            const renderError = (message) => {
                profileInfoContainer.innerHTML = `
                    <div class="alert alert-danger d-flex align-items-center" role="alert">
                        <i class="fas fa-exclamation-circle me-2"></i> ${message}
                    </div>
                `;
            };

            const tryFallback = () => {
                console.log("Attempting fallback with session attributes");
                const userData = {
                    firstName: '<%= session.getAttribute("userFirstName") != null ? session.getAttribute("userFirstName") : "" %>',
                    lastName: '<%= session.getAttribute("userLastName") != null ? session.getAttribute("userLastName") : "" %>',
                    phone: '<%= session.getAttribute("phone") != null ? session.getAttribute("phone") : "" %>',
                    gender: '<%= session.getAttribute("gender") != null ? session.getAttribute("gender") : "" %>',
                    email: '<%= session.getAttribute("email") != null ? session.getAttribute("email") : "" %>',
                };
                console.log("Fallback data:", userData);
                if (userData.firstName || userData.lastName || userData.phone || userData.gender || userData.email) {
                    renderUserProfile(userData);
                    populateEditModal(userData);
                } else {
                    console.warn("No valid fallback data available. Ensure session attributes are set in LoginServlet.");
                    renderError("No profile data available. Please log in again or contact support.");
                }
            };

            const populateEditModal = (data) => {
                console.log("Populating edit modal:", data);
                document.getElementById("edit-first-name").value = data.firstName || "";
                document.getElementById("edit-last-name").value = data.lastName || "";
                document.getElementById("edit-phone").value = data.phone || "";
                document.getElementById("edit-gender").value = data.gender || "";
            };

            loadProfile();
        }

       // Appointments handling



        document.querySelectorAll(".toggle-password").forEach((button) => {
            button.addEventListener("click", function () {
                const input = this.previousElementSibling;
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

        const newPasswordInput = document.getElementById("new-password");
        if (newPasswordInput) {
            newPasswordInput.addEventListener("input", function () {
                const password = this.value;
                let strength = 0;
                if (password.length >= 8) strength += 25;
                if (/[A-Z]/.test(password)) strength += 25;
                if (/[0-9]/.test(password)) strength += 25;
                if (/[^A-Za-z0-9]/.test(password)) strength += 25;

                const progressBar = document.querySelector(".progress-bar");
                const strengthText = document.querySelector(".password-strength-text");
                progressBar.style.width = `${strength}%`;
                progressBar.setAttribute("aria-valuenow", strength);
                if (strength <= 25) {
                    progressBar.classList.remove("bg-success", "bg-warning");
                    progressBar.classList.add("bg-danger");
                    strengthText.textContent = "Password strength: Very Weak";
                } else if (strength <= 50) {
                    progressBar.classList.remove("bg-danger", "bg-success");
                    progressBar.classList.add("bg-warning");
                    strengthText.textContent = "Password strength: Weak";
                } else if (strength <= 75) {
                    progressBar.classList.remove("bg-danger", "bg-success");
                    progressBar.classList.add("bg-warning");
                    strengthText.textContent = "Password strength: Moderate";
                } else {
                    progressBar.classList.remove("bg-danger", "bg-warning");
                    progressBar.classList.add("bg-success");
                    strengthText.textContent = "Password strength: Strong";
                }
            });
        }

        const editPhoneInput = document.getElementById("edit-phone");
        if (editPhoneInput) {
            editPhoneInput.addEventListener("input", function () {
                const phone = this.value;
                const phoneError = document.getElementById("edit-phone-error");
                if (!/^\d{10}$/.test(phone)) {
                    phoneError.textContent = "Please enter a valid 10-digit phone number.";
                } else {
                    phoneError.textContent = "";
                }
            });
        }

        const editProfileForm = document.getElementById("editProfileForm");
        if (editProfileForm) {
            editProfileForm.addEventListener("submit", function (e) {
                e.preventDefault();

                const firstName = document.getElementById("edit-first-name").value;
                const lastName = document.getElementById("edit-last-name").value;
                const phone = document.getElementById("edit-phone").value;
                const gender = document.getElementById("edit-gender").value;

                let hasError = false;

                const firstNameError = document.getElementById("edit-first-name-error") || document.createElement("div");
                firstNameError.id = "edit-first-name-error";
                firstNameError.className = "text-danger mt-2";
                if (!document.getElementById("edit-first-name-error")) {
                    document.getElementById("edit-first-name").parentNode.appendChild(firstNameError);
                }
                if (!firstName.trim()) {
                    document.getElementById("edit-first-name").classList.add("is-invalid");
                    firstNameError.textContent = "Please enter a valid first name.";
                    hasError = true;
                } else {
                    document.getElementById("edit-first-name").classList.remove("is-invalid");
                    firstNameError.textContent = "";
                }

                const lastNameError = document.getElementById("edit-last-name-error") || document.createElement("div");
                lastNameError.id = "edit-last-name-error";
                lastNameError.className = "text-danger mt-2";
                if (!document.getElementById("edit-last-name-error")) {
                    document.getElementById("edit-last-name").parentNode.appendChild(lastNameError);
                }
                if (!lastName.trim()) {
                    document.getElementById("edit-last-name").classList.add("is-invalid");
                    lastNameError.textContent = "Please enter a valid last name.";
                    hasError = true;
                } else {
                    document.getElementById("edit-last-name").classList.remove("is-invalid");
                    lastNameError.textContent = "";
                }

                const phoneError = document.getElementById("edit-phone-error") || document.createElement("div");
                phoneError.id = "edit-phone-error";
                phoneError.className = "text-danger mt-2";
                if (!document.getElementById("edit-phone-error")) {
                    document.getElementById("edit-phone").parentNode.appendChild(phoneError);
                }
                if (!/^\d{10}$/.test(phone)) {
                    document.getElementById("edit-phone").classList.add("is-invalid");
                    phoneError.textContent = "Please enter a valid 10-digit phone number.";
                    hasError = true;
                } else {
                    document.getElementById("edit-phone").classList.remove("is-invalid");
                    phoneError.textContent = "";
                }

                const genderError = document.getElementById("edit-gender-error") || document.createElement("div");
                genderError.id = "edit-gender-error";
                genderError.className = "text-danger mt-2";
                if (!document.getElementById("edit-gender-error")) {
                    document.getElementById("edit-gender").parentNode.appendChild(genderError);
                }
                if (!["male", "female", "other"].includes(gender)) {
                    document.getElementById("edit-gender").classList.add("is-invalid");
                    genderError.textContent = "Please select a valid gender.";
                    hasError = true;
                } else {
                    document.getElementById("edit-gender").classList.remove("is-invalid");
                    genderError.textContent = "";
                }

                if (hasError) {
                    return;
                }

                const btn = document.getElementById("saveProfileBtn");
                const spinner = btn.querySelector(".spinner-border");
                spinner.classList.remove("d-none");
                btn.disabled = true;

                const payload = {
                    firstName: firstName.trim(),
                    lastName: lastName.trim(),
                    phone: phone,
                    gender: gender
                };
                console.log("JSON payload sent:", payload);

                $.ajax({
                    url: window.location.origin + "/SalonManage/UpdateProfileServlet",
                    type: "POST",
                    contentType: "application/json",
                    data: JSON.stringify(payload),
                    dataType: "json",
                    success: function (response) {
                        console.log("Update profile response:", response);
                        if (response.success) {
                            $("#editProfileModal").modal("hide");
                            try {
                                const $successModal = $("#profileSuccessModal");
                                if ($successModal.length) {
                                    setTimeout(() => {
                                        $successModal.modal("show");
                                        console.log("profileSuccessModal shown successfully");
                                    }, 100);
                                } else {
                                    console.error("profileSuccessModal not found in DOM");
                                    alert("Profile updated, but success modal not found.");
                                }
                            } catch (e) {
                                console.error("Error showing profileSuccessModal:", e);
                                alert("Profile updated, but error showing success modal: " + e.message);
                            }
                            loadProfile();
                        } else {
                            alert("Error: " + response.message);
                        }
                        spinner.classList.add("d-none");
                        btn.disabled = false;
                    },
                    error: function (xhr, status, error) {
                        console.error("Update profile error:", { status: status, error: error, responseText: xhr.responseText });
                        alert("Error updating profile: " + (xhr.responseJSON?.message || error));
                        spinner.classList.add("d-none");
                        btn.disabled = false;
                    }
                });
            });
        }

        const passwordForm = document.getElementById("passwordForm");
        if (passwordForm) {
            passwordForm.addEventListener("submit", function () {
                const btn = this.querySelector('button[type="submit"]');
                const spinner = btn.querySelector(".spinner-border");
                spinner.classList.remove("d-none");
                btn.disabled = true;
            });
        }

        

        
    });
})(jQuery.noConflict());