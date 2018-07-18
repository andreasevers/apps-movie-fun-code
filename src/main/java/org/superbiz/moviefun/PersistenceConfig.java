package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import java.sql.SQLException;

import static org.springframework.orm.jpa.vendor.Database.MYSQL;

@Configuration
public class PersistenceConfig {

    @Bean
    public DatabaseServiceCredentials databaseServiceCredentials(@Value("${VCAP_SERVICES}") String VCAP_SERVICES) {
        return new DatabaseServiceCredentials(VCAP_SERVICES);
    }

    @Bean
    public DataSource albumsDataSource(DatabaseServiceCredentials serviceCredentials) throws SQLException {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.isWrapperFor(MysqlDataSource.class);
        dataSource.setJdbcUrl(serviceCredentials.jdbcUrl("albums-mysql", "p-mysql"));
        return dataSource;
    }

    @Bean
    public DataSource moviesDataSource(DatabaseServiceCredentials serviceCredentials) throws SQLException {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.isWrapperFor(MysqlDataSource.class);
        dataSource.setJdbcUrl(serviceCredentials.jdbcUrl("movies-mysql", "p-mysql"));
        return dataSource;
    }

    @Bean
    public HibernateJpaVendorAdapter hibernateJpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setDatabase(MYSQL);
        hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        return hibernateJpaVendorAdapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBeanForMovies(DataSource moviesDataSource, HibernateJpaVendorAdapter hibernateJpaVendorAdapter) {
        return createFactoryBean(moviesDataSource, hibernateJpaVendorAdapter, "org.superbiz.moviefun.movies", "movie-unit");
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBeanForAlbums(DataSource albumsDataSource, HibernateJpaVendorAdapter hibernateJpaVendorAdapter) {
        return createFactoryBean(albumsDataSource, hibernateJpaVendorAdapter, "org.superbiz.moviefun.albums", "album-unit");
    }

    private LocalContainerEntityManagerFactoryBean createFactoryBean(DataSource dataSource, HibernateJpaVendorAdapter hibernateJpaVendorAdapter, String s, String s2) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setPackagesToScan(s);
        localContainerEntityManagerFactoryBean.setPersistenceUnitName(s2);
        return localContainerEntityManagerFactoryBean;
    }

    @Bean
    public PlatformTransactionManager platformTransactionManagerForMovies(EntityManagerFactory localContainerEntityManagerFactoryBeanForMovies) {
        return new JpaTransactionManager(localContainerEntityManagerFactoryBeanForMovies);
    }

    @Bean
    public PlatformTransactionManager platformTransactionManagerForAlbums(EntityManagerFactory localContainerEntityManagerFactoryBeanForAlbums) {
        return new JpaTransactionManager(localContainerEntityManagerFactoryBeanForAlbums);
    }

    @Bean
    public TransactionOperations transactionOperationsForMovies(PlatformTransactionManager platformTransactionManagerForMovies) {
        TransactionTemplate transactionOperations = new TransactionTemplate();
        transactionOperations.setTransactionManager(platformTransactionManagerForMovies);
        return transactionOperations;
    }

    @Bean
    public TransactionOperations transactionOperationsForAlbums(PlatformTransactionManager platformTransactionManagerForAlbums) {
        TransactionTemplate transactionOperations = new TransactionTemplate();
        transactionOperations.setTransactionManager(platformTransactionManagerForAlbums);
        return transactionOperations;
    }

}
