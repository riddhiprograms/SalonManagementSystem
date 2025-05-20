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
    console.log("Loading dashboard.js");
    
    const dashboardTab = document.getElementById("dashboard-tab-btn");
    if (dashboardTab) {
        dashboardTab.addEventListener("shown.bs.tab", loadDashboardData);
        if (dashboardTab.classList.contains("active")) {
            loadDashboardData();
        }
    } else {
        console.log("Dashboard tab button not found, loading data anyway");
        loadDashboardData(); // Fallback
    }
});

function loadDashboardData() {
    const url = `${window.location.origin}/SalonManage/today-appointments`;
    console.log("Fetching dashboard data from:", url);
    
    fetch(url)
        .then(response => {
            console.log("Dashboard fetch response status:", response.status);
            if (!response.ok) {
                return response.json().then(errorData => {
                    throw new Error(errorData.error || `HTTP error! status: ${response.status}`);
                });
            }
            return response.json();
        })
        .then(data => {
            console.log("Dashboard data received:", data);
            const countElement = document.getElementById("today-appointments-count");
            if (countElement) {
                const count = Number(data.todayAppointments) || 0;
                countElement.textContent = count; // Or keep your badge version if preferred
                console.log("Updated appointment count to:", count);
            } else {
                console.error("Element #today-appointments-count not found");
            }
        })
        .catch(error => {
            console.error("Error loading dashboard data:", error);
            const countElement = document.getElementById("today-appointments-count");
            if (countElement) {
                countElement.innerHTML = '<span class="badge bg-danger">Error</span>';
            }
        });
        // Fetch New Bookings (last 7 days)
    fetch(`/SalonManage/new-bookings`)
        .then(response => {
            console.log("New bookings fetch response status:", response.status);
            if (!response.ok) {
                return response.json().then(errorData => {
                    throw new Error(errorData.error || `HTTP error! status: ${response.status}`);
                });
            }
            return response.json();
        })
        .then(data => {
            console.log("New bookings data:", data);
            const countElement = document.getElementById("new-bookings-count");
            if (countElement) {
                const count = Number(data.newBookingsCount) || 0;
                countElement.innerHTML = `<span class="badge bg-success">${count}</span>`;
            } else {
                console.error("Element #new-bookings-count not found");
            }
        })
        .catch(error => {
            console.error("Error loading new bookings data:", error);
            const countElement = document.getElementById("new-bookings-count");
            if (countElement) {
                countElement.innerHTML = '<span class="badge bg-danger">Error</span>';
            }
        });
        
        // Fetch Total Customers
    fetch(`/SalonManage/total-customers`)
        .then(response => {
            console.log("Customers fetch response status:", response.status);
            if (!response.ok) {
                return response.json().then(errorData => {
                    throw new Error(errorData.error || `HTTP error! status: ${response.status}`);
                });
            }
            return response.json();
        })
        .then(data => {
            console.log("Customers data:", data);
            const countElement = document.getElementById("total-customers");
            if (countElement) {
                const count = Number(data.totalCustomers) || 0;
                countElement.innerHTML = `<span class="badge bg-info">${count}</span>`;
            } else {
                console.error("Element #total-customers not found");
            }
        })
        .catch(error => {
            console.error("Error loading customers data:", error);
            const countElement = document.getElementById("total-customers");
            if (countElement) {
                countElement.innerHTML = '<span class="badge bg-danger">Error</span>';
            }
        });
        
        // Fetch Monthly Revenue
    fetch(`/SalonManage/monthly-revenue`)
        .then(response => {
            console.log("Revenue fetch response status:", response.status);
            if (!response.ok) {
                return response.json().then(errorData => {
                    throw new Error(errorData.error || `HTTP error! status: ${response.status}`);
                });
            }
            return response.json();
        })
        .then(data => {
            console.log("Revenue data:", data);
            const revenueElement = document.getElementById("monthly-revenue");
            if (revenueElement) {
                const revenue = Number(data.monthlyRevenue) || 0;
                const formattedRevenue = revenue.toLocaleString('en-IN', {
                    style: 'currency',
                    currency: 'INR',
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2
                });
                revenueElement.innerHTML = `<span class="badge bg-warning">${formattedRevenue}</span>`;
            } else {
                console.error("Element #monthly-revenue not found");
            }
        })
        .catch(error => {
            console.error("Error loading revenue data:", error);
            const revenueElement = document.getElementById("monthly-revenue");
            if (revenueElement) {
                revenueElement.innerHTML = '<span class="badge bg-danger">Error</span>';
            }
        });
        
        // Fetch Today's Upcoming Appointments
  fetch(`/SalonManage/upcoming-appointments`)
    .then((response) => {
      console.log(
        "Upcoming appointments fetch response status:",
        response.status
      );
      if (!response.ok) {
        return response.json().then((errorData) => {
          throw new Error(
            errorData.error || `HTTP error! status: ${response.status}`
          );
        });
      }
      return response.json();
    })
    .then((data) => {
      console.log("Upcoming appointments data:", data);
      const tableBody = document.getElementById("upcoming-appointments-table");
      if (tableBody) {
        if (data.appointments && data.appointments.length > 0) {
          tableBody.innerHTML = data.appointments
            .map(
              (appointment) => `
                        <tr>
                            <td>${appointment.time}</td>
                            <td>${appointment.customerName}</td>
                            <td>${appointment.serviceName}</td>
                            <td>${appointment.staffName}</td>
                            <td><span class="badge bg-primary">${appointment.status}</span></td>
                        </tr>
                    `
            )
            .join("");
        } else {
          tableBody.innerHTML =
            '<tr><td colspan="5" class="text-center">No upcoming appointments today</td></tr>';
        }
      } else {
        console.error("Element #upcoming-appointments-table not found");
      }
    })
    .catch((error) => {
      console.error("Error loading upcoming appointments data:", error);
      const tableBody = document.getElementById("upcoming-appointments-table");
      if (tableBody) {
        tableBody.innerHTML =
          '<tr><td colspan="5" class="text-center">Error loading appointments</td></tr>';
      }
    });
    
    //Fetch popular services
     fetch(`/SalonManage/popular-services`)
    .then((response) => {
      console.log("Popular services fetch response status:", response.status);
      if (!response.ok) {
        return response.json().then((errorData) => {
          throw new Error(
            errorData.error || `HTTP error! status: ${response.status}`
          );
        });
      }
      return response.json();
    })
    .then((data) => {
      console.log("Popular services data:", data);
      const chartContainer = document.getElementById("popular-services-chart");
      if (chartContainer) {
        chartContainer.innerHTML =
          '<canvas id="popularServicesCanvas"></canvas>';
        const ctx = document
          .getElementById("popularServicesCanvas")
          .getContext("2d");
        new Chart(ctx, {
          type: "bar",
          data: {
            labels: data.services.map((service) => service.name),
            datasets: [
              {
                label: "Bookings",
                data: data.services.map((service) => service.appointmentCount),
                backgroundColor: "rgba(255, 165, 0, 0.5)", // Orange
                borderColor: "rgba(255, 165, 0, 1)", // Solid orange
                borderWidth: 1,
              },
            ],
          },
          options: {
            indexAxis: "y", // Horizontal bars
            scales: {
              x: {
                display: true,
                ticks: { display: false }, // Hide x-axis ticks
                grid: { display: false }, // Hide grid lines
              },
              y: {
                title: {
                  display: true,
                  text: "Service",
                },
                ticks: { font: { size: 14 } },
              },
            },
            plugins: {
              legend: { display: false },
              datalabels: { display: false }, // Ensure no data labels
            },
            maintainAspectRatio: false,
            responsive: true,
            barPercentage: 0.8,
            categoryPercentage: 0.8,
          },
        });
        // Set a fixed height for the chart to match the card
        chartContainer.querySelector("canvas").style.height = "200px";
      } else {
        console.error("Element #popular-services-chart not found");
      }
    })
    .catch((error) => {
      console.error("Error loading popular services data:", error);
      const chartContainer = document.getElementById("popular-services-chart");
      if (chartContainer) {
        chartContainer.innerHTML =
          '<div class="text-center py-3"><p>Error loading service statistics</p></div>';
      }
    });
    fetch(`/SalonManage/recent-activities`)
    .then((response) => {
      console.log("Recent activities fetch response status:", response.status);
      if (!response.ok) {
        return response.json().then((errorData) => {
          throw new Error(
            errorData.error || `HTTP error! status: ${response.status}`
          );
        });
      }
      return response.json();
    })
    .then((data) => {
      console.log("Recent activities data:", data);
      const activitiesContainer = document.getElementById("recent-activities");
      if (activitiesContainer) {
        if (data.activities && data.activities.length > 0) {
          activitiesContainer.innerHTML =
            '<ul class="list-group list-group-flush">' +
            data.activities
              .map(
                (activity) => `
                            <li class="list-group-item">
                                <strong>${activity.action}</strong>: ${
                  activity.details
                } - 
                                <small class="text-muted">${new Date(
                                  activity.timestamp
                                ).toLocaleString()}</small>
                            </li>
                        `
              )
              .join("") +
            "</ul>";
        } else {
          activitiesContainer.innerHTML =
            '<div class="text-center py-3"><p>No recent activities</p></div>';
        }
      } else {
        console.error("Element #recent-activities not found");
      }
    })
    .catch((error) => {
      console.error("Error loading recent activities data:", error);
      const activitiesContainer = document.getElementById("recent-activities");
      if (activitiesContainer) {
        activitiesContainer.innerHTML =
          '<div class="text-center py-3"><p>Error loading recent activities</p></div>';
      }
    });
    
    // Fetch Inventory Status
  fetch(`/SalonManage/inventory-status`)
    .then((response) => {
      console.log("Inventory status fetch response status:", response.status);
      if (!response.ok) {
        return response.json().then((errorData) => {
          throw new Error(
            errorData.error || `HTTP error! status: ${response.status}`
          );
        });
      }
      return response.json();
    })
    .then((data) => {
      console.log("Inventory status data:", data);
      const inventoryContainer = document.getElementById("inventory-status");
      if (inventoryContainer) {
        if (data.products && data.products.length > 0) {
          inventoryContainer.innerHTML = `
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th>Product</th>
                                    <th>Stock</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${data.products
                                  .map(
                                    (product) => `
                                    <tr>
                                        <td>${product.name}</td>
                                        <td>${product.stockQuantity}</td>
                                        <td><span class="badge ${
                                          product.status === "Out of Stock"
                                            ? "bg-danger"
                                            : product.status === "Low Stock"
                                            ? "bg-warning"
                                            : "bg-success"
                                        }">
                                            ${product.status}
                                        </span></td>
                                    </tr>
                                `
                                  )
                                  .join("")}
                            </tbody>
                        </table>`;
        } else {
          inventoryContainer.innerHTML =
            '<div class="text-center py-3"><p>No inventory data</p></div>';
        }
      } else {
        console.error("Element #inventory-status not found");
      }
    })
    .catch((error) => {
      console.error("Error loading inventory status data:", error);
      const inventoryContainer = document.getElementById("inventory-status");
      if (inventoryContainer) {
        inventoryContainer.innerHTML =
          '<div class="text-center py-3"><p>Error loading inventory status</p></div>';
      }
    });
    
    fetch("/SalonManage/pending-admins")
    .then((response) => {
        console.log("Pending admins fetch response status:", response.status);
        if (!response.ok) {
            return response.text().then((text) => {
                console.error("Pending admins error response body:", text);
                throw new Error(`HTTP error! status: ${response.status}, body: ${text.substring(0, 100)}...`);
            });
        }
        return response.json();
    })
    .then((data) => {
        console.log("Pending admins data:", data);
        const tableBody = document.getElementById("pending-admins-table");
        const tableHead = document.getElementById("pending-admins-thead");
        const noPendingAdmins = document.getElementById("no-pending-admins");

        if (tableBody && noPendingAdmins) {
            if (data.users && data.users.length > 0) {
                noPendingAdmins.style.display = "none";
                tableBody.innerHTML = data.users
                    .map(
                        (user) => `
                            <tr>
                                <td>${user.userId}</td>
                                <td>${user.firstName} ${user.lastName}</td>
                                <td>${user.email}</td>
                                <td>${user.phone}</td>
                                <td>
                                    <button class="btn btn-success btn-sm me-2" onclick="approveAdmin(${user.userId})">Approve</button>
                                    <button class="btn btn-danger btn-sm" onclick="rejectAdmin(${user.userId})">Reject</button>
                                </td>
                            </tr>
                        `
                    )
                    .join("");
            } else {
                tableBody.innerHTML = "";
                tableHead.style.display = "none";
                noPendingAdmins.style.display = "block";
            }
        } else {
            console.error(
                "Element #pending-admins-table or #no-pending-admins not found"
            );
        }
    })
    .catch((error) => {
        console.error("Error loading pending admins data:", error);
        const tableBody = document.getElementById("pending-admins-table");
        const noPendingAdmins = document.getElementById("no-pending-admins");
        if (tableBody && noPendingAdmins) {
            tableBody.innerHTML =
                '<tr><td colspan="5" class="text-center">Error loading approvals</td></tr>';
            noPendingAdmins.style.display = "none";
        }
    });
}

function approveAdmin(userId) {
    performAdminAction(userId, "approve");
}

function rejectAdmin(userId) {
    performAdminAction(userId, "reject");
}

function performAdminAction(userId, action) {
    const url = `${window.location.origin}/SalonManage/admin-action`;
    console.log(`Sending ${action} request for userId=${userId} to ${url}`);
    
    fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded",
        },
        body: `action=${encodeURIComponent(action)}&userId=${encodeURIComponent(userId)}`,
    })
        .then((response) => {
            console.log(`${action} response Xresponse status:`, response.status);
            if (!response.ok) {
                return response.text().then((text) => {
                    console.error(`${action} error response body:`, text);
                    throw new Error(`HTTP error! status: ${response.status}, body: ${text.substring(0, 100)}...`);
                });
            }
            return response.json();
        })
        .then((data) => {
            console.log(`${action} response:`, data);
            showModal(data.message || `Admin ${action}ed successfully`, () => {
                loadDashboardData(); // Refresh the table
            });
        })
        .catch((error) => {
            console.error(`Error ${action}ing admin:`, error);
            showModal(`Failed to ${action} admin: ${error.message}`);
        });
}