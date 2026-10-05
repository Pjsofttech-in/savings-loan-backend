# Backend Configuration

Member registration payments use Razorpay Checkout. The registration form offers all available online methods or a UPI-only option for Google Pay and Razorpay's scan-to-pay flow when available. Member data and the uploaded ID proof are stored only after the backend verifies a captured payment. Configure the fee and Razorpay credentials in the backend process environment before starting the API:

```powershell
$env:REGISTRATION_FEE = "750"
$env:RAZORPAY_KEY_ID = "rzp_test_your_key_id"
$env:RAZORPAY_KEY_SECRET = "your_key_secret"
.\mvnw.cmd spring-boot:run
```

Run these commands from the `backend` directory. The registration form fetches the fee from the API, and order creation uses the backend-configured amount. Change `REGISTRATION_FEE` and restart the backend when the fee changes. Keep the key secret out of frontend environment files and source control.

There is not yet an authenticated admin settings screen or API. Fee changes are backend-operator controlled until the application has an authenticated admin workflow. Set `VITE_API_BASE_URL` in the frontend environment if the API is not at `http://localhost:8081`.

Backend tests use an in-memory H2 database and do not connect to the configured MySQL database.
