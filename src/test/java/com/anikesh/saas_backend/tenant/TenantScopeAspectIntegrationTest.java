package com.anikesh.saas_backend.tenant;

import com.anikesh.saas_backend.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:tenant-aspect-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class TenantScopeAspectIntegrationTest {

    @Autowired
    private MemberService memberService;

    @Test
    void serviceBeanShouldBeSpringAopProxy() {
        assertThat(AopUtils.isAopProxy(memberService)).isTrue();
    }
}
