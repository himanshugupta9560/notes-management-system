package com.nms.repositories;

import com.nms.entities.Note;
import com.nms.entities.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotesRepositories extends MongoRepository<Note, ObjectId> {
    Optional<Note> findByTitle(String title);
    List<Note> findByIdIn(List<ObjectId> ids);
}
