package xyz.erotskoob.expensetracker.service;

import xyz.erotskoob.expensetracker.constant.TokenType;
import xyz.erotskoob.expensetracker.entity.auth.User;
import xyz.erotskoob.expensetracker.entity.auth.VerificationToken;

public interface ITokenService {
    VerificationToken createToken(User user, TokenType tokenType);
    VerificationToken validateToken(String tokenString, TokenType expectedType);
    void markTokenAsUsed(VerificationToken token);
}
