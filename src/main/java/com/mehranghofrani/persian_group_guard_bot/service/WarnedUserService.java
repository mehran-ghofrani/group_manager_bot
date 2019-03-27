package com.mehranghofrani.persian_group_guard_bot.service;

import com.mehranghofrani.persian_group_guard_bot.model.entity.WarnedUser;
import com.mehranghofrani.persian_group_guard_bot.model.repository.WarnedUserRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Service
public class WarnedUserService {
    @Resource
    WarnedUserRepository warnedUserRepository;

    public WarnedUser findByUserId(int userId) {
        return warnedUserRepository.findByUserId(userId);
    }

    public WarnedUser save(WarnedUser warnedUser) {
        return warnedUserRepository.save(warnedUser);
    }

    public Set<Integer> getBadUsersIds() {
        return warnedUserRepository.getUserIdsByWarnCount(1);
    }
}
