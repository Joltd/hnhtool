function Gantt(selector) {

    let svgGroup = d3.select(selector)
        .selectAll('svg')
        .data([null]);
    let _selection = svgGroup.merge(svgGroup.enter().append('svg'));

    let _gantt = gantt();

    function refresh(data) {
        _selection.data([data])
            .call(_gantt);
    }

    return {
        refresh
    };

    // ##################################################
    // #                                                #
    // #  Gantt                                         #
    // #                                                #
    // ##################################################

    function gantt() {

        let _mainScale = d3.scaleLinear();
        let _xOffset = observable();
        let _yOffset = observable();

        let _viewport = viewport(_mainScale);
        let _xScroll = scroll(_xOffset);
        let _yScroll = scroll(_yOffset);
        let _taskList = taskList(() => {});
        let _timeAxis = timeAxis(_mainScale);

        function visual(_selection) {
            _selection.each(function (data) {
                let svgNodeBox = d3.select(this).node().getBoundingClientRect();

            });
        }

        return visual;

        function prepareData() {

        }

    }

    // ##################################################
    // #                                                #
    // #  Data model                                    #
    // #                                                #
    // ##################################################

    function dataModel() {

        let data = {};

        return {
            removeTask
        };

        // [{id,name,start,end}]
        function fullRefresh(tasks) {
            data.tasks = tasks;
            data.sizes
        }

        function removeTask(id) {

        }

    }

    // ##################################################
    // #                                                #
    // #  Viewport                                      #
    // #                                                #
    // ##################################################

    // [{start, end}]
    function viewport(mainScale) {

        let _bandSize = 30;

        function visual(_selection) {
            _selection.each(function (data) {

                let _selection = d3.select(this);
                let viewportGroup = _selection.selectAll('.viewport')
                    .data([null]);

                viewportGroup = viewportGroup.merge(viewportGroup.enter()
                    .append('g')
                    .attr('class', 'viewport'));

                let barGroup = viewportGroup.selectAll('.bar')
                    .data(data);

                barGroup.exit().remove();

                barGroup.merge(barGroup.enter()
                    .append('rect')
                    .attr('class', 'bar'))
                    .attr('x', d => mainScale(d.start))
                    .attr('y', (d, i) => _bandSize * i)
                    .attr('width', d => mainScale(d.end) - mainScale(d.start))
                    .attr('height', _bandSize);

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

        let _scrollThickness = 10;

        let _scrollScale;
        let _slider;

        orientation('VERTICAL');

        function visual(_selection) {
            _selection.each(function (data) {

                let scrollData = prepareScrollSliderData(data);
                _scrollScale = d3.scaleLinear()
                    .domain([0, data.viewportSize])
                    .range([0, data.fullSize])
                    .clamp(true);

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

                // may lead from handleDrag()
                offsetProperty.addOnChange(newOffset => {
                    let newSliderPosition = _scrollScale.invert(newOffset);
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

        function handleDrag(delta) {
            let data = _slider.datum();
            let newPosition = +_slider.attr(_orientationConfig.position) + delta;
            if (newPosition < 0) {
                newPosition = 0;
            } else if (newPosition + data.scrollSize > data.viewportSize) {
                newPosition = data.viewportSize - data.scrollSize;
            }
            offsetProperty.setValue(newPosition);
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
    function timeAxis(mainScale) {

        let _axisHeight = 30;
        let _bandSize = 30;

        function visual(_selection) {
            _selection.each(function (data) {

                let timeAxisGroup = d3.select(this)
                    .selectAll('.time-axis')
                    .data([null]);

                timeAxisGroup = timeAxisGroup.merge(timeAxisGroup.enter()
                    .append('g')
                    .attr('class', 'time-axis'));

                initLines(timeAxisGroup, data);
                initLabels(timeAxisGroup, data);

            });
        }

        return visual;

        function initLines(rootGroup, data) {
            let linesGroup = rootGroup.selectAll('.lines')
                .data([null]);
            linesGroup = linesGroup.merge(linesGroup.enter()
                .append('g')
                .attr('class', 'lines'));

            let lineSelection = linesGroup.selectAll('line')
                .data(data.ticks);

            lineSelection.exit().remove();

            lineSelection.merge(lineSelection.enter()
                .append('line'))
                .attr('x1', d => mainScale(d.time))
                .attr('y1', 0)
                .attr('x2', d => mainScale(d.time))
                .attr('y2', data.tasks * _bandSize + _axisHeight)
                .attr('stroke', 'black');
        }

        function initLabels(rootGroup, data) {
            let labelsGroup = rootGroup.selectAll('.labels')
                .data([null]);
            labelsGroup = labelsGroup.merge(labelsGroup.enter()
                .append('g')
                .attr('class', 'labels'));

            let labelGroup = labelsGroup.selectAll('text')
                .data(data.ticks);

            labelGroup.exit().remove();

            labelGroup.merge(labelGroup.enter()
                .append('text'))
                .attr('x', d => mainScale(d.time))
                .attr('y', _axisHeight)
                .text(d => d.time); // todo format time

        }

    }

    // ##################################################
    // #                                                #
    // #  Task List                                     #
    // #                                                #
    // ##################################################

    // [{id,name}]
    function taskList(onTaskRemove) {

        let _bandSize = 30;

        function visual(_selection) {
            _selection.each(function (data) {

                let selection = d3.select(this);

                let taskListGroup = selection.selectAll('.task-list')
                    .data([null]);
                taskListGroup = taskListGroup.merge(taskListGroup.enter()
                    .append('g')
                    .attr('class', 'task-list'));

                let taskGroup = taskListGroup.selectAll('.task')
                    .data(data);

                let taskGroupEnter = taskGroup.enter()
                    .append('g')
                    .attr('class', '.task');
                taskGroupEnter.append('text')
                    .text(d => d.name)
                    .attr('x', 0)
                    .attr('y', (d, i) => (i + 1) * _bandSize);
                taskGroupEnter.append('rect')
                    .attr('x', 0)
                    .attr('y', (d, i) => i * _bandSize)
                    .attr('width', 10)
                    .attr('height', 10)
                    .on('click', d => onTaskRemove(d.id));

                taskGroup.exit().remove();

                taskGroup.merge(taskGroupEnter);

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
        let _listeners = [];

        return {
            addOnChange,
            setValue,
            getValue
        };

        function addOnChange(onChange) {
            if (!onChange) {
                return;
            }

            _listeners.push(onChange);
        }

        function setValue(value) {
            if (value === _value) {
                return;
            }
            _value = value;
            for (const listener of _listeners) {
                listener(value);
            }
        }

        function getValue() {
            return _value;
        }

    }

}