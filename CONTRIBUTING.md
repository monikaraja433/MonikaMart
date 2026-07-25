# Contributing to MonikaMart

Thank you for contributing to the **MonikaMart** Capstone Project (Anna University R2025 Semester 3). To maintain high engineering standards, please adhere strictly to these guidelines.

---

## 1. Quickstart: Git Clone to Running Instance

### Prerequisites
- **JDK 17** or higher (`java -version`)
- **Apache Maven 3.8+** (`mvn -version`)
- **Git** (`git --version`)
- Optional: **Docker & Docker Compose** for containerized deployment

### Step-by-Step Setup
1. **Clone the repository:**
   ```bash
   git clone https://github.com/monika/monikamart.git
   cd monikamart
   ```

2. **Configure Environment Variables:**
   ```bash
   # Copy the sample environment file
   cp .env.example .env
   ```
   *Note:* The `.env` and `config.properties` files are excluded from version control in `.gitignore`. The application defaults out-of-the-box to persistent local file-based H2 database storage in `./data/monikamart`.

3. **Build and Run Test Suite:**
   ```bash
   mvn clean verify
   ```
   All 25 unit and integration tests across DAOs, Services, Chatbot, and Validators must execute with `BUILD SUCCESS`.

4. **Package and Deploy to Tomcat 9.0:**
   ```bash
   mvn clean package
   ```
   Deploy the generated `target/monikamart.war` into Tomcat's `webapps/` folder, or launch via Docker:
   ```bash
   docker-compose up --build
   ```

5. **Verify Running Application:**
   - **Catalog:** [http://localhost:8080/products](http://localhost:8080/products) (or `http://localhost:8080/monikamart/products`)
   - **Health API:** [http://localhost:8080/api/v1/health](http://localhost:8080/api/v1/health) (returns `{"status":"UP","db":"UP"}`)

---

## 2. Branching & Git Workflow Model

- **`main` Branch:** Always production-ready and deployable.
- **Feature Branches:** Work must be performed on dedicated branches named `feature/<name>` (e.g., `feature/ai-chatbot-ui`, `feature/order-status-workflow`).
- **Merge Process:** All feature branches must be merged into `main` via self-reviewed Pull Requests after satisfying the Definition of Done.

### Conventional Commit Prefixes
Every commit must use standard conventional prefixes:
- `feat:` New features or functional capabilities
- `fix:` Bug fixes or corrections
- `test:` Unit, integration, or DAO test suite additions
- `docs:` Documentation, architecture diagrams, and guides

---

## 3. Definition of Done (DoD) Checklist

Before submitting or merging any pull request to `main`:
- [x] Code compiles clean with JDK 17 and Maven without errors.
- [x] All SQL queries use `PreparedStatement` only — no concatenated queries.
- [x] Passwords hashed with jBCrypt; sensitive credentials omitted from logs and code.
- [x] Output properly escaped using JSTL / `fn:escapeXml` to prevent XSS vulnerabilities.
- [x] All newly written DAO and Service methods accompanied by unit tests passing in CI.
- [x] Architecture diagrams and documentation updated if contracts changed.
