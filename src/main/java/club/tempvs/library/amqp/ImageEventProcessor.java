package club.tempvs.library.amqp;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface ImageEventProcessor {

    @Output("image.delete-by-ids")
    MessageChannel deleteByIds();

    @Output("image.delete-for-entity")
    MessageChannel deleteForEntity();

    @Output("image.store")
    MessageChannel store();
}
