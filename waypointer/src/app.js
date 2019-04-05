let points = [
    {x:-919168, y:-923904},
    {x:-923392, y:-922112},
    {x:-924160, y:-922112},
    {x:-923776, y:-924032},
    {x:-920064, y:-924160},
    {x:-919040, y:-920064},
    {x:-920064, y:-920064},
    {x:-921728, y:-924160},
    {x:-920192, y:-919040},
    {x:-919168, y:-919040},
    {x:-918528, y:-921600},
];

let edges = [];

let offset = {
    x: toScreen(d3.min(points.map(p => p.x))),
    y: toScreen(d3.min(points.map(p => p.y)))
};

let firstPoint;

function render() {
    let svg = groupBuilder(d3.select('#root'), 'svg')
        .tag('svg')
        .build()
        .attr('width', 1024)
        .attr('height', 768);

    groupBuilder(svg, 'rect')
        .data(points)
        .tag('rect')
        .build()
        .attr('x', d => toScreen(d.x) - offset.x )
        .attr('y', d => toScreen(d.y) - offset.y )
        .attr('width', 10)
        .attr('height', 10)
        .on('click', d => {
            if (!firstPoint) {
                firstPoint = d;
                return;
            }
            edges.push({
                from: firstPoint,
                to: d
            });
            firstPoint = null;
            render();
        });

    groupBuilder(svg, 'line')
        .data(edges)
        .tag('line')
        .build()
        .attr('x1', d => toScreen(d.from.x) - offset.x)
        .attr('y1', d => toScreen(d.from.y) - offset.y)
        .attr('x2', d => toScreen(d.to.x) - offset.x)
        .attr('y2', d => toScreen(d.to.y) - offset.y);

}

function toScreen(value) {
    return value / 10;
}

// ##################################################
// #                                                #
// #  Group Builder                                 #
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

    function data(data, getKey) {
        _data = data;
        if (getKey) {
            _getKey = getKey;
        }
        return this;
    }

    function tag(_) {
        _tag = _;
        return this;
    }

    function style(_) {
        _style = _;
        return this;
    }

}