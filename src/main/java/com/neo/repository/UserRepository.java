package com.neo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.neo.entity.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long>  {

    User findById(long id);
    
    User findByToken(String token);
    
    User findByUserNameAndPassword(String userName, String password);
    
    User findByUserName(String userName);
   
    @RestResource(path="findUserByUser",rel="findUserByUser")
    Page<User> findByUserName(@Param("userName") String userName, Pageable p);

    Long deleteById(Long id);
}
