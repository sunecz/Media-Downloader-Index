package sune.app.mediadown.index;

import java.util.Map;

import org.apache.jena.http.auth.AuthEnv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.github.ledsoft.jopa.spring.transaction.DelegatingEntityManager;
import com.github.ledsoft.jopa.spring.transaction.JopaTransactionManager;

import cz.cvut.kbss.jopa.Persistence;
import cz.cvut.kbss.jopa.model.EntityManagerFactory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Configuration
@EnableTransactionManagement
public class ApplicationConfiguration {
	
	protected EntityManagerFactory emf;
	protected Environment environment;
	
	public ApplicationConfiguration(Environment environment) {
		this.environment = environment;
	}

	@PostConstruct
	protected void init() {
		emf = Persistence.createEntityManagerFactory(
			"mdi-pu",
			Map.of(
				"cz.cvut.jopa.scanPackage", "sune.app.mediadown.index.entity",
				"cz.cvut.kbss.jopa.model.PersistenceProvider", "cz.cvut.kbss.jopa.model.JOPAPersistenceProvider",
				"cz.cvut.jopa.dataSource.class", "cz.cvut.kbss.ontodriver.jena.JenaDataSource",
				"cz.cvut.jopa.ontology.physicalURI", environment.getProperty("APP_DB_URI"),
				"cz.cvut.kbss.ontodriver.jena.storage", "fuseki"
			)
		);
		
		// Configure HTTP Authorization for update operations in Fuseki. This is for Jena directly,
		// and should not affect JOPA itself, even if JOPA uses Jena underneath.
		// This is required for using direct Jena update request due to JOPA not being able to run
		// the update query correctly. It does not even throw any error, it just fails silently.
		// This is probably an Authorization issue, but configuring JOPA dataSource username
		// and password does not work at all.
		AuthEnv.get().registerUsernamePassword(
			environment.getProperty("APP_DB_URI"),
			environment.getProperty("APP_DB_USER"),
			environment.getProperty("APP_DB_PASS")
		);
	}
	
	@PreDestroy
	protected void close() {
		if(emf.isOpen()) {
			emf.close();
		}
	}
	
	@Bean
	public EntityManagerFactory entityManagerFactory() {
		return emf;
	}
	
	@Bean
	public DelegatingEntityManager entityManager() {
		return new DelegatingEntityManager();
	}
	
	@Bean(name = "txManager")
	public PlatformTransactionManager transactionManager(
		EntityManagerFactory emf,
		DelegatingEntityManager emProxy
	) {
		return new JopaTransactionManager(emf, emProxy);
	}
}
