package com.jiwei.library_management_system.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
public class Loan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private BookCopy copy;
    @Column(nullable = false)
    private LocalDate borrowedAt;
    @Column(nullable = false)
    private LocalDate dueAt;
    private LocalDate returnedAt;

    protected Loan() {}
    public Loan(Member member, BookCopy copy, LocalDate borrowedAt) {
        this.member = member;
        this.copy = copy;
        this.borrowedAt = borrowedAt;
        this.dueAt = borrowedAt.plusDays(14);
    }
    public Long getId() { return id; }
    public Member getMember() { return member; }
    public BookCopy getCopy() { return copy; }
    public LocalDate getBorrowedAt() { return borrowedAt; }
    public LocalDate getDueAt() { return dueAt; }
    public LocalDate getReturnedAt() { return returnedAt; }
    public void markReturned(LocalDate date) { returnedAt = date; }
}
