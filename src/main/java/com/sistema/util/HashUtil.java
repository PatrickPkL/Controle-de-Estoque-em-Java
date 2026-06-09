package com.sistema.util;

import java.security.MessageDigest;

/**
 * Utilitário para hash de senhas usando o algoritmo SHA-256.
 * <p>
 * As senhas nunca são armazenadas em texto puro no banco de dados.
 * Sempre que um usuário faz login ou cadastra uma nova senha,
 * o hash SHA-256 é calculado e armazenado.
 */
public class HashUtil {

    /**
     * Gera o hash SHA-256 de uma string.
     * @param base Texto a ser hasheado (ex.: senha digitada pelo usuário).
     * @return String hexadecimal de 64 caracteres representando o hash.
     * @throws RuntimeException Se o algoritmo não estiver disponível.
     */
    public static String sha256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
