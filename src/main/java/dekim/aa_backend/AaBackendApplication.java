package dekim.aa_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AaBackendApplication.class, args);
	}

}
