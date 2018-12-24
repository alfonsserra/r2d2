package com.systelab.r2d2.slack;

import me.ramswaroop.jbot.core.common.Controller;
import me.ramswaroop.jbot.core.common.EventType;
import me.ramswaroop.jbot.core.common.JBot;
import me.ramswaroop.jbot.core.slack.Bot;
import me.ramswaroop.jbot.core.slack.models.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.socket.WebSocketSession;

import java.util.Arrays;

@JBot
public class SlackBot extends Bot {

    private static final Logger logger = LoggerFactory.getLogger(SlackBot.class);

    @Value("${slackBotToken}")
    private String slackToken;

    @Override
    public String getSlackToken() {
        return slackToken;
    }

    @Override
    public Bot getSlackBot() {
        return this;
    }

    @Controller(events = {EventType.DIRECT_MESSAGE}, pattern = "(?i:.*(hi|hello|help|hey|hey there|what’s up|what's up|how’s it going|how's it going).*)")
    public void sayHello(WebSocketSession session, Event event) {
        reply(session, event, "Hi, I am " + slackService.getCurrentUser().getName());
    }


    @Controller(events = {EventType.DIRECT_MESSAGE}, pattern = "(?i:.*(setup meeting|setup a meeting).*)", next = "confirmTiming")
    public void setupMeeting(WebSocketSession session, Event event) {
        startConversation(event, "confirmTiming");
        reply(session, event, "Cool! At what time (ex. 15:30) do you want me to set up the meeting?");
    }

    @Controller(events = {EventType.DIRECT_MESSAGE})
    public void sayNoIdea(WebSocketSession session, Event event) {
        reply(session, event, "Hi, I am " + slackService.getCurrentUser().getName() + " and I don't know what do you want from me!");
    }


    @Controller(events = {EventType.DIRECT_MESSAGE}, next = "askWhetherToRemind")
    public void confirmTiming(WebSocketSession session, Event event) {

        if (event.getText().matches("(?i:.*(cancel|end|halt|terminate|stop|abandon|abort).*)")) {
            reply(session, event, "No problem. You can always schedule one with the 'setup meeting' command.");
            stopConversation(event);
        } else {
            if (event.getText().matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")) {
                reply(session, event, "Your meeting is set at " + event.getText() +
                        ". Would you like me to set a reminder for you?");
                nextConversation(event);
            } else {
                reply(session, event, "I have not understand you! At what time (ex. 15:30) do you want me to set up the meeting?");
            }
        }
    }

    @Controller(events = {EventType.DIRECT_MESSAGE})
    public void askWhetherToRemind(WebSocketSession session, Event event) {
        if (isOK(event.getText())) {
            reply(session, event, "Great! I will remind you tomorrow before the meeting.");
        } else {
            reply(session, event, "Okay, don't forget to attend the meeting tomorrow :)");
        }
        stopConversation(event);
    }

    @Controller(events = EventType.PIN_ADDED)
    public void onPinAdded(WebSocketSession session, Event event) {
        reply(session, event, "Thanks for the pin! You can find all pinned items under channel details.");
    }

    @Controller(events = EventType.FILE_SHARED)
    public void onFileShared(WebSocketSession session, Event event) {
        logger.info("File shared: {}", event);
    }

    public boolean isOK(String inputStr) {
        return Arrays.asList("yes", "ok", "okay", "yeah", "do it", "that's fine", "fine",
                "cool", "yea", "okey-dokey", "by all means", "aye aye",
                "roger", "yup", "totally", "sure", "10-4", "alright", "sounds good",
                "certainly", "definitely", "of course", "gladly", "indeed", "obviously", ":+1:", ":ok_hand:")
                .stream()
                .anyMatch(s -> inputStr.toLowerCase().contains(s));
    }
}