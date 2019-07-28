package club.tempvs.library.service.impl;

import club.tempvs.library.amqp.ImageEventProcessor;
import club.tempvs.library.dto.ImageDto;
import club.tempvs.library.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.messaging.support.MessageBuilder.withPayload;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageEventProcessor imageEventProcessor;

    @Override
    public void delete(List<String> objectIds) {
        imageEventProcessor.deleteByIds()
                .send(withPayload(objectIds).build());
    }

    @Override
    public void delete(String belongsTo, Long entityId) {
        String query = String.format("%1$s::%2$d", belongsTo, entityId);
        imageEventProcessor.deleteForEntity()
                .send(withPayload(query).build());
    }

    @Override
    public void store(ImageDto payload) {
        imageEventProcessor.store()
                .send(withPayload(payload).build());
    }
}
