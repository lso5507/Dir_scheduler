import org.quartz.Trigger;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class TriggerMaker {

    Trigger trigger = newTrigger()
            .withIdentity("HelloTrigger", "HelloGroup")
            .startNow()
            .withSchedule(simpleSchedule()
                    .withIntervalInSeconds(5)
                    .repeatForever())
            .build();

    public Trigger getTrigger() {
        return trigger;
    }
}