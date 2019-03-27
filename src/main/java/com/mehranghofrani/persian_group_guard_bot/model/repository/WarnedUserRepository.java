package com.mehranghofrani.persian_group_guard_bot.model.repository;

import com.mehranghofrani.persian_group_guard_bot.model.entity.WarnedUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface WarnedUserRepository extends CrudRepository<WarnedUser, Long> {

    WarnedUser findByUserId(Integer userId);

    @Query("SELECT warnedUser.userId FROM WarnedUser warnedUser WHERE warnedUser.warnsCount=:warnsCount")
    Set<Integer> getUserIdsByWarnCount(@Param("warnsCount") int warnsCount);

}
