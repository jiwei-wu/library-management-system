package com.jiwei.library_management_system.service;

import com.jiwei.library_management_system.api.ApiDtos.*;
import com.jiwei.library_management_system.api.BusinessRuleException;
import com.jiwei.library_management_system.api.ResourceNotFoundException;
import com.jiwei.library_management_system.model.*;
import com.jiwei.library_management_system.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LibraryService {
    private static final int MAX_ACTIVE_LOANS = 3;
    private final BookRepository books;
    private final BookCopyRepository copies;
    private final MemberRepository members;
    private final LoanRepository loans;
    private final Clock clock;

    public LibraryService(BookRepository books, BookCopyRepository copies,
                          MemberRepository members, LoanRepository loans, Clock clock) {
        this.books = books;
        this.copies = copies;
        this.members = members;
        this.loans = loans;
        this.clock = clock;
    }

    @Transactional
    public BookView createBook(CreateBook request) {
        String isbn = request.isbn().trim();
        if (books.existsByIsbn(isbn)) throw new BusinessRuleException("ISBN already exists");
        Book book = books.saveAndFlush(new Book(request.title().trim(), request.author().trim(), isbn));
        saveCopies(book, request.copies());
        return view(book);
    }

    @Transactional
    public BookView addCopies(Long bookId, AddCopies request) {
        Book book = books.lockById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        saveCopies(book, request.count());
        return view(book);
    }

    private void saveCopies(Book book, int count) {
        List<BookCopy> newCopies = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            newCopies.add(new BookCopy(book, "COPY-" + UUID.randomUUID()));
        }
        copies.saveAllAndFlush(newCopies);
    }

    @Transactional(readOnly = true)
    public Page<BookView> listBooks(String title, int page, int size) {
        PageRequest paging = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Book> result = title == null || title.isBlank()
                ? books.findAll(paging)
                : books.findByTitleContainingIgnoreCase(title.trim(), paging);
        return result.map(this::view);
    }

    @Transactional(readOnly = true)
    public BookView getBook(Long id) {
        return view(books.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found")));
    }

    @Transactional
    public MemberView createMember(CreateMember request) {
        String email = request.email().trim().toLowerCase(java.util.Locale.ROOT);
        if (members.existsByEmail(email)) throw new BusinessRuleException("Email already registered");
        Member saved = members.saveAndFlush(new Member(request.name().trim(), email));
        return new MemberView(saved.getId(), saved.getName(), saved.getEmail());
    }

    @Transactional
    public LoanView borrow(CreateLoan request) {
        // The member lock serializes checks of the three-loan limit for this reader.
        Member member = members.lockById(request.memberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        Book book = books.lockById(request.bookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        if (loans.countByMemberIdAndReturnedAtIsNull(member.getId()) >= MAX_ACTIVE_LOANS) {
            throw new BusinessRuleException("Member already has three active loans");
        }
        // Locking the book serializes checkout/return operations on its copies.
        BookCopy copy = copies.findFirstByBookIdAndStatusOrderByIdAsc(book.getId(), CopyStatus.AVAILABLE)
                .orElseThrow(() -> new BusinessRuleException("No copies available"));
        copy.lend();
        return view(loans.saveAndFlush(new Loan(member, copy, LocalDate.now(clock))));
    }

    @Transactional
    public LoanView returnLoan(Long loanId) {
        Loan loan = loans.lockById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        books.lockById(loan.getCopy().getBook().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        if (loan.getReturnedAt() != null) throw new BusinessRuleException("Loan was already returned");
        loan.markReturned(LocalDate.now(clock));
        loan.getCopy().returnToShelf();
        return view(loan);
    }

    @Transactional(readOnly = true)
    public List<LoanView> memberLoans(Long memberId) {
        if (!members.existsById(memberId)) throw new ResourceNotFoundException("Member not found");
        return loans.findByMemberIdOrderByBorrowedAtDescIdDesc(memberId).stream().map(this::view).toList();
    }

    private BookView view(Book book) {
        return new BookView(book.getId(), book.getTitle(), book.getAuthor(), book.getIsbn(),
                copies.countByBookId(book.getId()),
                copies.countByBookIdAndStatus(book.getId(), CopyStatus.AVAILABLE));
    }

    private LoanView view(Loan loan) {
        BookCopy copy = loan.getCopy();
        return new LoanView(loan.getId(), loan.getMember().getId(), copy.getBook().getId(),
                copy.getBook().getTitle(), copy.getBarcode(), loan.getBorrowedAt(),
                loan.getDueAt(), loan.getReturnedAt());
    }
}
