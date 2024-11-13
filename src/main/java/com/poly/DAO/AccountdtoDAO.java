package com.poly.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poly.DTO.AccountDTO;
import com.poly.entity.Account;
import java.util.Optional;

@Repository
public interface AccountdtoDAO extends JpaRepository<AccountDTO, Integer>{
	
}
