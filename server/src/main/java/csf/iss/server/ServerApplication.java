package csf.iss.server;

// import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		// SpringApplication.run(ServerApplication.class, args);

		SpringApplicationBuilder builder = new SpringApplicationBuilder(ServerApplication.class);

		builder.headless(false);

		// ConfigurableApplicationContext context = 
		builder.run(args);
	}

}
