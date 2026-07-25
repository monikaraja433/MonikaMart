<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    </main>

    <footer class="footer">
        <div class="container">
            <p>&copy; 2026 <strong>MonikaMart</strong> &mdash; Anna University R2025, Semester 3 Capstone Project.</p>
            <p style="margin-top: 6px; font-size: 0.8rem; color: #94a3b8;">
                Built with Java Servlets &bull; JDBC &bull; HikariCP &bull; H2 Database &bull; Apache Tomcat 9.0.x &bull; AI Chatbot Engine
            </p>
        </div>
    </footer>

    <!-- Include AI Chatbot Widget -->
    <jsp:include page="/WEB-INF/views/common/chatbot.jsp" />

    <script src="${pageContext.request.contextPath}/js/main.js"></script>
    <script src="${pageContext.request.contextPath}/js/chatbot.js"></script>
</body>
</html>
