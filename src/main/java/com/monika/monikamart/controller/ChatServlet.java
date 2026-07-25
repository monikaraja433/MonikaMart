package com.monika.monikamart.controller;

import com.monika.monikamart.dto.ApiResponse;
import com.monika.monikamart.dto.ChatRequestDTO;
import com.monika.monikamart.dto.ChatResponseDTO;
import com.monika.monikamart.exception.ValidationException;
import com.monika.monikamart.service.chat.ChatService;
import com.monika.monikamart.util.JsonUtil;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/api/chat"})
public class ChatServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ChatServlet.class.getName());
    private ChatService chatService;

    @Override
    public void init() {
        this.chatService = new ChatService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");

        HttpSession session = req.getSession(true);
        String sessionId = session.getId();

        try {
            ChatRequestDTO requestDto = JsonUtil.fromJson(req.getInputStream(), ChatRequestDTO.class);
            if (requestDto == null || requestDto.getMessage() == null || requestDto.getMessage().trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.writeJson(resp.getWriter(), ApiResponse.error("Message content cannot be blank."));
                return;
            }

            ChatResponseDTO responseDto = chatService.processMessage(sessionId, requestDto.getMessage());
            resp.setStatus(HttpServletResponse.SC_OK);
            JsonUtil.writeJson(resp.getWriter(), ApiResponse.success(responseDto));
        } catch (ValidationException ve) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonUtil.writeJson(resp.getWriter(), ApiResponse.error(ve.getMessage()));
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Chat processing exception: " + ex.getMessage(), ex);
            resp.setStatus(HttpServletResponse.SC_OK); // Degraded graceful response instead of 500
            ChatResponseDTO fallback = new ChatResponseDTO(
                "I am currently experiencing higher than usual traffic. For immediate help, please browse our product catalog or check order status directly.",
                "MonikaMart Assistant (Degraded Safe Mode)",
                null
            );
            JsonUtil.writeJson(resp.getWriter(), ApiResponse.success(fallback));
        }
    }
}
