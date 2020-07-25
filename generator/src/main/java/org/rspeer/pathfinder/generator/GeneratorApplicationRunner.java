package org.rspeer.pathfinder.generator;

import lombok.RequiredArgsConstructor;
import org.rspeer.pathfinder.generator.service.FlagsGeneratorService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GeneratorApplicationRunner implements ApplicationRunner {

    private final FlagsGeneratorService generatorService;
    private final ApplicationContext context;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        generatorService.generateAllFlags();
        SpringApplication.exit(context);
    }

}
