package pl.umk.wmii.msr.contributions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pl.umk.wmii.msr.contributions.config.AppConfig;

public class App {
    private static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        logger.debug("Starting app spring context");
        try (AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
                AppConfig.class)) {

            applicationContext.getBean(Msr14AppLauncher.class).launchCommitsTimeGrouper(args);

            applicationContext.destroy();
            logger.debug("App spring context destroyed");
        }

    }
}
