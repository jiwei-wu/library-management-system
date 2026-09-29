package com.jiwei.library_management_system.repository;

import com.jiwei.library_management_system.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    long countByMemberIdAndReturnedAtIsNull(Long memberId);
    List<Loan> findByMemberIdOrderByBorrowedAtDescIdDesc(Long memberId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from Loan l where l.id = :id")
    Optional<Loan> lockById(Long id);
}
