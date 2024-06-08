package ru.numbdev.interviewer.page.component;

import org.vaadin.vcamera.VCamera;

public class CameraComponent extends VCamera {

    public CameraComponent() {
    }

    @Override
    public void startRecording() {
        getElement().executeJs("""
                this.recorder = new MediaRecorder(this.stream);
                
                let webSocket = new WebSocket('ws://localhost:8890/video');
                 webSocket.addEventListener("open", (event) => {
                                   this.recorder.start();
                                    });
                this.recorder.ondataavailable = e => {
                    let target = this.getAttribute("target");
                    let formData = new FormData();
                    formData.append("data", e.data);
                     fetch(target, {
                           method: "post",
                           body: formData
                     }).then(response => console.log(response));
                }
                console.log(test);
                setInterval(() => {
                console.log("run");
                      this.recorder.requestData();
                    }, 5000);
                """);
    }
}
