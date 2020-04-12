package sc17dpc.individualproject;

import android.util.Log;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.webhook.Payload;
import com.github.seratch.jslack.api.webhook.WebhookResponse;

import java.io.IOException;

public class SlackApiObject {

    private final String url = "https://hooks.slack.com/services/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";
    private Slack slack = Slack.getInstance();
    private String text;

    private long time = 0;

    public void sendPayload(boolean state, String name) {
        WebhookResponse res = null;

        // Checks to see if you are entering or exiting office
        if (state) {
            text = name + " Is In Office";
        } else {
            text = name + " Is Out of Office";
        }

        // Builds the payload
        Payload payload = Payload.builder().text(text).build();
        try {
            // Checks to see if the last message was sent more than 5 seconds ago
            if(System.currentTimeMillis() >= time + 5000){
                res = slack.send(url, payload);
                Log.d("SlackApi", res.getBody());
                time = System.currentTimeMillis();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
