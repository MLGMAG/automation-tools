package com.mlgmag;

import com.mlgmag.facade.JiraFacade;
import com.mlgmag.service.ArgsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.Resource;

@SpringBootApplication
public class AutoRiApplication implements CommandLineRunner {

    @Resource
    private ConfigurableApplicationContext context;
    @Resource
    private ArgsService argsService;
    @Resource
    private JiraFacade defaultJiraFacade;

    public static void main(String[] args) {
        SpringApplication.run(AutoRiApplication.class, args).close();
    }

    @Override
    public void run(String... args) {
        argsService.validate(args);
        String fixVersion = argsService.getFixVersion(args);

        defaultJiraFacade.processReleaseTicket(fixVersion);
        defaultJiraFacade.transferAllInstructionsIntoReleaseInstruction(fixVersion);

        System.exit(SpringApplication.exit(context));
    }
}
