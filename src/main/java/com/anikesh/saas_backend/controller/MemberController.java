package com.anikesh.saas_backend.controller;

import com.anikesh.saas_backend.dto.*;
import com.anikesh.saas_backend.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

import java.util.List;

@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "List all members of the current tenant")
    @Parameter(in = ParameterIn.HEADER, name = "X-Tenant-Id", required = true, description = "Active tenant ID")
    @GetMapping
    public ResponseEntity<List<MemberResponseDTO>> getAll() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    @Operation(summary = "Add a member to the tenant", description = "Requires owner or admin role. Cannot grant owner role through this endpoint.")
    @Parameter(in = ParameterIn.HEADER, name = "X-Tenant-Id", required = true, description = "Active tenant ID")
    @PostMapping
    public ResponseEntity<MemberResponseDTO> add(@Valid @RequestBody MemberAddDTO dto) {
        return ResponseEntity.ok(memberService.addMember(dto));
    }

    @Operation(summary = "Remove a member from the tenant", description = "Requires owner or admin role. Cannot remove the tenant owner.")
    @Parameter(in = ParameterIn.HEADER, name = "X-Tenant-Id", required = true, description = "Active tenant ID")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> remove(@PathVariable Long userId) {
        memberService.removeMember(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Change a member's role", description = "Only the current owner can grant the owner role.")
    @Parameter(in = ParameterIn.HEADER, name = "X-Tenant-Id", required = true, description = "Active tenant ID")
    @PutMapping("/{userId}/role")
    public ResponseEntity<MemberResponseDTO> changeRole(@PathVariable Long userId,
            @Valid @RequestBody MemberRoleChangeDTO dto) {
        return ResponseEntity.ok(memberService.changeRole(userId, dto));
    }
}
