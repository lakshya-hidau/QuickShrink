package com.url.shortener.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtils jwtTokenProvider;

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String jwt = jwtTokenProvider.getJwtFromHeader(request);
			System.out.println("Extracted JWT: " + jwt); // Log the extracted token

			if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
				String username = jwtTokenProvider.getUserNameFromJwtToken(jwt);
				System.out.println("Extracted Username: " + username); // Log the extracted username
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				if (userDetails != null) {
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authentication);
					System.out.println("Authentication successful for user: " + username); // Log successful authentication
				}
			} else {
				System.out.println("Token validation failed or token is null"); // Log validation failure
			}
		} catch (Exception e) {
			SecurityContextHolder.clearContext(); // Clear the context in case of an error
			e.printStackTrace();
		} finally {
			filterChain.doFilter(request, response);
		}
	}
}