package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import liquibase.pro.packaged.E;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.service.NotificationTaskService;
import pro.sky.telegrambot.service.TelegramBotService;

import javax.annotation.PostConstruct;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.awt.SystemColor.text;
import static java.time.LocalDateTime.parse;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private static final Pattern PATTERN = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2}) ([А-я\\d.,!?:\\s]+)");

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private TelegramBot telegramBot;
    private final NotificationTaskService notificationTaskService;

    private final TelegramBotService telegramBotService;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationTaskService notificationTaskService,
                                      TelegramBotService telegramBotService) {
        this.telegramBot = telegramBot;
        this.notificationTaskService = notificationTaskService;
        this.telegramBotService = telegramBotService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        try {
            updates.forEach(update -> {
                logger.info("Processing update: {}", update);
                Message message = update.message();
                Matcher matcher;
                if (message != null) {
                    User user = message.from();
                    String text = message.text();
                    if ("/start".equals(text)) {
                        telegramBotService.sendMessage(
                                        user.id(),
                                        "Для планирования задачи отправьте ее в формате:" +
                                                "\\n*01.01.2022 20:00 Сделать домашнюю работу*"
                                );
                    } else if (text != null && (matcher = PATTERN.matcher(text)).matches()) {
                        LocalDateTime dateTime = parse(matcher.group(1));
                        String messageForNotNotification = matcher.group(2);
                        if (dateTime != null) {
                            notificationTaskService.save(new NotificationTask(user.id(),
                                    messageForNotNotification, dateTime.truncatedTo(ChronoUnit.MINUTES)));
                            telegramBotService.sendMessage(user.id(), "Задача запланирована!");
                        } else {
                            telegramBotService.sendMessage(user.id(), "Неккоректный формат даты и/или времени!");
                        }
                    } else {
                        telegramBotService.sendMessage(user.id(), "Отправьте сообщение согласно указанному формату!");

                    }
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }


    @Nullable
    private LocalDateTime parse(String dataTime) {
        try {
            return LocalDateTime.parse(dataTime, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }

    }

}