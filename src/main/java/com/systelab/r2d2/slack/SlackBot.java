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
        reply(session, event, "Hi, I am " + slackService.getCurrentUser().getName() + ". I'm here to help you to report your telecommuting days.\nStart a 'report telecommuting' command");
        stopConversation(event);
    }

    @Controller(events = {EventType.DIRECT_MESSAGE})
    public void sayNoIdea(WebSocketSession session, Event event) {
        reply(session, event, "Hi, I am " + slackService.getCurrentUser().getName() + ". I'm here to help you to report your telecommuting days.\nStart a 'report telecommuting' command");
        stopConversation(event);
    }


    @Controller(events = {EventType.DIRECT_MESSAGE}, pattern = "(?i:.*(report|report a day|report telecommuting).*)", next = "setDate")
    public void reportWork(WebSocketSession session, Event event) {
        startConversation(event, "setDate");

        reply(session, event, "Cool "+"!\nIt seems that we have here a :smiley: employee.\nBy the way, I always to telecommuting. :stuck_out_tongue_winking_eye:\nBut let's talk about you.\nWhen it was (ex. 2019-12-1)?");
    }

    @Controller(events = {EventType.DIRECT_MESSAGE}, next = "askForTheGoals")
    public void setDate(WebSocketSession session, Event event) {
        if (this.isConversationOn(event)) {
            if (shouldStopTheConversation(session, event)) {
                stopConversation(event);
            } else {
                if (event.getText().matches("^(20[0-9][0-9])-([1-9]|1[0-2])-([1-9]|1[0-9]|2[0-9]|3[0-1])$")) {
                    reply(session, event, "The day was " + event.getText() + ". Which were the initial goals?");
                    nextConversation(event);
                } else {
                    reply(session, event, "I have not understand you! When it was (ex. 2019-12-1)?");
                }
            }
        }
    }

    @Controller(events = {EventType.DIRECT_MESSAGE}, next = "askForTheCompletion")
    public void askForTheGoals(WebSocketSession session, Event event) {
        if (this.isConversationOn(event)) {
            if (shouldStopTheConversation(session, event)) {
                stopConversation(event);
            } else {
                reply(session, event, ":ok_hand:\nIt seems a lot of work to me. Did you achieve all the objectives?");
                nextConversation(event);
            }
        }
    }

    @Controller(events = {EventType.DIRECT_MESSAGE}, next = "askForIncidences")
    public void askForTheCompletion(WebSocketSession session, Event event) {
        if (this.isConversationOn(event)) {
            if (shouldStopTheConversation(session, event)) {
                stopConversation(event);
            } else {
                if (isOK(event.getText())) {
                    reply(session, event, ":clap::clap::clap:\nDid you have any kind of incidence?.");
                    nextConversation(event);
                } else {
                    if (isKO(event.getText())) {
                        reply(session, event, ":shit:\nDid you have any kind of incidence?.");
                        nextConversation(event);
                    } else {
                        reply(session, event, "Did you achieve all the objectives?");
                    }
                }
            }
        }
    }

    @Controller(events = {EventType.DIRECT_MESSAGE})
    public void askForIncidences(WebSocketSession session, Event event) {
        if (this.isConversationOn(event)) {
            if (shouldStopTheConversation(session, event)) {
                stopConversation(event);
            } else {
                if (isOK(event.getText())) {
                    reply(session, event, ":shit:\nI'm sorry, we are continuously improving. I'm going to send yor comments. You are all set. ");
                    stopConversation(event);
                } else {
                    if (isKO(event.getText())) {
                        reply(session, event, ":ok_hand:\nSound fantastic. Thank you very much, you are all set. ");
                        stopConversation(event);
                    } else {
                        reply(session, event, "Did you have any kind of incidence?");
                    }
                }
            }
        }
    }

    @Controller(events = EventType.PIN_ADDED)
    public void onPinAdded(WebSocketSession session, Event event) {
        reply(session, event, "Thanks for the pin! You can find all pinned items under channel details.");
    }

    @Controller(events = EventType.FILE_SHARED)
    public void onFileShared(WebSocketSession session, Event event) {
        logger.info("File shared: {}", event);
    }

    private boolean isOK(String inputStr) {
        return Arrays.asList("yes", "ok", "okay", "yeah", "do it", "that's fine", "fine",
                "cool", "yea", "okey-dokey", "by all means", "aye aye",
                "roger", "yup", "totally", "sure", "10-4", "alright", "sounds good",
                "certainly", "definitely", "of course", "gladly", "indeed", "obviously", ":+1:", ":ok_hand:")
                .stream()
                .anyMatch(s -> inputStr.toLowerCase().contains(s));
    }

    private boolean isKO(String inputStr) {
        return Arrays.asList("no", ":ko_hand:")
                .stream()
                .anyMatch(s -> inputStr.toLowerCase().contains(s));
    }


    private boolean shouldStopTheConversation(WebSocketSession session, Event event) {
        if (event.getText().matches("(?i:.*(cancel|end|halt|terminate|stop|abandon|abort).*)")) {
            reply(session, event, "No problem. You can always report me with the 'report telecommuting' command.");
            return true;
        } else return false;
    }
}