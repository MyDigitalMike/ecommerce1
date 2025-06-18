package core.ecommerce.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import core.ecommerce.entity.Role;
import core.ecommerce.repository.RoleRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role("ADMIN"));
            roleRepository.save(new Role("CLIENT"));
            System.out.println("Roles iniciales creados: ADMIN y CLIENT");
        }
    }
}
