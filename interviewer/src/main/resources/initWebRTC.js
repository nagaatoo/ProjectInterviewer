function initWebRTC(divId) {
    let localVideo = document.createElement('video');
    localVideo.autoplay = true;
    localVideo.muted = true;
    document.getElementById(divId).appendChild(localVideo);

    let pc = new RTCPeerConnection();

    navigator.mediaDevices.getUserMedia({ video: true, audio: true })
        .then(stream => {
            localVideo.srcObject = stream;
            stream.getTracks().forEach(track => pc.addTrack(track, stream));
        })
        .catch(error => console.error('Error accessing media devices.', error));

    pc.onicecandidate = event => {
        if (event.candidate) {
            webSocket.send(JSON.stringify({ type: 'candidate', candidate: event.candidate }));
        }
    };

    let webSocket = new WebSocket('ws://localhost:8890/video');
    webSocket.onmessage = event => {
        let message = JSON.parse(event.data);
        if (message.type === 'offer') {
            pc.setRemoteDescription(new RTCSessionDescription(message))
                .then(() => pc.createAnswer())
                .then(answer => pc.setLocalDescription(answer))
                .then(() => webSocket.send(JSON.stringify({ type: 'answer', answer: pc.localDescription })));
        } else if (message.type === 'answer') {
            pc.setRemoteDescription(new RTCSessionDescription(message));
        } else if (message.type === 'candidate') {
            pc.addIceCandidate(new RTCIceCandidate(message.candidate));
        }
    };

    pc.createOffer().then(offer => {
        pc.setLocalDescription(offer);
        webSocket.send(JSON.stringify({ type: 'offer', offer: pc.localDescription }));
    });
}


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

// let localVideo = document.createElement('video');
// localVideo.autoplay = true;
// localVideo.muted = true;
// document.getElementById("videoDiv").appendChild(localVideo);
//
// let pc = new RTCPeerConnection();
// let webSocket = new WebSocket('ws://localhost:8890/video');
// var stream = navigator.mediaDevices.getUserMedia({ video: true, audio: true });
// stream.then(stream => {
//
//     localVideo.srcObject = stream;
//     stream.getTracks().forEach(track => {pc.addTrack(track, stream);});
//     const recorder = new MediaRecorder(stream);
//     recorder.ondataavailable = event => {
//         console.log("ondataavailable");
//         console.log("send blob");
//
//         event.data.arrayBuffer().then(e => webSocket.send(e));
//
//     };
//     recorder.start();
//     setInterval(() => {
//         console.log("run");
//         recorder.requestData();
//     }, 5000);
// })
//     .catch(error => console.error('Error accessing media devices.', error));
//
//
// webSocket.addEventListener("open", (event) => {
//     pc.createOffer().then(offer => {
//         console.log("createOffer: " + offer);
//         pc.setLocalDescription(offer);
//
//
//         // fires every one second and passes an BlobEvent
//
//
//     });
// });
// pc.onicecandidate = event => {
//     console.log("onicecandidate: " + event.candidate);
//     if (event.candidate) {
//         console.log("onicecandidate send: ");
//         webSocket.send(JSON.stringify({ type: 'candidate', candidate: event.candidate }));
//     }
// };