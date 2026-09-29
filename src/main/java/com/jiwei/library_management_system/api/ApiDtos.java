package com.jiwei.library_management_system.api;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public final class ApiDtos {
    private ApiDtos() {}

    public record CreateBook(@NotBlank @Size(max = 255) String title,
                             @NotBlank @Size(max = 255) String author,
                             @NotBlank @Size(max = 255) String isbn,
                             @Min(1) @Max(20) int copies) {}
    public record AddCopies(@Min(1) @Max(20) int count) {}
    public record BookView(Long id, String title, String author, String isbn,
                           long totalCopies, long availableCopies) {}
    public record CreateMember(@NotBlank @Size(max = 255) String name,
                               @NotBlank @Email @Size(max = 255) String email) {}
    public record MemberView(Long id, String name, String email) {}
    public record CreateLoan(@NotNull @Positive Long memberId, @NotNull @Positive Long bookId) {}
    public record LoanView(Long id, Long memberId, Long bookId, String bookTitle,
                           String barcode, LocalDate borrowedAt, LocalDate dueAt, LocalDate returnedAt) {}
}
