package com.koi_express.repository;

import com.koi_express.entity.staff.StaffAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffAssignmentRepository extends JpaRepository<StaffAssignment, Long> {}
