package com.juliandbs.shareyourfiles.persistence.token.service;

import com.juliandbs.shareyourfiles.persistence.token.repository.TokenRepository;
import com.juliandbs.shareyourfiles.persistence.token.repository.exception.TokenAlreadyExistsException;
import com.juliandbs.shareyourfiles.persistence.token.repository.exception.TokenNotFoundException;
import com.juliandbs.shareyourfiles.persistence.token.service.exception.FailedTokenCreationException;
import com.juliandbs.shareyourfiles.persistence.token.service.exception.InvalidTokenException;
import com.juliandbs.shareyourfiles.persistence.token.service.exception.InvalidUserEmailException;
import com.juliandbs.shareyourfiles.persistence.token.tool.TokenTool;
import com.juliandbs.shareyourfiles.persistence.user.repository.UserRepository;
import com.juliandbs.shareyourfiles.persistence.user.repository.exception.UserNotFoundException;

import jakarta.servlet.ServletException;

import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
 * This class implements the TokenServiceI interface and is used as a service to manage the token operations.
 * @author JulianDbs
 */
@Service("tokenService")
public final class TokenService implements TokenServiceI {

    private final TokenTool tokenTool;

    private final TokenRepository tokenRepository;

    private final UserRepository userRepository;

    private final Integer TOKEN_BASE_MINUTES;

    private final Integer TOKEN_EXTRA_MINUTES;

    /**
     * Class constructor.
     * @param environment An Environment instance that represents the environment in witch the current application is running.
     * @param tokenRepository A TokenRepository instance that represents the Token Repository.
     * @param userRepository A UserRepository instance that represents the User Repository.
     */
    private TokenService(final Environment environment, final TokenRepository tokenRepository, final UserRepository userRepository) {
        String tokenKey = environment.getProperty("app.token.encryption-key");
        String baseMinutes = environment.getProperty("app.token.base-minutes", "20");
        String extraMinutes = environment.getProperty("app.token.extra-minutes", "20");
        this.TOKEN_EXTRA_MINUTES = Integer.valueOf(extraMinutes);
        this.TOKEN_BASE_MINUTES = Integer.valueOf(baseMinutes);
        this.tokenTool = new TokenTool(tokenKey);
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    /**
     * This method is used to decrypt a token encrypted using the TokenTool.
     * @param encryptedToken A String that represents the encrypted token.
     * @return A String that represents the decrypted token.
     * @throws NullPointerException If the 'encryptedToken' method argument is null.
     * @throws InvalidTokenException If the provided encrypted token is not valid.
     * @throws ServletException If an error occurs involving the TokenTool internal configuration.
     */
    @Override
    public String decryptToken(final String encryptedToken) throws NullPointerException, InvalidTokenException, ServletException {
        if (encryptedToken == null)
            throw new NullPointerException();
        if (encryptedToken.length() == 0)
            throw new InvalidTokenException();
        String decryptedToken;
        try {
            decryptedToken = tokenTool.decryptToken(encryptedToken);
        } catch (TokenTool.CipherTransformationAlgorithmException | TokenTool.CipherInitException | TokenTool.CipherDoFinalException | TokenTool.SecretKeyFactoryException e) {
            throw new ServletException("Internal Error");
        }
        return decryptedToken;
    }

    /**
     *  This method is used to encrypt a token using the TokenTool.
     * @param token A String that represents the token.
     * @return A String instance that represents the encrypted token.
     * @throws NullPointerException If the 'token' method argument is null.
     * @throws InvalidTokenException If the token provided contains a length of 0.
     * @throws ServletException If an error related to the internal TokenTool configuration.
     */
    @Override
    public String encryptToken(final String token) throws NullPointerException, InvalidTokenException, ServletException {
        if (token == null)
            throw new NullPointerException();
        if (token.length() == 0)
            throw new InvalidTokenException();
        String encryptedToken;
        try {
            UUID tokenId = UUID.fromString(token);
            encryptedToken = tokenTool.encryptTokenId(tokenId);
        } catch (TokenTool.SecretKeyFactoryException | TokenTool.CipherInitException | TokenTool.CipherDoFinalException | TokenTool.CipherTransformationAlgorithmException e) {
            throw new ServletException("Internal error"); //check this
        }
        return encryptedToken;
    }

    /**
     * This method is used to create a new token using the user email and the ip address.
     * @param userEmail A String instance that represents the user email.
     * @param ipAddress A String instance that represents the ip address.
     * @return A String instance that represents the new token.
     * @throws NullPointerException If any of the method arguments are null.
     * @throws InvalidUserEmailException If the user email does not correspond to a registered user.
     * @throws FailedTokenCreationException If an error occurs while the token was created.
     */
    @Override
    public String createEncryptedToken(final String userEmail, final String ipAddress) throws NullPointerException, InvalidUserEmailException, FailedTokenCreationException {
        if (userEmail == null || ipAddress == null)
            throw new NullPointerException();
        UUID userId;
        try {
            userId = this.userRepository.getUserIdByEmail(userEmail);
        } catch (UserNotFoundException e) {
            throw new InvalidUserEmailException(e);
        }
        UUID tokenId;
        try {
            LocalDateTime expirationDate = LocalDateTime.now().plusMinutes(TOKEN_BASE_MINUTES);
            tokenId = this.tokenRepository.addNewToken(userId, ipAddress, expirationDate);
        } catch (TokenAlreadyExistsException e) {
            throw new RuntimeException(e);
        }
        String encryptedToken;
        try {
            encryptedToken = tokenTool.encryptTokenId(tokenId);
        } catch (TokenTool.SecretKeyFactoryException | TokenTool.CipherTransformationAlgorithmException | TokenTool.CipherInitException | TokenTool.CipherDoFinalException e) {
            throw new FailedTokenCreationException(e);
        }
        return encryptedToken;
    }

    /**
     * This method is uses to return an encrypted token that contains the user id and ip address provided from the 'tokens' tabla.
     * @param userId A UUID instance that represents the user id.
     * @param ipAddress A String instance that represents the ip address.
     * @return A String that represents the encrypted token.
     * @throws NullPointerException If any of the method arguments are null.
     * @throws TokenNotFoundException If the 'tokens' table does not contain a token that uses the user id and ip address provided.
     * @throws ServletException If the TokenTool throw an error related to the encryption process.
     * @throws InvalidTokenException If the TokenTool does not accept the token provided.
     */
    @Override
    public String getEncryptedTokenByUserIdAndIpAddress(final UUID userId, final String ipAddress) throws NullPointerException, TokenNotFoundException, ServletException, InvalidTokenException {
        if (userId == null || ipAddress == null)
            throw new NullPointerException();
        String token = this.tokenRepository.getTokenIdByUserIdAndIpAddress(userId, ipAddress);
        return this.encryptToken(token);
    }

    /**
     * This method returns the email from the user who owns the token.
     * @param token A String instance that represents the token.
     * @param ipAddress A String instance that represents the ip address.
     * @return A String instance that represents the user email.
     * @throws NullPointerException If any of the method arguments are null.
     * @throws InvalidTokenException If the token provided is not valid.
     */
    @Override
    public String getTokenOwner(String token, String ipAddress) throws NullPointerException, InvalidTokenException {
        if (token == null || ipAddress == null)
            throw new NullPointerException();
        boolean isValid = this.tokenIsValid(token, ipAddress);
        if (!isValid)
            throw new InvalidTokenException();
        UUID tokenId = UUID.fromString(token);
        UUID userId;
        try {
            userId = this.tokenRepository.getTokenUserIdByIdAndIpAddress(tokenId, ipAddress);
        } catch (TokenNotFoundException e) {
            throw new InvalidTokenException();
        }
        String userEmail;
        try {
            userEmail = this.userRepository.getUserEmailById(userId);
        } catch (UserNotFoundException e) {
            throw new InvalidTokenException();
        }
        return userEmail;
    }

    /**
     * This method add extends the token expiration date using the TOKEN_EXTRA_MINUTES property.
     * @param token A String instance that represents the token.
     * @param ipAddress A String instance that represents the ip address.
     * @throws NullPointerException If any of the method arguments are null.
     * @throws InvalidTokenException If the token provided is not valid.
     */
    @Override
    public void refreshToken(String token, String ipAddress) throws NullPointerException, InvalidTokenException {
        if (token == null || ipAddress == null)
            throw new NullPointerException();
        boolean isValid = this.tokenIsValid(token, ipAddress);
        if (!isValid)
            throw new InvalidTokenException();
        UUID tokenId = UUID.fromString(token);
        try {
            LocalDateTime newExpirationDate = LocalDateTime.now().plusMinutes(TOKEN_EXTRA_MINUTES);
            this.tokenRepository.updateExpirationDateByIdAndIpAddress(tokenId, ipAddress, newExpirationDate);
        } catch (TokenNotFoundException e) {
            throw new InvalidTokenException();
        }
    }

    /**
     * This method returns true if the 'tokens' table contains a token that contains the token and ip address provided.
     * @param token A String instance that represents the token.
     * @param ipAddress A String instance that represents the ip address.
     * @return A boolean value, true if the table contains the token, false if not.
     * @throws NullPointerException If any of the method arguments are null.
     */
    @Override
    public boolean tokenExists(String token, String ipAddress) throws NullPointerException {
        if (token == null || ipAddress == null)
            throw new NullPointerException();
        UUID tokenId = UUID.fromString(token);
        return this.tokenRepository.tokenExistsByTokenId(tokenId, ipAddress);
    }

    /**
     * This method returns true if the 'tokens' table contains a token that contains the user id and ip address provided.
     * @param userId A UUID instance that represents the user id.
     * @param ipAddress A String instance that represents the ip address.
     * @return A boolean value, true if the table contains the token, false if not.
     * @throws NullPointerException If any of the method arguments are null.
     */
    @Override
    public boolean tokenExists(final UUID userId, String ipAddress) throws NullPointerException {
        if (userId == null || ipAddress == null)
            throw new NullPointerException();
        return this.tokenRepository.tokenExistsByUserId(userId, ipAddress);
    }

    /**
     * This method is used to check if the token provided has expired (requires the ip address of the current request to check it).
     * If the token is expired, this method delete the token.
     * @param token A String instance that represents the token.
     * @param ipAddress A String instance that represents the ip address.
     * @return A boolean value, true if the token has not yet expired, otherwise false.
     * @throws NullPointerException If any of the method arguments are null.
     */
    @Override
    public boolean tokenIsValid(String token, String ipAddress) throws NullPointerException {
        if (token == null || ipAddress == null)
            throw new NullPointerException();
        UUID tokenId = UUID.fromString(token);
        try {
            LocalDateTime expirationDate = this.tokenRepository.getTokenExpirationDateByIdAndIpAddress(tokenId, ipAddress);
            if (expirationDate.isAfter(LocalDateTime.now())) {
                return true;
            } else {
                this.tokenRepository.deleteTokenByIdAndIpAddress(tokenId, ipAddress);
            }
        } catch (TokenNotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * This method id used to check if the token that uses the user id and ip address has expired.
     * If the token is expired, this method delete the token.
     * @param userId A UUID instance that represents the user id.
     * @param ipAddress a String instance that represents the ip address.
     * @return  A boolean value, true if the token has not yet expired, otherwise false.
     * @throws NullPointerException If any of the method arguments are null.
     */
    @Override
    public boolean tokenIsValid(final UUID userId, final String ipAddress) throws NullPointerException {
        if (userId == null || ipAddress == null)
            throw new NullPointerException();
        try {
            LocalDateTime expirationDate = this.tokenRepository.getTokenExpirationDateByUserIdAndIpAddress(userId, ipAddress);
            if (expirationDate.isAfter(LocalDateTime.now())) {
                return true;
            } else {
                this.tokenRepository.deleteTokenByUserIdAndIpAddress(userId, ipAddress);
            }
        } catch (TokenNotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * This method is used to remove a record from the 'tokens' table using the used id and ip address provided.
     * @param userId A UUID instance that represents the user id.
     * @param ipAddress A String instance that represents the ip address.
     * @throws NullPointerException If any of the method arguments are null.
     * @throws TokenNotFoundException If the 'tokens' table does not contain any token that use the user id and ip address provided.
     */
    @Override
    public void removeTokenByUserIdAndIpAddress(UUID userId, String ipAddress) throws NullPointerException, TokenNotFoundException {
        if (userId == null || ipAddress == null)
            throw new NullPointerException();
        this.tokenRepository.deleteTokenByUserIdAndIpAddress(userId, ipAddress);
    }

    /**
     * This method is used to remove a record from the 'tokens' table that contains the token id provided.
     * @param tokenId A UUID instance that represents the token id.
     * @param ipAddress A String instance that represents the ip address.
     * @throws NullPointerException If any of the method arguments are null.
     * @throws TokenNotFoundException If the 'tokens' table does not contain any token that use the user id and ip address provided.
     */
    @Override
    public void removeTokenByTokenAndIpAddress(UUID tokenId, String ipAddress) throws NullPointerException, TokenNotFoundException {
        if (tokenId == null || ipAddress == null)
            throw new NullPointerException();
        this.tokenRepository.deleteTokenByIdAndIpAddress(tokenId, ipAddress);
    }

    @Scheduled(timeUnit = TimeUnit.MINUTES, initialDelay = 1, fixedDelay = 1)
    private void findAndDeleteExpiredTokens() {
        this.tokenRepository.findAndRemoveExpiredTokens();
    }
}
