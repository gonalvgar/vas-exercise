package com.vasapi.vasapiexercise.repository;

import com.vasapi.vasapiexercise.domain.Communication;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunicationRepository extends CrudRepository<Communication, Long> {
    @Query(value = "SELECT MESSAGE_CONTENT FROM Communication", nativeQuery = true)
    List<String> getOrderedWordsByOccurrence();

    @Query(value = "SELECT ORIGIN FROM Communication ", nativeQuery = true)
    List<Long> getAllOrigins();

    @Query(value = "SELECT * FROM Communication a WHERE a.message_type = 'CALL'", nativeQuery = true)
    List<Communication> getCalls();

    @Query(value = "SELECT * FROM Communication a WHERE a.message_type = 'MSG'", nativeQuery = true)
    List<Communication> getMessages();

    @Query(value = "SELECT * FROM Communication ", nativeQuery = true)
    List<Communication> getAll();

    @Query(value = "SELECT MAX(id) as max_id from Communication", nativeQuery = true)
    Integer maxId();
}
