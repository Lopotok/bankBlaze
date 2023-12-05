let stompClient = null;
let socket = new SockJS('/bankBlaze-websocket');
let id;

function connect() {
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/topic/app', function (desk) {
            sendRefresh(JSON.parse(desk.body));
            clickOnIt();
            flashing(JSON.parse(desk.body));
        });
    });

}

function sendRefresh(desk) {
    id = "#queueNumber" + desk.id;
    $(id).text(desk.queueNumber.number);
}

function clickOnIt() {
    document.querySelector('#notifyButton').click();
}

function notify() {
    let audio = document.getElementById('notify');
    let promise = audio.play();
    if (promise) {
        promise.catch(function (error) {
            console.error(error);
        });
    }
}

function flashing(desk) {
    let currentNumber = document.getElementById('queueNumber' + desk.id);
    let currentDesk = document.getElementById('desk' + desk.id);
    currentNumber.classList.add('flash');
    currentDesk.classList.add('flash')
    setTimeout(function () {
        currentNumber.classList.remove('flash')
        currentDesk.classList.remove('flash')
    }, 4000);
}