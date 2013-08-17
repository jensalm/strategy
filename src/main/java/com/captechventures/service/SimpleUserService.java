package com.captechventures.service;

import com.captechventures.model.Profile;
import com.captechventures.model.User;
import org.springframework.stereotype.Service;

/**
 * Static service for simple user switching.
 */
@Service
public class SimpleUserService implements UserService {

    @Override
    public User getUser(String id) {
        switch (id) {
            case "1":
                return new User("1", "limited", "password", Profile.LIMITED);
            case "2":
                return new User("2", "premium", "password", Profile.PREMIUM);
            default:
                return new User("0", "free", "password", Profile.FREE);
        }
    }

}
