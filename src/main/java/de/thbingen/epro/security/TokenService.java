package de.thbingen.epro.security;

import java.util.Map;

public interface TokenService {
    String newToken(final Map<String, String> attributes);

    Map<String, String> verify(String token);
}
