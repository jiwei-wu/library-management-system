package com.jiwei.library_management_system;

import com.jiwei.library_management_system.api.ApiDtos.*;
import com.jiwei.library_management_system.api.BusinessRuleException;
import com.jiwei.library_management_system.service.LibraryService;
import com.jiwei.library_management_system.repository.BookRepository;
import com.jiwei.library_management_system.repository.BookCopyRepository;
import com.jiwei.library_management_system.repository.MemberRepository;
import com.jiwei.library_management_system.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LibraryServiceIntegrationTests {
    @Autowired LibraryService library;
    @Autowired BookRepository books;
    @Autowired BookCopyRepository copies;
    @Autowired MemberRepository members;
    @Autowired LoanRepository loans;

    @BeforeEach
    void resetDatabase() {
        loans.deleteAll();
        copies.deleteAll();
        books.deleteAll();
        members.deleteAll();
    }

    @Test
    void borrowAndReturnUpdatesAvailabilityAndPreservesHistory() {
        BookView book = library.createBook(new CreateBook("The Left Hand of Darkness", "Ursula Le Guin", "ISBN-A", 1));
        MemberView member = library.createMember(new CreateMember("Eileen", "eileen@example.com"));

        LoanView loan = library.borrow(new CreateLoan(member.id(), book.id()));
        assertEquals(0, library.getBook(book.id()).availableCopies());
        assertEquals(loan.borrowedAt().plusDays(14), loan.dueAt());
        assertThrows(BusinessRuleException.class, () -> library.borrow(new CreateLoan(member.id(), book.id())));

        LoanView returned = library.returnLoan(loan.id());
        assertNotNull(returned.returnedAt());
        assertEquals(1, library.getBook(book.id()).availableCopies());
        assertEquals(1, library.memberLoans(member.id()).size());
        assertThrows(BusinessRuleException.class, () -> library.returnLoan(loan.id()));

        LoanView secondLoan = library.borrow(new CreateLoan(member.id(), book.id()));
        assertEquals(loan.barcode(), secondLoan.barcode());
    }

    @Test
    void limitsReadersToThreeActiveLoansAndRejectsDuplicateIsbn() {
        MemberView member = library.createMember(new CreateMember("A Reader", "reader@example.com"));
        for (int i = 0; i < 3; i++) {
            BookView book = library.createBook(new CreateBook("Title " + i, "Author", "ISBN-" + i, 1));
            library.borrow(new CreateLoan(member.id(), book.id()));
        }
        BookView fourth = library.createBook(new CreateBook("Fourth", "Author", "ISBN-FOURTH", 1));
        assertThrows(BusinessRuleException.class, () -> library.borrow(new CreateLoan(member.id(), fourth.id())));
        assertEquals(1, library.getBook(fourth.id()).availableCopies());
        assertThrows(BusinessRuleException.class,
                () -> library.createBook(new CreateBook("Duplicate", "Author", "ISBN-FOURTH", 1)));
    }

    @Test
    void searchesTitlesAndAddsCopies() {
        BookView book = library.createBook(new CreateBook("Clean Code", "Robert Martin", "ISBN-CLEAN", 1));
        library.createBook(new CreateBook("Another Book", "Writer", "ISBN-OTHER", 1));
        assertEquals(1, library.listBooks("clean", 0, 10).getTotalElements());
        BookView updated = library.addCopies(book.id(), new AddCopies(2));
        assertEquals(3, updated.totalCopies());
        assertEquals(3, updated.availableCopies());
    }
}
