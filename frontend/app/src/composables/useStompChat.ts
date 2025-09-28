import { ref, onUnmounted, type Ref } from 'vue';
import SockJS from 'sockjs-client';
import { Client, type IFrame, type IMessage } from '@stomp/stompjs';
import type { ChatMessage, SignalMessage } from '@/types/chat';

export function useStompChat(
  roomId: string,
  sender: Ref<string>,
  receiver: Ref<string>,
  handleSignal: (signal: SignalMessage) => void
) {
  const messages = ref<ChatMessage[]>([]);
  const stompClient = ref<Client | null>(null);
  const isConnected = ref(false);

  const connect = () => {
    const socket = new SockJS('http://localhost:8080/ws');
    const client = new Client({
      webSocketFactory: () => socket,
      debug: (str) => console.log(new Date(), str),
      connectHeaders: {
        username: sender.value,
      },
      onConnect: (frame: IFrame) => {
        console.log('STOMP connection successful:', frame);
        isConnected.value = true;

        // Subscribe to public chat messages
        client.subscribe(`/topic/chat/room/${roomId}`, (message: IMessage) => {
          const receivedMessage: ChatMessage = JSON.parse(message.body);
          messages.value.push(receivedMessage);
        });

        // Subscribe to WebRTC signaling messages on a public topic
        client.subscribe(`/topic/signal/${sender.value}`, (message: IMessage) => {
          const signal: SignalMessage = JSON.parse(message.body);
          handleSignal(signal);
        });

        // Send enter message
        const enterMessage: ChatMessage = {
          type: 'ENTER',
          roomId: roomId,
          sender: sender.value,
          message: ''
        };
        client.publish({
          destination: '/app/chat/message',
          body: JSON.stringify(enterMessage),
        });
      },
      onStompError: (frame: IFrame) => {
        console.error('STOMP error:', frame);
        isConnected.value = false;
      },
      onDisconnect: () => {
        console.log('STOMP disconnected');
        isConnected.value = false;
      }
    });

    client.activate();
    stompClient.value = client;
  };

  const disconnect = () => {
    if (stompClient.value) {
      stompClient.value.deactivate();
    }
  };

  const sendMessage = (message: string) => {
    if (isConnected.value && message.trim()) {
      const chatMessage: ChatMessage = {
        type: 'TALK',
        roomId: roomId,
        sender: sender.value,
        message: message,
      };
      stompClient.value?.publish({
        destination: '/app/chat/message',
        body: JSON.stringify(chatMessage),
      });
    }
  };

  const sendSignal = (type: 'offer' | 'answer' | 'candidate', payload: any) => {
    if (isConnected.value && receiver.value) {
      const signalMessage: SignalMessage = {
        type,
        sender: sender.value,
        receiver: receiver.value,
        payload,
      };
      console.log(`>>> Sending signal: ${type}`, signalMessage);
      stompClient.value?.publish({
        destination: '/app/chat/signal',
        body: JSON.stringify(signalMessage),
      });
    }
  };

  onUnmounted(() => {
    disconnect();
  });

  return {
    messages,
    isConnected,
    connect,
    sendMessage,
    sendSignal,
  };
}
