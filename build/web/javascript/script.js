document.addEventListener('DOMContentLoaded', () => {
    console.log('Site is loaded and ready!');

    // Scroll to top functionality
    const scrollTopButton = document.getElementById('scroll-top-btn');
    if (scrollTopButton) {
        scrollTopButton.addEventListener('click', () => {
            window.scrollTo({ top: 0, behavior: 'smooth' });
        });
    }
});
document.addEventListener('DOMContentLoaded', () => {
    const staffContainer = document.getElementById('staff-list');

    fetch('/staff')
        .then(response => response.json())
        .then(data => {
            staffContainer.innerHTML = data.map(staff => `
                <div class="col-md-4 col-lg-3">
                    <div class="card text-center">
                        <img src="${staff.imageUrl}" class="card-img-top" alt="${staff.firstName} ${staff.lastName}">
                        <div class="card-body">
                            <h5 class="card-title">${staff.firstName} ${staff.lastName}</h5>
                            <p class="card-text">${staff.role}</p>
                            <p class="text-muted">Specialties: ${staff.specialties}</p>
                            <p class="text-muted">Experience: ${staff.experience} years</p>
                        </div>
                    </div>
                </div>
            `).join('');
        })
        .catch(error => {
            console.error('Error fetching staff:', error);
            staffContainer.innerHTML = '<p class="text-danger">Unable to load staff members.</p>';
        });
});
