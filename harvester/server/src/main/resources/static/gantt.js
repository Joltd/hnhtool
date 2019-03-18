function gantt() {

// ##################################################
// #                                                #
// #  Gantt                                         #
// #                                                #
// ##################################################

function visual(_selection) {
    _selection.each(function (data) {

    });
}

return visual;


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

    let _value;
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

};