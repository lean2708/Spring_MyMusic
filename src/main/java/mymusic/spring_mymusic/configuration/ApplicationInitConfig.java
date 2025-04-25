package mymusic.spring_mymusic.configuration;

import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import mymusic.spring_mymusic.entity.Role;
import mymusic.spring_mymusic.repository.RoleRepository;
import mymusic.spring_mymusic.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import mymusic.spring_mymusic.constant.RoleEnum;
import mymusic.spring_mymusic.entity.User;
import mymusic.spring_mymusic.repository.RoleRepository;
import mymusic.spring_mymusic.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ApplicationInitConfig {
    private final PasswordEncoder passwordEncoder;

    @NonFinal
    static final String ADMIN_EMAIL = "admin@gmail.com";

    @NonFinal
    static final String ADMIN_PASSWORD = "123456";

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository){
        log.info("Init application....");
        return args ->{
            if(!userRepository.existsByEmail("admin@gmail.com")){
                Role userRole = roleRepository.save(Role.builder()
                                .name(RoleEnum.USER.name())
                                .description("User Role")
                        .build());

                Role premiumRole = roleRepository.save(Role.builder()
                        .name(RoleEnum.PREMIUM.name())
                        .description("Premium Role")
                        .build());

                Role adminRole = roleRepository.save(Role.builder()
                        .name(RoleEnum.ADMIN.name())
                        .description("Admin Role")
                        .build());

                Set<Role> roles = new HashSet<>();
                roles.add(adminRole);

                User admin = User.builder()
                        .name("ADMIN")
                        .email(ADMIN_EMAIL)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .roles(roles)
                        .build();

                userRepository.save(admin);
            }
        };
    }
}
