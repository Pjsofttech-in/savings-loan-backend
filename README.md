# Backend Configuration

Member registration payments use Razorpay Checkout. The registration form offers all available online methods or a UPI-only option for Google Pay and Razorpay's scan-to-pay flow when available. Member data and the uploaded ID proof are stored only after the backend verifies a captured payment. Configure the Razorpay credentials and admin password in the backend process environment before starting the API:

```powershell
$env:RAZORPAY_KEY_ID = "rzp_test_your_key_id"
$env:RAZORPAY_KEY_SECRET = "your_key_secret"
$env:ADMIN_PASSWORD = "choose-a-strong-private-password"
.\mvnw.cmd spring-boot:run
```

Run these commands from the `backend` directory. In the app, open **Settings → Registration fee**, enter the amount and admin password, then save. The fee is stored in the database and is used for customer checkouts without restarting the API. Keep the Razorpay secret and admin password out of frontend environment files and source control. Use HTTPS when the app is deployed.

The registration fee starts as unconfigured until an admin saves an amount. This backend project contains only the Spring Boot API; Maven does not install Node.js, build the Vite app, or package frontend files. The API runs at `http://localhost:8081`. In local frontend development, Vite proxies `/api` requests to this backend. When deploying the frontend separately, set `VITE_API_BASE_URL` to the backend's public URL before building the frontend and configure the backend to allow requests from the frontend origin. To expose both through one public URL, use a reverse proxy that routes `/api/...` to this backend and `/` to the frontend.

Backend tests use an in-memory H2 database and do not connect to the configured MySQL database.

To load nine clearly labelled demo members and linked share transactions into a local development database, set `$env:APP_SAMPLE_DATA_ENABLED = "true"` before starting the backend. The initializer skips demo member IDs and share references that are already present. Leave this disabled for production; the sample contact information and records are fictional.
