package xyz.erotskoob.expensetracker.service;

import xyz.erotskoob.expensetracker.constant.TokenType;
import xyz.erotskoob.expensetracker.entity.User;
import xyz.erotskoob.expensetracker.entity.VerificationToken;

public interface TokenService {
    VerificationToken createToken(User user, TokenType tokenType);
    VerificationToken validateToken(String tokenString, TokenType expectedType);
    void markTokenAsUsed(VerificationToken token);
}
