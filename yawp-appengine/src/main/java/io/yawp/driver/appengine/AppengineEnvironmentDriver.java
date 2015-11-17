package io.yawp.driver.appengine;

import io.yawp.driver.api.EnvironmentDriver;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.utils.SystemProperty;

public class AppengineEnvironmentDriver implements EnvironmentDriver {

    @Override
    public boolean isProduction() {
        return SystemProperty.environment.value() == SystemProperty.Environment.Value.Production;
    }

    @Override
    public boolean isDevelopment() {
        return SystemProperty.environment.value() == SystemProperty.Environment.Value.Development;
    }

    @Override
    public boolean isTest() {
        return !isProduction() && !isDevelopment();
    }

    @Override
    public boolean isAdmin() {
        UserService userService = UserServiceFactory.getUserService();
        return userService.isUserLoggedIn() && userService.isUserAdmin();
    }

}
