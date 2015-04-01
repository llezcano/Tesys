//boot.js
requirejs.config({
    baseUrl: 'js',
    paths: {
    		'jquery': 'libs/jquery-1.7.1.min',
         	'extractor': 'elasticsearch/extractor',
         	'handlers': 'handlers',
         	'app': 'app-index',
         	'adaptor': 'charts/chartAdaptor',
			'bar': 'charts/barPlotter',
			'radar': 'charts/radarPlotter',
    		'amcharts': 'libs/amcharts/amcharts',
    		'amcharts.radar': 'libs/amcharts/radar',
    		'amcharts.serial': 'libs/amcharts/serial',
    		'parser': 'parser'
	},

	shim: {
		'amcharts'    : {
		    exports: 'AmCharts',
		    init: function() {
		        AmCharts.isReady = true;
		    }
		},
		'amcharts.radar'    : {
		    deps: ['amcharts'],
		    exports: 'AmCharts',
		    init: function() {
		        AmCharts.isReady = true;
		    }
		},
		'amcharts.serial'   : {
		    deps: ['amcharts'],
		    exports: 'AmCharts',
		    init: function() {
		        AmCharts.isReady = true;
		    }
		},
		'radar'    : {
		    deps: ['adaptor'],
		    exports: 'radar'
		},
		'bar'    : {
		    deps: ['adaptor'],
		    exports: 'bar'
		}
	}
});


requirejs(["app", "handlers"], function(App) {
    App.start() ;
});

