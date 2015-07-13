package com.captechventures.strategy.sample.model;

public class User {

    private String id;

    private String login;
    private String password;

    private Profile profile;

    public User(String id, String login, String password, Profile profile) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.profile = profile;
    }

    public String getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
