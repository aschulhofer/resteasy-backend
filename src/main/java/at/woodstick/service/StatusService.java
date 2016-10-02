package at.woodstick.service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.util.Date;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class StatusService {

	private static Logger LOG = LoggerFactory.getLogger(StatusService.class);
	
	@PostConstruct
	public void startup() {
		LOG.info("StatusService constructed.");
	}
	
	@Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
	public void heartbeatLogging() {
		LOG.info("StatusService alive.");
	}
	
	@Schedule(second = "*/10", minute = "*", hour = "*", persistent = false)
	public void task(Timer timer) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		
		Date nextScheduleLegacyDate = timer.getNextTimeout();
		Instant nextScheduleInstant = nextScheduleLegacyDate.toInstant();
		LocalDateTime nextSchedule = LocalDateTime.ofInstant(nextScheduleInstant, ZoneId.systemDefault());
		
		Duration schedulePeriod = Duration.between(now, nextSchedule);
		LocalDateTime previousSchedule = now.minus(schedulePeriod);
		
		LOG.info("Task run @ -- {}", now.format(dtf));
		LOG.info("Next run @ -- {}", nextSchedule.format(dtf));
		LOG.info("Prev run @ -- {}", previousSchedule.format(dtf));
		LOG.info("Duration @ -- {}", schedulePeriod);
	}
	
	@Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
	public void taskNextSchedule(Timer timer) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		
		Date nextScheduleLegacyDate = timer.getNextTimeout();
		LocalDateTime previousSchedule = now.with(previousSchedule(nextScheduleLegacyDate));
		
		LOG.info("Adjuster--Task run @ -- {}", now.format(dtf));
		LOG.info("Adjuster--Prev run @ -- {}", previousSchedule.format(dtf));
	}
	
	// ------------------------------------------------------------------------------------------
	
	/**
	 * Returns the "previous schedule" adjuster, which returns a new date set to the previous schedule
	 * depending on the duration between the current date and the next schedule date.
	 * 
	 * @param nextScheduleLegacyDate datetime set to the next execution
	 * @return date time of the previous execution
	 * @see #previousSchedule(Temporal)
	 */
	public static TemporalAdjuster previousSchedule(Date nextScheduleLegacyDate) {
		Objects.requireNonNull(nextScheduleLegacyDate, "nextScheduleLegacyDate must not be null");
		Instant nextScheduleInstant = nextScheduleLegacyDate.toInstant();
		LocalDateTime nextSchedule = LocalDateTime.ofInstant(nextScheduleInstant, ZoneId.systemDefault());
		
		return previousSchedule(nextSchedule);
	}
	
	/**
	 * Returns the "previous schedule" adjuster, which returns a new date set to the previous schedule
	 * depending on the duration between the current date and the next schedule date.
	 * 
	 * @param nextSchedule datetime set to the next execution
	 * @return date time of the previous execution
	 */
	public static TemporalAdjuster previousSchedule(Temporal nextSchedule) {
		Objects.requireNonNull(nextSchedule, "nextSchedule must not be null");
		return (temporal) -> {
			Duration schedulePeriod = Duration.between(temporal, nextSchedule);
			LocalDateTime temporalDateTime = LocalDateTime.from(temporal);
			LocalDateTime previousSchedule = temporalDateTime.minus(schedulePeriod);
			
			return previousSchedule;
		};
	}
}
