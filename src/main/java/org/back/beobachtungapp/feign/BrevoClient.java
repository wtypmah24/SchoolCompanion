package org.back.beobachtungapp.feign;

import org.back.beobachtungapp.config.BrevoClientConfig;
import org.back.beobachtungapp.dto.brevo.BrevoEmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "brevoClient",
    url = "https://api.brevo.com/v3",
    configuration = BrevoClientConfig.class)
public interface BrevoClient {

  @PostMapping("/smtp/email")
  void sendEmail(@RequestBody BrevoEmailRequest request);

  @DeleteMapping("/smtp/email/{identifier}")
  void cancelEmail(@PathVariable("identifier") String identifier);
}
