package cit.edu.pawfect.match.chat;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat").setAllowedOrigins("*");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                List<String> authHeaders = accessor.getNativeHeader("Authorization");
                if (authHeaders != null && !authHeaders.isEmpty()) {
                    String token = authHeaders.get(0).replace("Bearer ", "");
                    try {
                        String userId = extractUserIdFromToken(token);
                        if (userId == null || userId.isEmpty()) {
                            throw new RuntimeException("Invalid token: No userId found");
                        }
                        accessor.getSessionAttributes().put("userId", userId);
                        accessor.setUser(new SimplePrincipal(userId));
                    } catch (JwtException | IllegalArgumentException e) {
                        throw new RuntimeException("Invalid token: " + e.getMessage(), e);
                    }
                } else {
                    throw new RuntimeException("Missing or invalid Authorization header");
                }
                return message;
            }

            private String extractUserIdFromToken(String token) {
                byte[] keyBytes;
                try {
                    keyBytes = Decoders.BASE64.decode(jwtSecret);
                } catch (DecodingException e) {
                    throw new IllegalArgumentException("Invalid Base64-encoded secret key in configuration", e);
                }
                try {
                    return Jwts.parser()
                            .verifyWith(Keys.hmacShaKeyFor(keyBytes))
                            .build()
                            .parseSignedClaims(token)
                            .getPayload()
                            .getSubject();
                } catch (JwtException e) {
                    throw new RuntimeException("Failed to extract userId from JWT token: " + e.getMessage(), e);
                }
            }
        });
    }

    // Custom Principal implementation
    private static class SimplePrincipal implements Principal {
        private final String name;

        SimplePrincipal(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}