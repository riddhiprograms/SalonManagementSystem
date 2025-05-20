<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reset Password</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <!-- Reset Password Section -->
    <section class="py-5">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-6 col-lg-5">
                    <div class="card shadow">
                        <div class="card-body p-4">
                            <div class="text-center mb-4">
                                <h2 class="fw-bold mb-2">Reset Your Password</h2>
                                <p class="text-muted">Enter your new password below.</p>
                            </div>

                            <!-- Display error/success message -->
                            <c:if test="${not empty param.error}">
                                <div class="alert alert-danger">
                                    <c:out value="${param.error}" />
                                </div>
                            </c:if>
                            <c:if test="${not empty param.message}">
                                <div class="alert alert-success">
                                    <c:out value="${param.message}" />
                                </div>
                            </c:if>

                            <!-- Reset Password Form -->
                            <form id="reset-password-form" action="${pageContext.request.contextPath}/reset-password" method="post">
                                <!-- Hidden Token -->
                                <input type="hidden" name="token" value="<c:out value='${param.token}' />">

                                <!-- New Password -->
                                <div class="mb-3">
                                    <label for="new-password" class="form-label">New Password</label>
                                    <input type="password" class="form-control" id="new-password" name="newPassword" placeholder="Enter new password" required>
                                </div>

                                <!-- Confirm Password -->
                                <div class="mb-3">
                                    <label for="confirm-password" class="form-label">Confirm Password</label>
                                    <input type="password" class="form-control" id="confirm-password" placeholder="Confirm new password" required>
                                </div>

                                <div class="d-grid gap-2">
                                    <button type="submit" class="btn btn-primary">Reset Password</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Scripts -->
    <script>
        document.getElementById('reset-password-form').addEventListener('submit', (event) => {
            const newPassword = document.getElementById('new-password').value;
            const confirmPassword = document.getElementById('confirm-password').value;
            if (newPassword !== confirmPassword) {
                alert('Passwords do not match!');
                event.preventDefault(); // Prevent form submission
            }
        });
    </script>
</body>
</html>
