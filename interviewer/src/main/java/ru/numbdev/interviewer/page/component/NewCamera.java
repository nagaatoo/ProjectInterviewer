package ru.numbdev.interviewer.page.component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Div;

//@JsModule("./src/main/js/Camera.js")
//@JavaScript("Camera.js")
public class NewCamera extends Div {

    public NewCamera(boolean isCamera) {
        Div videoDiv = new Div();
        videoDiv.setId(isCamera ? "videoDiv" : "companionDiv");
        add(videoDiv);

//        UI.getCurrent().getElement().executeJs("initWebRTC($0)", videoDiv.getId().get());
        if (isCamera) {
//            UI.getCurrent().getPage().addJavaScript("Camera.js");
//            UI.getCurrent().getPage()
//                    .executeJs(
//                            """
//                                    let localVideo = document.createElement('video');
//                                        localVideo.autoplay = true;
//                                        localVideo.muted = true;
//                                        document.getElementById("videoDiv").appendChild(localVideo);
//                                        let pc = new RTCPeerConnection();
//                                        let webSocket = new WebSocket('ws://localhost:8890/video');
//
//
//                                        pc.onicecandidate = event => {
//                                            if (event.candidate) {
//                                                webSocket.send(JSON.stringify({ event: 'candidate', data: event.candidate }));
//                                            }
//                                        };
//
//                                        webSocket.addEventListener("open", event => {
//                                        console.log("open web");
//                                        navigator.mediaDevices.getUserMedia({ video: true, audio: true })
//                                            .then(stream => {
//                                                console.log("offer track");
//                                                localVideo.srcObject = stream;
//                                                stream.getTracks().forEach(track => pc.addTrack(track, stream));
//                                                pc.createOffer().then(offer => {
//                                            pc.setLocalDescription(offer);
//                                            webSocket.send(JSON.stringify({ event: 'offer', data: pc.localDescription }));
//                                            })
//                                            .catch(error => console.error('Error accessing media devices.', error));
//
//                                        });
//                                        });
//                                        webSocket.onmessage = event => {
//                                            let message = JSON.parse(event.data);
//                                            if (message.type === 'offer') {
//                                                pc.setRemoteDescription(new RTCSessionDescription(message))
//                                                    .then(() => pc.createAnswer())
//                                                    .then(answer => pc.setLocalDescription(answer))
//                                                    .then(() => webSocket.send(JSON.stringify({ event: 'answer', data: pc.localDescription })));
//                                            } else if (message.type === 'answer') {
//                                                pc.setRemoteDescription(new RTCSessionDescription(message));
//                                            } else if (message.type === 'candidate') {
//                                                console.log("candidate message: " + message);
//                                                pc.addIceCandidate(new RTCIceCandidate(message.candidate));
//                                            }
//                                        };
//
//
//                                    """
//                    );
        } else {
            UI.getCurrent().getPage()
                    .executeJs(
                            """
                                    let localVideo = document.createElement('video');
                                        localVideo.autoplay = true;
                                        localVideo.muted = true;
                                        document.getElementById("companionDiv").appendChild(localVideo);
                                        let pc = new RTCPeerConnection();
                                        let webSocket = new WebSocket('ws://localhost:8890/video');
                                  
                                  
                                        webSocket.addEventListener("open", event => {
                                        console.log("tv open web");
                                        navigator.mediaDevices.getUserMedia({ video: true, audio: true })
                                            .then(stream => {
                                                console.log("tv offer track");
                                                localVideo.srcObject = stream;
                                                stream.getTracks().forEach(track => pc.addTrack(track, stream));
                                                pc.createOffer().then(offer => {
                                            pc.setLocalDescription(offer);
                                            webSocket.send(JSON.stringify({ event: 'offer', data: pc.localDescription }));
                                            })
                                            .catch(error => console.error('Error accessing media devices.', error));
                                        });
                                        });
                                        pc.ontrack = event => {
                                            console.log("tv ontrack: " + event.candidate);
                                            const video = new MediaStream([event.track]);
                                            localVideo.srcObject = video;
                                          };
                                        pc.onicecandidate = event => {
                                            console.log("tv candidate: ");
                                            if (event.candidate) {
                                                webSocket.send(JSON.stringify({ event: 'candidate', data: event.candidate }));
                                            }
                                        };          
                                        
                                         webSocket.onmessage = event => {
                                            let message = JSON.parse(event.data);
                                            if (message.type === 'offer') {
                                                console.log("tv offer: ");
                                                pc.setRemoteDescription(new RTCSessionDescription(message))
                                                    .then(() => pc.createAnswer())
                                                    .then(answer => pc.setLocalDescription(answer))
                                                    .then(() => webSocket.send(JSON.stringify({ event: 'answer', data: pc.localDescription })));
                                            } else if (message.type === 'answer') {
                                                console.log("tv answer: ");
                                                pc.setRemoteDescription(new RTCSessionDescription(message));
                                            } else if (message.type === 'candidate') {
                                                console.log("tv candidate: ");
                                                console.log("candidate message: " + message);
                                                pc.addIceCandidate(new RTCIceCandidate(message.candidate));
                                            }
                                        };                            
                                    """
                    );
        }
    }
}
