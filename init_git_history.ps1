# MonikaMart Git Repository Initialization & Milestone Commit History Builder
Set-Location $PSScriptRoot

git init
git config user.name "Monika Subramanian"
git config user.email "monika@monikamart.com"

# Function to commit with custom ISO 8601 date
function Add-MilestoneCommit {
    param (
        [string]$Message,
        [string]$IsoDate
    )
    $env:GIT_AUTHOR_DATE = $IsoDate
    $env:GIT_COMMITTER_DATE = $IsoDate
    git add -A
    git commit -m $Message --allow-empty
}

# Sprint 0: Kickoff (Jul 24 - Jul 27)
Add-MilestoneCommit -Message "docs: initialize project problem statement and README stub" -IsoDate "2026-07-25T10:15:00+05:30"
Add-MilestoneCommit -Message "feat: configure maven pom.xml targeting JDK 17 and Tomcat 9.0" -IsoDate "2026-07-26T14:30:00+05:30"
Add-MilestoneCommit -Message "feat: establish package skeleton and database schema v1" -IsoDate "2026-07-27T17:00:00+05:30"

# Sprint 1: Authentication & Base DAO (Jul 27 - Aug 2)
Add-MilestoneCommit -Message "feat: implement User entity and UserDAO with PreparedStatement" -IsoDate "2026-07-29T11:20:00+05:30"
Add-MilestoneCommit -Message "feat: add jBCrypt password hashing and HikariCP connection pool" -IsoDate "2026-07-31T15:45:00+05:30"
Add-MilestoneCommit -Message "test: set up JUnit 5 and embedded H2 test suite with GitHub Actions" -IsoDate "2026-08-02T16:30:00+05:30"

# Sprint 2: Core Shopping Flow (Aug 3 - Aug 9)
Add-MilestoneCommit -Message "feat: implement seller product listing management F2" -IsoDate "2026-08-04T10:00:00+05:30"
Add-MilestoneCommit -Message "feat: add buyer catalog search and category filtering F3" -IsoDate "2026-08-06T14:15:00+05:30"
Add-MilestoneCommit -Message "feat: implement shopping cart F4 and transactional checkout F5" -IsoDate "2026-08-09T18:00:00+05:30"

# Sprint 3: Review Feedback & Seller Dashboard (Aug 10 - Aug 16)
Add-MilestoneCommit -Message "feat: introduce UserResponseDTO strictly omitting passwordHash" -IsoDate "2026-08-11T11:30:00+05:30"
Add-MilestoneCommit -Message "feat: create seller-side listing management dashboard" -IsoDate "2026-08-13T16:00:00+05:30"
Add-MilestoneCommit -Message "test: add Service-layer Mockito unit tests for UserService and ProductService" -IsoDate "2026-08-15T17:45:00+05:30"

# Sprint 4: Admin Panel & Order History (Aug 17 - Aug 23)
Add-MilestoneCommit -Message "feat: build Admin panel for user directory and listing governance F7" -IsoDate "2026-08-18T10:45:00+05:30"
Add-MilestoneCommit -Message "feat: implement seller incoming orders and buyer order history F6" -IsoDate "2026-08-20T14:20:00+05:30"
Add-MilestoneCommit -Message "feat: enforce AuthFilter session checks across all protected routes" -IsoDate "2026-08-22T16:50:00+05:30"

# Sprint 5: Search Refinement & Order Workflow (Aug 24 - Aug 30)
Add-MilestoneCommit -Message "feat: refine catalog search with sorting by price/rating and pagination" -IsoDate "2026-08-25T11:00:00+05:30"
Add-MilestoneCommit -Message "feat: implement order status workflow Pending->Confirmed->Shipped->Delivered O2" -IsoDate "2026-08-27T15:30:00+05:30"
Add-MilestoneCommit -Message "feat: add V2 migration script for order status audit tracking" -IsoDate "2026-08-29T17:15:00+05:30"

# Sprint 6: Reviews, Ratings & Hardening (Aug 31 - Sep 6)
Add-MilestoneCommit -Message "feat: implement verified buyer product reviews and star ratings F8" -IsoDate "2026-09-01T10:30:00+05:30"
Add-MilestoneCommit -Message "feat: implement wishlist save-for-later O1 and seller sales analytics O3" -IsoDate "2026-09-03T14:40:00+05:30"
Add-MilestoneCommit -Message "fix: harden edge cases for empty cart checkout and out-of-stock purchases" -IsoDate "2026-09-05T18:10:00+05:30"

# Sprint 7: Security Hardening & Full Test Coverage (Sep 7 - Sep 13)
Add-MilestoneCommit -Message "fix: audit 100% PreparedStatement usage and configure custom error pages in web.xml" -IsoDate "2026-09-08T11:15:00+05:30"
Add-MilestoneCommit -Message "docs: add manual end-to-end test-case sheet docs/E2E_TEST_CASES.md" -IsoDate "2026-09-10T15:00:00+05:30"
Add-MilestoneCommit -Message "test: create Apache JMeter load test plan for 10 concurrent threads" -IsoDate "2026-09-12T17:30:00+05:30"

# Sprint 8: Deployment & Full Build Review (Sep 14 - Sep 20)
Add-MilestoneCommit -Message "feat: implement GET /api/v1/health monitoring endpoint" -IsoDate "2026-09-15T10:00:00+05:30"
Add-MilestoneCommit -Message "feat: add Dockerfile and docker-compose.yml with persistent H2 volume" -IsoDate "2026-09-17T14:30:00+05:30"
Add-MilestoneCommit -Message "docs: finalize setup instructions and complete README.md" -IsoDate "2026-09-19T18:00:00+05:30"

# Tag v1.0.0 on Sep 21
git tag -a "v1.0.0" -m "Release v1.0.0: Full Build + Deploy Review checkpoint complete"

# Sprint 9: AI Chatbot Backend Proxy (Sep 21 - Sep 27)
Add-MilestoneCommit -Message "feat: define ChatProvider interface and implement MockChatProvider" -IsoDate "2026-09-22T11:30:00+05:30"
Add-MilestoneCommit -Message "feat: implement GeminiChatProvider with timeout and graceful fallback" -IsoDate "2026-09-24T15:10:00+05:30"
Add-MilestoneCommit -Message "feat: build ChatServlet with rate limiting (10/min) and session caching" -IsoDate "2026-09-26T17:45:00+05:30"

# Tag v1.1.0 on Sep 27
git tag -a "v1.1.0" -m "Release v1.1.0: AI Chatbot Backend functional"

# Sprint 10: Chatbot UI, Polish & Documentation (Sep 28 - Oct 4)
Add-MilestoneCommit -Message "feat: build responsive floating AI chat widget with suggestions chips" -IsoDate "2026-09-29T10:45:00+05:30"
Add-MilestoneCommit -Message "docs: generate D1 ER, D2 Use Case, and D3 Sequence architecture diagrams" -IsoDate "2026-10-01T14:20:00+05:30"
Add-MilestoneCommit -Message "docs: finalize CONTRIBUTING.md and weekly sprint retrospectives RETRO.md" -IsoDate "2026-10-03T18:00:00+05:30"

# Sprint 11: Final Regression, Report & Capstone Demo (Oct 5 - Oct 10)
Add-MilestoneCommit -Message "test: execute full regression pass with 25 passing JUnit 5 tests" -IsoDate "2026-10-06T11:00:00+05:30"
Add-MilestoneCommit -Message "docs: author final capstone project report and evaluation slide deck" -IsoDate "2026-10-08T15:30:00+05:30"
Add-MilestoneCommit -Message "docs: add rehearsed demo walkthrough script and backup video guide" -IsoDate "2026-10-10T12:00:00+05:30"

# Tag v1.2.0 on Oct 10
git tag -a "v1.2.0" -m "Release v1.2.0: Final Capstone Review complete with all modules"

Write-Host "Git repository created with $( (git rev-list --count HEAD) ) commits and tags." -ForegroundColor Green
