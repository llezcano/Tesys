define(["jquery", "extractor", "parser"], function($, extractor, Parser) {


	/* Main function */
  	var start = function() {
		var skillPlotter ;
		var metricPlotter ;
		extractor.getUsers('#users') ;
		extractor.getSkills('#skills') ;
		
		extractor.getMetrics('#metrics') ;
		//Busco los issues del primer item seleccionado
		extractor.getIssuesByUser($('#users').find('option:selected').val(), '#issues') ;

		p = new Parser() ;
		console.log("hola mundo") ;
	}

	return { 
		'start': start 
	};
});