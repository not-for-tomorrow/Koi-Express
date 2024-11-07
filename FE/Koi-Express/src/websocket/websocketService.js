import SockJS from 'sockjs-client';
import {Stomp} from '@stomp/stompjs';

function initializeStompClient(token) {

    const socket = new SockJS('http://localhost:8080/ws');
    const stompClient = Stomp.over(socket);

    stompClient.connect(
        {Authorization: `Bearer ${token}`},
        (frame) => {
            console.log('Connected: ' + frame);

        },
        (error) => {
            console.error('STOMP error: ', error);
        }
    );

    return stompClient;
}

export default initializeStompClient;
