// Login functionality
function handleLogin(email, password) {
    fetch('/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
    })
    .then(response => {
        if (response.ok) {
            alert('Login successful!');
            window.location.href = '/profile.jsp';
        } else {
            alert('Login failed. Please check your credentials.');
        }
    })
    .catch(error => console.error('Error logging in:', error));
}

// Logout functionality
function handleLogout() {
    fetch('/logout')
        .then(response => {
            if (response.ok) {
                alert('Logged out successfully!');
                window.location.href = '/login.jsp';
            } else {
                alert('Logout failed.');
            }
        })
        .catch(error => console.error('Error logging out:', error));
}

