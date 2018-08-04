package com.carecru.reservationservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(basePackages = {
        "com.carecru.reservationservice.repositories"
})
@EnableTransactionManagement
@EntityScan(basePackages = {"com.carecru.reservationservice.entities"})
public class DatabaseTestConfig {

    private JpaProperties jpaProperties = new JpaProperties();

    @Autowired(required = false)
    private PersistenceUnitManager persistenceUnitManager;

    @Bean
    public DataSource dataSource(){
        return DataSourceBuilder.create()
                .url("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1")
                .username("sa")
                .password("")
                .build();
    }

    @Bean
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        Map<String, String> jpaProperties = this.jpaProperties.getProperties();
        jpaProperties.put("spring.jpa.generate-ddl", "true");

        return new EntityManagerFactoryBuilder(adapter, jpaProperties, this.persistenceUnitManager);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource,
            EntityManagerFactoryBuilder factoryBuilder) {
        Map<String, Object> vendorProperties = new HashMap<>();
        vendorProperties.put("hibernate.hbm2ddl.auto", "create-drop");

        return factoryBuilder
                .dataSource(dataSource)
                .packages("com.carecru.reservationservice.entities")
                .properties(vendorProperties)
                .build();
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
