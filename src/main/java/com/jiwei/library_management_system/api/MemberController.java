package com.jiwei.library_management_system.api;

import com.jiwei.library_management_system.api.ApiDtos.*;
import com.jiwei.library_management_system.service.LibraryService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500"})
public class MemberController {
    private final LibraryService service;
    public MemberController(LibraryService service) { this.service = service; }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public MemberView create(@Valid @RequestBody CreateMember request) { return service.createMember(request); }

    @GetMapping("/{id}/loans")
    public List<LoanView> loans(@PathVariable Long id) { return service.memberLoans(id); }
}
