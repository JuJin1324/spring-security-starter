package starter.spring.security.user.adapter.in.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import starter.spring.security.user.application.domain.UserId;
import starter.spring.security.user.application.port.in.ListRetrievedUserQuery;
import starter.spring.security.user.application.port.in.RetrievedUser;

import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */

@RestController
@RequiredArgsConstructor
public class ListRetrievedUserController {
    private final ListRetrievedUserQuery listRetrievedUserQuery;

    @GetMapping("/users/{userId}")
    public RetrievedUserResource getOneRetrievedUser(@PathVariable("userId") String userId) {
        RetrievedUser user = listRetrievedUserQuery.getOne(new UserId(UUID.fromString(userId)));

        return new RetrievedUserResource(
                user.getId().getValue().toString(),
                user.getFullName(),
                new EmailResource(user.getEmail().getAddress())
        );
    }

    @RequiredArgsConstructor
    @Getter
    private static class RetrievedUserResource {
        @JsonProperty("id")
        private final String id;
        @JsonProperty("full_name")
        private final String fullName;
        @JsonProperty("email")
        private final EmailResource email;
    }

    @RequiredArgsConstructor
    @Getter
    private static class EmailResource {
        @JsonProperty("address")
        private final String address;
    }

}
