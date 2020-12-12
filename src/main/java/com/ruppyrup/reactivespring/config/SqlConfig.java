package com.ruppyrup.reactivespring.config;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
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

@Configuration
@EnableR2dbcRepositories
public class SqlConfig extends AbstractR2dbcConfiguration {

    @Override
    @Qualifier("MysqlConnectionFactory")
    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactory connectionFactory = ConnectionFactories.get(options);
        connectionFactory.create();
        return connectionFactory;
    }

    ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
            .option(DRIVER, "mysql")
            .option(HOST, "127.0.0.1")
            .option(USER, "devuser")
            .option(PORT, 3306)  // optional, default 3306
            .option(PASSWORD, "devuser") // optional, default null, null means has no password
            .option(DATABASE, "testdb") // optional, default null, null means not specifying the database
            .option(CONNECT_TIMEOUT, Duration.ofSeconds(3)) // optional, default null, null means no timeout
            .option(SSL, false) // optional, default sslMode is "preferred", it will be ignore if sslMode is set
//            .option(Option.valueOf("sslMode"), "verify_identity") // optional, default "preferred"
//            .option(Option.valueOf("sslCa"), "/path/to/mysql/ca.pem") // required when sslMode is verify_ca or verify_identity, default null, null means has no server CA cert
//            .option(Option.valueOf("sslCert"), "/path/to/mysql/client-cert.pem") // optional, default null, null means has no client cert
//            .option(Option.valueOf("sslKey"), "/path/to/mysql/client-key.pem") // optional, default null, null means has no client key
//            .option(Option.valueOf("sslKeyPassword"), "key-pem-password-in-here") // optional, default null, null means has no password for client key (i.e. "sslKey")
//            .option(Option.valueOf("tlsVersion"), "TLSv1.3,TLSv1.2,TLSv1.1") // optional, default is auto-selected by the server
//            .option(Option.valueOf("sslHostnameVerifier"), "com.example.demo.MyVerifier") // optional, default is null, null means use standard verifier
//            .option(Option.valueOf("sslContextBuilderCustomizer"), "com.example.demo.MyCustomizer") // optional, default is no-op customizer
//            .option(Option.valueOf("zeroDate"), "use_null") // optional, default "use_null"
//            .option(Option.valueOf("useServerPrepareStatement"), true) // optional, default false
//            .option(Option.valueOf("tcpKeepAlive"), true) // optional, default false
//            .option(Option.valueOf("tcpNoDelay"), true) // optional, default false
//            .option(Option.valueOf("autodetectExtensions"), false) // optional, default false
            .build();


    @Bean
    R2dbcTransactionManager transactionManager(@Qualifier("MysqlConnectionFactory") ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }

    @Bean
    public ConnectionFactoryInitializer initializer(@Qualifier("MysqlConnectionFactory") ConnectionFactory connectionFactory) {

        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);

        CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
        populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
        populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("data.sql")));
        initializer.setDatabasePopulator(populator);

        return initializer;
    }

}
