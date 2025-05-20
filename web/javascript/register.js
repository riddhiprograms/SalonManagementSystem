$(document).ready(function() {
    $('#register-form').submit(function(event) {
        event.preventDefault();

        // Retrieve form values
        const firstName = $('#first-name').val().trim();
        const lastName = $('#last-name').val().trim();
        const email = $('#register-email').val().trim();
        const phone = $('#phone').val().trim();
        const password = $('#register-password').val().trim();
        const confirmPassword = $('#confirm-password').val().trim();
        const registerType = $('input[name="registerType"]:checked').val();
        const termsAgree = $('#terms-agree').prop('checked');

        // Validate inputs
        if (!firstName || !lastName || !email || !phone || !password || !confirmPassword || !registerType) {
            $('#error-message').text('All fields are required!').removeClass('d-none');
            return;
        }

        if (!/^\d{10}$/.test(phone)) {
            $('#error-message').text('Phone number must be 10 digits.').removeClass('d-none');
            return;
        }

        if (!email.includes('@')) {
            $('#error-message').text('Please enter a valid email address.').removeClass('d-none');
            return;
        }

        if (password.length < 6) {
            $('#error-message').text('Password must be at least 6 characters long.').removeClass('d-none');
            return;
        }

        if (password !== confirmPassword) {
            $('#error-message').text('Passwords do not match!').removeClass('d-none');
            return;
        }

        if (!termsAgree) {
            $('#error-message').text('You must agree to the Terms of Service and Privacy Policy.').removeClass('d-none');
            return;
        }

        if (!['customer', 'admin'].includes(registerType)) {
            $('#error-message').text('Invalid registration type.').removeClass('d-none');
            return;
        }

        // Send AJAX request
        $.ajax({
            url: 'register',
            type: 'POST',
            data: $('#register-form').serialize(),
            dataType: 'json',
            success: function(response) {
                console.log('Register response:', response);
                if (response.status === 'success') {
                    $('#otpModal').modal('show');
                    $('#error-message').text('').addClass('d-none');
                } else {
                    $('#errorModalMessage').text(response.message);
                    $('#errorModal').modal('show');
                    $('#error-message').text('').addClass('d-none');
                }
            },
            error: function(xhr, status, error) {
                console.error('AJAX error:', {
                    status: status,
                    error: error,
                    responseText: xhr.responseText,
                    statusCode: xhr.status
                });
                let errorMsg = 'An error occurred. Please try again.';
                if (xhr.status === 0) {
                    errorMsg = 'Network error: Unable to reach the server.';
                } else if (xhr.status >= 400) {
                    errorMsg = `Server error (${xhr.status}): ${xhr.statusText}`;
                }
                $('#errorModalMessage').text(errorMsg);
                $('#errorModal').modal('show');
                $('#error-message').text('').addClass('d-none');
            }
        });
    });

    $('#otp-form').submit(function(event) {
        event.preventDefault();
        $.ajax({
            url: 'verifyOtp',
            type: 'POST',
            data: $('#otp-form').serialize(),
            dataType: 'json',
            success: function(response) {
                console.log('OTP response:', response);
                if (response.status === 'success') {
                    $('#otpModal').modal('hide');
                    $('#successModal').modal('show');
                    $('#otp-error').text('').addClass('d-none');
                } else {
                    $('#otp-error').removeClass('d-none').text(response.message);
                }
            },
            error: function(xhr, status, error) {
                console.error('OTP AJAX error:', {
                    status: status,
                    error: error,
                    responseText: xhr.responseText,
                    statusCode: xhr.status
                });
                $('#otp-error').removeClass('d-none').text('An error occurred. Please try again.');
            }
        });
    });

    // Password strength indicator
    $('#register-password').on('input', function() {
        const password = $(this).val();
        const strengthIndicator = $('.password-strength');
        if (password.length < 6) {
            strengthIndicator.text('Weak').css('color', 'red');
        } else if (password.length < 10) {
            strengthIndicator.text('Medium').css('color', 'orange');
        } else {
            strengthIndicator.text('Strong').css('color', 'green');
        }
    });
});