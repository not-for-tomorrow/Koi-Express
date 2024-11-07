// src/websocket/websocketService.js
import SockJS from 'sockjs-client';
import {Stomp} from '@stomp/stompjs';

// Tạo kết nối SockJS
const socket = new SockJS('http://localhost:8080/ws'); // Điều chỉnh URL endpoint WebSocket
const stompClient = Stomp.over(socket);

export default stompClient;
