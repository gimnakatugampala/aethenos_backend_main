package lk.exon.aethenosapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lk.exon.aethenosapi.entity.GupType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.nio.file.AccessDeniedException;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {

	private static final long serialVersionUID = -2550185165626007488L;

	public static final long JWT_TOKEN_VALIDITY = 12 * 60 * 60;

	@Value("${jwt.secret}")
	private String secret;

	public String getUsernameFromToken(String token) throws Exception {
		return getClaimFromToken(token, Claims::getSubject);
	}

	public Date getIssuedAtDateFromToken(String token) throws Exception {
		return getClaimFromToken(token, Claims::getIssuedAt);
	}

	public Date getExpirationDateFromToken(String token) throws Exception {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) throws Exception {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) throws Exception {
		JwtTokenGenerator jwtTokenGenerator = new JwtTokenGenerator();
		return jwtTokenGenerator.parseJwt(token);
	}

	private Boolean isTokenExpired(String token) throws Exception {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	private Boolean ignoreTokenExpiration(String token) {
		// here you specify tokens, for that the expiration is ignored
		return false;
	}

	public String generateToken(UserDetails userDetails) throws Exception {
		JwtTokenGenerator jwtTokenGenerator = new JwtTokenGenerator();
		String token = jwtTokenGenerator.generateToken(userDetails);
		return token;
	}


	public Boolean canTokenBeRefreshed(String token) throws Exception {
		return (!isTokenExpired(token) || ignoreTokenExpiration(token));
	}

	public Boolean validateToken(String token, UserDetails userDetails) throws Exception {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	public GupType getGupTypeFromToken(String jwtToken) throws Exception {
		Claims claims = getAllClaimsFromToken(jwtToken);
		return GupType.valueOf((String) claims.get("gupType"));
	}

}