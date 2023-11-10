package com.yasu.ftpLogic.repository;

import com.yasu.ftpLogic.entity.ERole;
import com.yasu.ftpLogic.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
