package com.manager.repository;

import com.manager.model.StudentWeeklyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentWeeklyReportsRepository extends JpaRepository<StudentWeeklyReport, Long> {
}
