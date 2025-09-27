import { ref, type Ref } from 'vue';
import type { SignalMessage } from '@/types/chat';

export function useWebRTC(sendSignal: (type: 'offer' | 'answer' | 'candidate', data: any) => void) {
  const localVideo = ref<HTMLVideoElement | null>(null);
  const remoteVideo = ref<HTMLVideoElement | null>(null);
  const localStream = ref<MediaStream | null>(null);
  const peerConnection = ref<RTCPeerConnection | null>(null);
  const isMuted = ref(false);

  const createPeerConnection = () => {
    const pc = new RTCPeerConnection({
      iceServers: [
        {
          urls: [
            "stun:stun.l.google.com:19302",
            "stun:stun1.l.google.com:19302",
          ],
        },
      ],
    });

    pc.onicecandidate = (event) => {
      if (event.candidate) {
        sendSignal('candidate', event.candidate);
      }
    };

    pc.ontrack = (event) => {
      if (remoteVideo.value) {
        remoteVideo.value.srcObject = event.streams[0];
      }
    };

    if (localStream.value) {
      localStream.value.getTracks().forEach(track => {
        pc.addTrack(track, localStream.value!);
      });
    }

    peerConnection.value = pc;
  };

  const getMediaStream = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({
        video: true,
        audio: true,
      });
      localStream.value = stream;
      if (localVideo.value) {
        localVideo.value.srcObject = stream;
      }
    } catch (error) {
      console.error("Error accessing media devices.", error);
      alert("카메라/마이크에 접근할 수 없습니다. 권한을 확인해주세요.");
    }
  };

  const initiateCall = async () => {
    await getMediaStream();
    createPeerConnection();

    if (peerConnection.value) {
      try {
        const offer = await peerConnection.value.createOffer();
        await peerConnection.value.setLocalDescription(offer);
        sendSignal('offer', offer);
      } catch (error) {
        console.error("Error creating offer:", error);
      }
    }
  };

  const handleSignal = async (signal: SignalMessage) => {
    if (!peerConnection.value || !signal.data) {
        // Ensure we have a peer connection and data before proceeding
        if (signal.type === 'offer') {
            await getMediaStream();
            createPeerConnection();
        } else {
            console.warn('Peer connection not established or signal data missing, ignoring signal:', signal.type);
            return;
        }
    }

    // Ensure peerConnection is available after potential creation
    const pc = peerConnection.value!;

    try {
        switch (signal.type) {
            case 'offer':
                await pc.setRemoteDescription(new RTCSessionDescription(signal.data));
                const answer = await pc.createAnswer();
                await pc.setLocalDescription(answer);
                sendSignal('answer', answer);
                break;
            case 'answer':
                await pc.setRemoteDescription(new RTCSessionDescription(signal.data));
                break;
            case 'candidate':
                // Add candidate only if remote description is set
                if (pc.remoteDescription) {
                    await pc.addIceCandidate(new RTCIceCandidate(signal.data));
                } else {
                    console.warn('Remote description not set, ignoring ICE candidate.');
                }
                break;
        }
    } catch (error) {
        console.error('Error handling signal:', signal.type, error);
    }
  };

  const toggleMute = () => {
    if (localStream.value) {
      localStream.value.getAudioTracks().forEach(track => {
        track.enabled = !track.enabled;
      });
      isMuted.value = !isMuted.value;
      console.log(isMuted.value ? "Muted" : "Unmuted");
    }
  };

  const hangUp = () => {
    if (peerConnection.value) {
      peerConnection.value.close();
      peerConnection.value = null;
    }
    if (localStream.value) {
      localStream.value.getTracks().forEach(track => track.stop());
      localStream.value = null;
    }
    if (localVideo.value) {
      localVideo.value.srcObject = null;
    }
    if (remoteVideo.value) {
      remoteVideo.value.srcObject = null;
    }
    console.log("Call ended.");
  };

  return {
    localVideo,
    remoteVideo,
    localStream,
    peerConnection,
    isMuted,
    initiateCall,
    handleSignal,
    toggleMute,
    hangUp,
  };
}
