package cit.edu.pawfect.match.chat;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new AuthChannelInterceptor(jwtSecret));
    }

    @RequiredArgsConstructor
    public class AuthChannelInterceptor implements ChannelInterceptor {
        private final String jwtSecret;

        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                try {
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        throw new JwtException("Missing or invalid Authorization header");
                    }
                    String token = authHeader.replace("Bearer ", "");
                    String userId = validateToken(token);
                    accessor.setUser(new SimplePrincipal(userId));
                } catch (JwtException e) {
                    System.err.println("JWT validation failed: " + e.getMessage());
                    throw new RuntimeException("Authentication failed", e);
                }
                catch(Exception e){
                    System.err.println("Unexpected error during authentication: " + e.getMessage());
                    throw new RuntimeException("Authentication error", e);
                }
            }
            return message;
        }

        private String validateToken(String token) {
            try {
                return Jwts.parser()
                        .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret)))
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .getSubject();
            } catch (JwtException e) {
                throw new JwtException("Invalid token: " + e.getMessage());
            }
        }
    }
}