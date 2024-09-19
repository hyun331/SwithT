package com.tweety.SwithT.tutee.service;


import com.tweety.SwithT.tutee.repository.TuteeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TuteeService {

    private final TuteeRepository tuteeRepository;

    @Autowired
    public TuteeService(TuteeRepository tuteeRepository) {
        this.tuteeRepository = tuteeRepository;
    }



}
