package pro.sky.telegrambot.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_tasks")
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "user_id")
    private long userId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "notification_date_time", nullable = false)
    private LocalDateTime notificationDateTime;

    public NotificationTask(long userId, String message, LocalDateTime notificationDateTime) {
        this.userId = userId;
        this.message = message;
        this.notificationDateTime = notificationDateTime;
    }

    public NotificationTask() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getNotificationDateTime() {
        return notificationDateTime;
    }

    public void setNotificationDateTime(LocalDateTime notificationDateTime) {
        this.notificationDateTime = notificationDateTime;
    }
}
