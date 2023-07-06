package com.juliandbs.shareyourfiles.persistence.token.tool;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.UUID;

/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

/**
 * This class is used as a tool for encrypt and decrypt a token using the AED/GCM/NoPadding encryption algorithm.
 * @author JulianDbs
 */
public final class TokenTool {

    private static final String ENCRYPTION_ALGORITHM = "AES/GCM/NoPadding";
    private static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String SECRET_KEY_SPEC_ALGORITHM = "AES";
    private static final int PBE_KEY_ITERATION_COUNT = 65536;
    private static final int PBE_KEY_LENGTH = 256;
    private static final int AUTHENTICATION_TAG_LENGTH = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int SALT_LENGTH_BYTE = 16;
    private static final Charset UTF_8 = StandardCharsets.UTF_8;
    private static final String TOKEN_COOKIE_NAME = "token";
    private final char[] TOKEN_KEY;


    /**
     * This class is used to group the exceptions thrown by the Cipher class related to the 'getInstance(...)' method.
     */
    public static class CipherTransformationAlgorithmException extends Exception {
        public CipherTransformationAlgorithmException(final Throwable cause) {super("The Cipher throws an exception related to the 'getInstance( String transformation)' method and its argument.", cause);}
    }

    /**
     * This class is used to group the exceptions thrown by the Cipher class related to the 'init(...)' method.
     */
    public static class CipherInitException extends Exception {
        public CipherInitException(final Throwable cause) {super("The Cipher throws an exception related to the 'init(int mode, Key key, AlgorithmParameterSpec params)' method and its arguments.", cause);}
    }

    /**
     * This class is used to group the exceptions thrown by the Cipher class related to the 'doFinal(...)' method.
     */
    public static class CipherDoFinalException extends Exception {
        public CipherDoFinalException(final Throwable cause) {super("The Cipher throws an exception related to the 'doFinal(byte[] input)' method and its argument.", cause);}
    }

    /**
     * This class is used to group the exceptions thrown by the SecretKeyFactory class.
     */
    public static class SecretKeyFactoryException extends Exception {
        public SecretKeyFactoryException(final String message, final Throwable cause) {super(message, cause);}
    }

    /**
     * Class constructor.
     * @param tokenKey A String representing the token key used to encrypt and decrypt the token.
     * @throws NullPointerException If the class constructor parameter is null.
     */
    public TokenTool(final String tokenKey) throws NullPointerException {
        if (tokenKey == null)
            throw new NullPointerException();
        this.TOKEN_KEY = tokenKey.toCharArray();
    }

    /**
     * This method is used to encrypt a token id.
     * @param tokenId A UUID instance that represents the token id.
     * @return A String instance that represents the encrypted token id.
     * @throws NullPointerException If the tokenId method argument is null.
     * @throws SecretKeyFactoryException If an error occurs while this tool generates a secret key.
     * @throws CipherTransformationAlgorithmException If an error occurs involving the Cipher 'getInstance(...)' method while the token is being encrypted.
     * @throws CipherInitException If an error occurs involving the Cipher 'init(...)' method while the token is being encrypted.
     * @throws CipherDoFinalException If an error occurs involving the Cipher 'doFinal(...)' method while the token is being encrypted.
     */
    public String encryptTokenId(final UUID tokenId) throws NullPointerException, SecretKeyFactoryException, CipherTransformationAlgorithmException, CipherInitException, CipherDoFinalException {
       if (tokenId == null)
           throw new NullPointerException();
        try {
            String token = String.valueOf(tokenId);
            return this.encrypt(token.getBytes());
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            throw new TokenTool.CipherTransformationAlgorithmException(e);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | UnsupportedOperationException e) {
            throw new TokenTool.CipherInitException(e);
        } catch (IllegalStateException | IllegalBlockSizeException | BadPaddingException e) {
            throw new TokenTool.CipherDoFinalException(e);
        }
    }

    /**
     *  This method encrypt the content of a byte array instance.
     * @param content A byte array instance that represents the content to encrypt.
     * @return A String instance that represents the decrypted content.
     * @throws NoSuchPaddingException If transformation is null, empty, in an invalid format, or if no Provider supports a CipherSpi implementation for the specified algorithm.
     * @throws NoSuchAlgorithmException If transformation contains a padding scheme that is not available.
     * @throws InvalidKeyException If the given key is inappropriate for initializing this cipher, or its key size exceeds the maximum allowable key size (as determined from the configured jurisdiction policy files).
     * @throws InvalidAlgorithmParameterException If the given algorithm parameters are inappropriate for this cipher, or this cipher requires algorithm parameters and params is null, or the given algorithm parameters imply a cryptographic strength that would exceed the legal limits (as determined from the configured jurisdiction policy files).
     * @throws UnsupportedOperationException If (@code opmode} is WRAP_MODE or UNWRAP_MODE but the mode is not implemented by the underlying CipherSpi.
     * @throws IllegalStateException If this cipher is in a wrong state (e.g., has not been initialized).
     * @throws IllegalBlockSizeException If this cipher is a block cipher, no padding has been requested (only in encryption mode), and the total input length of the data processed by this cipher is not a multiple of block size; or if this encryption algorithm is unable to process the input data provided.
     * @throws BadPaddingException If this cipher is in decryption mode, and (un)padding has been requested, but the decrypted data is not bounded by the appropriate padding bytes.
     * @throws SecretKeyFactoryException If an error occurs while this tool generates a secret key.
     */
    private String encrypt(final byte[] content) throws SecretKeyFactoryException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException, UnsupportedOperationException, IllegalStateException, IllegalBlockSizeException, BadPaddingException {
        byte[] salt = this.getRandomByteArray(SALT_LENGTH_BYTE);
        byte[] iv = this.getRandomByteArray(IV_LENGTH_BYTE);
        SecretKey aesKey = this.getAESKEYFromTokenKey(salt);
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, new GCMParameterSpec(AUTHENTICATION_TAG_LENGTH, iv));
        byte[] cipherContent = cipher.doFinal(content);
        byte[] cipherContentWithIvAndSalt = ByteBuffer.allocate(iv.length + salt.length + cipherContent.length)
                                                        .put(iv)
                                                        .put(salt)
                                                        .put(cipherContent)
                                                        .array();
        return Base64.getEncoder().encodeToString(cipherContentWithIvAndSalt);
    }

    /**
     * This method is used to decrypt a token encrypted with this tool.
     * @param encryptedToken A String instance that represents the encryptedToken
     * @return A String that represents the decrypted token.
     * @throws NullPointerException If the encryptedToken parameter is null.
     * @throws CipherTransformationAlgorithmException If an error occurs involving the Cipher 'getInstance(...)' method while the token is being decrypted.
     * @throws CipherInitException If an error occurs involving the Cipher 'init(...)' method while the token is being decrypted.
     * @throws CipherDoFinalException If an error occurs involving the Cipher 'doFinal(...)' method while the token is being decrypted.
     * @throws SecretKeyFactoryException If an error occurs while this tool generates a secret key.
     */
    public String decryptToken(final String encryptedToken) throws NullPointerException, CipherTransformationAlgorithmException, CipherInitException, CipherDoFinalException, SecretKeyFactoryException {
        if (encryptedToken == null)
            throw new NullPointerException();
        try {
            return decrypt(encryptedToken);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            throw new TokenTool.CipherTransformationAlgorithmException(e);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | UnsupportedOperationException e) {
            throw new TokenTool.CipherInitException(e);
        } catch (IllegalStateException | IllegalBlockSizeException | BadPaddingException e) {
            throw new TokenTool.CipherDoFinalException(e);
        }
    }

    /**
     *  This method decrypt a String instance encoded with the AES encryption algorithm.
     * @param encryptedData A String instance that represent the encrypted content.
     * @return A String instance that represents the decrypted content.
     * @throws NoSuchPaddingException If transformation is null, empty, in an invalid format, or if no Provider supports a CipherSpi implementation for the specified algorithm.
     * @throws NoSuchAlgorithmException If transformation contains a padding scheme that is not available.
     * @throws InvalidKeyException If the given key is inappropriate for initializing this cipher, or its key size exceeds the maximum allowable key size (as determined from the configured jurisdiction policy files).
     * @throws InvalidAlgorithmParameterException If the given algorithm parameters are inappropriate for this cipher, or this cipher requires algorithm parameters and params is null, or the given algorithm parameters imply a cryptographic strength that would exceed the legal limits (as determined from the configured jurisdiction policy files).
     * @throws UnsupportedOperationException If (@code opmode} is WRAP_MODE or UNWRAP_MODE but the mode is not implemented by the underlying CipherSpi.
     * @throws IllegalStateException If this cipher is in a wrong state (e.g., has not been initialized).
     * @throws IllegalBlockSizeException If this cipher is a block cipher, no padding has been requested (only in encryption mode), and the total input length of the data processed by this cipher is not a multiple of block size; or if this encryption algorithm is unable to process the input data provided.
     * @throws BadPaddingException If this cipher is in decryption mode, and (un)padding has been requested, but the decrypted data is not bounded by the appropriate padding bytes.
     * @throws SecretKeyFactoryException If an error occurs while this tool generates a secret key.
     */
    private String decrypt(String encryptedData) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException, UnsupportedOperationException, IllegalStateException, IllegalBlockSizeException, BadPaddingException, SecretKeyFactoryException {
        byte[] decode = Base64.getDecoder().decode(encryptedData.getBytes(UTF_8));
        ByteBuffer byteBuffer = ByteBuffer.wrap(decode);
        byte[] iv = new byte[IV_LENGTH_BYTE];
        byteBuffer.get(iv);
        byte[] salt = new byte[SALT_LENGTH_BYTE];
        byteBuffer.get(salt);
        byte[] cipherContent = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherContent);
        SecretKey aesKey = this.getAESKEYFromTokenKey(salt);
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(AUTHENTICATION_TAG_LENGTH, iv));
        byte[] plainContent = cipher.doFinal(cipherContent);
        return new String(plainContent, UTF_8);
    }

    /**
     * This method return a byte array instance with custom length and a random content.
     * @param numberOfBytes An int value that represent the length of the returned byte array.
     * @return A byte array with random values.
     */
    private byte[] getRandomByteArray(final int numberOfBytes) {
        byte[] result = new byte[numberOfBytes];
        new SecureRandom().nextBytes(result);
        return result;
    }

    /**
     * This method return a SecretKey created using the Token Key and the provided salt.
     * @param salt A byte array instance that represents the salt.
     * @return A SecretKey instance.
     * @throws SecretKeyFactoryException If the SecretKeyFactory throws an exception related to the algorithm or the key used to generate the SecretKey.
     */
    private SecretKey getAESKEYFromTokenKey(final byte[] salt) throws SecretKeyFactoryException {
        SecretKeyFactory secretKeyFactory;
        try {
            secretKeyFactory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new SecretKeyFactoryException("Invalid algorithm", e);
        }
        KeySpec keySpec = new PBEKeySpec(TOKEN_KEY, salt, PBE_KEY_ITERATION_COUNT, PBE_KEY_LENGTH);
        byte[] encodedSecret;
        try {
            encodedSecret = secretKeyFactory.generateSecret(keySpec).getEncoded();
        } catch (InvalidKeySpecException e) {
            throw new SecretKeyFactoryException("Invalid key", e);
        }
        return new SecretKeySpec(encodedSecret, SECRET_KEY_SPEC_ALGORITHM);
    }

    /**
     * This method is used to search in the cookie list of a HttpServletRequest a token cookie.
     * @param request A HttpServletRequest that represents the request.
     * @return A boolean value, true if the request contains a cookie with the name of the token cookie, otherwise false.
     */
    public static boolean requestContainsTokenCookie(final HttpServletRequest request) {
        boolean result = false;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie != null && cookie.getName() != null && cookie.getName().equals(TOKEN_COOKIE_NAME)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * This method returns the token cookie value from a HttpServletRequest.
     * @param request A HttpServletRequest instance that represents the request.
     * @return A String instance that represents the token cookie value, if the request does not contain the token
     * cookie returns null.
     */
    public static String getTokenCookieFromRequest(final HttpServletRequest request) {
        String result = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(TOKEN_COOKIE_NAME)) {
                result = cookie.getValue();
                break;
            }
        }
        return result;
    }

}
