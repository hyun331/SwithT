package com.tweety.SwithT.tutee.repository;

import com.tweety.SwithT.tutee.domain.Tutee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TuteeRepository extends JpaRepository<Tutee,Long> {

}
