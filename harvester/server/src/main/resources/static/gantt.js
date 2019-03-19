// todo y scroll - wrong behavior when too low task count (blinking)
// todo x scroll - when timeline is to big slider penetrates right side of the chart
// todo design for remove button
// todo implementation of buttons panel
// todo implementation of resize
// todo deference with colors by task type

function Gantt(selector) {

    let PIXEL_PER_SECOND = 50;
    let BAND_SIZE = 20;
    let AXIS_HEIGHT = 30;
    let AXIS_TICK_STEP = 5000; // msec
    let TASK_LIST_WIDTH = 100;
    let TASK_LIST_PADDING = 5;
    let TASK_LIST_REMOVE_SIZE = 15;
    let BAR_RADIUS = 5;
    let BAR_PADDING = 5;
    let SCROLL_THICKNESS = 10;
    let TIME_FORMAT = new Intl.DateTimeFormat(
        'ru-RU',
        {
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
        }
    );

    let svgGroup = d3.select(selector)
        .selectAll('svg')
        .data([null]);
    let _selection = svgGroup.merge(svgGroup.enter().append('svg'))
        .attr('style','width:100%; height:100%');

    let _gantt = gantt();
    let _tasks = [];

    function clear() {
        _tasks = [];
        refresh();
    }

    function setTasks(tasks) {
        _tasks = tasks;
        refresh();
    }

    function addTask(task) {
        if (!task.end) {
            task.end = new Date().getTime();
            task.inProgress = true;
        }
        _tasks.push(task);
        refresh();
    }

    function updateTask(task) {
        let foundTask = _tasks.find(_task => _task.id === task.id);
        if (!foundTask) {
            return;
        }
        foundTask.end = task.end;
        foundTask.inProgress = false;
        refresh();
    }

    function removeTask(id) {
        let index = _tasks.findIndex(task => task.id === id);
        if (index < 0) {
            return;
        }
        _tasks = _tasks.splice(index, 1);
        refresh();
    }

    function refresh() {
        for (const task of _tasks) {
            if (task.inProgress) {
                task.end = new Date().getTime();
            }
        }
        _selection.data([_tasks])
            .call(_gantt);
    }

    return {
        refresh,
        clear,
        setTasks,
        addTask,
        updateTask,
        removeTask
    };

    // ##################################################
    // #                                                #
    // #  Gantt                                         #
    // #                                                #
    // ##################################################

    function gantt() {

        let _mainScale = d3.scaleLinear().clamp(true);
        let _xOffset = observable();
        let _yOffset = observable();

        let _viewport = viewport(_mainScale, _xOffset, _yOffset);
        let _xScroll = scroll(_xOffset).orientation('HORIZONTAL');
        let _yScroll = scroll(_yOffset).orientation('VERTICAL');
        let _taskList = taskList(() => {}, _yOffset);
        let _timeAxis = timeAxis(_mainScale, _xOffset);
        let _buttonPanel = buttonPanel();

        function visual(_selection) {
            _selection.each(function (data) {

                let svgSelection = d3.select(this);

                let svgBox = svgSelection.node().getBoundingClientRect();
                let chartData = prepareData(data, svgBox);
                svgSelection.datum(chartData);

                _xOffset.removeListeners();
                _xOffset.setMaxValue(chartData.x.fullSize - chartData.x.viewportSize);
                _yOffset.removeListeners();
                _yOffset.setMaxValue(chartData.y.fullSize - chartData.y.viewportSize);

                svgSelection.call(_viewport)
                    .call(_timeAxis)
                    .call(_taskList)
                    .call(_buttonPanel);

                groupBuilder(svgSelection, '.x-scroll-group')
                    .style('x-scroll-group')
                    .build()
                    .attr('transform', 'translate(' + TASK_LIST_WIDTH + ',' + (svgBox.height - SCROLL_THICKNESS) + ')')
                    .datum(chartData.x)
                    .call(_xScroll);

                groupBuilder(svgSelection, '.y-scroll-group')
                    .style('y-scroll-group')
                    .build()
                    .attr('transform', 'translate(' + (svgBox.width - SCROLL_THICKNESS) + ',' + AXIS_HEIGHT + ')')
                    .datum(chartData.y)
                    .call(_yScroll);

            });
        }

        return visual;

        function prepareData(data, svgBox) {

            let timeFrom = d3.min(data.map(d => d.start));
            let timeTo = d3.max(data.map(d => d.end));
            let rangeDelta = timeTo - timeFrom;
            let rangeTo = rangeDelta / 1000 * PIXEL_PER_SECOND;

            _mainScale.domain([timeFrom, timeTo])
                .range([0, rangeTo]);

            let result = {};
            result.tasks = data;

            result.x = {
                svgSize: svgBox.width,
                fullSize: rangeTo,
                viewportSize: svgBox.width - TASK_LIST_WIDTH - SCROLL_THICKNESS
            };
            result.y = {
                svgSize: svgBox.height,
                fullSize: BAND_SIZE * result.tasks.length,
                viewportSize: svgBox.height - AXIS_HEIGHT - SCROLL_THICKNESS
            };

            result.ticks = [];
            let tickFrom = AXIS_TICK_STEP * Math.round(timeFrom / AXIS_TICK_STEP);
            let tickTo = AXIS_TICK_STEP * Math.round(timeTo / AXIS_TICK_STEP) + AXIS_TICK_STEP;
            for (let step = tickFrom; step <= tickTo; step = step + AXIS_TICK_STEP) {
                result.ticks.push({
                    time: step
                })
            }

            return result;

        }

    }

    // ##################################################
    // #                                                #
    // #  Button panel                                  #
    // #                                                #
    // ##################################################

    function buttonPanel() {

        function visual(_selection) {
            _selection.each(function (data) {

                let buttonPanelGroup = groupBuilder(d3.select(this), '.button-panel')
                    .style('button-panel')
                    .build();

                groupBuilder(buttonPanelGroup, 'rect')
                    .tag('rect')
                    .data([data])
                    .build()
                    .attr('x', 0)
                    .attr('y', 0)
                    .attr('width', TASK_LIST_WIDTH)
                    .attr('height', AXIS_HEIGHT)
                    .attr('fill', 'white');

            });
        }

        return visual;
    }

    // ##################################################
    // #                                                #
    // #  Viewport                                      #
    // #                                                #
    // ##################################################

    // [{start, end}]
    function viewport(mainScale, xOffsetProperty, yOffsetProperty) {

        let x = 0;
        let y = 0;

        function visual(_selection) {
            _selection.each(function (data) {

                let root = d3.select(this)
                    .selectAll('.viewport-group')
                    .data([data]);
                let rootEnter = root.enter()
                    .append('g')
                    .attr('class', 'viewport-group');
                rootEnter.append('rect')
                    .attr('x', 0)
                    .attr('y', 0)
                    .attr('width', data.x.viewportSize)
                    .attr('height', data.y.viewportSize)
                    .attr('fill', 'white')
                    .on('wheel', d => yOffsetProperty.accumulateValue(-d3.event.wheelDelta));
                root = root.merge(rootEnter)
                    .attr('transform', 'translate(' + TASK_LIST_WIDTH + ',' + AXIS_HEIGHT + ')');

                let viewportGroup = groupBuilder(root, '.viewport')
                    .style('viewport')
                    .build();

                groupBuilder(viewportGroup, '.bar')
                    .data(data.tasks)
                    .tag('rect')
                    .style('bar')
                    .build()
                    .attr('pointer-events','none')
                    .attr('x', d => mainScale(d.start) + BAR_PADDING)
                    .attr('y', (d, i) => BAND_SIZE * i + BAR_PADDING)
                    .attr('width', d => {
                        let width = mainScale(d.end) - mainScale(d.start) - 2 * BAR_PADDING;
                        return width < 10 ? 10 : width;
                    })
                    .attr('height', BAND_SIZE - 2 * BAR_PADDING)
                    .attr('rx', BAR_RADIUS)
                    .attr('ry', BAR_RADIUS);

                xOffsetProperty.addOnChange(newPosition => {
                    x = -newPosition;
                    viewportGroup.attr('transform','translate('+x+','+y+')');
                });

                yOffsetProperty.addOnChange(newPosition => {
                    y = -newPosition;
                    viewportGroup.attr('transform','translate('+x+','+y+')');
                });

            });
        }

        return visual;

    }

    // ##################################################
    // #                                                #
    // #  Scroll                                        #
    // #                                                #
    // ##################################################

    // {fullSize,viewportSize}
    function scroll(offsetProperty) {

        let _orientation;
        let _orientationConfig;

        let _scrollScale;
        let _slider;

        function visual(_selection) {
            _selection.each(function (data) {

                let scrollData = prepareScrollSliderData(data);
                _scrollScale = d3.scaleLinear()
                    .domain([0, data.viewportSize])
                    .range([0, data.fullSize])
                    .clamp(true);

                let root = d3.select(this)
                    .selectAll('.scroll-root')
                    .data(scrollData);
                root.exit().remove();
                let rootGroupEnter = root.enter()
                    .append('g')
                    .attr('class', 'scroll-root');
                rootGroupEnter.append('rect')
                    .attr('x', 0)
                    .attr('y', 0)
                    .attr(_orientationConfig.sliderThicknessSide, SCROLL_THICKNESS)
                    .attr(_orientationConfig.sliderSizeSide, data.viewportSize)
                    .attr('fill', 'white')
                    .on('click', () => handleClick(d3.mouse(this)));
                root = root.merge(rootGroupEnter);

                _slider = groupBuilder(root, '.scroll-slider')
                    .data(scrollData)
                    .tag('rect')
                    .style('scroll-slider')
                    .build()
                    .attr('x', 0)
                    .attr('y', 0)
                    .attr(_orientationConfig.sliderThicknessSide, SCROLL_THICKNESS)
                    .attr(_orientationConfig.sliderSizeSide, d => d.scrollSize)
                    .call(d3.drag().on('drag', () => handleDrag()));

                // may lead from handleDrag()
                offsetProperty.addOnChange(newViewportPosition => {
                    let newSliderPosition = _scrollScale.invert(newViewportPosition);
                    _slider.attr(_orientationConfig.position, newSliderPosition);
                })

            });
        }

        visual.orientation = orientation;
        return visual;

        function orientation(_) {
            _orientation = _;
            _orientationConfig = _ === 'HORIZONTAL'
                ? {
                    sliderThicknessSide: 'height',
                    sliderSizeSide: 'width',
                    position: 'x',
                    positionDelta: () => d3.event.dx,
                    mouseEvent: event => event[0]
                }
                : {
                    sliderThicknessSide: 'width',
                    sliderSizeSide: 'height',
                    position: 'y',
                    positionDelta: () => d3.event.dy,
                    mouseEvent: event => event[1]
                };
            return this;
        }

        function prepareScrollSliderData(data) {
            let relativeSize = data.viewportSize / data.fullSize;
            if (relativeSize >= 1) {
                return [];
            }

            return [{
                scrollSize: data.viewportSize * (relativeSize < 0.2
                    ? 0.2
                    : relativeSize),
                viewportSize: data.viewportSize,
                fullSize: data.fullSize,
            }];
        }

        function handleDrag() {
            let newSliderPosition = +_slider.attr(_orientationConfig.position)
                + _orientationConfig.positionDelta();
            let newViewportPosition = _scrollScale(newSliderPosition);
            offsetProperty.setValue(newViewportPosition);
        }

        function handleClick(event) {
            let newViewportPosition = _scrollScale(_orientationConfig.mouseEvent(event));
            offsetProperty.setValue(newViewportPosition);
        }

    }

    // ##################################################
    // #                                                #
    // #  Time Axis                                     #
    // #                                                #
    // ##################################################

    // {
    //  ticks: [{time}],
    //  tasks: <number>
    // }
    function timeAxis(mainScale, offsetProperty) {

        function visual(_selection) {
            _selection.each(function (data) {

                let root = d3.select(this)
                    .selectAll('.time-axis-group')
                    .data([data]);
                let rootEnter = root.enter()
                    .append('g')
                    .attr('class', 'time-axis-group');
                rootEnter.append('rect')
                    .attr('x', 0)
                    .attr('y', 0)
                    .attr('width', data.x.viewportSize + SCROLL_THICKNESS)
                    .attr('height', AXIS_HEIGHT)
                    .attr('fill', 'white')
                    .on('wheel', d => offsetProperty.accumulateValue(-d3.event.wheelDelta));
                root = root.merge(rootEnter)
                    .attr('transform', 'translate(' + TASK_LIST_WIDTH + ',0)');

                let timeAxisGroup = groupBuilder(root, '.time-axis')
                    .style('time-axis')
                    .build();

                initLines(timeAxisGroup, data);
                initLabels(timeAxisGroup, data);

                offsetProperty.addOnChange(newPosition => {
                    timeAxisGroup.attr('transform', 'translate(' + (-newPosition) + ',0)')
                })

            });
        }

        return visual;

        function initLines(rootGroup, data) {
            let linesGroup = groupBuilder(rootGroup, '.lines')
                .style('lines')
                .build();

            groupBuilder(linesGroup, 'line')
                .data(data.ticks)
                .tag('line')
                .build()
                .attr('x1', d => mainScale(d.time))
                .attr('y1', AXIS_HEIGHT)
                .attr('x2', d => mainScale(d.time))
                .attr('y2', data.tasks.length * BAND_SIZE + AXIS_HEIGHT)
                .attr('stroke', 'black')
                .attr('pointer-events','none');
        }

        function initLabels(rootGroup, data) {
            let labelsGroup = groupBuilder(rootGroup, '.labels')
                .style('labels')
                .build();

            groupBuilder(labelsGroup, 'text')
                .data(data.ticks)
                .tag('text')
                .build()
                .attr('x', d => mainScale(d.time))
                .attr('y', AXIS_HEIGHT/2)
                .text(d => formatTime(d.time))
                .attr('pointer-events','none');

        }

        function formatTime(value) {
            let date = new Date(value);
            return TIME_FORMAT.format(date);
        }

    }

    // ##################################################
    // #                                                #
    // #  Task List                                     #
    // #                                                #
    // ##################################################

    // [{id,name}]
    function taskList(onTaskRemove, offsetProperty) {

        function visual(_selection) {
            _selection.each(function (data) {

                let root = d3.select(this)
                    .selectAll('.task-list-group')
                    .data([data]);
                let rootEnter = root.enter()
                    .append('g')
                    .attr('class', 'task-list-group');
                rootEnter.append('rect')
                    .attr('x', 0)
                    .attr('y', 0)
                    .attr('width', TASK_LIST_WIDTH)
                    .attr('height', data.y.viewportSize + SCROLL_THICKNESS)
                    .attr('fill', 'white');
                root = root.merge(rootEnter)
                    .attr('transform', 'translate(0,' + AXIS_HEIGHT + ')');

                let taskListGroup = groupBuilder(root, '.task-list')
                    .style('task-list')
                    .build();

                let taskGroup = taskListGroup.selectAll('.task')
                    .data(data.tasks);
                let taskGroupEnter = taskGroup.enter()
                    .append('g')
                    .attr('class', 'task');
                taskGroupEnter.append('text')
                    .text(d => d.name)
                    .attr('x', TASK_LIST_PADDING)
                    .attr('y', (d, i) => (i + 1) * BAND_SIZE - BAND_SIZE/2)
                    .attr('pointer-events','none');
                taskGroupEnter.append('rect')
                    .attr('x', TASK_LIST_WIDTH - TASK_LIST_PADDING - TASK_LIST_REMOVE_SIZE)
                    .attr('y', (d, i) => i * BAND_SIZE + BAND_SIZE/2 - TASK_LIST_REMOVE_SIZE/2)
                    .attr('width', TASK_LIST_REMOVE_SIZE)
                    .attr('height', TASK_LIST_REMOVE_SIZE)
                    .on('click', d => onTaskRemove(d.id));
                taskGroup.exit().remove();
                taskGroup.merge(taskGroupEnter);

                offsetProperty.addOnChange(newPosition => {
                    taskListGroup.attr('transform', 'translate(0,' + (-newPosition) + ')')
                })

            });
        }

        return visual;

    }

    // ##################################################
    // #                                                #
    // #  Observable                                    #
    // #                                                #
    // ##################################################

    function observable() {

        let _value = 0;
        let _maxValue = 0;
        let _listeners = [];

        return {
            addOnChange,
            accumulateValue,
            setValue,
            setMaxValue,
            getValue,
            removeListeners
        };

        function addOnChange(onChange) {
            if (!onChange) {
                return;
            }

            _listeners.push(onChange);
        }

        function accumulateValue(value) {
            setValue(getValue() + value);
        }

        function setValue(value) {
            value = limitValue(value);
            if (value === _value) {
                return;
            }
            _value = value;
            for (const listener of _listeners) {
                listener(value);
            }
        }

        function setMaxValue(maxValue) {
            _maxValue = maxValue;
        }

        function removeListeners() {
            _listeners = [];
        }

        function getValue() {
            return _value;
        }

        function limitValue(value) {
            if (value < 0) {
                return 0;
            } else if (value > _maxValue) {
                return _maxValue;
            } else {
                return value;
            }
        }

    }

    // ##################################################
    // #                                                #
    // #  Group builder                                 #
    // #                                                #
    // ##################################################

    function groupBuilder(selection, selector) {

        let _data = [null];
        let _getKey = (d,i) => i;
        let _tag = 'g';
        let _style = null;

        return {
            data,
            tag,
            style,
            build
        };

        function build() {

            let group = selection.selectAll(selector)
                .data(_data, _getKey);
            let groupEnter = group.enter()
                .append(_tag);

            if (_style) {
                groupEnter.attr('class', _style);
            }

            group.exit().remove();

            return group.merge(groupEnter);

        }

        /**
         * Specify data which will be joined to selection and optional data key function.
         * Default is a [null] and getKey is depend on data entry index.
         */
        function data(data, getKey) {
            _data = data;
            if (getKey) {
                _getKey = getKey;
            }
            return this;
        }

        /**
         * Specify 'tag' which will be appended for new join entries. Default is a 'g'
         */
        function tag(_) {
            _tag = _;
            return this;
        }

        /**
         * All new appended elements will be marked with specified style class
         */
        function style(_) {
            _style = _;
            return this;
        }

    }

}