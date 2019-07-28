package club.tempvs.library.service;

import club.tempvs.library.amqp.ImageEventProcessor;
import club.tempvs.library.dto.ImageDto;
import club.tempvs.library.service.impl.ImageServiceImpl;
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ImageServiceTest {

    @InjectMocks
    private ImageServiceImpl imageService;

    @Mock
    private ImageEventProcessor imageEventProcessor;
    @Mock
    private MessageChannel messageChannel;
    @Mock
    private ImageDto imageDto;

    @Test
    public void testDeleteImagesByIds() {
        String objectId1 = "objectId1";
        String objectId2 = "objectId2";
        List<String> objectIds = ImmutableList.of(objectId1, objectId2);

        when(imageEventProcessor.deleteByIds()).thenReturn(messageChannel);

        imageService.delete(objectIds);

        verify(imageEventProcessor).deleteByIds();
        verify(messageChannel).send(any(Message.class));
        verifyNoMoreInteractions(imageEventProcessor, messageChannel);
    }

    @Test
    public void testDeleteImagesForEntity() {
        String belongsTo = "item";
        Long entityId = 3L;

        when(imageEventProcessor.deleteForEntity()).thenReturn(messageChannel);

        imageService.delete(belongsTo, entityId);

        verify(imageEventProcessor).deleteForEntity();
        verify(messageChannel).send(any(Message.class));
        verifyNoMoreInteractions(imageEventProcessor, messageChannel);
    }

    @Test
    public void testStore() {
        when(imageEventProcessor.store()).thenReturn(messageChannel);

        imageService.store(imageDto);

        verify(imageEventProcessor).store();
        verify(messageChannel).send(any(Message.class));
        verifyNoMoreInteractions(imageEventProcessor, messageChannel);
    }
}
