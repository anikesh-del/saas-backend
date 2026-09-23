package com.anikesh.saas_backend.service;

import com.anikesh.saas_backend.Exception.CustomException;
import com.anikesh.saas_backend.dto.*;
import com.anikesh.saas_backend.entity.*;
import com.anikesh.saas_backend.repository.*;
import com.anikesh.saas_backend.security.CurrentUserProvider;
import com.anikesh.saas_backend.tenant.RequiresRole;
import com.anikesh.saas_backend.tenant.TenantContext;
import com.anikesh.saas_backend.tenant.TenantScoped;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class MemberService {

    private final TenantMembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PlanEnforcementService planEnforcementService;
    private final AuditService auditService;

    public MemberService(TenantMembershipRepository membershipRepository,
                          UserRepository userRepository,
                          RoleRepository roleRepository,PlanEnforcementService planEnforcementService, AuditService auditService) {
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.planEnforcementService = planEnforcementService;
        this.auditService = auditService;
    }

    @Transactional
    @TenantScoped
    public List<MemberResponseDTO> getAllMembers() {
        // membership rows aren't RLS-protected yet, only projects/tasks are.
        return membershipRepository.findAllByTenant_TenantId(TenantContext.getTenantId())
                .stream().map(this::toDto).toList();
    }

    @Transactional
    @TenantScoped
    @RequiresRole({"owner", "admin"})
    public MemberResponseDTO addMember(MemberAddDTO dto) {
        if ("owner".equalsIgnoreCase(dto.getRole())) {
            throw new CustomException("Cannot grant owner role through this endpoint", HttpStatus.FORBIDDEN);
        }

        Long tenantId = TenantContext.getTenantId();
        Long currentUserId = CurrentUserProvider.getCurrentUserId();
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        if (membershipRepository.findByTenant_TenantIdAndUser_UserId(tenantId, user.getUserId()).isPresent()) {
            throw new CustomException("User is already a member of this tenant", HttpStatus.CONFLICT);
        }
        
        long currentMembers =
        membershipRepository.countByTenant_TenantId(tenantId);

planEnforcementService.enforceLimit(
        tenantId,
        "max_users",
        currentMembers,
        "Member"
);

        Role role = roleRepository.findByName(dto.getRole())
                .orElseThrow(() -> new CustomException("Invalid role", HttpStatus.BAD_REQUEST));

        TenantMembership membership = new TenantMembership();
        membership.setId(new TenantMembershipId(tenantId, user.getUserId()));
       
        Tenant tenantRef = new Tenant();
        tenantRef.setTenantId(tenantId);
        membership.setTenant(tenantRef);
        membership.setUser(user);
        membership.setRole(role);
        membership.setJoinedAt(OffsetDateTime.now());

        membershipRepository.save(membership);
        auditService.record(
                tenantId,
                "tenant_memberships",
                user.getUserId(),
                "INSERT",
                currentUserId,
                null,
                toDto(membership)
        );

        return toDto(membership);
    }

    @Transactional
    @TenantScoped
    @RequiresRole({"owner", "admin"})
    public void removeMember(Long userId) {
        Long tenantId = TenantContext.getTenantId();
        Long currentUserId = CurrentUserProvider.getCurrentUserId();


        TenantMembership membership = membershipRepository.findByTenant_TenantIdAndUser_UserId(tenantId, userId)
                .orElseThrow(() -> new CustomException("Membership not found", HttpStatus.NOT_FOUND));

        if ("owner".equalsIgnoreCase(membership.getRole().getName())) {
            throw new CustomException("Cannot remove the tenant owner", HttpStatus.FORBIDDEN);
        }

        MemberResponseDTO before = toDto(membership);
        membershipRepository.delete(membership);

        auditService.record(
                tenantId,
                "tenant_memberships",
                userId,
                "DELETE",
                currentUserId,
                before,
                null
        );
    }

    @Transactional
    @TenantScoped
    @RequiresRole({"owner", "admin"})
    public MemberResponseDTO changeRole(Long userId, MemberRoleChangeDTO dto) {
        Long tenantId = TenantContext.getTenantId();
        Long currentUserId = CurrentUserProvider.getCurrentUserId();
        String callerRole = TenantContext.getRole();

        // only owner can change role to "owner"
        if ("owner".equalsIgnoreCase(dto.getRole()) && !"owner".equalsIgnoreCase(callerRole)) {
            throw new CustomException("Only the current owner can grant the owner role", HttpStatus.FORBIDDEN);
        }

        TenantMembership membership = membershipRepository.findByTenant_TenantIdAndUser_UserId(tenantId, userId)
                .orElseThrow(() -> new CustomException("Membership not found", HttpStatus.NOT_FOUND));
        
        MemberResponseDTO before = toDto(membership);

        Role newRole = roleRepository.findByName(dto.getRole())
                .orElseThrow(() -> new CustomException("Invalid role", HttpStatus.BAD_REQUEST));

        membership.setRole(newRole);
        MemberResponseDTO after = toDto(membership);


        auditService.record(
                tenantId,
                "tenant_memberships",
                userId,
                "UPDATE",
                currentUserId,
                before,
                after
        );

        return after;
    }

    private MemberResponseDTO toDto(TenantMembership m) {
        return new MemberResponseDTO(
                m.getUser().getUserId(), m.getUser().getName(), m.getUser().getEmail(),
                m.getRole().getName(), m.getJoinedAt()
        );
    }
}
