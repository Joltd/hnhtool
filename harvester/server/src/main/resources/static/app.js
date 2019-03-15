'use strict';

// ##################################################
// #                                                #
// #  Logic                                         #
// #                                                #
// ##################################################

let tasks = [];
let ganttChart = gantt();

function handleTaskEvent(taskEvent) {
    if (taskEvent.type === 'START') {
        tasks.push({
            id: taskEvent.id,
            name: taskEvent.name,
            start: taskEvent.time,
            end: new Date().getTime(),
            inProgress: true
        })
    } else if (taskEvent.type === 'END') {
        let task = tasks.find(value => value.id === taskEvent.id);
        if (!task) {
            return
        }

        task.end = taskEvent.time;
        task.inProgress = false;
    }

    updateTasks();
}

function updateTasks() {
    for (const task of tasks) {
        if (task.inProgress) {
            task.end = new Date().getTime()
        }
    }
    d3.select('#tasks')
        .data([tasks])
        .call(ganttChart);
}

// ##################################################
// #                                                #
// #  Web Sockets                                   #
// #                                                #
// ##################################################

function connect() {

    let socket = new SockJS('/harvester');
    let client = Stomp.over(socket);
    client.connect(
        {},
        frame => client.subscribe(
            '/topic/tasks',
            ( task => handleTaskEvent( JSON.parse(task.body) ) )
        )
    );

}

// ##################################################
// #                                                #
// #  Gantt                                         #
// #                                                #
// ##################################################

function gantt() {

    let mainScale;
    let bandHeight = 20;
    let nameWidth = 200;
    let barPadding = 5;
    let offsetX = 0;
    let offsetY = 0;

    function chart(_selection) {
        _selection.each(function (data) {

            prepareScaleData(data);

            let selection = d3.select(this);
            let svgGroup = selection.selectAll('svg')
                .data([null]);
            svgGroup = svgGroup.merge(svgGroup.enter()
                .append('svg'))
                .attr('width', mainScale.range()[1])
                .attr('height', tasks.length * bandHeight);

            let barsGroup = svgGroup.selectAll('.bar')
                .data(data);
            barsGroup.exit().remove();
            barsGroup.merge(barsGroup.enter()
                .append('rect')
                .attr('class', 'bar'))
                .attr('x', d => mainScale(d.start) + barPadding)
                .attr('y', (d,i) => i * bandHeight + barPadding + bandHeight)
                .attr('width', d => mainScale(d.end) - mainScale(d.start) - barPadding * 2)
                .attr('height', () => bandHeight - barPadding * 2)
                .attr('rx', 5)
                .attr('ry', 5)
                .attr('fill', '#2196F3');

            let nameGroup = svgGroup.selectAll('.name')
                .data(data);
            nameGroup.exit().remove();
            nameGroup.merge(nameGroup.enter()
                .append('text')
                .attr('class', 'name'))
                .attr('x', () => 0)
                .attr('y', (d,i) => i * bandHeight + (bandHeight * 2 / 3) + bandHeight)
                .text(d => d.name);

        });
    }

    return chart;

    function prepareScaleData(data) {
        let domainFrom = d3.min(data.map(function (value) { return value.start; }));
        let domainTo = d3.max(data.map(function (value) { return value.end; }));
        let rangeFrom = nameWidth;
        let rangeTo = nameWidth + (domainTo - domainFrom) / 1000 * 100;

        mainScale = d3.scaleLinear([domainFrom, domainTo], [rangeFrom, rangeTo]);
    }

}

// [
//     {id,name,start,end},
//     {id,name,start,end}
// ]