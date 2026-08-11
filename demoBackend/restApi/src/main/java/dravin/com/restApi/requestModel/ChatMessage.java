package dravin.com.restApi.requestModel;


import dravin.com.restApi.constant.ChatMessageType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {

    private String sender;
    private String content;
    private String recipient;
    private String dataTime;
    private ChatMessageType type;
}