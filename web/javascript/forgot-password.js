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

$(document).ready(function() {
    let currentStep = 1;
    const totalSteps = 3;

    // Update progress bar
    function updateProgressBar() {
        const progress = (currentStep / totalSteps) * 100;
        $('.progress-bar').css('width', progress + '%').attr('aria-valuenow', progress);
    }

    // Show specific step
    function showStep(step) {
        $('.step').removeClass('active');
        $('#step' + step).addClass('active');
        currentStep = step;
        updateProgressBar();
    }

    // Shake effect for invalid inputs
    function shakeInput(input) {
        $(input).addClass('shake');
        setTimeout(() => $(input).removeClass('shake'), 300);
    }

    // Handle forgot password form (Step 1)
    $('#forgot-password-form').submit(function(event) {
        event.preventDefault();
        const email = $('#reset-email').val().trim();

        // Validate email
        if (!email) {
            $('#email-error').text('Email is required!').removeClass('d-none');
            shakeInput('#reset-email');
            return;
        }
        if (!email.includes('@')) {
            $('#email-error').text('Please enter a valid email address.').removeClass('d-none');
            shakeInput('#reset-email');
            return;
        }

        // Show loading spinner
        $('#forgot-password-form button').prop('disabled', true).prepend('<span class="spinner-border spinner-border-sm me-2"></span>');

        // Send AJAX request
        $.ajax({
            url: 'forgotPassword',
            type: 'POST',
            data: { email: email },
            dataType: 'json',
            success: function(response) {
                console.log('Forgot password response:', response);
                if (response.status === 'success') {
                    showStep(2);
                    $('#email-error').text('').addClass('d-none');
                    startResendCooldown();
                } else {
                    $('#email-error').text(response.message).removeClass('d-none');
                    shakeInput('#reset-email');
                }
            },
            error: function(xhr, status, error) {
                console.error('Forgot password AJAX error:', {
                    status: status,
                    error: error,
                    responseText: xhr.responseText,
                    statusCode: xhr.status
                });
                $('#email-error').text('An error occurred. Please try again.').removeClass('d-none');
            },
            complete: function() {
                $('#forgot-password-form button').prop('disabled', false).find('.spinner-border').remove();
            }
        });
    });

    // Handle reset password form (Step 2)
    $('#reset-password-form').submit(function(event) {
        event.preventDefault();
        const otp = $('#otp').val().trim();
        const password = $('#new-password').val().trim();
        const confirmPassword = $('#confirm-password').val().trim();

        // Validate inputs
        if (!otp) {
            $('#reset-error').text('OTP is required!').removeClass('d-none');
            shakeInput('#otp');
            return;
        }
        if (!password || !confirmPassword) {
            $('#reset-error').text('All fields are required!').removeClass('d-none');
            shakeInput('#new-password, #confirm-password');
            return;
        }
        if (password.length < 6) {
            $('#reset-error').text('Password must be at least 6 characters long.').removeClass('d-none');
            shakeInput('#new-password');
            return;
        }
        if (password !== confirmPassword) {
            $('#reset-error').text('Passwords do not match!').removeClass('d-none');
            shakeInput('#confirm-password');
            return;
        }

        // Show loading spinner
        $('#reset-password-form button').prop('disabled', true).prepend('<span class="spinner-border spinner-border-sm me-2"></span>');

        // Send AJAX request
        $.ajax({
            url: 'resetPassword',
            type: 'POST',
            data: {
                otp: otp,
                password: password
            },
            dataType: 'json',
            success: function(response) {
                console.log('Reset password response:', response);
                if (response.status === 'success') {
                    showStep(3);
                    $('#reset-error').text('').addClass('d-none');
                } else {
                    $('#reset-error').text(response.message).removeClass('d-none');
                    shakeInput('#otp');
                }
            },
            error: function(xhr, status, error) {
                console.error('Reset password AJAX error:', {
                    status: status,
                    error: error,
                    responseText: xhr.responseText,
                    statusCode: xhr.status
                });
                $('#reset-error').text('An error occurred. Please try again.').removeClass('d-none');
            },
            complete: function() {
                $('#reset-password-form button').prop('disabled', false).find('.spinner-border').remove();
            }
        });
    });

    // Password strength meter
    $('#new-password').on('input', function() {
        const password = $(this).val();
        const meter = $('.password-strength-meter');
        if (password.length < 6) {
            meter.css({ width: '33%', backgroundColor: 'red' });
        } else if (password.length < 10) {
            meter.css({ width: '66%', backgroundColor: 'orange' });
        } else {
            meter.css({ width: '100%', backgroundColor: 'green' });
        }
    });

    // Resend OTP with cooldown
    let cooldown = 30;
    function startResendCooldown() {
        $('#resend-otp').prop('disabled', true).text(`Resend OTP (${cooldown}s)`);
        const interval = setInterval(() => {
            cooldown--;
            $('#resend-otp').text(`Resend OTP (${cooldown}s)`);
            if (cooldown <= 0) {
                clearInterval(interval);
                $('#resend-otp').prop('disabled', false).text('Resend OTP');
                cooldown = 30;
            }
        }, 1000);
    }

    $('#resend-otp').click(function() {
        const email = $('#reset-email').val().trim();
        $.ajax({
            url: 'forgotPassword',
            type: 'POST',
            data: { email: email },
            dataType: 'json',
            success: function(response) {
                console.log('Resend OTP response:', response);
                if (response.status === 'success') {
                    $('#reset-error').text('New OTP sent!').removeClass('d-none').css('color', 'green');
                    setTimeout(() => $('#reset-error').addClass('d-none').css('color', 'red'), 3000);
                    startResendCooldown();
                } else {
                    $('#reset-error').text(response.message).removeClass('d-none');
                }
            },
            error: function(xhr, status, error) {
                console.error('Resend OTP AJAX error:', {
                    status: status,
                    error: error,
                    responseText: xhr.responseText,
                    statusCode: xhr.status
                });
                $('#reset-error').text('An error occurred. Please try again.').removeClass('d-none');
            }
        });
    });

    // Password visibility toggle for reset form
    $('#reset-password-form .toggle-password').click(function() {
        const passwordInput = $('#new-password');
        const icon = $(this).find('i');
        if (passwordInput.attr('type') === 'password') {
            passwordInput.attr('type', 'text');
            icon.removeClass('fa-eye').addClass('fa-eye-slash');
        } else {
            passwordInput.attr('type', 'password');
            icon.removeClass('fa-eye-slash').addClass('fa-eye');
        }
    });

    // Reset modal on close
    $('#forgotPasswordModal').on('hidden.bs.modal', function() {
        showStep(1);
        $('#forgot-password-form')[0].reset();
        $('#reset-password-form')[0].reset();
        $('#email-error, #reset-error').text('').addClass('d-none');
        $('.password-strength-meter').css({ width: '0', backgroundColor: 'red' });
        cooldown = 30;
        $('#resend-otp').prop('disabled', false).text('Resend OTP');
    });
});