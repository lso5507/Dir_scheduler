import org.quartz.Trigger;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class TriggerMaker {

    Trigger trigger = newTrigger()
            .withIdentity("HelloTrigger", "HelloGroup")
            .startNow()
            .withSchedule(simpleSchedule()
                    .withIntervalInMinutes(1)
                    .withRepeatCount(3))
            .build();

    public Trigger getTrigger() {
        return trigger;
    }
}