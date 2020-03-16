package sc17dpc.individualproject;

import android.util.Log;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.webhook.Payload;
import com.github.seratch.jslack.api.webhook.WebhookResponse;

import java.io.IOException;

public class SlackApiObject {

    private String url = "https://hooks.slack.com/services/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";
    private Slack slack = Slack.getInstance();
    private String text;

    private long time = 0;

    public void sendPayload(boolean state) {
        WebhookResponse res = null;


        if (state) {
            text = "In Office";
        } else {
            text = "Out of Office";
        }

        Payload payload = Payload.builder().text(text).build();
        try {
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
