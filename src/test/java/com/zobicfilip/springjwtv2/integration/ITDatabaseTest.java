package com.zobicfilip.springjwtv2.integration;

import com.zobicfilip.springjwtv2.repository.RoleRepository;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

public class ITDatabaseTest extends Base {

    @Autowired
    private RoleRepository roleRepository;

    public static Stream<Arguments> provideRoleValues() {
        return Stream.of(
                Arguments.of("ADMIN"),
                Arguments.of("MODERATOR"),
                Arguments.of("USER")
        );
    }

    public static Stream<Arguments> provideAuthorityValues() {
        return Stream.of(
                Arguments.of("ADMIN"),
                Arguments.of("MODERATOR"),
                Arguments.of("USER")
        );
    }
// return arguments of
    private static class RolePreAppenderAggregator implements ArgumentsAggregator {
        @Override // we can change return type if subclass of parent method String extends Object
        public String aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws ArgumentsAggregationException {
            return Strings.concat("ROLE_",accessor.get(0,String.class));
        }
    }

    @ParameterizedTest
    @MethodSource("provideRoleValues")
    public void roles_shouldExist_whenPresentInDatabase(@AggregateWith(RolePreAppenderAggregator.class) String role) {
        Assertions.assertTrue(roleRepository.findRoleByTitle(role).isPresent());
    }
}
