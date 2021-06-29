package com.simple.common.props;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
@ConfigurationProperties(prefix="simple")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppProps {
    private String domainName;
}
