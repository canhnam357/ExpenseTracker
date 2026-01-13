package xyz.erotskoob.expensetracker.security;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import xyz.erotskoob.expensetracker.entity.auth.User;
import xyz.erotskoob.expensetracker.exception.AuthenticationException;
import xyz.erotskoob.expensetracker.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @NonNull
    public UserDetail loadUserByUsername(@NonNull String username) {
        User user = userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new AuthenticationException("Not found User with username: " + username));
        return new UserDetail(user);
    }
}
