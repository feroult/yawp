package io.yawp.driver.postgresql.configuration;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;

public class DataSourceInfo {

    public static final String INIT_DATASOURCE = "jdbc/_yawp_init";

    private String name;

    private String driverClassName;

    private String url;

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

    public DataSource buildDatasource() {
        BasicDataSource ds = new BasicDataSource();

        ds.setDriverClassName(driverClassName);
        ds.setUrl(url);
        ds.setMaxTotal(50);

        return ds;
    }

    public DataSourceInfo getInitDatasource() {
        DataSourceInfo dsInit = new DataSourceInfo();

        dsInit.setName(INIT_DATASOURCE);
        dsInit.setDriverClassName(getDriverClassName());
        dsInit.setUrl(getInitDatabaseUrl());

        return dsInit;
    }

    public String getInitDatabaseUrl() {
        int endIndex = StringUtils.ordinalIndexOf(url, "/", 3) + 1;
        return url.substring(0, endIndex) + "template1";
    }

    public String getDatabaseName() {
        int startIndex = StringUtils.ordinalIndexOf(url, "/", 3) + 1;
        String databaseName = url.substring(startIndex);

        int endIndex = databaseName.indexOf("?");
        if(endIndex == -1) {
            return databaseName;
        }
        return databaseName.substring(0, endIndex);
    }
}
