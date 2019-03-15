'use strict';

// ##################################################
// #                                                #
// #  Logic                                         #
// #                                                #
// ##################################################

// let tasks = [];
// let ganttChart = gantt();

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

    let data = [
        {x: 0, width: 200, color: '#00FF00'},
        {x: 200, width: 200, color: '#0000FF'},
        {x: 400, width: 200, color: '#FF0000'},
        {x: 600, width: 200, color: '#FFF000'},
        {x: 800, width: 200, color: '#00FFFF'}
    ];

    let svgGroup = d3.selectAll('svg')
        .data([null]);
    svgGroup = svgGroup.merge(svgGroup.enter()
        .append('svg'))
        .attr('width', 400)
        .attr('height', 400);

    let paneGroup = svgGroup.selectAll('.pane')
        .data([{x:0,y:0}]);
    paneGroup = paneGroup.merge(paneGroup.enter().append('g').attr('class', 'pane'));

    let blockGroup = paneGroup.selectAll('.block')
        .data(data);
    blockGroup.merge(blockGroup.enter().append('rect').attr('class', '.block'))
        .attr('x', d => d.x)
        .attr('y', d => d.x)
        .attr('width', d => d.width)
        .attr('height', d => d.width)
        .attr('fill', d => d.color);

    let scrollBarX = scroll()
        .orientation('HORIZONTAL')
        .onScroll(d => {
            let offset = paneGroup.datum();
            offset.x = -d;
            paneGroup.attr('transform', 'translate(' + offset.x + ',' + offset.y + ')')
        });
    let scrollXGroup = svgGroup.selectAll('.scroll-x')
        .data([{
            viewportSize: 400,
            fullSize: 1000
        }]);
    scrollXGroup.merge(scrollXGroup.enter().append('g').attr('class', 'scroll-x'))
        .call(scrollBarX);

    let scrollBarY = scroll()
        .orientation('VERTICAL')
        .onScroll(d => {
            let offset = paneGroup.datum();
            offset.y = -d;
            paneGroup.attr('transform', 'translate(' + offset.x + ',' + offset.y + ')')
        });
    let scrollYGroup = svgGroup.selectAll('.scroll-y')
        .data([{
            viewportSize: 400,
            fullSize: 1000
        }]);
    scrollYGroup.merge(scrollYGroup.enter().append('g').attr('class', 'scroll-y'))
        .call(scrollBarY);

    // let chart = dragable();

    //
    // d3.select('#tasks')
    //     .data([data])
    //     .call(chart);

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

function scroll() {

    let _orientationConfig;
    let _orientation;
    let _onScroll;
    let _scrollThickness = 10;
    let _scrollScale;
    let _slider;

    orientation('VERTICAL');

    function visual(_selection) {
        _selection.each(function (data) {

            prepareScrollScale(data);
            let scrollData = prepareScrollSliderData(data);

            let selection = d3.select(this);

            let rootGroup = selection.selectAll('.scroll-root')
                .data(scrollData);
            rootGroup.exit().remove();
            rootGroup = rootGroup.merge(rootGroup.enter()
                .append('g')
                .attr('class', 'scroll-root'));

            let sliderGroup = rootGroup.selectAll('.scroll-slider')
                .data(scrollData);
            sliderGroup.exit().remove();
            _slider = sliderGroup.merge(sliderGroup.enter()
                .append('rect')
                .attr('class', 'scroll-slider'))
                .attr('x', 0)
                .attr('y', 0)
                .attr(_orientationConfig.sliderThicknessSide, _scrollThickness)
                .attr(_orientationConfig.sliderSizeSide, d => d.scrollSize)
                .call(d3.drag().on('drag', () => handleDrag(_orientationConfig.positionDelta())));

        });
    }

    visual.orientation = orientation;
    visual.onScroll = onScroll;
    return visual;

    function orientation(_) {
        _orientation = _;
        _orientationConfig = _ === 'HORIZONTAL'
            ? {
                sliderThicknessSide: 'height',
                sliderSizeSide: 'width',
                position: 'x',
                positionDelta: () => d3.event.dx
            }
            : {
                sliderThicknessSide: 'width',
                sliderSizeSide: 'height',
                position: 'y',
                positionDelta: () => d3.event.dy
            };
        return this;
    }

    function onScroll(_) {
        _onScroll = _;
        return this;
    }

    function prepareScrollSliderData(data) {
        let relativeSize = data.viewportSize / data.fullSize;
        if (relativeSize >= 1) {
            return [];
        }

        return [{
            scrollSize: data.viewportSize * (data.viewportSize / data.fullSize < 0.2
                ? 0.2
                : data.viewportSize / data.fullSize),
            viewportSize: data.viewportSize,
            fullSize: data.fullSize,
        }];
    }

    function prepareScrollScale(data) {
        _scrollScale = d3.scaleLinear()
            .domain([0, data.viewportSize])
            .range([0, data.fullSize]);
    }

    function handleDrag(delta) {
        let data = _slider.datum();
        let newPosition = +_slider.attr(_orientationConfig.position) + delta;
        if (newPosition < 0) {
            newPosition = 0;
        } else if (newPosition + data.scrollSize > data.viewportSize) {
            newPosition = data.viewportSize - data.scrollSize;
        }
        _slider.attr(_orientationConfig.position, newPosition);
        if (_onScroll) {
            _onScroll(_scrollScale(newPosition));
        }
    }

}