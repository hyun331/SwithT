package com.tweety.SwithT.tutor.repository;

import com.tweety.SwithT.tutor.domain.TuTor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TutorRepository extends JpaRepository<TuTor, Long> {



}
