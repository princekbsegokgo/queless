# QueLess — Clinic Queue Showcase

A simple web demonstration with two separate experiences:

- **Patient page:** join a queue, view a ticket and waiting estimate, and see when staff call you.
- **Staff dashboard:** the original four-colour-card business dashboard with a live queue table, analytics and working buttons.

This is intentionally a **class project demo**, not a live healthcare service.

## How to demonstrate in two browsers

1. Open the homepage: https://queless-rvbn.onrender.com/
2. In browser A select **I'm a patient**, then choose General Consultation, Pharmacy or Laboratory.
3. Enter your name and click **Get my queue ticket**. Keep the ticket page open.
4. In browser B select **I'm clinic staff**. It opens **/dashboard** directly, without a password or extra login screen.
5. On the business dashboard, select the same department, then click **Call next ticket**. The patient's ticket page updates automatically.
6. Click **Complete service** to mark the visit finished. **Pause queue** stops new joins to the selected department; **Resume queue** reopens it.

For a quicker presentation, staff can click **Load demo patients** on an empty queue: this adds four example names to that department and marks the first as serving. If a department already has active tickets, it won't add duplicates.

## Links

| Page | Link |
| --- | --- |
| Start / choose role | https://queless-rvbn.onrender.com/ |
| Patient joins | https://queless-rvbn.onrender.com/joinqueue |
| Staff dashboard | https://queless-rvbn.onrender.com/dashboard |
| Old staff/login bookmark | https://queless-rvbn.onrender.com/staff/login (redirects to dashboard) |

The existing QR code still points to the homepage, so it doesn't need to change.

## Render setup

No staff password or environment variable is required for showcase mode. Render needs to deploy the latest GitHub `main` commit. The repository's root `Dockerfile` compiles the Maven WAR and starts Tomcat.

## Important demo limitations

- **Staff functions are deliberately open to visitors for the class demo.** There are no staff accounts or logins. Use invented names only; do not deploy this version for actual clinic use.
- Tickets and statistics live in memory. They reset whenever Render restarts or redeploys, and multiple instances would not share their queue state.
- The three departments and sample patient names are illustrative.
- The five-minute-per-person waiting estimate is approximate.
- Ticket updates work by refreshing the page; there are no real SMS, WhatsApp or push messages.
- Avoid sharing private ticket URLs or entering patient identifiers or sensitive health information.
- This app does not provide emergency care or triage.

For actual clinic use later, add a persistent database, named staff accounts with authentication, audit logs, privacy rules, and reliable communications.
