package dravin.com.authentication;

import dravin.com.constant.enumConstant.Permissions;
import dravin.com.constant.enumConstant.Roles;
import dravin.com.entity.PermissionEntity;
import dravin.com.entity.RoleEntity;
import dravin.com.repository.PermissionRepository;
import dravin.com.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;
import java.util.Set;

@SpringBootApplication
@EnableJpaRepositories("dravin.com.repository")
@EntityScan("dravin.com.entity")


//@ComponentScan(basePackages = "dravin.com")
public class AuthenticationApplication implements CommandLineRunner {

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    RoleRepository roleRepository;

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationApplication.class, args);
	}

    @Override
    public void run(String... args) {

        Optional<PermissionEntity> permissionCheck = permissionRepository.findByName(Permissions.ALL);

        PermissionEntity permissionEntity;

        permissionEntity = permissionCheck.orElseGet(() -> permissionRepository.save(new PermissionEntity(Permissions.ALL)));

        Optional<RoleEntity> roleCheck = roleRepository.findByName(Roles.ROLE_SUPER_ADMIN);

        if(roleCheck.isEmpty())
        {
            roleRepository.save(new RoleEntity(Roles.ROLE_SUPER_ADMIN, Set.of(permissionEntity)));
        }
    }

}
