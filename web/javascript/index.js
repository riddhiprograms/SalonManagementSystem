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
    const servicesListContainer = $("#services-list");

    // Check if the container exists on the page
    if (servicesListContainer.length) {
        servicesListContainer.empty(); // Clear the loading spinner or placeholder

        // Fetch service categories and services
        fetch(window.location.origin +"/SalonManage/getServiceCategories") // Your backend endpoint
            .then(response => response.json())
            .then(serviceCategories => {
                let servicesList = "";

                // Iterate over each service category
                serviceCategories.forEach(categoryData => {
                    servicesList += `
                        <div class="service-category-section mb-4">
                            <h4 class="fs-6 fw-semibold mb-3">${categoryData.categoryName}</h4>
                            <div class="row g-3">
                                ${categoryData.services
                                    .map(
                                        service => `
                                        <div class="col-md-6">
                                            <div class="card service-selection-card h-100">
                                                <div class="card-body">
                                                    <div class="form-check">
                                                        <input class="form-check-input service-checkbox" type="checkbox" 
                                                            value="${service.serviceId}" id="service-${service.serviceId}">
                                                        <label class="form-check-label w-100" for="service-${service.serviceId}">
                                                            <div class="d-flex justify-content-between align-items-start">
                                                                <div>
                                                                    <p class="fw-medium mb-0">${service.name}</p>
                                                                    <small class="text-muted d-block">${service.duration} min</small>
                                                                    <small class="service-description text-muted d-block">${service.description}</small>
                                                                </div>
                                                                <span class="service-price">₹${service.price.toFixed(2)}</span>
                                                            </div>
                                                        </label>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    `
                                    )
                                    .join("")}
                            </div>
                        </div>
                    `;
                });

                // Append services list to the container
                servicesListContainer.append(servicesList);
            })
            .catch(error => {
                console.error("Error fetching service categories:", error);
                servicesListContainer.html(`
                    <div class="alert alert-danger" role="alert">
                        Failed to load services. Please try again later.
                    </div>
                `);
            });
    }
});
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
        const serviceCategories = document.getElementById("service-categories").querySelector(".row");
        const carouselContainer = document.querySelector(".carousel-container");
        const prevArrow = document.querySelector(".carousel-arrow-left");
        const nextArrow = document.querySelector(".carousel-arrow-right");

        // Function to load services
        const loadServices = () => {
            serviceCategories.innerHTML = `
            <div class="text-center py-3">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
                <p class="mt-2">Loading services...</p>
            </div>
        `;

            fetch(window.location.origin + "/SalonManage/services-index")
                .then(response => {
                    if (!response.ok) {
                        throw new Error("Failed to fetch services.");
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.error || data.length === 0) {
                        serviceCategories.innerHTML = `
                        <div class="text-center py-3">
                            <p class="mt-2">No services available at the moment.</p>
                        </div>
                    `;
                        updateArrows();
                        return;
                    }

                    // Render services dynamically
                    let servicesHTML = "";
                    data.forEach(service => {
                        servicesHTML += `
                        <div class="col-md-4">
                            <div class="card h-100">
                                <div class="card-body text-center">
                                    <h5 class="card-title">${service.name}</h5>
                                    <p class="card-text">${service.description}</p>
                                </div>
                            </div>
                        </div>
                    `;
                    });

                    serviceCategories.innerHTML = servicesHTML;
                    updateArrows();
                })
                .catch(error => {
                    console.error("Error loading services:", error);
                    serviceCategories.innerHTML = `
                    <div class="text-center py-3">
                        <p class="text-danger mt-2">An error occurred while loading services.</p>
                    </div>
                `;
                    updateArrows();
                });
        };

        // Function to update arrow states
        const updateArrows = () => {
            const scrollLeft = carouselContainer.scrollLeft;
            const maxScroll = carouselContainer.scrollWidth - carouselContainer.clientWidth;

            prevArrow.disabled = scrollLeft <= 0;
            nextArrow.disabled = scrollLeft >= maxScroll - 1;
        };

        // Scroll by one card width
        const scrollCarousel = (direction) => {
            const cardWidth = serviceCategories.querySelector(".col-md-4").offsetWidth + 16; // Including gap
            const scrollAmount = direction === "next" ? cardWidth : -cardWidth;
            carouselContainer.scrollBy({ left: scrollAmount, behavior: "smooth" });
        };

        // Event listeners for arrows
        prevArrow.addEventListener("click", () => scrollCarousel("prev"));
        nextArrow.addEventListener("click", () => scrollCarousel("next"));

        // Update arrows on scroll
        carouselContainer.addEventListener("scroll", updateArrows);

        // Initial load
        loadServices();
    });
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
    const staffListContainer = document.getElementById("staff-list");

    // Function to load staff members
    const loadStaff = () => {
        staffListContainer.innerHTML = `
            <div class="text-center py-3">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
                <p class="mt-2">Loading team members...</p>
            </div>
        `;

        fetch(window.location.origin + "/SalonManage/staff")
            .then(response => {
                if (!response.ok) {
                    throw new Error("Failed to fetch staff members.");
                }
                return response.json();
            })
            .then(data => {
                if (data.error || data.length === 0) {
                    staffListContainer.innerHTML = `
                        <div class="text-center py-3">
                            <p class="mt-2">No team members available at the moment.</p>
                        </div>
                    `;
                    return;
                }

                // Render staff dynamically
                let staffHTML = "";
                data.forEach(staff => {
                    staffHTML += `
                        <div class="col-md-4 col-lg-3">
                            <div class="card text-center">
                                <img src="${staff.imageUrl}" class="card-img-top" alt="${staff.firstName} ${staff.lastName}">
                                <div class="card-body">
                                    <h5 class="card-title">${staff.firstName} ${staff.lastName}</h5>
                                    <p class="card-text">${staff.role}</p>
                                </div>
                            </div>
                        </div>
                    `;
                });

                staffListContainer.innerHTML = staffHTML;
            })
            .catch(error => {
                console.error("Error loading staff members:", error);
                staffListContainer.innerHTML = `
                    <div class="text-center py-3">
                        <p class="text-danger mt-2">An error occurred while loading team members.</p>
                    </div>
                `;
            });
    };

    // Call the function to load staff
    loadStaff();
});