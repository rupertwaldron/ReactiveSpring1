package com.ruppyrup.reactivespring.config;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

import java.time.Duration;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@ComponentScan
@Configuration
@EnableR2dbcRepositories
public class SqlConfig extends AbstractR2dbcConfiguration {

    @Value("${mydb.name}")
    private String database;

    @Override
    @Qualifier("MysqlConnectionFactory")
    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                .option(DRIVER, "mysql")
                .option(HOST, "127.0.0.1")
                .option(USER, "devuser")
                .option(PORT, 3306)  // optional, default 3306
                .option(PASSWORD, "devuser") // optional, default null, null means has no password
                .option(DATABASE, database) // optional, default null, null means not specifying the database
                .option(CONNECT_TIMEOUT, Duration.ofSeconds(3)) // optional, default null, null means no timeout
                .option(SSL, false) // optional, default sslMode is "preferred", it will be ignore if sslMode is set
                .build();
        ConnectionFactory connectionFactory = ConnectionFactories.get(options);
        connectionFactory.create();
        return connectionFactory;
    }


    @Bean
    R2dbcTransactionManager transactionManager(@Qualifier("MysqlConnectionFactory") ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }

    @Bean
    public ConnectionFactoryInitializer initializer(@Qualifier("MysqlConnectionFactory") ConnectionFactory connectionFactory,
                                                    @Value("${spring.datasource.schema}") String schema,
                                                    @Value("${spring.datasource.data}") String data) {

        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);

        CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
        populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource(schema)));
        populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource(data)));
        initializer.setDatabasePopulator(populator);

        return initializer;
    }

}
