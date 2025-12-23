package lk.exon.aethenosapi.utils;

import lk.exon.aethenosapi.model.GenerateKeys;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class GenerateKey {

    public GenerateKeys generateKeys() throws Exception {

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();

        String privateKey = "-----BEGIN PRIVATE KEY-----\n" +
                Base64.getEncoder().encodeToString( kp.getPrivate().getEncoded()) + "\n" +
                "-----END PRIVATE KEY-----";

        String publicKey = "-----BEGIN PUBLIC KEY-----\n" +
                Base64.getEncoder().encodeToString( kp.getPublic().getEncoded()) + "\n" +
                "-----END PUBLIC KEY-----";

        GenerateKeys keys = new GenerateKeys();
        keys.setPrivateKey(privateKey);
        keys.setPublicKey(publicKey);

        return keys;

    }

}
