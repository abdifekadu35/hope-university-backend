package com.hopeuniversity.hope_university_management.security;

import com.hopeuniversity.hope_university_management.domain.entities.User;
import com.hopeuniversity.hope_university_management.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        if (user.getDeletedAt() != null) {
            throw new UsernameNotFoundException("User has been deleted");
        }
        return UserPrincipal.create(user);
    }
}