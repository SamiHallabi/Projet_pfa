//package schedule;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import service.ReservationService;
//
//@Configuration
//@EnableScheduling
//public class ReminderScheduler {
//
//    private final ReservationService reservationService;
//
//    @Autowired
//    public ReminderScheduler(ReservationService reservationService) {
//        this.reservationService = reservationService;
//    }
//
//    // Run every day at noon to send reminders for shows happening the next day
//    @Scheduled(cron = "0 0 12 * * ?")
//    public void sendDailyReminders() {
//        reservationService.sendReminderEmails();
//    }
//}