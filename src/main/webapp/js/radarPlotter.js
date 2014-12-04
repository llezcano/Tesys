/**
	Actualiza un Chart automaticamente.
*/

//amChart: amChart
//keys: string[]
function RadarChartPlotter(amChart, keys) {
	this.chart = amChart ;
	this.adaptor = new ChartAdaptor() ;
	this.keys = keys ;
    this.dataProvider = this.adaptor.generateDataProvider(this.keys);


}

//tag: string
//values: json
//graph: json (amChart addGraph format)
RadarChartPlotter.prototype.addGraph = function (tag, values) {
	this.adaptor.addDataToProvider(tag, values, this.dataProvider) ;
	
    this.chart.addGraph({
        "balloonText": tag+" [ [[category]] ] = [["+tag+"]] % (from [[total]]) ",
        "bullet": "round",
        "fillAlphas": 0.3, //mas claro u oscuro el relleno del Graph
        "valueField": tag
    });
    
	this.chart.dataProvider = this.adaptor.normalizeDataProvider(this.dataProvider) ;
	this.chart.validateData();
	this.chart.animateAgain();
};
