package com.jiwei.library_management_system.api;

import com.jiwei.library_management_system.api.ApiDtos.*;
import com.jiwei.library_management_system.service.LibraryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500"})
public class LoanController {
    private final LibraryService service;
    public LoanController(LibraryService service) { this.service = service; }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public LoanView borrow(@Valid @RequestBody CreateLoan request) { return service.borrow(request); }

    @PostMapping("/{id}/return")
    public LoanView returnLoan(@PathVariable Long id) { return service.returnLoan(id); }
}
