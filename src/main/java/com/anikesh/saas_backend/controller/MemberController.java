package com.anikesh.saas_backend.controller;

import com.anikesh.saas_backend.dto.*;
import com.anikesh.saas_backend.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<List<MemberResponseDTO>> getAll() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    @PostMapping
    public ResponseEntity<MemberResponseDTO> add(@Valid @RequestBody MemberAddDTO dto) {
        return ResponseEntity.ok(memberService.addMember(dto));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> remove(@PathVariable Long userId) {
        memberService.removeMember(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/role")
    public ResponseEntity<MemberResponseDTO> changeRole(@PathVariable Long userId,
                                                          @Valid @RequestBody MemberRoleChangeDTO dto) {
        return ResponseEntity.ok(memberService.changeRole(userId, dto));
    }
}
