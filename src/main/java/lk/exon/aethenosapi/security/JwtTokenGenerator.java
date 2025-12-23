package lk.exon.aethenosapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lk.exon.aethenosapi.AethenosApiApplication;
import lk.exon.aethenosapi.entity.GeneralUserProfile;
import lk.exon.aethenosapi.repository.GeneralUserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

public class JwtTokenGenerator {
    @Autowired
    GeneralUserProfileRepository generalUserProfileRepository;

    public Claims parseJwt(String jwtString) throws InvalidKeySpecException, NoSuchAlgorithmException {

        PublicKey publicKey = getPublicKey();
        Claims jwt = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(jwtString).getBody();
        return jwt;
    }

    public String generateToken(UserDetails userDetails) throws Exception{
        PrivateKey privateKey = getPrivateKey();
        Instant now = Instant.now();
//        System.out.println("////////////////////"+generalUserProfileRepository.getGeneralUserProfileByEmail(userDetails.getUsername()));
        String jwtToken = Jwts.builder()

                .claim("username", userDetails.getUsername())
//                .claim("gupType","Student")
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(12, ChronoUnit.HOURS)))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
        return jwtToken;

    }


    private static PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {

        String rsaPrivateKey = AethenosApiApplication.keys.getPrivateKey();
        rsaPrivateKey = rsaPrivateKey.replace("-----BEGIN PRIVATE KEY-----\n", "");
        rsaPrivateKey = rsaPrivateKey.replace("\n-----END PRIVATE KEY-----", "");
        byte[] decode = Base64.getDecoder().decode(rsaPrivateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decode);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privKey = kf.generatePrivate(keySpec);
        return privKey;

    }

    private static PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {

        String rsaPublicKey = AethenosApiApplication.keys.getPublicKey();
        rsaPublicKey = rsaPublicKey.replace("-----BEGIN PUBLIC KEY-----\n", "");
        rsaPublicKey = rsaPublicKey.replace("\n-----END PUBLIC KEY-----", "");
        byte[] decode = Base64.getDecoder().decode(rsaPublicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decode);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey publicKey = kf.generatePublic(keySpec);
        return publicKey;

    }

}
