package ru.numbdev.interviewer.page.component;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InterviewerWebCamComponent extends HorizontalLayout {

    private final CameraComponent camera = null;
    private final VidoeComponent newCamera;
    private final VidoeComponent companionCamera;
    private final List<Component> roommates = new ArrayList<>();

    private final List<ByteArrayOutputStream> streams = new ArrayList<>(); //MappedByteBuffer
//    private final BufferedOutputStream buffer = new BufferedOutputStream();
    public InterviewerWebCamComponent() {
//        var candidate = new CustomVideo();
//        camera = new CameraComponent();
//        camera.openCamera();
//        camera.setReceiver(r -> {
//            System.out.println("->" + r);
//            var stream = new ByteArrayOutputStream();
//            streams.add(stream);
//            return stream;
//        });
//        camera.addFinishedListener(event -> {
////            candidate.setSrc();
//        });
//        add(camera);
//        add(candidate);

        newCamera = new VidoeComponent(true);
        add(newCamera);

        companionCamera = new VidoeComponent(false);
        add(companionCamera);
    }

    public void start() {
//        camera.startRecording();
    }
}
