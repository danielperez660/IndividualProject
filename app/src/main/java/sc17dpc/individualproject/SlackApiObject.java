package sc17dpc.individualproject;

import android.util.Log;

import com.github.seratch.jslack.*;
import com.github.seratch.jslack.api.webhook.*;

import java.io.IOException;

public class SlackApiObject {

    private String url = "https://hooks.slack.com/services/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";
    private Slack slack = Slack.getInstance();
    private String text;

    public void sendPayload(boolean state) {
        WebhookResponse res = null;


        if (state) {
            text = "In Office";
        } else {
            text = "Out of Office";
        }

        Payload payload = Payload.builder().text(text).build();
        try {
            res = slack.send(url, payload);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("SlackApi", res.getBody());
    }
}
