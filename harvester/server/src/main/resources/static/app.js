google.charts.load('current', {'packages':['gantt'], 'callback': initGantt});

var gantt;

function initGantt() {

    gantt = new google.visualization.Gantt(document.getElementById('tasks'));

    var data = new google.visualization.DataTable();
    data.addColumn('string', 'Task ID');
    data.addColumn('string', 'Task Name');
    data.addColumn('date', 'Start');
    data.addColumn('date', 'End');
    data.addColumn('number', 'Duration');
    data.addColumn('number', 'Percent');
    data.addColumn('string', 'Dep');

    data.addRows([
        ['1001', 'Session1', new Date(2019,3,15,0,0,0,0), new Date(2019,3,15,1,0,0,0), null, 10, null],
        ['1002', 'Session2', new Date(2019,3,15,0,0,0,0), new Date(2019,3,15,1,0,0,0), null, 10, null]
    ]);

    gantt.draw(data, {width: 600});

}

// var client;
//
// function connect() {
//
//     var socket = new SockJS('/harvester');
//     client = Stomp.over(socket);
//     client.connect({}, function (frame) {
//         client.subscribe('/topic/tasks', function (task) {
//             addTask(JSON.parse(task.body));
//         })
//     });
//
// }
//
// function addTask(task) {
//
//     var div = document.createElement('div');
//     div.appendChild(document.createTextNode(task.id));
//     div.appendChild(document.createTextNode(task.time));
//     div.appendChild(document.createTextNode(task.type));
//
//     var nodes = document.getElementById('nodes');
//     nodes.appendChild(div);
//
// }