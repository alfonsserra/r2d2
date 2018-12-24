package com.systelab.r2d2.slack;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.ramswaroop.jbot.core.slack.models.Attachment;
import me.ramswaroop.jbot.core.slack.models.RichMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;


@Component
public class SlackWebhookService {

    private static final Logger logger = LoggerFactory.getLogger(SlackWebhookService.class);

    @Value("${slackIncomingWebhookUrl}")
    private String slackIncomingWebhookUrl;

    public void sendMessage(RichMessage richMessage) {
        RestTemplate restTemplate = new RestTemplate();

        // For debugging purpose only
        try {
            logger.debug("Reply (RichMessage): {}", new ObjectMapper().writeValueAsString(richMessage));
        } catch (JsonProcessingException e) {
            logger.debug("Error parsing RichMessage: ", e);
        }

        try {
            restTemplate.postForEntity(slackIncomingWebhookUrl, richMessage.encodedMessage(), String.class);
        } catch (RestClientException e) {
            logger.error("Error posting to Slack Incoming Webhook: ", e);
        }
    }

    public RichMessage getSampleMessage() {

        RichMessage richMessage = new RichMessage("This is a basic text.");

        Attachment[] attachments = new Attachment[1];
        attachments[0] = new Attachment();
        attachments[0].setText("First ~attachment~ with markdown.");
        attachments[0].setPretext("Pre text");
        attachments[0].setMarkdownIn(Arrays.asList("text"));
        attachments[0].setAuthorName("Author");
        attachments[0].setColor("#36a64f");
        attachments[0].setFooter("Footer");
        richMessage.setAttachments(attachments);

        return richMessage;
    }
}
