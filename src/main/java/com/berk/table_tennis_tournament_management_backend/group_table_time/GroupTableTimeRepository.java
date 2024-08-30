package com.berk.table_tennis_tournament_management_backend.group_table_time;

import com.berk.table_tennis_tournament_management_backend.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupTableTimeRepository extends JpaRepository<GroupTableTime, Long> {
    GroupTableTime findByGroup(Group group);
}
