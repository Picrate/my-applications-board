package info.patriceallary.myapplicationsboard;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "info.patriceallary.myapplicationsboard.repositories")
public class PersistenceConfig {}
