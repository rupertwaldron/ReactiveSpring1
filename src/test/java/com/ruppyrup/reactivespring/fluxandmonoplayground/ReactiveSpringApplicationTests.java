package com.ruppyrup.reactivespring.fluxandmonoplayground;

import com.ruppyrup.reactivespring.config.SqlConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SqlConfig.class)
@ActiveProfiles("test")
public class ReactiveSpringApplicationTests {

    @Test
    public void contextLoads() {
    }

}
