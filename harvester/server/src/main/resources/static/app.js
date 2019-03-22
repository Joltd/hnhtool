'use strict';

// ##################################################
// #                                                #
// #  Web Sockets                                   #
// #                                                #
// ##################################################

function connect() {

    let data = [
        {id: 1001, name: 'Test#1', start: 15679764, end: 15681764},
        {id: 1002, name: 'Test#2', start: 15679889, end: 15682100},
        {id: 1003, name: 'Test#3', start: 15682100, end: 15684100},
        {id: 1005, name: 'Test#4', start: 15665100, end: 15680000},
        {id: 1006, name: 'Test#5', start: 15683500, end: 15684100},
        {id: 1007, name: 'Test#6', start: 15681500, end: 15751700},
        {id: 1008, name: 'Test#7', start: 15682500, end: 15703000}
    ];

    let chart = Gantt('#tasks');
    chart.setTasks(data);

    // setInterval(() => {
    //     let rnd = Math.random();
    //     if (rnd < 0.3) {
    //         // console.log('add');
    //         chart.addTask({
    //             id: Math.floor(Math.random() * 2000 + 1010),
    //             name: 'Test#'+(Math.floor(Math.random() * 50 + 10)),
    //             start: new Date().getTime()
    //         });
    //     } else if (rnd < 0.7) {
    //         // console.log('update');
    //         chart.updateTask({
    //             id: Math.floor(Math.random() * 2000 + 1010),
    //             end: new Date().getTime()
    //         });
    //     } else {
    //         // console.log('remove');
    //         chart.removeTask(Math.floor(Math.random() * 2000));
    //     }
    // }, 2000);


    // let socket = new SockJS('/harvester');
    // let client = Stomp.over(socket);
    // client.connect(
    //     {},
    //     frame => client.subscribe(
    //         '/topic/tasks',
    //         ( task => handleTaskEvent( JSON.parse(task.body) ) )
    //     )
    // );

}