package com.neo.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.neo.entity.Periodical;

public interface PeriodicalRepository extends PagingAndSortingRepository<Periodical, Long>  {
	 
	 List<Periodical> findByExpertUserName(String expertUserName);
	 List<Periodical> findByPublishUserName(String publishUserName);
}