package demo.api.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EntityScan (basePackages = {"demo.api.rest.model"})
@ComponentScan (basePackages = {"demo.api.rest.**"})
@EnableJpaRepositories(basePackages = {"demo.api.rest.repository"})
@EnableTransactionManagement
@EnableWebMvc
@RestController
@EnableAutoConfiguration
@EnableCaching
public class DemospringrestapiApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(DemospringrestapiApplication.class, args);
		/*Gera a senha cryptografada, faz uma vez só
		System.out.print(new BCryptPasswordEncoder().encode("123"));
		 * */
	}
	
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		
		
		

		/*libera acesso a todos controllers e endpoints*/
		//registry.addMapping("/**"); 
		
		/*libera endpoinst dentro de /usuario */
		//registry.addMapping("/usuario/**");
		
		/*libera alguns endpoitns de /usuario 
		registry.addMapping("/usuario/**").allowedMethods("POST","PUT", "DELETE"); 
		 * */
		
		/*libera alguns endpoitns de /usuario para um ou mais dominio(s) especifico(s) - não precisa de { }
		registry.addMapping("/usuario/**")
		.allowedMethods("POST","PUT")
		.allowedOrigins("www.sistemaX.com.br", "localhost:8080");
		 * */
		
		/*libera o mapeamento de usuario para todas as origens */
		registry.addMapping("/usuario/**")
		.allowedMethods("*")
		.allowedOrigins("*");
		
		registry.addMapping("/profissao/**")
		.allowedMethods("*")
		.allowedOrigins("*");
		
		registry.addMapping("/recuperar/**")
		.allowedMethods("*")
		.allowedOrigins("*");
		

		
	}

}
