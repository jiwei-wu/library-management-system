package com.jiwei.library_management_system.model;

import jakarta.persistence.*;

@Entity
@Table(name = "book_copies", uniqueConstraints = @UniqueConstraint(columnNames = "barcode"))
public class BookCopy {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Book book;
    @Column(nullable = false)
    private String barcode;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CopyStatus status = CopyStatus.AVAILABLE;

    protected BookCopy() {}
    public BookCopy(Book book, String barcode) {
        this.book = book;
        this.barcode = barcode;
    }
    public Long getId() { return id; }
    public Book getBook() { return book; }
    public String getBarcode() { return barcode; }
    public CopyStatus getStatus() { return status; }
    public void lend() { status = CopyStatus.ON_LOAN; }
    public void returnToShelf() { status = CopyStatus.AVAILABLE; }
}
