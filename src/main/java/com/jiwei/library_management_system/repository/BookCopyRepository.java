package com.jiwei.library_management_system.repository;

import com.jiwei.library_management_system.model.BookCopy;
import com.jiwei.library_management_system.model.CopyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    long countByBookId(Long bookId);
    long countByBookIdAndStatus(Long bookId, CopyStatus status);
    Optional<BookCopy> findFirstByBookIdAndStatusOrderByIdAsc(Long bookId, CopyStatus status);
}
