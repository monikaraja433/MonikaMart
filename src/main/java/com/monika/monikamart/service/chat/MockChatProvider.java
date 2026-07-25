package com.monika.monikamart.service.chat;

import com.monika.monikamart.dto.ChatResponseDTO;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MockChatProvider implements ChatProvider {

    @Override
    public ChatResponseDTO generateReply(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return new ChatResponseDTO(
                "Hello! How can I assist you with your shopping on MonikaMart today?",
                getProviderName(),
                Arrays.asList("Browse Electronics", "Track Order", "Return Policy", "Become a Seller")
            );
        }

        String query = userMessage.toLowerCase().trim();

        if (query.contains("track") || query.contains("where is my order") || query.contains("delivery status")) {
            return new ChatResponseDTO(
                "You can track your orders anytime by clicking on 'My Orders' in the top navigation bar. Every order shows its real-time workflow status: Pending → Confirmed → Shipped → Delivered.",
                getProviderName(),
                Arrays.asList("Go to My Orders", "Shipping Timelines", "Cancel an Order")
            );
        }

        if (query.contains("shipping") || query.contains("delivery time") || query.contains("deliver")) {
            return new ChatResponseDTO(
                "Standard delivery on MonikaMart takes 2 to 4 business days across Tamil Nadu and major metro cities. Express delivery is automatically assigned for priority orders.",
                getProviderName(),
                Arrays.asList("Track Order", "Shipping Costs", "View Products")
            );
        }

        if (query.contains("return") || query.contains("refund") || query.contains("exchange") || query.contains("replacement")) {
            return new ChatResponseDTO(
                "MonikaMart offers a hassle-free 7-day return and replacement policy for all delivered products. You can request a return directly from your Order Details page once the item is marked DELIVERED.",
                getProviderName(),
                Arrays.asList("Check Order History", "Product Warranty", "Customer Support")
            );
        }

        if (query.contains("seller") || query.contains("sell on") || query.contains("list product") || query.contains("merchant")) {
            return new ChatResponseDTO(
                "Interested in selling on MonikaMart? Sign up choosing the 'Seller' role! You will immediately get access to the Seller Dashboard to create listings, manage inventory, and fulfill incoming buyer orders.",
                getProviderName(),
                Arrays.asList("Register as Seller", "Seller Guidelines", "Commission Rates")
            );
        }

        if (query.contains("payment") || query.contains("pay") || query.contains("upi") || query.contains("card")) {
            return new ChatResponseDTO(
                "We support Mock Instant Payment for testing, simulating Cards, NetBanking, and UPI payments securely with instant order confirmation and stock reservation.",
                getProviderName(),
                Arrays.asList("Checkout Cart", "Refund Status", "Security Info")
            );
        }

        if (query.contains("book") || query.contains("clean code") || query.contains("design pattern") || query.contains("stationery")) {
            return new ChatResponseDTO(
                "We have a curated collection of software engineering books (including Clean Code and Design Patterns) and premium notebooks in our Books & Stationery sections.",
                getProviderName(),
                Arrays.asList("Browse Books", "Browse Stationery", "View Cart")
            );
        }

        if (query.contains("electronic") || query.contains("headphone") || query.contains("keyboard") || query.contains("mouse") || query.contains("monitor")) {
            return new ChatResponseDTO(
                "Check out top-rated electronics including Sony WH-1000XM5 headphones, Logitech MX Master 3S mouse, RGB mechanical keyboards, and 4K Dell monitors in our Electronics catalog.",
                getProviderName(),
                Arrays.asList("View Electronics", "Filter by Rating", "Sort by Price")
            );
        }

        if (query.contains("wishlist") || query.contains("save for later")) {
            return new ChatResponseDTO(
                "You can save items to your personal Wishlist by clicking the heart icon on any product page, and move them to your cart when you're ready to purchase!",
                getProviderName(),
                Arrays.asList("View My Wishlist", "Browse Products")
            );
        }

        if (query.contains("hello") || query.contains("hi") || query.contains("hey")) {
            return new ChatResponseDTO(
                "Hello! Welcome to MonikaMart AI Shopping Assistant. How may I help you today?",
                getProviderName(),
                Arrays.asList("Top Deals", "Track Order", "Return Policy", "Seller Dashboard")
            );
        }

        // Generic fallback within domain scope
        return new ChatResponseDTO(
            "I'm here to assist with products, orders, shipping, and seller questions on MonikaMart. Could you please specify if you'd like help with finding products, checking orders, or account settings?",
            getProviderName(),
            Arrays.asList("Browse Catalog", "Track My Orders", "Return Policy", "Contact Support")
        );
    }

    @Override
    public String getProviderName() {
        return "MockChatProvider (Offline FAQ Engine)";
    }
}
