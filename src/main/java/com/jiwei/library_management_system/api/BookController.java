package com.jiwei.library_management_system.api;

import com.jiwei.library_management_system.api.ApiDtos.*;
import com.jiwei.library_management_system.service.LibraryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500"})
public class BookController {
    private final LibraryService service;
    public BookController(LibraryService service) { this.service = service; }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public BookView create(@Valid @RequestBody CreateBook request) { return service.createBook(request); }

    @GetMapping
    public Page<BookView> list(@RequestParam(required = false) String title,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "20") int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page must be >= 0 and size must be 1–100");
        }
        return service.listBooks(title, page, size);
    }

    @GetMapping("/{id}")
    public BookView get(@PathVariable Long id) { return service.getBook(id); }

    @PostMapping("/{id}/copies")
    public BookView addCopies(@PathVariable Long id, @Valid @RequestBody AddCopies request) {
        return service.addCopies(id, request);
    }
}
