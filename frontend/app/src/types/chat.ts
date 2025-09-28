export interface ChatRoom {
  roomId: string;
  name: string;
  participants: string[];
}

export interface ChatMessage {
  type: 'ENTER' | 'TALK';
  roomId: string;
  sender: string;
  message: string;
}

export interface SignalMessage {
  type: 'offer' | 'answer' | 'candidate';
  sender: string;
  receiver: string;
  payload: any;
}
