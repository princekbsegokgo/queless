# QueLess — Digital Clinic Queue Demonstration

QueLess lets clinic visitors join a queue from their phone, track their place in line, and see when staff call them. Clinic staff have a **separate, password-protected portal**.

## How to use

### Patients (no login needed)
1. Open the site at `https://queless-rvbn.onrender.com/`.
2. Choose **I'm a patient** or open `/joinqueue` directly.
3. Choose **General Consultation**, **Pharmacy**, or **Laboratory**.
4. Enter a display name and choose **Get my queue ticket**.
5. Keep the automatically generated private `/status?ticket=...` link.
6. The ticket updates every six seconds, showing people ahead, estimated waiting time, and whether staff have called you.
7. You can cancel a ticket while still waiting. You cannot cancel after being called.

The QR code pointing to the site's homepage stays valid when these pages change.

### Staff (password required)
1. Open `https://queless-rvbn.onrender.com/staff/login`.
2. Enter the private staff password.
3. Select the relevant department on the business dashboard.
4. **Call next ticket** to call a waiting patient. If a patient is currently being served, this button finishes their service and calls the next one.
5. **Complete service** to finish a visit without calling another patient.
6. **Pause queue** to stop new patients joining and prevent further calls. **Resume queue** opens it again.
7. **Log out** when done.

**Required Render setup before staff sign-in works:** In Render, open the QueLess web service → **Environment** → add an environment variable named `QUELESS_STAFF_PASSWORD` with a strong, unique secret you choose → save and redeploy. Do **not** put that password into GitHub source code. There is deliberately no default staff password.

Local testing is possible using Java 21 and Maven; for staff login set `QUELESS_STAFF_PASSWORD` in the local environment before starting the app. Do not use the CI test password for actual deployment.

## Where the pages live

| Purpose | Route |
| --- | --- |
| Choose patient/staff | `/` |
| Patient queue selection | `/joinqueue` |
| Private patient ticket | `/status?ticket=<secret>` |
| Staff login | `/staff/login` |
| Staff dashboard | `/dashboard` |
| Staff logout | POST `/staff/logout` |

All staff queue action endpoints require staff login and a per-session CSRF token. Patient and staff sessions remain separate if you use different browsers/private browsing sessions.

## Running this demo

The root `Dockerfile` builds `queless-webapp/` using Maven and runs the WAR in Tomcat 10.1. GitHub Actions compile the application and run integration smoke tests with a test-only staff password.

## Important current limitations

- **Demo only.** All tickets and dashboard metrics are held in server memory. Deploys, Render restarts, and spinning up additional instances lose or split queue data. A persistent database and transaction-safe ticket handling must be added before a real clinic pilot.
- The three departments are examples, not evidence that any named clinic is actively using QueLess.
- No actual email, SMS, WhatsApp, or mobile push notifications. The ticket page checks the app automatically while it is open.
- The "estimated waiting time" uses five minutes per person ahead, not a trained prediction; actual service times vary.
- The password is one shared staff secret and the basic session login is suitable only for a **class project demo**. A real facility requires named staff accounts, strong authentication, role-based access, audit trails, rate limits and privacy controls.
- Patient ticket links are private capabilities. Anyone with a link can see that ticket's display name and status; do not share links or enter diagnoses, identity numbers or other sensitive health details.
- This application does not provide clinical triage or emergency services. Emergencies must be handled through regular clinical procedures.

## Recommended next upgrades

Persistent PostgreSQL database; staff accounts and audit logs; queue transfer between departments; secure SMS/WhatsApp notification opt-in; reception-assisted ticket creation for patients without smartphones; accessibility and multilingual review; privacy and data-retention policy; service-time analytics and staffing reports.
