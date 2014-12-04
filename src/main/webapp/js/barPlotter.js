/**
    Actualiza un Chart automaticamente.
*/

//amChart: amChart
//keys: string[]
function BarChartPlotter(amChart, keys) {
    this.chart = amChart ;
    this.adaptor = new ChartAdaptor() ;
    this.keys = keys ;
    this.dataProvider = this.adaptor.generateDataProvider(this.keys);


}

//tag: string
//values: json
BarChartPlotter.prototype.addGraph = function (tag, values) {
    this.adaptor.addDataToProvider(tag, values, this.dataProvider) ;

    this.chart.addGraph({
        "balloonText"   : tag+" [ [[category]] ] = [["+tag+"]] ",
        "fillAlphas"    : 1, //mas claro u oscuro el relleno del Graph
        "title"         : "Column graph",
        "type"          : "column",
        "valueField"    : tag
    });
    
    this.chart.dataProvider = this.dataProvider ;
    this.chart.validateData();
    this.chart.animateAgain();
};
