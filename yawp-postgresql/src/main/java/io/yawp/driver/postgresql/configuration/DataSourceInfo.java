package io.yawp.driver.postgresql.configuration;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;

public class DataSourceInfo {

    private String name;

    private String driverClassName;

    private String url;

    private String env;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public DataSource buildDatasource() {
        BasicDataSource ds = new BasicDataSource();

        ds.setDriverClassName(driverClassName);
        ds.setUrl(url);

        return ds;
    }
}
