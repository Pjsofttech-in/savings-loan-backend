# Backend Configuration

Member registration payments use Razorpay Checkout. The registration form offers all available online methods or a UPI-only option for Google Pay and Razorpay's scan-to-pay flow when available. Member data and the uploaded ID proof are stored only after the backend verifies a captured payment. Configure the Razorpay credentials and admin password in the backend process environment before starting the API:

```powershell
$env:RAZORPAY_KEY_ID = "rzp_test_your_key_id"
$env:RAZORPAY_KEY_SECRET = "your_key_secret"
$env:ADMIN_PASSWORD = "choose-a-strong-private-password"
.\mvnw.cmd spring-boot:run
```

Run these commands from the `backend` directory. In the app, open **Settings → Registration fee**, enter the amount and admin password, then save. The fee is stored in the database and is used for customer checkouts without restarting the API. Keep the Razorpay secret and admin password out of frontend environment files and source control. Use HTTPS when the app is deployed.

The registration fee starts as unconfigured until an admin saves an amount. The backend Maven build installs the frontend dependencies, builds the Vite app, and packages it into the Spring Boot JAR. Open the app at `http://localhost:8081`; the frontend and `/api/...` requests then use the same base URL. The frontend Axios client reads `VITE_API_BASE_URL` from `frontend/.env`, set to `http://localhost:8081` for local use. For local frontend development, Vite proxies `/api` requests to the backend on port `8081`. When deploying, set `VITE_API_BASE_URL` to the public backend URL (or the shared frontend/backend origin) before building.

Backend tests use an in-memory H2 database and do not connect to the configured MySQL database.

To load nine clearly labelled demo members and linked share transactions into a local development database, set `$env:APP_SAMPLE_DATA_ENABLED = "true"` before starting the backend. The initializer skips demo member IDs and share references that are already present. Leave this disabled for production; the sample contact information and records are fictional.
