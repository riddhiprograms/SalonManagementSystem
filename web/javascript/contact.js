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
    const contactForm = document.getElementById("contact-form");

    contactForm.addEventListener("submit", async function (event) {
        event.preventDefault();
        
        const submitBtn = contactForm.querySelector('button[type="submit"]');
        const originalBtnText = submitBtn.innerHTML;
        
        try {
            // Show loading state
            submitBtn.disabled = true;
            submitBtn.innerHTML = `<span class="spinner-border spinner-border-sm" role="status"></span> Sending...`;
            
            // Collect form data
            const formData = {
                name: document.getElementById("name").value,
                email: document.getElementById("email").value,
                phone: document.getElementById("phone").value,
                message: document.getElementById("message").value
            };
            
            console.log("Submitting:", formData); // Debug log
            
            const response = await fetch(window.location.origin + "/SalonManage/contact-action", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(formData)
            });
            
            const data = await response.json();
            
            if (!response.ok) {
                throw new Error(data.error || "Failed to send message");
            }
            
            showModal(data.success ? "Message sent successfully!" : "Failed to send message", () => {
                if (data.success) contactForm.reset();
            });
            
        } catch (error) {
            console.error("Error details:", error);
            showModal(error.message || "An error occurred while sending your message");
        } finally {
            submitBtn.disabled = false;
            submitBtn.innerHTML = originalBtnText;
        }
    });
});