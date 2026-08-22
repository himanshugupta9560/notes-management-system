package com.nms.repositories;

import com.nms.entities.AuthProviderType;
import com.nms.entities.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepositories extends MongoRepository<User, ObjectId> {
    Optional<User> findByUsername(String username);

    Optional<User> findByProviderIdAndAuthProviderType(String providerId, AuthProviderType authProviderType);
}
