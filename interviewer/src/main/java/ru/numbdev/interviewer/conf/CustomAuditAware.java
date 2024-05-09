package ru.numbdev.interviewer.conf;

import org.springframework.data.domain.AuditorAware;
import ru.numbdev.interviewer.utils.SecurityUtil;

import java.util.Optional;

public class CustomAuditAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(SecurityUtil.getUserName());
    }
}
